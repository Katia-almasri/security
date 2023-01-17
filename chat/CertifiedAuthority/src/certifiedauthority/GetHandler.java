
package certifiedauthority;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import sun.security.x509.X509CertImpl;

public class GetHandler extends Thread{
    Socket cs;
    
    public  GetHandler(Socket cs){
        this.cs=cs;
    }
    
    public void run(){
        try{
            ObjectOutputStream os=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream is=new ObjectInputStream(cs.getInputStream());
            String id=(String)is.readObject();
            File certfile=new File(id+".crt");
            String exist="no cert";
            if(certfile.exists()){
                  exist="exist";
            }
            os.writeObject(exist); 
            os.flush();
            if(exist.equals("exist")){
                ObjectInputStream ois =new ObjectInputStream(new BufferedInputStream(new FileInputStream(certfile)));
                X509CertImpl crt=(X509CertImpl)ois.readObject();
                ois.close();
                os.writeObject(crt);
                os.flush();
            }
            os.close();
            is.close();
            cs.close();
                    
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
