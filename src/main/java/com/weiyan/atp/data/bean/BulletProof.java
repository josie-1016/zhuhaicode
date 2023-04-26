package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.response.chaincode.plat.ContentResponse;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class BulletProof {
//    private String range;
//    @JsonProperty("Commit1")
//    private Commit commit1;
//    @JsonProperty("Commit2")
//    private Commit commit2;
//    @JsonProperty("Proof")
//    private Proof rangeProof;

//    @Data
//    public static class Proof {
        @JsonProperty("A")
        private Commit.ECPoint a;
        @JsonProperty("S")
        private Commit.ECPoint s;
        @JsonProperty("T1")
        private Commit.ECPoint t1;
        @JsonProperty("T2")
        private Commit.ECPoint t2;
        @JsonProperty("Tau")
        private String tau;
        @JsonProperty("Th")
        private String th;
        @JsonProperty("Mu")
        private String mu;
        @JsonProperty("IPP")
        private InnerProdArg ipp;
        @JsonProperty("Cy")
        private String cy;
        @JsonProperty("Cz")
        private String cz;
        @JsonProperty("Cx")
        private String cx;
//    }

    @Data
    public static class InnerProdArg{
        @JsonProperty("L")
        private List<Commit.ECPoint> l;
        @JsonProperty("R")
        private List<Commit.ECPoint> r;
        @JsonProperty("A")
        private String a;
        @JsonProperty("B")
        private String b;
        @JsonProperty("Challenges")
        private List<String> challenges;
    }
}
