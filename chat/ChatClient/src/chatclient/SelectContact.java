/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author Muhammad
 */
public class SelectContact extends javax.swing.JFrame {
    public String phone="";
    String contact="";
    /**
     * Creates new form SelectContact
     */
    public SelectContact() {
        initComponents();
    }
    
    public void setPhone(String Phone){
        this.phone=Phone;
        getContacts();
    }
    
    
    public void getContacts(){
        try{
            Socket cs=new Socket("localhost",1234);
            ObjectOutputStream oos=new ObjectOutputStream(cs.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(cs.getInputStream());
            oos.writeObject("ChatServer");
            oos.flush();
            String res=(String)ois.readObject();
            if(res.equals("no cert")){
                oos.close();
                ois.close();
                cs.close();
                jLabel1.setText("untrusted chat server");
            }else{
                X509CertImpl cert=(X509CertImpl)ois.readObject();
                oos.close();
                ois.close();
                cs.close();
                cs=new Socket("localhost",5555);
                oos=new ObjectOutputStream(cs.getOutputStream());
                ois=new ObjectInputStream(cs.getInputStream());
                oos.writeObject(phone);
                oos.flush();
                res=(String)ois.readObject();
            
                if(res.equals("you are untrusted")){
                    System.out.println("if1 entered");
                    oos.close();
                    ois.close();
                    cs.close();
                    jLabel1.setText("create certificate before plz");
                }else{
                    
                    File file=new File("private.ky");
                    ObjectInputStream oiis=new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
                    PrivateKey mypriv=(PrivateKey)oiis.readObject();
                    oiis.close();
                   
                    Signature sig=Signature.getInstance("MD5WithRSA");
                    sig.initSign(mypriv);
                    //signature
                    byte [] signature=sig.sign();
                  
                    oos.writeObject(signature);
                    oos.flush();
                  
                    res=(String)ois.readObject();
                    
                    if(res.equals("unreal connection")){
                        oos.close();
                        ois.close();
                        cs.close();
                        jLabel1.setText("you are impersonating some one");
                    }else{
                        PublicKey srvpub=cert.getPublicKey();
                        KeyGenerator kgen = KeyGenerator.getInstance("AES");
                        kgen.init(128);
                        SecretKey skey = kgen.generateKey();
                        String encoded = Base64.getEncoder().encodeToString(skey.getEncoded());
                        Cipher c=Cipher.getInstance("RSA");
                        c.init(Cipher.ENCRYPT_MODE, srvpub);
                        byte[] EncryptedKey =c.doFinal(encoded.getBytes());
                        oos.writeObject(EncryptedKey);
                        oos.flush(); 
                        
                        c=Cipher.getInstance("AES");
                        c.init(Cipher.ENCRYPT_MODE, skey);
                        
                        
                        byte [] enc=(byte[])ois.readObject();
                        c.init(Cipher.DECRYPT_MODE, skey);
                        res=new String(c.doFinal(enc));
                        
                        oos.close();
                        ois.close();
                        cs.close();
                        if(res.equals(" ")==false){
                            res.trim();
                            String [] results=res.split(" ");
                            for(int i=0;i<results.length;i++)
                                jComboBox1.addItem(results[i]);
                        }else{
                            jLabel1.setText("invalid phone");
                        }
                        
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("begin chat");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(186, 186, 186)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2)
                            .addComponent(jButton1))))
                .addContainerGap(153, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       Chat chat=new Chat();
       chat.phone=phone;
       chat.contact=jComboBox1.getSelectedItem().toString();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelectContact.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectContact.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectContact.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectContact.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SelectContact().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
