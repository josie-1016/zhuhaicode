package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weiyan.atp.data.response.chaincode.plat.ContentResponse;

import com.weiyan.atp.data.response.chaincode.plat.ProofResponse;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProofContent {
//    private String proofFileName;
    private String proof;
    private String proofpre;
    private List<String> tags;
    private String uid;
    private String pid;
    private String range;
//    private Commit commit1;
//    private Commit commit2;
    private String commit1;
    private String commit2;
    private String open;
    private String timeStamp;
//    private BulletProof bulletProof;

    public ProofContent(ProofResponse response) {
        this.tags = response.getTags();
        this.proof = response.getProof();
        this.proofpre = response.getProofpre();
        this.uid = response.getUid();
        this.pid = response.getPid();
        this.range = response.getRange();
        this.commit1 = JsonProviderHolder.JACKSON.toJsonString(response.getCommit1());
        this.commit2 = JsonProviderHolder.JACKSON.toJsonString(response.getCommit2());
        this.open = response.getOpen();
        this.timeStamp = response.getTimestamp();
//        if(!Objects.equals(response.getProofFileName(), "")) {
//            try {
//                String b = FileUtils.readFileToString(
//                        new File("atp/proof/" + response.getUid() + "/" + response.getProofFileName()),
//                        StandardCharsets.UTF_8);
//                this.bulletProof = JsonProviderHolder.JACKSON.parse(b, BulletProof.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
