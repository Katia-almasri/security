/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.Signature;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author Muhammad
 */
public class RegisterContact extends Thread{
    Socket cs=null;
    public RegisterContact(Socket cs) {
        this.cs=cs;
    }
   
    public void run(){
        try{
            ObjectOutputStream oos=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(cs.getInputStream());
            //read the phone
            String Clientphone=(String)ois.readObject();
            Socket css=new Socket("localhost",1234);
            ObjectOutputStream os=new ObjectOutputStream(css.getOutputStream());
            ObjectInputStream is=new ObjectInputStream(css.getInputStream());
            os.writeObject(Clientphone);
            os.flush();
            String res=(String)is.readObject();
            if(res.equals("no cert")){
                System.out.println("no cert was received");
                os.close();
                is.close();
                css.close();
                res="you are untrusted";
                oos.writeObject(res);
                oos.flush();
                System.out.println("untrusted sent");
                oos.close();
                ois.close();
                cs.close();
            }else{
                X509CertImpl crt=(X509CertImpl)is.readObject();
                System.out.println("cert was received");
                os.close();
                is.close();
                css.close();
                res="you are trusted";
                oos.writeObject(res);
                oos.flush();
                System.out.println("trusted sent");
                byte [] signature=(byte [])ois.readObject();
                System.out.println("sig received");
                //decode the sig using RSA algorithm
                Signature sig=Signature.getInstance("MD5WithRSA");
                sig.initVerify(crt.getPublicKey());
                if(sig.verify(signature)==false){
                    res="unreal connection";
                    oos.writeObject(res);
                    oos.flush();
                    System.out.println("unreal was sent");
                    oos.close();
                    ois.close();
                    cs.close();
                }else{
                    res="real connection";
                    oos.writeObject(res);
                    oos.flush();
                    System.out.println("real was sent");
                    byte [] skey=(byte[])ois.readObject();
                    System.out.println("encrypted key was received");
                    File fll=new File("private.ky");
                    ObjectInputStream oiis=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fll)));
                    PrivateKey pr=(PrivateKey)oiis.readObject();
                    oiis.close();
                    Cipher c=Cipher.getInstance("RSA");
                    c.init(Cipher.DECRYPT_MODE, pr);
                    byte [] ssky=c.doFinal(skey);
                    byte[] decoded = Base64.getDecoder().decode(ssky);
                    SecretKey seckey = new SecretKeySpec(decoded, 0, decoded.length, "AES");  
                    byte [] EncryptedContactPhone =(byte[])ois.readObject();
                    System.out.println("encrypted contact phone was received");
	            c=Cipher.getInstance("AES");
                    c.init(Cipher.DECRYPT_MODE,seckey);
                    byte [ ]dec_res = c.doFinal(EncryptedContactPhone);
                    String ContactPhone=new String(dec_res);
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/chat?zeroDateTimeBehavior=convertToNull","root","");
                    Statement stmt=conn.createStatement();
                    stmt=conn.createStatement();
                    int result=stmt.executeUpdate("insert into contacs(user_phone,contact_phone) values ('"+Clientphone+"','"+ContactPhone+"')");
                    
                    if(result==0){
                        res="insertion failed!!";
                   
                    }else{
                        res="insertion done!!";
                    }
                    conn.close(); 
                    System.out.println(res);
                    c=Cipher.getInstance("AES");
                    c.init(Cipher.ENCRYPT_MODE, seckey);
                    byte [] enc_res=c.doFinal(res.getBytes());
                    oos.writeObject(enc_res);
                    oos.flush();
                    ois.close();
                    oos.close();
                    cs.close();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
   }
}
