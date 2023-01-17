/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Muhammad
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp=kpg.generateKeyPair();
            PublicKey pub=kp.getPublic();
            PrivateKey priv=kp.getPrivate();
            File pubk= new File("public.ky");
            File privk=new File("private.ky");
            ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pubk)));
            oos.writeObject(pub);
            oos.close();
            oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(privk)));
            oos.writeObject(priv);
            oos.close();
            System.out.println("Keys were generated");
            Socket cs=new Socket("localhost",4321);
            ObjectOutputStream os=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream is=new ObjectInputStream(cs.getInputStream());
            String id="ChatServer";
            os.writeObject(id);
            os.flush();
            os.writeObject(pub);
            os.flush();
            PublicKey capub =(PublicKey)is.readObject();
            is.close();
            os.close();
            cs.close();
            System.out.println("certificate was registered at CA");
            List<User> loggedInUsers=new ArrayList<User>();
            new RegisterContactServer().start();
            new AddAccountServer().start();
            new LoginServer(loggedInUsers).start();
            new GetContactsServer().start();
            System.out.println("servers started");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
