package com.weiyan.atp.data.request.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.Commit;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateBatchBulletProofRequest {
    private List<String> tags;
    private String batchProofRequestsStr;
//    private List<BatchProofRequest> batchProofRequests;
    private String range;
    @NotEmpty
    private String userName;
    private String userType;
    private String channel;

    @Data
    public static class BatchProofRequest{
        private String pid;
        private String commit1;
        private String timestamp;
    }
}