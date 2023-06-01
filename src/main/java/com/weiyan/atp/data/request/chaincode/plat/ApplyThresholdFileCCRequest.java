package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class ApplyThresholdFileCCRequest extends BaseCCRequest {
    private String orgName;
    private String fileName;
    private String userName;
}
