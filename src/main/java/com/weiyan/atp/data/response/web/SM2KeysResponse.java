package com.weiyan.atp.data.response.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SM2KeysResponse {
    private String priKey;
    private String pubKey;
}
