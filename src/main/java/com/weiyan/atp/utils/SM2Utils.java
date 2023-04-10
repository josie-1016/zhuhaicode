package com.weiyan.atp.utils;

import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PrivateKey;
import com.weiyan.atp.data.bean.PublicKey;
import com.weiyan.atp.data.bean.SignaTure;
import com.weiyan.atp.data.response.web.SM2KeysResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.math.ec.ECPoint;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 使用SM2实现交易签名
 */
@Slf4j
public class SM2Utils {

        private static SM2 sm02 = new SM2();

        private static String userPath="atp/user/";

        public static SM2KeysResponse getNewPublicKey(){
                try {
                        // 生成公私钥对
                        SM2KeyPair keyPair = sm02.generateKeyPair();
                        ECPoint publicKey = keyPair.getPublicKey();
                        BigInteger privateKey = keyPair.getPrivateKey();
                        // 公私钥对json转换
                        PublicKey publ=new PublicKey();
                        publ.setX(publicKey.getXCoord().toBigInteger());
                        publ.setY(publicKey.getYCoord().toBigInteger());
                        String pub=JsonProviderHolder.JACKSON.toJsonString(publ);
                        PrivateKey priv=new PrivateKey();
                        priv.setKey(privateKey);
                        String pri =JsonProviderHolder.JACKSON.toJsonString(priv);
                        return SM2KeysResponse.builder()
                                .priKey(pri)
                                .pubKey(pub)
                                .build();
                }catch(Exception e) {
                        log.warn("getNewPublicKey error", e);
                        return null;
                }
        };

        public static String getSign(@NotEmpty String priKey,String pubKey,String userName,String args){
          try{
                  // 用户公私钥转换
                  PrivateKey priv=JsonProviderHolder.JACKSON.parse(priKey,PrivateKey.class);
                  PublicKey publ=JsonProviderHolder.JACKSON.parse(pubKey,PublicKey.class);
                  ECPoint publicKey= sm02.getPoint(publ.getX(),publ.getY());
                  // 生成KeyPair
                  SM2KeyPair key=new SM2KeyPair(publicKey,priv.getKey());
                  // 签名
                  SM2.Signature signature=sm02.sign(args,userName,key);
                  SignaTure Sign=new SignaTure();
                  Sign.setR(signature.r);
                  Sign.setS(signature.s);
                  String sign=JsonProviderHolder.JACKSON.toJsonString(Sign);
                  return sign;
          }catch(Exception e) {
                  log.warn("getSign error", e);
                  return null;
          }
        };

        public static boolean getVerify(@NotEmpty String userName,String pub, String sign, String args){
                // 获得公钥
                PublicKey publ=JsonProviderHolder.JACKSON.parse(pub,PublicKey.class);
                ECPoint publicKey= sm02.getPoint(publ.getX(),publ.getY());
                // 获得签名
                SignaTure Sign=JsonProviderHolder.JACKSON.parse(sign,SignaTure.class);
                SM2.Signature signature=new SM2.Signature(Sign.getR(),Sign.getS());

                // 验签
                if (sm02.verify(args,signature,userName,publicKey)){
                        return true;
                }
                return false;
        };


}
