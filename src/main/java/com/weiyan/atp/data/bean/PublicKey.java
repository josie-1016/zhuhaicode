package com.weiyan.atp.data.bean;

import lombok.Data;

import java.math.BigInteger;

@Data
public class PublicKey {
    private BigInteger x;
    private BigInteger y;
}

