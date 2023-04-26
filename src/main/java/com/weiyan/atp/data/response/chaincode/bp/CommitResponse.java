package com.weiyan.atp.data.response.chaincode.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.Commit;
import lombok.Data;

@Data
public class CommitResponse {
    @JsonProperty("Proof")
    private BulletProof proof;
    @JsonProperty("Commit1")
    private Commit commit1;
    @JsonProperty("Open")
    private String open;
}
