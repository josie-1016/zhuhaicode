package com.weiyan.atp.utils;

import com.weiyan.atp.data.bean.PrivateKey;
import com.weiyan.atp.data.bean.PublicKey;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Base64;

/**
 * SM2密钥对Bean
 * @author Potato
 *
 */
public class SM2KeyPair {
	private final ECPoint publicKey;
	private final BigInteger privateKey;


	SM2KeyPair(ECPoint publicKey, BigInteger privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public ECPoint getPublicKey() {
		return publicKey;
	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}
}
