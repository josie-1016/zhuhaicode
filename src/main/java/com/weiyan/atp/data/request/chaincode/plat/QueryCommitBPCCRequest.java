package com.weiyan.atp.data.request.chaincode.plat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCommitBPCCRequest {
    private String uid;
    private List<String> pids;
}
