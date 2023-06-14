package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.constant.OrgApplyStatusEnum;
import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.*;
import com.weiyan.atp.data.bean.DABEUser.ASKPart;
import com.weiyan.atp.data.bean.DABEUser.OSKPart;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptThresholdContentCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.*;
import com.weiyan.atp.data.request.web.*;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.OrgRepositoryService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@Service
@Slf4j
@Validated
public class OrgRepositoryServiceImpl implements OrgRepositoryService {
    private final ChaincodeService chaincodeService;
    private final DABEService dabeService;
    private final UserRepositoryService userRepositoryService;

    @Value("${atp.path.dabeUser}")
    private String userPath;
    @Value("${atp.path.privateKey}")
    private String priKeyPath;
    @Value("${atp.path.publicKey}")
    private String pubKeyPath;

    @Value("atp/orgThreshold/enc/")
    private String thresholdEncDataPath;

    @Value("atp/orgThreshold/dec/")
    private String thresholdDecDataPath;
    public OrgRepositoryServiceImpl(ChaincodeService chaincodeService, DABEService dabeService,
                                    UserRepositoryService userRepositoryService) {
        this.chaincodeService = chaincodeService;
        this.dabeService = dabeService;
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public ChaincodeResponse applyCreateOrg(CreateOrgRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        //check request
        if (request.getUsers().size() != request.getN()
            || request.getN() < request.getT()
            || request.getT() < 1
            || !request.getUsers().contains(user.getName())) {
            throw new BaseException("request error");
        }

        CreateOrgCCRequest ccRequest = CreateOrgCCRequest.builder()
            .t(request.getT())
            .n(request.getN())
            .orgId(request.getOrgName())
            .uidList(request.getUsers())
            .uid(user.getName())
            .userStr(JsonProviderHolder.JACKSON.toJsonString(user))
            .build();
        CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
        return chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/createOrgApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyCreateOrg2(CreateOrgRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        //check request
        if (request.getUsers().size() != request.getN()
                || request.getN() < request.getT()
                || request.getT() < 1
                || !request.getUsers().contains(user.getName())) {
            throw new BaseException("request error");
        }

        CreateOrgCCRequest ccRequest = CreateOrgCCRequest.builder()
                .t(request.getT())
                .n(request.getN())
                .orgId(request.getOrgName())
                .uidList(request.getUsers())
                .uid(user.getName())
                .userStr(JsonProviderHolder.JACKSON.toJsonString(user))
                .build();
        CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/createOrgApply", ccRequest);
    }

    //门限用户申请生成公钥
    @Override
    public ChaincodeResponse applyThresholdOrg(String orgName, String uid) {
        DABEUser user = dabeService.getUser(uid);
        Preconditions.checkNotNull(user.getName());

        CreateThresholdOrgCCRequst requst = CreateThresholdOrgCCRequst.builder()
                .orgId(orgName)
                .uid(uid)
                .build();

        CCUtils.SM2sign(requst, uid,user.getName());
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/ThresholdOrgApply", requst);
    }

    @Override
    public ChaincodeResponse applyDeclareOrgAttr(DeclareOrgAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        DeclareOrgAttrCCRequest ccRequest = DeclareOrgAttrCCRequest.builder()
            .orgId(request.getOrgName())
            .attrName(request.getAttrName())
            .uid(user.getName())
            .build();
        CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
        return chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/declareAttrApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyDeclareOrgAttr2(DeclareOrgAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        DeclareOrgAttrCCRequest ccRequest = DeclareOrgAttrCCRequest.builder()
                .orgId(request.getOrgName())
                .attrName(request.getAttrName())
                .uid(user.getName())
                .build();

        CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/declareAttrApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyThresholdFile(ThresholdApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getUserName());
        Preconditions.checkNotNull(user.getName());

//        DeclareOrgAttrCCRequest ccRequest = DeclareOrgAttrCCRequest.builder()
//                .orgId(request.getOrgName())
//                .attrName(request.getFileName())
//                .uid(user.getName())
//                .build();
//        CCUtils.sign(ccRequest, getPriKey(request.getFileName()));
        ApplyThresholdFileCCRequest ccRequest = ApplyThresholdFileCCRequest.builder()
                .uid(request.getUserName())
                .orgId(request.getOrgName())
                .fileName(request.getFileName())
                .build();
        System.out.println("tttttttttttttttttttttttt");
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/ThresholdFileApply", ccRequest);
    }

    public String getPriKey(String fileName) {
        try {
            return FileUtils.readFileToString(
                new File(priKeyPath + fileName),
                StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BaseException("io error:" + e.getMessage());
        }
    }

    /**
     * 同意组织的申请
     * 0_5. 查询必要的申请信息
     * 1. plat的同意加入
     * 2. dabe生成对应的秘密
     * 3. plat提交秘密
     */
    @Override
    public void approveOrgApply(OrgApplyTypeEnum type,
                                ApproveOrgApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        // 0_5. 查询必要的申请信息
        PlatOrgApply orgApply = queryOrgApply(request.getOrgName(), type, request.getAttrName());

        // 1. plat的同意加入
        if (!orgApply.getFromUserName().equals(user.getName())) {
            ApproveOrgApplyCCRequest ccRequest1 = ApproveOrgApplyCCRequest.builder()
                    .orgId(request.getOrgName())
                    .uid(user.getName())
                    .attrName(request.getAttrName())
                    .build();
            CCUtils.SM2sign(ccRequest1,request.getFileName(),user.getName());
            ChaincodeResponse response1 = chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, type.getApproveFunctionName(), ccRequest1);
            if (response1.isFailed()) {
                throw new BaseException("approve apply in plat error:" + response1.getMessage());
            }
        }

        // 2. dabe生成对应的秘密
        ArrayList<String> params = new ArrayList<>(Arrays.asList(
                JsonProviderHolder.JACKSON.toJsonString(user),
                request.getOrgName(),
                StringUtils.join(orgApply.getUidMap().keySet(), ","),
                orgApply.getT().toString(),
                orgApply.getN().toString()));
        if (StringUtils.isNotEmpty(request.getAttrName())) {
            params.add(request.getAttrName());
        }
        ChaincodeResponse response2 = chaincodeService.query(ChaincodeTypeEnum.DABE,
                "/user/share", params);
        if (response2.isFailed()) {
            throw new BaseException("generate share in dabe error: " + response2.getMessage());
        }

//        String filePath = userPath + fileName;
//        String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
//        DABEUser user = JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response2.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        CCUtils.saveDABEUser(userPath + request.getFileName(),
                JsonProviderHolder.JACKSON.toJsonString(newUser));

        // 3. plat提交秘密
        Map<String, String> shareMap = type == OrgApplyTypeEnum.CREATION
                ? newUser.getOskMap().get(request.getOrgName()).getShare()
                : newUser.getOskMap().get(request.getOrgName()).getAskMap().get(request.getAttrName()).getShare();
        shareMap.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(user.getName()))
                .forEach(entry -> {
                    PlatUser platUser = userRepositoryService.queryUser(
                            QueryUserRequest.builder().userName(entry.getKey()).build());

                    SubmitOrgShareCCRequest ccRequest = SubmitOrgShareCCRequest.builder()
                            .orgId(request.getOrgName())
                            .share(new String(Base64.encode(
                                    SecurityUtils.encrypt(SecurityUtils.RSA_PKCS1,
                                            SecurityUtils.from(SecurityUtils.X509, platUser.getPublicKey()),
                                            entry.getValue().getBytes()))))
                            .toUid(entry.getKey())
                            .type(type)
                            .uid(user.getName())
                            .attrName(request.getAttrName())
                            .build();
                    CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
                    ChaincodeResponse response = chaincodeService.invoke(
                            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/shareSecret", ccRequest);
                    if (response.isFailed()) {
                        throw new BaseException("submit share to " + entry.getKey()
                                + " error: " + response.getMessage());
                    }
                });
    }

    @Override
    public void approveOrgApply2(OrgApplyTypeEnum type,
                                 ApproveOrgApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
//        String priKey = getPriKey(request.getFileName());

        // 0_5. 查询必要的申请信息
        PlatOrgApply orgApply = queryOrgApply(request.getOrgName(), type, request.getAttrName());
        System.out.println(orgApply);

        // 1. plat的同意加入
        if (!orgApply.getFromUserName().equals(user.getName())) {
            ApproveOrgApplyCCRequest ccRequest1 = ApproveOrgApplyCCRequest.builder()
                    .orgId(request.getOrgName())
                    .uid(user.getName())
                    .attrName(request.getAttrName())
                    .build();
            CCUtils.SM2sign(ccRequest1,request.getFileName(),user.getName());
            ChaincodeResponse response1 = chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, type.getApproveFunctionName(), ccRequest1);
            if (response1.isFailed()) {
                throw new BaseException("approve apply in plat error:" + response1.getMessage());
            }
        }

        // 2. dabe生成对应的秘密
        ArrayList<String> params = new ArrayList<>(Arrays.asList(
                JsonProviderHolder.JACKSON.toJsonString(user),
                request.getOrgName(),
                StringUtils.join(orgApply.getUidMap().keySet(), ","),
                orgApply.getT().toString(),
                orgApply.getN().toString()));
        if (StringUtils.isNotEmpty(request.getAttrName())) {
            params.add(request.getAttrName());
        }
        ChaincodeResponse response2 = chaincodeService.query(ChaincodeTypeEnum.DABE,
                "/user/share", params);
        if (response2.isFailed()) {
            throw new BaseException("generate share in dabe error: " + response2.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response2.getMessage(), DABEUser.class);
        System.out.println("CHECKKKKK");
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + request.getFileName(),
                JsonProviderHolder.JACKSON.toJsonString(newUser));
        System.out.println("checkkkkkkkkkkkkkkkkkk");
        System.out.println(JsonProviderHolder.JACKSON.toJsonString(user));
        System.out.println(JsonProviderHolder.JACKSON.toJsonString(newUser));


        // 3. plat提交秘密
        Map<String, String> shareMap = type == OrgApplyTypeEnum.CREATION
                ? newUser.getOskMap().get(request.getOrgName()).getShare()
                : newUser.getOskMap().get(request.getOrgName()).getAskMap().get(request.getAttrName()).getShare();
        System.out.println(type);
        System.out.println(shareMap);
        shareMap.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(user.getName()))
                .forEach(entry -> {
                    PlatUser platUser = userRepositoryService.queryUser(
                            QueryUserRequest.builder().userName(entry.getKey()).build());

                    SubmitOrgShareCCRequest ccRequest = SubmitOrgShareCCRequest.builder()
                            .orgId(request.getOrgName())
                            .share(entry.getValue())
                            .toUid(entry.getKey())
                            .type(type)
                            .uid(user.getName())
                            .attrName(request.getAttrName())
                            .build();
                    CCUtils.SM2sign(ccRequest,request.getFileName(),user.getName());
                    ChaincodeResponse response = chaincodeService.invoke(
                            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/shareSecret", ccRequest);
                    if (response.isFailed()) {
                        throw new BaseException("submit share to " + entry.getKey()
                                + " error: " + response.getMessage());
                    }
                });
    }
//同意生成门限公钥
    @Override
    public void approveThresholdApply(String orgName, String uid) {
        DABEUser user = dabeService.getUser(uid);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(uid);
        String part_sk = user.getOskMap().get(orgName).getOsk();

        ApproveThresholdOrgCCRequest request = ApproveThresholdOrgCCRequest.builder()
                .partSk(part_sk)
                .orgId(orgName)
                .uid(uid)
                .build();
        CCUtils.SM2sign(request,uid,user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/approveThresholdOrgApply", request);
        if (response.isFailed()) {
            throw new BaseException("submit thresholdSK to " + orgName
                    + " error: " + response.getMessage());
        }

    }

    /**
     * 提交part pk
     * 1. 获取其他人提供的秘密：检查是否能提交
     * 2. dabe 合并share
     * 3. plat 提交
     */
    @Override
    public void submitPartPk(OrgApplyTypeEnum type, String orgName,
                             String fileName, String attrName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());

        if ((type == OrgApplyTypeEnum.CREATION && user.getOpkMap().containsKey(orgName)
            && StringUtils.isNotEmpty(user.getOpkMap().get(orgName).getOpk()))
            ||
            (type == OrgApplyTypeEnum.ATTRIBUTE && user.getOpkMap().containsKey(orgName)
                && user.getOpkMap().get(orgName).getApkMap().containsKey(attrName))) {
            //no need do anything
            log.info("already has part pk");
        } else {
            user = generatePartPk(type, orgName, fileName, attrName, user);
        }

        // 3. plat 提交 /org/submitPartPK
        SubmitOrgPartPKCCRequest request = SubmitOrgPartPKCCRequest.builder()
            .orgId(orgName)
            .partPk(type == OrgApplyTypeEnum.CREATION ? user.getOpkMap().get(orgName).getOpk()
                : user.getOpkMap().get(orgName).getApkMap().get(attrName))
            .type(type)
            .uid(user.getName())
            .attrName(attrName)
            .build();
        CCUtils.SM2sign(request,request.getUid(),user.getName());
        ChaincodeResponse response2 = chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/submitPartPK", request);
        if (response2.isFailed()) {
            throw new BaseException("submit part pk in plat error:" + response2.getMessage());
        }
    }

    @Override
    public void submitPartPk2(OrgApplyTypeEnum type, String orgName,
                             String fileName, String attrName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());

        if ((type == OrgApplyTypeEnum.CREATION && user.getOpkMap().containsKey(orgName)
                && StringUtils.isNotEmpty(user.getOpkMap().get(orgName).getOpk()))
                ||
                (type == OrgApplyTypeEnum.ATTRIBUTE && user.getOpkMap().containsKey(orgName)
                        && user.getOpkMap().get(orgName).getApkMap().containsKey(attrName))) {
            //no need do anything
            log.info("already has part pk");
        } else {
            user = generatePartPk(type, orgName, fileName, attrName, user);
            System.out.println("0000000000000000000000000000");
            System.out.println(user);
//            user = generatePartPk2(type, orgName, fileName, attrName, user);
        }

        // 3. plat 提交 /org/submitPartPK
        SubmitOrgPartPKCCRequest request = SubmitOrgPartPKCCRequest.builder()
                .orgId(orgName)
                .partPk(type == OrgApplyTypeEnum.CREATION ? user.getOpkMap().get(orgName).getOpk()
                        : user.getOpkMap().get(orgName).getApkMap().get(attrName))
                .type(type)
                .uid(user.getName())
                .attrName(attrName)
                .build();
        CCUtils.SM2sign(request,request.getUid(),user.getName());
        ChaincodeResponse response2 = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/submitPartPK", request);
        if (response2.isFailed()) {
            throw new BaseException("submit part pk in plat error:" + response2.getMessage());
        }
    }

    @Override
    public void submitThresholdPartPK(String orgName, String fileName, String uid, String fromUid) {
        DABEUser user = dabeService.getUser(uid);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(uid);
        SubmitThresholdPartOSKCCRequest request = SubmitThresholdPartOSKCCRequest.builder()
                .orgId(orgName)
                .fileName(fileName)
                .uid(uid)
                .fromUid(fromUid)
                .partPK(user.getOskMap().get(orgName).getOsk())
                .build();
        CCUtils.SM2sign(request,uid,user.getName());
        ChaincodeResponse response2 = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/approveandsubmitThresholdPartOSK", request);
        if (response2.isFailed()) {
            throw new BaseException("submit part pk in plat error:" + response2.getMessage());
        }
    }

    public DABEUser generatePartPk(OrgApplyTypeEnum type, String orgName, String fileName,
                                   String attrName, DABEUser user) {
        // 1. 获取其他人提供的秘密：检查是否能提交
        PlatOrgApply orgApply = queryOrgApply(orgName, type, attrName);
        if (orgApply.getShareMap().getOrDefault(user.getName(), new HashMap<>())
                .size() != orgApply.getN() - 1) {
            throw new BaseException("share not enough");
        }
        if (!user.getOskMap().containsKey(orgName)) {
            throw new BaseException("need share secret first");
        }
//        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
//                orgApply.getShareMap().get(user.getName())
//                        .put(k, new String(SecurityUtils.decrypt(
//                                SecurityUtils.RSA_PKCS1, privateKey, Base64.decode(v)))));

        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
                orgApply.getShareMap().get(user.getName())
                        .put(k, v));
        if (type == OrgApplyTypeEnum.CREATION) {
            OSKPart oskPart = user.getOskMap().get(orgName);
            oskPart.getOthersShare().add(oskPart.getShare().get(user.getName()));
            oskPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        } else {
            ASKPart askPart = user.getOskMap().get(orgName).getAskMap().get(attrName);
            askPart.getOthersShare().add(askPart.getShare().get(user.getName()));
            askPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        }

        // 2. dabe 合并share
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/assembleShare",
                new ArrayList<>(Arrays.asList(
                        JsonProviderHolder.JACKSON.toJsonString(user),
                        orgName,
                        StringUtils.isEmpty(attrName) ? "" : attrName,
                        orgApply.getN().toString()
                )));
        if (response.isFailed()) {
            throw new BaseException("assemble share in dabe error: " + response.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + fileName,
                JsonProviderHolder.JACKSON.toJsonString(newUser));
//        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
//        CCUtils.saveDABEUser(userPath + fileName,
//                JsonProviderHolder.JACKSON.toJsonString(newUser));
        return newUser;
    }

    public DABEUser generatePartPk2(OrgApplyTypeEnum type, String orgName, String fileName,
                                    String attrName, DABEUser user) {
        // 1. 获取其他人提供的秘密：检查是否能提交
        PlatOrgApply orgApply = queryOrgApply(orgName, type, attrName);
        if (orgApply.getShareMap().getOrDefault(user.getName(), new HashMap<>())
                .size() != orgApply.getN() - 1) {
            throw new BaseException("share not enough");
        }
        if (!user.getOskMap().containsKey(orgName)) {
            throw new BaseException("need share secret first");
        }
        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
                orgApply.getShareMap().get(user.getName())
                        .put(k, v));
        if (type == OrgApplyTypeEnum.CREATION) {
            OSKPart oskPart = user.getOskMap().get(orgName);
            oskPart.getOthersShare().add(oskPart.getShare().get(user.getName()));
            oskPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        } else {
            ASKPart askPart = user.getOskMap().get(orgName).getAskMap().get(attrName);
            askPart.getOthersShare().add(askPart.getShare().get(user.getName()));
            askPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        }

        // 2. dabe 合并share
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/assembleShare",
                new ArrayList<>(Arrays.asList(
                        JsonProviderHolder.JACKSON.toJsonString(user),
                        orgName,
                        StringUtils.isEmpty(attrName) ? "" : attrName,
                        orgApply.getN().toString()
                )));
        if (response.isFailed()) {
            throw new BaseException("assemble share in dabe error: " + response.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + fileName,
                JsonProviderHolder.JACKSON.toJsonString(newUser));
//        CCUtils.saveDABEUser(userPath + fileName,
//                JsonProviderHolder.JACKSON.toJsonString(newUser));
        return newUser;
    }

    @Override
    public void mixPartPk(OrgApplyTypeEnum type, String orgName, String attrName, String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());

        MixPartPKCCRequest request = MixPartPKCCRequest.builder()
            .orgId(orgName)
            .type(type)
            .uid(user.getName())
            .attrName(attrName)
            .build();
        CCUtils.SM2sign(request,fileName,user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/mixPartPK", request);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }
    }

    @Override
    public void mixPartPk2(OrgApplyTypeEnum type, String orgName, String attrName, String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());
        MixPartPKCCRequest request = MixPartPKCCRequest.builder()
                .orgId(orgName)
                .type(type)
                .uid(user.getName())
                .attrName(attrName)
                .build();
        CCUtils.SM2sign(request,request.getUid(),user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/mixPartPK", request);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }
    }

    @Override
    public void mixThresholdPartSk(String orgName, String uid) {
        DABEUser user = dabeService.getUser(uid);
        Preconditions.checkNotNull(user.getName());

        CreateThresholdOrgCCRequst ccRequst = CreateThresholdOrgCCRequst.builder()
                .uid(uid)
                .orgId(orgName)
                .build();
        CCUtils.SM2sign(ccRequst,uid,user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/mixPartOSKForThresholdPub", ccRequst);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }
    }

    @Override
    public void Thresholdmixdownload(String orgName, String uid, String fileName) throws IOException {
        DABEUser user = dabeService.getUser(uid);
        Preconditions.checkNotNull(user.getName());
        String cipher = FileUtils.readFileToString(new File(thresholdEncDataPath+"/"+orgName+"/"+fileName), StandardCharsets.UTF_8);
        MixThresholdPartOSKCCRequest request = MixThresholdPartOSKCCRequest.builder()
                .cipherContent(cipher)
                .orgId(orgName)
                .fileName(fileName)
                .uid(uid)
                .build();
        CCUtils.SM2sign(request, uid,user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/decryptThreshold", request);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }

//        DecryptThresholdContentCCRequest thresholdContentCCRequest = new DecryptThresholdContentCCRequest(cipher,response.getMessage());
//        ChaincodeResponse response1 = chaincodeService.query(ChaincodeTypeEnum.DABE,"/common/decryptThreshold",thresholdContentCCRequest);
//
//        File dest = new File(new File(thresholdDecDataPath).getAbsolutePath()+ "/" + uid+"/"+orgName+"/"+fileName);
//        System.out.println(dest.getPath());
////        if (!dest.getParentFile().exists()) {
////            dest.getParentFile().mkdir();
////        }
////        FileUtils.write(dest,response1.getMessage(),StandardCharsets.UTF_8);
//        FileUtils.write(dest,response.getMessage(),StandardCharsets.UTF_8);
    }


    @Override
    public PlatOrg queryOrg(@NotEmpty String orgName) {
//        ChaincodeResponse response = chaincodeService.query(
//            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrg",
//            new ArrayList<>(Collections.singletonList(orgName)));
//        if (response.isFailed()) {
//            throw new BaseException("no org exists for " + orgName);
//        }
//        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrg.class);

        QueryOrgCCRequest orgCCRequestequest = QueryOrgCCRequest.builder()
                .orgId(orgName)
                .build();
        ChaincodeResponse response =
                chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrg", orgCCRequestequest);
        if (response.isFailed()) {
            throw new BaseException("no org exists for " + orgName);
        }
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrg.class);
    }

    @Override
    public PlatOrgApply queryOrgApply(@NotEmpty String orgName, OrgApplyTypeEnum type, String attrName) {
        System.out.println(type.getQueryFunctionName());
        ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM,
            type.getQueryFunctionName(),
            QueryOrgApplyCCRequest.builder()
                .orgId(orgName)
                .status(OrgApplyStatusEnum.PENDING.getIota())
                .attrName(attrName)
                .type(type)
                .build());
        if (response.isFailed()) {
            throw new BaseException("query cc error: " + response.getMessage());
        }
        System.out.println(response.getMessage());
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrgApply.class);
    }

    @Override
    public PlatOrgApply queryThresholdFileApply(String orgName, String fileName, String fromUid) {
        ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM,
                "/org/queryThresholdApply",
                QueryThresholdFileCCRequest.builder()
                        .orgId(orgName)
                        .fileName(fileName)
                        .fromUid(fromUid)
                        .build());
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrgApply.class);
    }


}
