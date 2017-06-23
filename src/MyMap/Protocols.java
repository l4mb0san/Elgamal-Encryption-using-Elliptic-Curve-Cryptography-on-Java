/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyMap;

import java.math.BigInteger;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.Calendar;

/**
 *
 * @author lehuu
 */

/**
 * Encryption and Decryption Protocol is based on article 
 * "Elgamal Encryption using Elliptic Curve Cryptography"
 *          Rosy Sunuwar, Suraj Ketan Samal
 *      CSCE 877 - Cryptography and Computer Security
 *          University of Nebraska- Lincoln 
 *              December 9, 2015 
 */

public class Protocols {
    SupportFunctions obj = new SupportFunctions();
    BigInteger sizeblock = new BigInteger("20");
    BigInteger maxblockbits = sizeblock.multiply(new BigInteger("8"));
    
    /*Diffie-Hellman protocol*/
    /*inputexcelFilePath is generating sets of points from a point path*/
    /*n is the maximum number of point in inputexcelFilePath*/
    public Point DiffieHellman(EllipticCurve E, BigInteger n, String inputexcelFilePath ) {
        System.out.println("1. Alice pick up key pair:");
        SupportFunctions.KeyPair Akp = obj.keypairGeneration(n, inputexcelFilePath);
        System.out.println("Private Key: " + Akp.PrivateKey);
        System.out.println("Public Key: " + "(" + Akp.PublicKey.x + "," + Akp.PublicKey.y + ")");
        
        System.out.println("\n2. Bob pick up key pair:");
        SupportFunctions.KeyPair Bkp = obj.keypairGeneration(n, inputexcelFilePath);
        System.out.println("Private Key: " + Bkp.PrivateKey);
        System.out.println("Public Key: " + "(" + Bkp.PublicKey.x + "," + Bkp.PublicKey.y + ")");
        
        BigInteger An = E.generateorderGroup(Bkp.PublicKey, "Alice's Secret Key.xls");
        Point Asecretkey = new Point();
        Asecretkey.kPoint(Akp.PrivateKey, An, "Alice's Secret Key.xls");
        System.out.print("\n3. Alice compute Secret Key: (" + Asecretkey.x + "," + Asecretkey.y + ")");
        
        BigInteger Bn = E.generateorderGroup(Akp.PublicKey, "Bob's Secret Key.xls");
        Point Bsecretkey = new Point();
        Bsecretkey.kPoint(Bkp.PrivateKey, Bn, "Bob's Secret Key.xls");
        System.out.print("\n4. Bob compute Secret Key: (" + Bsecretkey.x + "," + Bsecretkey.y + ")");
        
        if (!Asecretkey.compareTo(Bsecretkey)) {
            return new Point();
        }
        return Bsecretkey;
    }
    
    
    /*Elgamal Encryption using Elliptic Curve Cryptography*/
    /*The calculation is stored in binary form, but hexadecimal format screen output*/
    public String[] Encrypt (EllipticCurve E, Point alphaPoint, Point betaPoint, String plaintext) {
    //  String[] m = plaintext.split("(?<=\\G.{" + sizeblock + "})"); //split into blocks of 20 bytes but not usable if plaintext have "\n"
        long begin = Calendar.getInstance().getTimeInMillis();
        String[] m = obj.splitEqually(plaintext, sizeblock.intValue()); //split into blocks of 20 bytes
        int maxblocks = m.length; //the number of blocks
        BigInteger ki;
        Point ri, kiBeta;
        do {
            ki = new BigInteger(256, new Random()); //choosing a random ki
            ri = alphaPoint.kPoint(E, ki); //ri = (ki o α)
            kiBeta = betaPoint.kPoint(E, ki); //(ki o β)
        } while (ri.isPOSITIVE_INFINITY() || kiBeta.isPOSITIVE_INFINITY());
        BigInteger ht = new BigInteger(DigestUtils.sha1Hex(obj.append(kiBeta.x.toString(), kiBeta.y.toString())),16); //ht = H (ki o β)
        String[] htmi = new String[maxblocks + 1];
        htmi[0] = obj.appendBinary(E, ri, ""); //store a point by binary format (not 20 bytes)
        for (int i = 1; i < maxblocks + 1; i++) {
            BigInteger mi = obj.stringToBigInt(m[i - 1], -1);
            //System.out.println("Plaintext (Hex): " + mi.toString(16));
            htmi[i] = ht.xor(mi).toString(2); //calculate htmi = ht ⊕ mi (⊕ is bitwise XOR)
            htmi[i] = obj.paddingBin(htmi[i], "0", maxblockbits); //padded "0" on the left until maxblockbits
        }
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Encryption Time: " + (end - begin) + "ms");
        return htmi; //array htmi is array binary strings (cipher text)
    }
    
    /*Elgamal Decryption using Elliptic Curve Cryptography*/
    /*The calculation is stored in binary form*/
    public String Decrypt (EllipticCurve E, BigInteger secretNumber, String[] ciphertext) {
        long begin = Calendar.getInstance().getTimeInMillis();
        int maxbits = E.p.toString(2).length(); //the maximum number of bits to represent a point in the finite field p
        int cipherblocks = ciphertext.length; //the number of blocks
        BigInteger x = obj.stringToBigInt(ciphertext[0].substring(0, maxbits), 2); //get coordinates X from the first block
        BigInteger y = obj.stringToBigInt(ciphertext[0].substring(maxbits, ciphertext[0].length()), 2); //get coordinates Y from the first block
        Point ri = new Point(x, y); //point α
        ri = ri.kPoint(E, secretNumber); //point β
        BigInteger ari = new BigInteger(DigestUtils.sha1Hex(obj.append(ri.x.toString(), ri.y.toString())),16); //H(a o ri ) = H(ki o β)
        String m = "";
        for (int i = 1; i < cipherblocks; i++) {
            BigInteger htmi = obj.stringToBigInt(ciphertext[i], 2);
            String mi = ari.xor(htmi).toString(2); //H(a o ri ) ⊕ htmi 
            m += obj.binToASCII(mi); //convert binary string into ASCII string
        }
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Decryption Time: " + (end - begin) + "ms");
        return m; // m is ASCII string (clear text)
    }
}
