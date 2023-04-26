package com.weiyan.atp.data.request.chaincode.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.Commit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class VerifyBulletProofCCRequest {
    @JsonProperty("Range")
    private String range;
    @JsonProperty("Commit1")
    private Commit commit1;
    @JsonProperty("Commit2")
    private Commit commit2;
    @JsonProperty("Proof")
    private BulletProof proof;
    @JsonProperty("ProofPre")
    private BulletProof proofpre;

    public VerifyBulletProofCCRequest(String range, Commit commit1, Commit commit2, BulletProof proof, BulletProof proofpre) {
        this.range = range;
        this.commit1 = commit1;
        this.commit2 =commit2;
        this.proof = proof;
        this.proofpre = proofpre;
    }
}