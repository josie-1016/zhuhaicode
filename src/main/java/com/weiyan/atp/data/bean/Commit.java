package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class Commit {
    @JsonProperty("Comm")
    private ECPoint comm;

    @Data
    public static class ECPoint {
        @JsonProperty("X")
        private String x;
        @JsonProperty("Y")
        private String y;
    }

//    @Override
//    public String toString() {
//        return "Commit{" + comm.x + '\'' +
//                "," + comm.y +
//                '}';
//    }
}
