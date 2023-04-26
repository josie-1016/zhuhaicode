package com.weiyan.atp.data.response.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.PlatContent;
import com.weiyan.atp.data.bean.ProofContent;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulletProofResponse {
    private List<ProofContent> bulletProofs;

//    private String username;
    private String bookmark;

    private int pageSize;
    private int count;
}
