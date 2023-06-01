package com.weiyan.atp.data.bean;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RingKey {
    private BigInteger modulus;
    private BigInteger pub;
}
