package com.weiyan.atp.data.request.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.Commit;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class VerifyBulletProofRequest {
        private String pid;
        private String range;
        private String userName;
        private String commit1;
        private String commit2;
        private String proof;
        private String proofpre;
}
