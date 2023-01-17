
package certifiedauthority;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Date;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class RegisterHandler extends Thread{
     Socket cs;
    PrivateKey privky;
    public RegisterHandler(Socket cs, PrivateKey privky){
        this.cs=cs;
        this.privky=privky;
    }
    public X509CertImpl createCertificate(PublicKey public_key,PrivateKey serverkey,String id)
        throws InvalidKeyException, NoSuchProviderException, SignatureException, CertificateException, NoSuchAlgorithmException, IOException
    {
                
        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + 2 * 86400000l); 
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name("CN="+id);
        X500Name server = new X500Name("CN=CertifiedAuthority"); 
        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        info.set(X509CertInfo.SUBJECT, server);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(public_key));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo)); 
         X509CertImpl cert = new X509CertImpl(info);
        
        cert.sign(serverkey, "MD5WithRSA");
         return cert; 
    }
    
    public void run(){
        try{
            ObjectOutputStream os=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream is=new ObjectInputStream(cs.getInputStream());
            String id=(String)is.readObject();
            PublicKey cspub=(PublicKey)is.readObject();
            X509CertImpl x509cert=createCertificate(cspub, privky, id);
            File certf=new File(id+".crt");
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(certf));
            oos.writeObject(x509cert);
            oos.close();
            File key = new File("pub.ky");
            ObjectInputStream oois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(key)));
            PublicKey pubky=(PublicKey)oois.readObject();
            oois.close();
            os.writeObject(pubky);
            os.flush();
            os.close();
            is.close();
            cs.close();
            
                    
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
