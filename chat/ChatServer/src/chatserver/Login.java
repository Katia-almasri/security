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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author Muhammad
 */
public class Login extends Thread{
    Socket cs=null;
    List<User> loggedInUsers=new ArrayList<User>();
    public Login(Socket cs,List<User> loggedInUsers) {
        this.cs=cs;
        this.loggedInUsers=loggedInUsers;
    }
    public void run(){
        try{
            ObjectOutputStream oos=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(cs.getInputStream());
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
                    byte [] EncryptedPassword =(byte[])ois.readObject();
                    System.out.println("encrypted password was received");
	            c=Cipher.getInstance("AES");
                    c.init(Cipher.DECRYPT_MODE,seckey);
                    byte [ ]dec_res = c.doFinal(EncryptedPassword);
                    String Password=new String(dec_res);
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/chat?zeroDateTimeBehavior=convertToNull","root","");
                    Statement stmt=conn.createStatement();
                    ResultSet rs=stmt.executeQuery("select * from users where phone='"+Clientphone+"' and password='"+Password+"'");
                    String result=" ";
                    while(rs.next()){
                      result+=rs.getString("name");
                    }
                    conn.close();
                    System.out.println(res);
                    c=Cipher.getInstance("AES");
                    c.init(Cipher.ENCRYPT_MODE, seckey);
                    byte [] enc_res=c.doFinal(result.getBytes());
                    oos.writeObject(enc_res);
                    oos.flush();
                    User user=new User();
                    user.phone=Clientphone;
                    user.ip=cs.getInetAddress().toString();
                    loggedInUsers.add(user);
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
