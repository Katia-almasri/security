/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certifiedauthority;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author Muhammad
 */
public class CertifiedAuthority {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        ServerSocket ss;
        Socket cs;
        
        try{
            
            KeyPairGenerator kpg= KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp=kpg.genKeyPair();
            PrivateKey pr=kp.getPrivate();
            PublicKey pb=kp.getPublic();
            File pubkfl=new File("pub.ky");
            File privkfl=new File("priv.ky");
            ObjectOutputStream oiss1=new ObjectOutputStream(new FileOutputStream(pubkfl));
            ObjectOutputStream oiss2=new ObjectOutputStream(new FileOutputStream(privkfl));
            oiss1.writeObject(pb);
            oiss2.writeObject(pr);
            oiss1.close();
            oiss2.close();
            System.out.println("Keys were generated");
            new GetServer().start();
            new RegisterServer().start();
            System.out.println("servers started");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
