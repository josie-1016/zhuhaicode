package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weiyan.atp.data.response.chaincode.plat.SM2ContentResponse;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatSM2Content {
    private String fileName;

    private String toName;

    private String cipher;

    //分享者
    private String sharedUser;

    private String timeStamp;

    public PlatSM2Content(SM2ContentResponse response) {
        System.out.println("content response");
        System.out.println(response.toString());
        this.toName = response.getToName();
        this.fileName = response.getFileName();
        this.sharedUser = response.getUid();
        this.timeStamp = response.getTimestamp();
        try {
            this.cipher= FileUtils.readFileToString(
                    new File("atp/SM2data/enc/" + response.getUid() + "/" + response.getFileName()),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(cipher!=null){
            // 密文哈希
            this.cipher = SecurityUtils.md5(this.cipher);
        }

    }
}
