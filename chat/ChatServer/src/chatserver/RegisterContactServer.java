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
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

/**
 *
 * @author Muhammad
 */
public class RegisterContactServer extends Thread{
    ServerSocket ss;
     Socket cs;
    public void run(){
        try{
            ss=new ServerSocket(1111);
            while(true){
                cs=ss.accept();
                new RegisterContact(cs).start();
            }
           
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
