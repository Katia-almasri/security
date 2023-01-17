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
public class AddAccount extends Thread{
    Socket cs=null;

    public AddAccount(Socket cs) {
        this.cs=cs;
    }
    public void run(){
        try{
        	//register new client
            ObjectOutputStream oos=new ObjectOutputStream(cs.getOutputStream()); //read from the file
            ObjectInputStream ois=new ObjectInputStream(cs.getInputStream()); //read from the file storing the input from interface
            String Clientphone=(String)ois.readObject();
            Socket css=new Socket("localhost",1234); //socket = ip, port
            ObjectOutputStream os=new ObjectOutputStream(css.getOutputStream()); //write then phone nu,ber into o file
            ObjectInputStream is=new ObjectInputStream(css.getInputStream());
            os.writeObject(Clientphone); //read phone number from the input interface and store it in the file
            os.flush();
            String res=(String)is.readObject(); // readed from somewhere
            //the client has to have cert
            if(res.equals("no cert")){
                System.out.println("no cert was received");
                os.close();
                is.close();
                css.close();  //close the socket
                res="you are untrusted";
                oos.writeObject(res);
                oos.flush();
                System.out.println("untrusted sent");
                oos.close();
                ois.close();
                cs.close();
            }else{
                X509CertImpl crt=(X509CertImpl)is.readObject(); //read the cert
                System.out.println("cert was received");
                os.close();
                is.close();
                css.close();// close the socket
                res="you are trusted";
                oos.writeObject(res);
                oos.flush();
                System.out.println("trusted sent");
                byte [] signature=(byte [])ois.readObject(); //for sypher
                System.out.println("sig received");
                Signature sig=Signature.getInstance("MD5WithRSA"); //used to ds md2...
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
                	// in the server side
                    res="real connection";
                    oos.writeObject(res);
                    oos.flush();
                    System.out.println("real was sent");
                    //its a temp key to make the sym key (in hyprid sypher)
                    //session key generated in the client and wrote into the file
                    byte [] skey=(byte[])ois.readObject(); //read from the file in each session will change
                    System.out.println("encrypted key was received");
                    File fll=new File("private.ky");//readed from the file
                    ObjectInputStream oiis=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fll))); //to read tue pkey from the file
                    PrivateKey pr=(PrivateKey)oiis.readObject();
                    oiis.close();
                    Cipher c=Cipher.getInstance("RSA"); //asym
                    c.init(Cipher.DECRYPT_MODE, pr); // initial values to decrypt the private key
                    byte [] ssky=c.doFinal(skey); //decrypt the session key
                    byte[] decoded = Base64.getDecoder().decode(ssky);
                    SecretKey seckey = new SecretKeySpec(decoded, 0, decoded.length, "AES");  //sym sypher AES sym sypher algo
                    byte [] EncryptedName =(byte[])ois.readObject(); //it should be decrypted
                    byte [] EncryptedPassword =(byte[])ois.readObject();
                    System.out.println("encrypted name and password were received");
	            c=Cipher.getInstance("AES");
                    c.init(Cipher.DECRYPT_MODE,seckey);
                    byte [ ]dec_res = c.doFinal(EncryptedName);
                    String Name=new String(dec_res);
                    dec_res = c.doFinal(EncryptedPassword);
                    String Password=new String(dec_res);
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/chat?zeroDateTimeBehavior=convertToNull","root","");
                    Statement stmt=conn.createStatement();
                    stmt=conn.createStatement();
                    int result=stmt.executeUpdate("insert into users(phone,name,password) values ('"+Clientphone+"','"+Name+"','"+Password+"')");
                    conn.close();
                    if(result==0){
                        res="insertion failed!!";
                   
                    }else{
                        res="insertion done!!";
                    }
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
