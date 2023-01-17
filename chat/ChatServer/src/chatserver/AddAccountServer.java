/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Muhammad
 */

//	in the server
public class AddAccountServer extends Thread{
    ServerSocket ss;
     Socket cs;
    public void run(){
        try{
            ss=new ServerSocket(2222);
            while(true){
                cs=ss.accept();
                new AddAccount(cs).start();
            }
           
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
