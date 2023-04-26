package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateCommitRequest {
    @NotEmpty
    private String fileName;

    @NotEmpty
    private String number;

    @NotNull
    private List<String> tags;

//    @NotEmpty
//    private String userType;
//
//    @NotEmpty
//    private String channel;
}