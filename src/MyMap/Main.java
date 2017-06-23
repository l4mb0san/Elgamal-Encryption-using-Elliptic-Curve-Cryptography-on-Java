/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyMap;

import java.math.BigInteger;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author lehuu
 */
public class Main {
    
    public static void main(String[] args) {
        SupportFunctions obj = new SupportFunctions();
        
        BigInteger p = new BigInteger("2011", 10);
        BigInteger a = new BigInteger("9", 10);
        BigInteger b = new BigInteger("7", 10);
        if (!obj.isprime(p)) {
            System.err.println("p = " + p + " is not a prime number");
            return;
        }
        EllipticCurve E = new EllipticCurve(a,b,p);
        if (!E.belongsField()) {
            System.err.println("a = " + a
                    + "\nor\nb = " + b
                    + "\nnot belong to p = " + p);
            return;
        }
        
        //select a point in the finite field p (original point)
        Point P = new Point(new BigInteger("756", 10), new BigInteger("1012", 10));
        if (!E.PointbelongsField(P)) {
            System.err.println("P(x,y) not belong to Field");
            return;
        }
        BigInteger rd;
        Point Q;
        do {
            //chooses a secret  integer rd
            rd = new BigInteger(256, new Random());
            //Q = rd.P
            Q = P.kPoint(E, rd);
        } while (Q.isPOSITIVE_INFINITY());
        
//        String plaintext = StringUtils.repeat("A", 1000); /*<--- 100bytes using to test Time Enc/Decr */
        String plaintext = "Tai Le Huu, Hang Bui Thi Thanh"
                + "\nStudent of The Faculty of Computer Networks & Communications"
                + "\nUniversity of Information Technology,"
                + "\nVietnam National University of Hochiminh City - VNUHCM";
        System.out.println("Plaintext (ASCII): " + plaintext);
        System.out.println("Size: " + plaintext.length());
        Protocols protocol = new Protocols();
        
        System.out.println("\n----- Encrypt -----");
        String[] CipherText = protocol.Encrypt(E, P, Q, plaintext);
        System.out.println();
        for(String s : CipherText) {
            System.out.println("CipherText (Hex): " + new BigInteger(s, 2).toString(16));
        }
        
        System.out.println("\n----- Decrypt -----");
        String ClearText = protocol.Decrypt(E, rd, CipherText);
        System.out.println("\n" + ClearText);
        
        System.out.println("\n***End***");
    }
}
