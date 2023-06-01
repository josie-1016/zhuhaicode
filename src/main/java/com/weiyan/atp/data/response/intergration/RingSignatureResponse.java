package com.weiyan.atp.data.response.intergration;

import com.weiyan.atp.data.bean.RingKey;
import com.weiyan.atp.utils.RSAKeyPair;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Eric
 * @date 2021/11/8 16:02
 */
@Data
public class RingSignatureResponse {
    private String sig;
    private RingKey[] rsa;

}

