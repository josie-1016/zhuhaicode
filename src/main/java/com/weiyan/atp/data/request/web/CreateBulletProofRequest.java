package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateBulletProofRequest {
    private List<String> tags;

    @NotEmpty
    private String pid;

    private String value;

    private String range;

    private String open;

    @NotEmpty
    private String userName;

    private String userType;

    private String channel;
    private String timestamp;
}