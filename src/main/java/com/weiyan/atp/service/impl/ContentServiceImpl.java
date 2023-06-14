package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatContent;
import com.weiyan.atp.data.bean.PlatOrg;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.EncryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryContentsCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryOrgCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.ShareContentCCRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.request.chaincode.plat.*;
import com.weiyan.atp.data.request.web.ThresholdFilesRequest;
import com.weiyan.atp.data.response.chaincode.plat.BaseListResponse;
import com.weiyan.atp.data.response.chaincode.plat.ContentResponse;
import com.weiyan.atp.data.response.chaincode.plat.OrgMembersResponse;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.data.response.intergration.RingSignatureResponse;
import com.weiyan.atp.data.response.intergration.ThresholdResponse;
import com.weiyan.atp.data.response.web.PlatContentsResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.ContentService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.*;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Service
@Slf4j
@Validated
public class ContentServiceImpl implements ContentService {
    private final ChaincodeService chaincodeService;
    private final AttrService attrService;
    private final UserRepositoryService userRepositoryService;
    private final OrgRepositoryServiceImpl orgRepositoryService;
    private final DABEService dabeService;

    @Value("${atp.path.privateKey}")
    private String priKeyPath;

    @Value("${atp.path.publicKey}")
    private String pubKeyPath;

    @Value("${atp.path.shareData}")
    private String shareDataPath;

    public ContentServiceImpl(ChaincodeService chaincodeService, AttrService attrService,
                              UserRepositoryService userRepositoryService,
                              OrgRepositoryServiceImpl orgRepositoryService,
                              DABEService dabeService) {
        this.chaincodeService = chaincodeService;
        this.attrService = attrService;
        this.userRepositoryService = userRepositoryService;
        this.orgRepositoryService = orgRepositoryService;
        this.dabeService = dabeService;
    }

    /**
     * 分享内容
     */
    @Override
    public void shareContent(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
            .uid(user.getName())
            .tags(request.getTags())
            .content(encryptedContent)
            .build();
        CCUtils.SM2sign(shareContentCCRequest, request.getFileName(),user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke share content to plat error: {}", response.getMessage());
            throw new BaseException("invoke share content to plat error");
        }
        log.info("invoke share content to plat success");
    }

    /**
     * 系统对接 - 分享内容
     */
    @Override
    public EncryptionResponse encContent(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                .uid(user.getName())
                .tags(request.getTags())
                //.content(encryptedContent)
                .fileName(request.getSharedFileName())
                .build();
        CCUtils.SM2sign(shareContentCCRequest, request.getFileName(),user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke share content to plat error: {}", response.getMessage());
            throw new BaseException("invoke share content to plat error");
        }
        log.info("invoke share content to plat success");

        return EncryptionResponse.builder()
                .cipher(encryptedContent)
                .cipherHash(SecurityUtils.md5(encryptedContent))
                .policy(request.getPolicy())
                .tags(request.getTags())
                .uid(user.getName())
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .build();
    }

    @Override
    public EncryptionResponse encContent2(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        // 环签名
        String org="";
        RingSignatureResponse ringSig=new RingSignatureResponse();
        if(!request.getUploader().equals(request.getFileName())){
            // getRingSignature
            org=request.getUploader();
            // get Org Members
            String[] orgMembers=getOrgMembers(org);
            // 确保签名用户在组织内
            if (!Arrays.asList(orgMembers).contains(request.getFileName())){
                log.info("user is not in org {}",org);
                throw new BaseException("user is not in org");
            }
            List<SM2KeyPair> orgKey = new ArrayList<>();
            try {
                // 获取签名用户公私钥
                String priKey = FileUtils.readFileToString(
                        new File(priKeyPath + request.getFileName()),
                        StandardCharsets.UTF_8);
                String pubKey = FileUtils.readFileToString(new File(pubKeyPath + request.getFileName()),
                        StandardCharsets.UTF_8);
                orgKey.add(SM2Utils.GetKey(priKey,pubKey));
                // 获取组织内其他人公钥
                for(int i=0;i<orgMembers.length;i++){
                    if(orgMembers[i].equals(request.getFileName())) {continue;}
                    pubKey = FileUtils.readFileToString(new File(pubKeyPath + orgMembers[i]),
                            StandardCharsets.UTF_8);
                    orgKey.add(SM2Utils.GetKey(null,pubKey));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SM2KeyPair[] KeyPairs= orgKey.toArray(new SM2KeyPair[orgKey.size()]);
            try {
                ringSig=RingSignatures.genRing(KeyPairs,encryptedContent.getBytes(),1024,new Random());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            //get 'encryptedContent' Signature
            try {
                String priKey = FileUtils.readFileToString(
                        new File(priKeyPath + user.getName()),
                        StandardCharsets.UTF_8);
                String pubKey = FileUtils.readFileToString(new File(pubKeyPath + user.getName()),
                        StandardCharsets.UTF_8);
                String ringS=SM2Utils.getSign(priKey,pubKey,user.getName(),encryptedContent);
                ringSig.setSig(ringS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                .uid(user.getName())
                .tags(request.getTags())
               // .content(encryptedContent)
                .timestamp(new Date().toString())
                .fileName(request.getSharedFileName())
                .ip(request.getIp())
                .location(request.getLocation())
                .policy(request.getPolicy())
                .org(org)
                .build();
        System.out.println("ccccccccccccccccc");
        System.out.println(shareContentCCRequest.toString());
        CCUtils.SM2sign(shareContentCCRequest,request.getFileName(),user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke share content to plat error: {}", response.getMessage());
            throw new BaseException("invoke share content to plat error");
        }
        log.info("invoke share content to plat success");

        // 环签名
        // return signature
        return EncryptionResponse.builder()
                .cipher(encryptedContent)
                .cipherHash(SecurityUtils.md5(encryptedContent))
                .policy(request.getPolicy())
                .tags(request.getTags())
                .uid(user.getName())
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .ringSignature(ringSig)
                .build();
    }


    @Override
    public ThresholdResponse encThresholdContent(ThresholdFilesRequest request) {
        String encryptedContent = getThresholdEncryptedContent(request);

        PlatOrg platOrg = orgRepositoryService.queryOrg(request.getOrgName());
//        QueryOrgCCRequest orgCCRequestequest = QueryOrgCCRequest.builder()
//                .orgId(request.getOrgName())
//                .build();
//        ChaincodeResponse responseOrg =
//                chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrg", orgCCRequestequest);
//        if (responseOrg.isFailed()) {
//            throw new BaseException("no org exists for " + request.getOrgName());
//        }
//        PlatOrg platOrg = JsonProviderHolder.JACKSON.parse(responseOrg.getMessage(), PlatOrg.class);
        Preconditions.checkNotNull(platOrg.getOrgId());
        ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                .uid(platOrg.getOrgId())
//                    .tags(request.getTags())
                // .content(encryptedContent)
                .timestamp(new Date().toString())
                .fileName(request.getFileName())
//                    .ip(request.getIp())
//                    .location(request.getLocation())
//                    .policy(request.getPolicy())
                .build();
        System.out.println(request.getFileName());
        System.out.println(shareContentCCRequest.getFileName());
        System.out.println("ccccccccccccccccc");
//        CCUtils.SM2sign(shareContentCCRequest,request.getFileName(),user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareThreholdMessage", shareContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke Threshold content to plat error: {}", response.getMessage());
            throw new BaseException("invoke Threshold content to plat error");
        }

        log.info("invoke Threshold content to plat success");
        return ThresholdResponse.builder()
                .cipher(encryptedContent)
                .orgName(request.getOrgName())
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .filename(request.getFileName())
                .build();
    }



    public String decryptContent(@NotEmpty String cipher, @NotEmpty String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(cipher, "", "", user);
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/common/decrypt", ccRequest);
        if (response.isFailed()) {
            throw new BaseException("decryption error: " + response.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public ChaincodeResponse decryptContent2(@NotEmpty String cipher, @NotEmpty String userName, @NotEmpty String fileName, @NotEmpty String sharedUser) {
        DABEUser user = dabeService.getUser(userName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(cipher, fileName, sharedUser, user);
        return chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/common/decrypt", ccRequest);
    }

    @Override
    public ChaincodeResponse getCipher(@NotEmpty String userName, @NotEmpty String fileName, @NotEmpty String sharedUser) {
        DABEUser user = dabeService.getUser(userName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(userName, fileName, sharedUser);
        return chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/common/getCipher", ccRequest);
    }

    @Override
    public PlatContentsResponse queryPlatContents(String fromUserName, String tag,
                                                  int pageSize, String bookmark) {
//        if (StringUtils.isEmpty(fromUserName) && StringUtils.isEmpty(tag)) {
//            throw new BaseException("request error, cannot query all message");
//        }
        QueryContentsCCRequest request = QueryContentsCCRequest.builder()
                .fromUid(fromUserName)
                .tag(tag)
                .bookmark(bookmark)
                .pageSize(pageSize)
                .build();
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getMessage", request);
        if (response.isFailed()) {
            log.info("query contents from plat error: {}", response.getMessage());
            throw new BaseException("query contents from plat error: " + response.getMessage());
        }
        System.out.println("response:");
        System.out.println(response.getMessage());
        BaseListResponse<ContentResponse> baseListResponse = JsonProviderHolder.JACKSON.parse(
                response.getMessage(), new TypeReference<BaseListResponse<ContentResponse>>() {
                });
        //System.out.println("baselistresponse:");
        //System.out.println(baseListResponse.getResult().stream());
        return PlatContentsResponse.builder()
                .bookmark(baseListResponse.getResponseMetadata().getBookmark())
                .count(Integer.parseInt(baseListResponse.getResponseMetadata().getRecordsCount()))
                .pageSize(pageSize)
                .contents(baseListResponse.getResult().stream()
                        .map(contentResponseCCResult -> new PlatContent(contentResponseCCResult.getRecord()))
                        .collect(Collectors.toList()))
                .build();

    }
    @Override
    public PlatContentsResponse queryThresholdPlatContents(String orgName, String fileName) {
        QueryThresholdCCRequest request = QueryThresholdCCRequest.builder()
                .fileName(fileName)
                .orgName(orgName)
                .build();
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getThresholdMessage", request);
        if (response.isFailed()) {
            log.info("query ThresholdContents from plat error: {}", response.getMessage());
            throw new BaseException("query ThresholdContents from plat error: " + response.getMessage());
        }
        System.out.println(response.getMessage());
        BaseListResponse<ContentResponse> baseListResponse = JsonProviderHolder.JACKSON.parse(
                response.getMessage(), new TypeReference<BaseListResponse<ContentResponse>>() {
                });
        return PlatContentsResponse.builder()
                .bookmark(baseListResponse.getResponseMetadata().getBookmark())
                .count(Integer.parseInt(baseListResponse.getResponseMetadata().getRecordsCount()))
                .pageSize(1)
                .contents(baseListResponse.getResult().stream()
                        .map(contentResponseCCResult -> new PlatContent(contentResponseCCResult.getRecord()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String queryOrgThresholdPlatContents(String orgName, String fileName,String fromUid) {

        QueryOrgThresholdFileCCRequest request = QueryOrgThresholdFileCCRequest.builder()
                .orgId(orgName)
                .fileName(fileName)
                .fromUid(fromUid)
                .build();
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryThresholdFileApply", request);
        if (response.isFailed()) {
            log.info("query ThresholdContents from plat error: {}", response.getMessage());
            throw new BaseException("query ThresholdContents from plat error: " + response.getMessage());
        }
        System.out.println(response.getMessage());

        return response.getMessage();
    }

    private String getEncryptedContent(ShareContentRequest request) {
        EncryptContentCCRequest ccRequest = EncryptContentCCRequest.builder()
                .plainContent(request.getPlainContent())
                .policy(request.getPolicy())
                .fileName(request.getSharedFileName())
                .userName(request.getFileName())
                .authorityMap(EncryptContentCCRequest.buildAuthorityMap(
                        request.getPolicy(), attrService, userRepositoryService, orgRepositoryService))
                .build();

        ChaincodeResponse response =
                chaincodeService.query(ChaincodeTypeEnum.DABE, "/common/encrypt", ccRequest);
        if (response.isFailed()) {
            log.info("query for encrypt error: {}", response.getMessage());
            throw new BaseException("query for encrypt error: " + response.getMessage());
        }
        return response.getMessage();
    }

    private String getThresholdEncryptedContent(ThresholdFilesRequest request) {

//        ChaincodeResponse response1 =
//                chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrgMen", request.getOrgName());
//        EncyptThresholdContentCCRequest ccRequest = EncyptThresholdContentCCRequest.builder()
//                .PlainContent(request.getPlainContent())
//                .PubKey(response1.getMessage())
//                .build();
        EncryptThresholdContentCCRequest ccRequest = EncryptThresholdContentCCRequest.builder()
                .plainContent(request.getPlainContent())
                .orgId(request.getOrgName())
                .build();
        System.out.println("门限加密信息");

        System.out.println("门限公钥信息");
        System.out.println(orgRepositoryService.queryOrg(request.getOrgName()));
        ChaincodeResponse response =
                chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/org/thresholdEncrypt", ccRequest);
        if (response.isFailed()) {
            log.info("query for encrypt error: {}", response.getMessage());
            throw new BaseException("query for encrypt error: " + response.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public String[] getOrgMembers(String org) {
        QueryOrgCCRequest request = QueryOrgCCRequest.builder()
                .orgId(org)
                .build();
        ChaincodeResponse response =
                chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrg", request);
        if (response.isFailed()) {
            log.info("query for Org Members error: {}", response.getMessage());
            throw new BaseException("query for Org Members error: " + response.getMessage());
        }
        System.out.println(response.getMessage());
        OrgMembersResponse baseListResponse = JsonProviderHolder.JACKSON.parse(
                response.getMessage(), new TypeReference<OrgMembersResponse>() {});
        return baseListResponse.getUidSet();
    }
}


