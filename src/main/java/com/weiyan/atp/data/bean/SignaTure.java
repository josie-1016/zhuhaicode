package com.weiyan.atp.data.bean;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SignaTure {
    private BigInteger r;
    private BigInteger s;
}
