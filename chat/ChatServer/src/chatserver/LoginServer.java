/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Muhammad
 */
public class LoginServer extends Thread{
    List<User> loggedInUsers=new ArrayList<User>();
    Socket cs=null;
    ServerSocket ss=null;

    public LoginServer(List<User> loggedInUsers) {
        this.loggedInUsers=loggedInUsers;
    }
    
    public void run(){
        try{
            ss=new ServerSocket(3333);
            while(true){
                cs=ss.accept();
                new Login(cs,loggedInUsers).start();
            }
           
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
