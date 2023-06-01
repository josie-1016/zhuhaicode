package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ThresholdResponse {
//    private String cipherHash;
    private String cipher;

    private String orgName;
    private String filename;

    private String timeStamp;
}
