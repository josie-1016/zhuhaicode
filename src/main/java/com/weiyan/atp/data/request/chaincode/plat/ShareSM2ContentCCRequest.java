package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class ShareSM2ContentCCRequest extends BaseCCRequest {
    /**
     * 加密后的cipher
     */
    private String content;

    private String fileName;

    private String toName;


    @Override
    public String toString() {
        return "ShareContentCCRequest{" +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", toName='" + toName + '\'' +
                '}';
    }
}
