package com.weiyan.atp.data.request.chaincode.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.Commit;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class VerifyCommitsBPCCRequest {
    @JsonProperty("Commits")
    private List<Commit> commits;
    @JsonProperty("Commit1")
    private Commit commit1;
    @JsonProperty("Range")
    private String range;
    @JsonProperty("Commit2")
    private Commit commit2;
    @JsonProperty("Proof")
    private BulletProof proof;
}
