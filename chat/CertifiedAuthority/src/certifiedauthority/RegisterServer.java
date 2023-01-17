/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certifiedauthority;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RegisterServer extends Thread{
     ServerSocket ss;
     Socket cs;
    public void run(){
        try{
            File key = new File("priv.ky");
            ObjectInputStream oois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(key)));
            PrivateKey privky=(PrivateKey)oois.readObject();
            oois.close();
            ss=new ServerSocket(4321);
            while(true){
                cs=ss.accept();
                new RegisterHandler(cs,privky).start();
            }
           
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
