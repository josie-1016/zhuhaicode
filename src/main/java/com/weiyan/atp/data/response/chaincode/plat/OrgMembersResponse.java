package com.weiyan.atp.data.response.chaincode.plat;

import lombok.Data;

import java.util.List;

@Data
public class OrgMembersResponse {
    //private String content;
    private String orgId;
    private String[] uidSet;
    private String[] attrSet;
    private int t;
    private int n;
    private String opk;

    @Override
    public String toString() {
        return "ContentResponse{" +
                "orgId='" + orgId + '\'' +
                ", uidSet='" + uidSet +
                ", attrSet=" + attrSet +
                ", t='" + t + '\'' +
                ", n='" + n + '\'' +
                ", opk='" + opk + '\'' +
                '}';
    }
}
