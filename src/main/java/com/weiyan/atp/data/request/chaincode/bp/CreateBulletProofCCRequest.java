package com.weiyan.atp.data.request.chaincode.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CreateBulletProofCCRequest {
    @JsonProperty("Value")
    private String value;
    @JsonProperty("Range")
    private String range;

    public CreateBulletProofCCRequest(String value, String range) {
        this.value = value;
        this.range = range;
    }
}

