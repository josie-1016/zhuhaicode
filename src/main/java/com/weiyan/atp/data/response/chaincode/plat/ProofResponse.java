package com.weiyan.atp.data.response.chaincode.plat;

import com.weiyan.atp.data.bean.Commit;
import lombok.Data;

import java.util.List;

@Data
public class ProofResponse {
//    private String proofFileName;
    private String proof;
    private String proofpre;
    private List<String> tags;
    private String uid;
    private String pid;
    private String range;
    private Commit commit1;
    private Commit commit2;
    private String open;
    private String timestamp1;
    private String timestamp2;
}
