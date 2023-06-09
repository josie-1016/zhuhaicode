package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
//用户内组织查询文件的结构
public class QueryOrgThresholdFileCCRequest extends BaseCCRequest {
    private String orgId;
    private String fileName;

    private String fromUid;
}
