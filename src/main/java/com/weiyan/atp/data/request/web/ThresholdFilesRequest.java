package com.weiyan.atp.data.request.web;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class ThresholdFilesRequest {
    @NotEmpty
    private String orgName;

    private String plainContent;

    @NotEmpty
    private String fileName;

    MultipartFile file;



}
