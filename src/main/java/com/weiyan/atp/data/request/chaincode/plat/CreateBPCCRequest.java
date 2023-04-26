package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.data.bean.BulletProof;
import com.weiyan.atp.data.bean.Commit;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class CreateBPCCRequest extends BaseCCRequest{
//    private String proofFileName;
    private String proofpre;
    private String proof;
    private List<String> tags;
    private String pid;
    private String range;
//    private Commit commit1;
//    private Commit commit2;
    private Commit commit1;
    private Commit commit2;
    private String open;
//    private BulletProof bulletProof;

}
