package com.weiyan.atp.data.response.web;

import com.weiyan.atp.data.bean.PlatSM2Content;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class PlatSM2ContentsResponse {
    private List<PlatSM2Content> contents;

    private String bookmark;

    private int pageSize;
    private int count;
}
