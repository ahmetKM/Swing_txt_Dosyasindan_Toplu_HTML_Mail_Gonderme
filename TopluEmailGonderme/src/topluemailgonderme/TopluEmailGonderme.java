/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topluemailgonderme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author AKM
 */
public class TopluEmailGonderme {

    private String gonderenAdresi = "";
    private char[] sifre;
    List<String> emailList = new ArrayList<String>();

    public List<String> getEmailList() {
        return emailList;
    }

    public void setGonderenAdresi(String gonderenAdresi) {
        this.gonderenAdresi = gonderenAdresi;
    }

    public String getGonderenAdresi() {
        return gonderenAdresi;
    }

    public char[] getSifre() {
        return sifre;
    }

    public void setSifre(char[] sifre) {
        this.sifre = sifre;
    }

    public void MailGonderMetodu(String gönderilecekAdres, String mesajIcerigi, String Baslik) {
        try {
            String[] to = {gönderilecekAdres};
            String host = "smtp.gmail.com";
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", getGonderenAdresi());
            props.put("mail.smtp.password", getSifre());
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getGonderenAdresi()));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            for (int j = 0; j < to.length; j++) {
                toAddress[j] = new InternetAddress(to[j]);
            }
            for (int j = 0; j < toAddress.length; j++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[j]);
            }
            message.setSubject(Baslik);
            message.setContent(mesajIcerigi, "text/html; charset=utf-8");
            Transport transport = session.getTransport("smtp");
            String password = new String(getSifre());
            transport.connect(host, getGonderenAdresi(), password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] MailleriSay(File dosyaUzantisi) throws IOException {
        int tekrarSayac = 0, mailSayisi = 0;
        int[] dosyaBilgileri = new int[2]; //dosyabilgilerini RETURN etmek için...
        boolean tekrar = false; // tekrar eden veri kontrolü için...
        
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(dosyaUzantisi));
        String satir = reader.readLine();
        //tekrar eden veriler kontrol ediliyor...
        while (satir != null) {
            System.out.println(satir);
            if (emailList.size() > 0) { //eklenene eleman ilk eleman değil ise
                for (int i = 0; i < emailList.size(); i++) {
                    if (emailList.get(i).equals(satir)) {
                        tekrarSayac++;
                        System.out.println("tekrar eden veri");
                        tekrar = true;
                        break;
                    }
                }
                if (!tekrar) { //tekrar etme durumu yoksa listeye ekleniyor...
                    emailList.add(satir);
                }
            } else if (emailList.size() == 0) { //eklenene eleman ilk eleman ise
                emailList.add(satir);
            }
            tekrar = false;
            //tekrar başa döneceği false yaptık
            satir = reader.readLine();
        }
        //text field alanlarında gösterilmek üzere dosya bilgileri diziye atılıyor...
        dosyaBilgileri[0] = emailList.size();
        dosyaBilgileri[1] = tekrarSayac;

        FileOutputStream fos = new FileOutputStream(dosyaUzantisi);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < emailList.size(); i++) {  //Tekrar eden veriler silindi
            bw.write(emailList.get(i));
            bw.newLine();
        }
        bw.close();
        reader.close();
        return dosyaBilgileri;
    }

    public static void main(String[] args) {
        
    }

}
