package com.weiyan.atp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatSM2Content;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.EncryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QuerySM2ContentsCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.ShareSM2ContentCCRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.response.chaincode.plat.BaseListResponse;
import com.weiyan.atp.data.response.chaincode.plat.SM2ContentResponse;
import com.weiyan.atp.data.response.web.PlatSM2ContentsResponse;
import com.weiyan.atp.service.*;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.SM2Utils;
import com.weiyan.atp.utils.JsonProviderHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class SM2ContentServiceImpl implements SM2ContentService {
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

    public SM2ContentServiceImpl(ChaincodeService chaincodeService, AttrService attrService,
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
     * 加密定向文件
     */
    @Override
    public String encContent(String data, String filename, String userName, String toName) throws IOException {
        // 使用toName公钥加密
        String pubKey = FileUtils.readFileToString(new File(pubKeyPath + toName),
                StandardCharsets.UTF_8);
        String cipher = SM2Utils.Encrypt(pubKey, data);

        // 使用userName签名后将密文上链
        DABEUser user = dabeService.getUser(userName);
        Preconditions.checkNotNull(user.getName());
        ShareSM2ContentCCRequest shareSM2ContentCCRequest = ShareSM2ContentCCRequest.builder()
                .uid(user.getName())
                .timestamp(new Date().toString())
                .content(cipher)
                .fileName(filename)
                .toName(toName)
                .build();
        System.out.println(shareSM2ContentCCRequest.toString());
        CCUtils.SM2sign(shareSM2ContentCCRequest, userName, user.getName());
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/sm2common/shareMessage", shareSM2ContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke share content to plat error: {}", response.getMessage());
            throw new BaseException("invoke share content to plat error");
        }
        log.info("invoke share content to plat success");

        return cipher;
    }

    /**
     * 查询定向文件
     *
     * @param fromUserName
     * @param toName
     * @param pageSize
     * @param bookmark
     * @return
     */
    @Override
    public PlatSM2ContentsResponse queryPlatContents(String fromUserName, String toName, int pageSize, String bookmark) {
        // 链上查询加密文件
        QuerySM2ContentsCCRequest request = QuerySM2ContentsCCRequest.builder()
                .fromUid(fromUserName)
                .toName(toName)
                .bookmark(bookmark)
                .pageSize(pageSize)
                .build();
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/sm2common/getMessage", request);

        if (response.isFailed()) {
            log.info("query contents from plat error: {}", response.getMessage());
            throw new BaseException("query contents from plat error: " + response.getMessage());
        }
        System.out.println("response:");
        System.out.println(response.getMessage());

        // 返回查询结果
        BaseListResponse<SM2ContentResponse> baseListResponse = JsonProviderHolder.JACKSON.parse(
                response.getMessage(), new TypeReference<BaseListResponse<SM2ContentResponse>>() {
                });
        System.out.println("baselistresponse:");
        System.out.println(baseListResponse.getResult().stream());
        return PlatSM2ContentsResponse.builder()
                .bookmark(baseListResponse.getResponseMetadata().getBookmark())
                .count(Integer.parseInt(baseListResponse.getResponseMetadata().getRecordsCount()))
                .pageSize(pageSize)
                .contents(baseListResponse.getResult().stream()
                        .map(contentResponseCCResult -> new PlatSM2Content(contentResponseCCResult.getRecord()))
                        .collect(Collectors.toList()))
                .build();

    }
}
