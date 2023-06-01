package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.constant.OrgApplyTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
public class QueryOrgCCRequest {
    private String orgId;
}
