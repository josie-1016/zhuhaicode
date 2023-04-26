package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class QueryBPCCRequest {
    private String uid;
    private String pid;
    private String tag;
    @Builder.Default
    private Integer pageSize = 10;
    @Builder.Default
    private String bookmark = "";
}
