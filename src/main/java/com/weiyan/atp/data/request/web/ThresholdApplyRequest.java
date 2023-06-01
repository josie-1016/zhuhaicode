package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ThresholdApplyRequest {

    @NotEmpty
    private String orgName;

    @NotEmpty
    private  String fileName;

    @NotEmpty
    private  String userName;
}
