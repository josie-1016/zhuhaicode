package com.weiyan.atp.data.request.chaincode.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.data.bean.Commit;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class VerifyBatchBPCCRequest {
    @JsonProperty("Range")
    private String range;
    @JsonProperty("Commits")
    private List<Commit> commits;
    @JsonProperty("Opens")
    private List<String> opens;
    @JsonProperty("Values")
    private List<String> values;

}
