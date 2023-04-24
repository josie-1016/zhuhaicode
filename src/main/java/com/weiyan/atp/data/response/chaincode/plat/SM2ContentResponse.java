package com.weiyan.atp.data.response.chaincode.plat;

import lombok.Data;

import java.util.List;

@Data
public class SM2ContentResponse {
    //private String content;
    private String fileName;
    private String uid;
    private String timestamp;
    private String toName;

    @Override
    public String toString() {
        return "ContentResponse{" +
                "fileName='" + fileName + '\'' +
                ", uid='" + uid + '\'' +
                ", timeStamp='" + timestamp + '\'' +
                ", toName='" + toName + '\'' +
                '}';
    }
}
