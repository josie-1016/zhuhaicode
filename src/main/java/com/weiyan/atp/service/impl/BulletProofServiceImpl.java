package com.weiyan.atp.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.*;
import com.weiyan.atp.data.request.chaincode.bp.VerifyBatchBPCCRequest;
import com.weiyan.atp.data.request.chaincode.bp.VerifyBulletProofCCRequest;
import com.weiyan.atp.data.request.chaincode.bp.VerifyCommitsBPCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.CreateBPCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryBPCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryCommitBPCCRequest;
import com.weiyan.atp.data.request.web.CreateBatchBulletProofRequest;
import com.weiyan.atp.data.request.web.CreateBulletProofRequest;
import com.weiyan.atp.data.request.web.VerifyBulletProofRequest;
import com.weiyan.atp.data.response.chaincode.bp.CommitResponse;
import com.weiyan.atp.data.response.chaincode.plat.BPResponse;
import com.weiyan.atp.data.response.chaincode.plat.BaseListResponse;
import com.weiyan.atp.data.response.chaincode.plat.ProofResponse;
import com.weiyan.atp.data.response.web.BulletProofResponse;
import com.weiyan.atp.service.BulletProofService;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class BulletProofServiceImpl implements BulletProofService {
    private final ChaincodeService chaincodeService;
    private final DABEService dabeService;

    @Value("${atp.path.privateKey}")
    private String priKeyPath;

    @Value("atp/proof/")
    private String proofPath;

    @Value("atp/value/")
    private String valuePath;

    public BulletProofServiceImpl(ChaincodeService chaincodeService, DABEService dabeService) {
        this.chaincodeService = chaincodeService;
        this.dabeService = dabeService;
    }
//    @Override
//    public ChaincodeResponse commit(CreateCommitRequest request) {
//        String commit = getCommit(request.getNumber());
//        DABEUser user = dabeService.getUser(request.getFileName());
//        Preconditions.checkNotNull(user.getName());
//        CommitBPCCRequest commitBPCCRequest = CommitBPCCRequest.builder()
//                .uid(user.getName())
//                .commit(commit)
//                .tags(request.getTags())
//                .build();
//        return chaincodeService.invoke(
//                ChaincodeTypeEnum.TRUST_PLATFORM, "/zk/create", commitBPCCRequest);
//    }


    @Override
    public ChaincodeResponse createBulletProof(CreateBulletProofRequest request) {
        DABEUser user = dabeService.getUser(request.getUserName());
        Preconditions.checkNotNull(user.getName());
        ChaincodeResponse response;
        try {
            String  priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getUserName()),
                    StandardCharsets.UTF_8);
            CreateBPCCRequest createBPCCRequest = CreateBPCCRequest.builder()
                    .uid(user.getName())
                    .pid(request.getPid())
                    .tags(request.getTags())
                    .build();
            String fileName = request.getPid()+createBPCCRequest.getTimestamp();
            //TODO:还是把commit和proof分开
            if(request.getRange() == null){
                CommitResponse commitResponse = getCommit(request.getValue());
                createBPCCRequest.setCommit1(commitResponse.getCommit1());
                createBPCCRequest.setOpen(commitResponse.getOpen());
                String proofpre = JsonProviderHolder.JACKSON.toJsonString(commitResponse.getProof());
                createBPCCRequest.setProofpre(proofpre);
                CCUtils.saveValue(valuePath, request.getUserName(), fileName, request.getValue(), commitResponse.getOpen());
//                bulletProof = BulletProof.builder().commit1(commit).build();
            }else{
                //获取value
                try {
                    String b = FileUtils.readFileToString(
                            new File(valuePath +  request.getUserName()+ "/" + request.getPid()+request.getTimestamp()),
                            StandardCharsets.UTF_8);
                    String[] s = b.split(",");
                    request.setValue(s[0]);
                    request.setOpen(s[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //还是整个proof上链
                BPResponse bp = getBulletProof(request.getValue(), request.getRange(), request.getOpen());
                createBPCCRequest.setCommit2(bp.getCommit2());
                createBPCCRequest.setRange(request.getRange());
                String proof = JsonProviderHolder.JACKSON.toJsonString(bp.getProof());
                createBPCCRequest.setProof(proof);
//                createBPCCRequest.setProofFileName(fileName);
            }
//            createBPCCRequest.setBulletProof(bulletProof);
            CCUtils.sign(createBPCCRequest, priKey);
            //TODO：把proof存在文件中，只把文件名和commit上链？

            response =  chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/zk/create", createBPCCRequest);
            if (response.isFailed()) {
                log.info("invoke create zk error: {}", response.getMessage());
                throw new BaseException("invoke create zk error");
            }
        } catch (IOException e) {
            log.info("get priKey", e);
            throw new BaseException(e.getMessage());
        }
        return response;
    }

    @Override
    public BulletProofResponse queryBulletProof(String userName, String pid, String tag, int pageSize, String bookmark) {
        QueryBPCCRequest request = QueryBPCCRequest.builder()
                .uid(userName)
                .pid(pid)
                .tag(tag)
                .bookmark(bookmark)
                .pageSize(pageSize)
                .build();
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/zk/getZKs", request);
        if (response.isFailed()) {
            log.info("query zks from bp error: {}", response.getMessage());
            throw new BaseException("query zks from bp error: " + response.getMessage());
        }
        BaseListResponse<ProofResponse> baseListResponse = JsonProviderHolder.JACKSON.parse(
                response.getMessage(), new TypeReference<BaseListResponse<ProofResponse>>() {
                });
        return BulletProofResponse.builder()
                .bookmark(baseListResponse.getResponseMetadata().getBookmark())
                .count(Integer.parseInt(baseListResponse.getResponseMetadata().getRecordsCount()))
                .pageSize(pageSize)
                .bulletProofs(baseListResponse.getResult().stream()
                        .map(contentResponseCCResult -> new ProofContent(contentResponseCCResult.getRecord()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public ChaincodeResponse verifyBulletProof(VerifyBulletProofRequest request) {
        BulletProof proof = JsonProviderHolder.JACKSON.parse(request.getProof(), BulletProof.class);
        BulletProof proofpre = new BulletProof();
        if(!Objects.equals(request.getProofpre(), "")){
            proofpre = JsonProviderHolder.JACKSON.parse(request.getProofpre(), BulletProof.class);
        } else {

        }
        Commit commit1 = JsonProviderHolder.JACKSON.parse(request.getCommit1(), Commit.class);
        Commit commit2 = JsonProviderHolder.JACKSON.parse(request.getCommit2(), Commit.class);
        String[] pids = request.getPid().split(",");
        //query tp 找到所有uid，pid，返回所有commit1
        if(pids.length > 1) {
            QueryCommitBPCCRequest queryCommitBPCCRequest = QueryCommitBPCCRequest.builder().pids(Arrays.asList(pids)).uid(request.getUserName()).build();
            ChaincodeResponse response1 = chaincodeService.query(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/zk/getCommits", queryCommitBPCCRequest);
            //，bp检查commits相加是否等于commit1？
            if (response1.isFailed()) {
                log.info("query commits from tp error: {}", response1.getMessage());
                throw new BaseException("query commits from tp error: " + response1.getMessage());
            }
            List<Commit> commits = JsonProviderHolder.JACKSON.parse(
                    response1.getMessage(), new TypeReference<List<Commit>>() {
                    });
            VerifyCommitsBPCCRequest verifyCommitsBPCCRequest = VerifyCommitsBPCCRequest.builder().range(request.getRange()).commits(commits).commit1(commit1).commit2(commit2).proof(proof).build();
            return chaincodeService.query(
                    ChaincodeTypeEnum.BP, "/verifyCommits", verifyCommitsBPCCRequest);
        }
        VerifyBulletProofCCRequest verifyBulletProofCCRequest = new VerifyBulletProofCCRequest(request.getRange(), commit1, commit2, proof, proofpre);
        return chaincodeService.query(
                ChaincodeTypeEnum.BP, "/verify", verifyBulletProofCCRequest);
    }

    @Override
    public ChaincodeResponse createBatchBulletProof(CreateBatchBulletProofRequest request) {
        List<CreateBatchBulletProofRequest.BatchProofRequest> batchProofRequests = JsonProviderHolder.JACKSON.parse(
                request.getBatchProofRequestsStr(), new TypeReference<List<CreateBatchBulletProofRequest.BatchProofRequest>>() {
                });
        DABEUser user = dabeService.getUser(request.getUserName());
        Preconditions.checkNotNull(user.getName());
        ChaincodeResponse response;
        try {
            String  priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getUserName()),
                    StandardCharsets.UTF_8);
            CreateBPCCRequest createBPCCRequest = CreateBPCCRequest.builder()
                    .uid(user.getName())
                    .tags(request.getTags())
                    .build();
            int value = 0;
            BigInteger open=new BigInteger("0");
//            List<Commit> commits = new ArrayList<>();
            StringBuilder pids = new StringBuilder();
            try {
                for (int i = 0; i < batchProofRequests.size(); i++) {
                    String b = FileUtils.readFileToString(
                            new File(valuePath +  request.getUserName()+ "/" + batchProofRequests.get(i).getPid()+batchProofRequests.get(i).getTimestamp()),
                            StandardCharsets.UTF_8);
                    String[] s = b.split(",");
                    value += Integer.parseInt(s[0]);
                    open = open.add(new BigInteger(s[1]));
                    if(i!=0){
                        pids.append(",");
                    }
                    pids.append(batchProofRequests.get(i).getPid());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            createBPCCRequest.setPid(pids.toString());
            BPResponse bp = getBulletProof(value+"", request.getRange(), open+"");
            createBPCCRequest.setCommit1(bp.getCommit1());
            createBPCCRequest.setCommit2(bp.getCommit2());
            createBPCCRequest.setRange(request.getRange());
            String proof = JsonProviderHolder.JACKSON.toJsonString(bp.getProof());
            createBPCCRequest.setProof(proof);
//                createBPCCRequest.setProofFileName(fileName);
//            createBPCCRequest.setBulletProof(bulletProof);
            CCUtils.sign(createBPCCRequest, priKey);
            //TODO：把proof存在文件中，只把文件名和commit上链？

            response =  chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/zk/create", createBPCCRequest);
            if (response.isFailed()) {
                log.info("invoke create zk error: {}", response.getMessage());
                throw new BaseException("invoke create zk error");
            }
        } catch (IOException e) {
            log.info("get priKey", e);
            throw new BaseException(e.getMessage());
        }
        return response;
    }

    private CommitResponse getCommit(String value) {
//        CCUtils.saveValue(valuePath, username, fileName, value);
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.BP, "/commit", new ArrayList<>(Arrays.asList(value)));
        CommitResponse comm = JsonProviderHolder.JACKSON.parse(response.getMessage(), CommitResponse.class);
        return comm;
    }

    private BPResponse getBulletProof(String value, String range, String open) {
//        CreateBulletProofCCRequest ccRequest = new CreateBulletProofCCRequest(value,range);
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.BP, "/create", new ArrayList<>(Arrays.asList(value,range,open)));
        if (response.getStatus() == ChaincodeResponse.Status.FAIL) {
            log.warn("query chaincode error: {}", response.getMessage());
            return null;
        }
        BPResponse bp = JsonProviderHolder.JACKSON.parse(response.getMessage(), BPResponse.class);
//        CCUtils.saveProof(proofPath, username, fileName, bp.getProof());
//        return bp.getCommit2();
        return bp;
    }

    private BPResponse getBatchBulletProof(VerifyBatchBPCCRequest createBatchBPCCRequest) {
//        CreateBulletProofCCRequest ccRequest = new CreateBulletProofCCRequest(value,range);
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.BP, "/createBatch", createBatchBPCCRequest);
        if (response.getStatus() == ChaincodeResponse.Status.FAIL) {
            log.warn("query chaincode error: {}", response.getMessage());
            return null;
        }
        BPResponse bp = JsonProviderHolder.JACKSON.parse(response.getMessage(), BPResponse.class);
//        CCUtils.saveProof(proofPath, username, fileName, bp.getProof());
//        return bp.getCommit2();
        return bp;
    }

}
