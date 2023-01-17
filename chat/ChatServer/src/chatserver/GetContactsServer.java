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
public class GetContactsServer extends Thread{
        Socket cs=null;
    ServerSocket ss=null;
    public void run(){
        try{
            ss=new ServerSocket(5555);
            while(true){
                cs=ss.accept();
                new GetContacts(cs).start();
            }
           
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
