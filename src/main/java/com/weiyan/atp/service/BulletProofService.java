package com.weiyan.atp.service;

import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.request.web.CreateBatchBulletProofRequest;
import com.weiyan.atp.data.request.web.CreateBulletProofRequest;
import com.weiyan.atp.data.request.web.CreateCommitRequest;
import com.weiyan.atp.data.request.web.VerifyBulletProofRequest;
import com.weiyan.atp.data.response.web.BulletProofResponse;

import java.util.List;

public interface BulletProofService {
//    ChaincodeResponse commit(CreateCommitRequest request);

    ChaincodeResponse createBulletProof(CreateBulletProofRequest request);

    BulletProofResponse queryBulletProof(String userName, String pid, String tag, int pageSize, String bookmark);

    ChaincodeResponse verifyBulletProof(VerifyBulletProofRequest request);

    ChaincodeResponse createBatchBulletProof(CreateBatchBulletProofRequest request);
}
