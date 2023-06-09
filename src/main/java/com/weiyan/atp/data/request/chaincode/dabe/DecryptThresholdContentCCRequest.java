package com.weiyan.atp.data.request.chaincode.dabe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class DecryptThresholdContentCCRequest {
    @JsonProperty("Cipher")
    private String cipher;
    @JsonProperty("ThresholdPriv")
    private String thresholdpriv;

    public DecryptThresholdContentCCRequest(String cipher ,String thresholdpriv){
        this.cipher = cipher;
        this.thresholdpriv = thresholdpriv;
    }
}
