/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certifiedauthority;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Muhammad
 */
public class GetServer extends Thread{
    
    ServerSocket ss;
    Socket cs;
    
    public void run(){
                
        try{
            ss=new ServerSocket(1234);
            while(true){
                cs=ss.accept();
                new GetHandler(cs).start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
