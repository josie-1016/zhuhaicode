package com.weiyan.atp.utils;
//package Main;

import com.weiyan.atp.data.bean.PrivateKey;
import com.weiyan.atp.data.bean.RingKey;
import com.weiyan.atp.data.bean.RingSignature;
import com.weiyan.atp.data.response.intergration.RingSignatureResponse;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.weiyan.atp.utils.Util.encrypt;

/**
 *
 * @author leijurv
 */
public class RingSignatures {
    
    
    public static byte[] hash(byte[] message, int size) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message);
        byte[] d = digest.digest();
        byte[] res = new byte[size / 8];//Extend this hash out to however long it needs to be
        int dlen = d.length;
        for (int i = 0; i < res.length / dlen; i++) {
            System.arraycopy(d, 0, res, i * dlen, dlen);
        }
        return res;
    }
    
    public static boolean verify(String siga, RingKey[] Rkeys, byte[] message, int bitlength) throws Exception {
        //RSAKeyPair[] keys=new RSAKeyPair[Rkeys.length];
        List<RSAKeyPair> Lkeys = new ArrayList<>();
        for(int i=0;i< Rkeys.length;i++){
            Lkeys.add(new RSAKeyPair(null,Rkeys[i].getPub(),Rkeys[i].getModulus()));
        }
        RSAKeyPair[] keys=Lkeys.toArray(new RSAKeyPair[Lkeys.size()]);

        RingSignature ringS=JsonProviderHolder.JACKSON.parse(siga,RingSignature.class);
        byte[][] sig= ringS.getResult();

        byte[] k = hash(message, 256);
        byte[] v = sig[sig.length - 1];
        BigInteger[] x = new BigInteger[sig.length - 1];
        BigInteger[] y = new BigInteger[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = new BigInteger(Util.leadingZero(sig[i]));
            y[i] = keys[i].encode(x[i], bitlength);
        }
        byte[] res = Util.runCKV(k, v, y);

        //System.out.println("vvvvvvvvvvvv"+ new BigInteger(v));
        //System.out.println("rrrvvvvvvvvv"+new BigInteger(res));

        return new BigInteger(res).equals(new BigInteger(v));
    }
    
    
    /**
     * 
     * @param Skeys 群内所有人的公私玥（包括一个私钥和剩余的公钥）
     * @param message 消息本身
     * @param b 
     * @param r
     * @return
     * @throws Exception
     */
    public static RingSignatureResponse genRing(SM2KeyPair[] Skeys, byte[] message, int b, Random r) throws Exception {

        RSAKeyPair[] keys = new RSAKeyPair[Skeys.length];
        Random rr = new Random(System.currentTimeMillis());

        int signer = 0;

        for(int i = 0; i < Skeys.length; i++){
            BigInteger x = Skeys[i].getPublicKey().getXCoord().toBigInteger();
            BigInteger y = Skeys[i].getPublicKey().getYCoord().toBigInteger();

            BigInteger defaultPub = new BigInteger("65537");
            x = x.add(y);
            x = x.remainder(defaultPub);
            //System.out.println(x);

            if(Skeys[i].getPrivateKey() == null){
                keys[i] = RSAKeyPair.generate(new BigInteger(b / 2 - 4, 8, rr), new BigInteger(b / 2, 8, rr), x);
                keys[i] = keys[i].withoutPriv();
            }
            else{
                signer = i;
                //new BigInteger(bitlength / 2 - 4, 8, rondom)
                BigInteger ppri = new BigInteger(b / 2 - 4, 8, new Random(Skeys[i].getPrivateKey().intValue()));
                keys[i] = RSAKeyPair.generate(ppri, new BigInteger(b / 2, 8, rr), x);
            }

            //System.out.println(i + "   RSARSARSARSAKey:" + keys[i].toString());

        }

        byte[] k = hash(message, 256);
        int s = -1;
        for (int i = 0; i < keys.length; i++) {
        	//找到群里唯一一个有私钥的
        	//Find the one that we have the private key to
            if (keys[i].hasPrivate()) {
                if (s != -1) {
                    throw new IllegalStateException("Too many private keyssss");
                }
                s = i;
            }
        }
        
        if (s == -1) {
            throw new IllegalStateException("Need at least 1 private key to create a ring signature");
        }
        
        byte[] v = new byte[b / 8];//Number of bytes = number of bits / 8
        r.nextBytes(v);//Maybe this should be more random?
        BigInteger[] x = new BigInteger[keys.length];
        BigInteger[] y = new BigInteger[keys.length];
        
        for (int i = 0; i < keys.length; i++) {
            if (i != s) {//Do this for everyone but me, mine is generated later
                x[i] = new BigInteger(b, r);//b为新生成大数的最大bit数
                y[i] = keys[i].encode(x[i], b);
            }
        }
        //k是message的哈希值
        byte[] CKV = Util.solveCKV(k, v, y, s);
        //System.out.println(CKV.length);
//System.out.println("--------------------------"+CKV[0]);
        y[s] = new BigInteger(Util.leadingZero(CKV));
        x[s] = keys[s].decode(y[s], b);
        //System.out.println("YS: " + y[s]);
        byte[] check = Util.runCKV(k, v, y);
        int d = new BigInteger(check).compareTo(new BigInteger(v));
        if (d != 0) {
            throw new IllegalStateException("Shrek");
        }
        byte[][] result = new byte[keys.length + 1][];
        for (int i = 0; i < keys.length; i++) {
            byte[] X = x[i].toByteArray();
            if (X.length == 129) {
                X = Util.trimLeading(X);
            }
            result[i] = X;
        }
        result[keys.length] = v;

        //return result;

        keys[signer] = keys[signer].withoutPriv();

        RingSignature re=new RingSignature();
        re.setResult(result);
        //return JsonProviderHolder.JACKSON.toJsonString(re);

        List<RingKey> Rkeys=new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            RingKey rk=new RingKey();
            rk.setModulus(keys[i].modulus);
            rk.setPub(keys[i].pub);
            Rkeys.add(rk);
        }
        RingKey[] rkeys=Rkeys.toArray(new RingKey[Rkeys.size()]);

        RingSignatureResponse res=new RingSignatureResponse();
        res.setSig(JsonProviderHolder.JACKSON.toJsonString(re));
        res.setRsa(rkeys);

        return res;

    }
    

    
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws Exception {

        /*
         System.out.println(System.getProperty("java.home"));
         RSAKeyPair dank = RSAKeyPair.generate(new BigInteger("61"), new BigInteger("53"), new BigInteger("17"));
         System.out.println(dank.encode(new BigInteger("65000000"), 100));
         System.out.println(dank.decode(new BigInteger("65001491"), 100));
         byte[] key = new BigInteger("5021").toByteArray(); // TODO
         byte[] input = new BigInteger("5021").toByteArray(); // TODO
         //byte[] output = encrypt(hash(key), input);
        //System.out.println(new BigInteger(decrypt(hash(key), encrypt(hash(key), input))));

         */
        int bitlength = 1024;
        Random rondom = new Random(1224);//new Random(5021);
        //Random rondom = new Random(5021);


        while (true) {

            int numKeys = 2;

            //int numKeys = rondom.nextInt(10) + 1;//群内成员个数
            System.out.println("---------Group num will be:"+numKeys);
            /*
            RSAKeyPair[] keys = new RSAKeyPair[numKeys];
            int s = rondom.nextInt(numKeys);
            byte[] message = new byte[8];//一个8字节的随机数组
            rondom.nextBytes(message);
            
            for (int i = 0; i < keys.length; i++) {//给每个群内成员都生成公私玥
                keys[i] = RSAKeyPair.generate(new BigInteger(bitlength / 2 - 4, 8, rondom), new BigInteger(bitlength / 2, 8, rondom));
                if (i != s) {
                	//只需要一个私钥   别的keys存公钥就行
                    keys[i] = keys[i].withoutPriv();//We only need one of the private keys
                }
                System.out.println("keypair_"+i+"__"+keys[i].toString()+"\n");
            }

             */

            SM2KeyPair[] keys = new SM2KeyPair[2];

            System.out.println("startstart");

            keys[0] = SM2Utils.GetKey("{\"key\":12540212391549021614846123452055096287557823355110824853161938982783578071127}", "{\"x\":27603994718234689321574809997801592740805412856277983229103449673945218579672,\"y\":60243566254172216355937470008709010126938858774312433423415407312180993873437}");
            keys[1] = SM2Utils.GetKey(null, "{\"x\":34746190019188031283895971937132551302309425156331183961583860197031110749853,\"y\":23420708797262614631847303855882799788545001164830490476021722203091736841514}\n");
            //System.out.println(keys[0].getPrivateKey());
            //System.out.println(keys[0].getPublicKey());
            //System.out.println(keys[0].getPublicKey().getAffineXCoord());

            //System.out.println("eeeeeeeee"+new BigInteger(bitlength / 2, 8, rondom));


            int s = rondom.nextInt(numKeys);
            byte[] message = new byte[8];//一个8字节的随机数组
            rondom.nextBytes(message);


            System.out.println("---------Creating and verifying");
            long time = System.currentTimeMillis();
            //byte[][] sig = genRing(keys, message, bitlength, rondom);

            BigInteger modu = new BigInteger("10697292596396326120538602740810739125420505301675107910363568922884828110776133296645990845709362000684671777207428475439997303868616852249627887259373553");
            BigInteger tmppri = new BigInteger("594809438805641452785364409745928659901876844395428886700201250875664777889192662607404392158354514236874278984234308189146835712280033434551280865888417");
            BigInteger prik = new BigInteger("8001174908255598584458657495680310450905260302251578652162429345532228857303746280060263132461787682197647049350082109824160887808857174422810035219897869");


            RingSignatureResponse sig = genRing(keys, message, bitlength, rondom);
            boolean b = verify(sig.getSig(), sig.getRsa(), message, bitlength);
            System.out.println(b + " numKeys:" + numKeys + " No." + s + " time:" + (System.currentTimeMillis() - time));
//            if (!b) {
//                return;
//            }
            System.out.println("result: "+b);
            return;
        }
    }//main ends


}