/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.net.Socket;

/**
 *
 * @author Muhammad
 */
public class Chating extends Thread{
    Socket cs=null;

    public Chating(Socket cs) {
        this.cs=cs;
    }
    
    
}
