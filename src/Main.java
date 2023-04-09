import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.*;

public class Main {
    public static void main(String[] args)throws IOException  {
        //ana menü sayesinde kullanıcıdan gerçekleştirmek istediği işlemi alıyorz
        Scanner scn = new Scanner(System.in);
        System.out.println("Gerçekleştirmek istediğiniz işlemin rakamını giriniz");
        System.out.println("1)Elit üye ekleme\n2)Genel Üye ekleme\n3)Mail Gönderme");
        int num = scn.nextInt();
        scn.nextLine();
        Member member = new Member();
        SendMail sm = new SendMail();
        CheckFile cf = new CheckFile();

        // kullanıcıyı seçtiği işleme göre yönlendiriyoruz
        if(num==1 || num==2) {
            member.AddMember(num);
        }
        else if(num==3){
            cf.Check();
            sm.MailSender();
        }
        else
        {
            System.out.println("geçerli bir değer girmediniz");
        }
    }
}
class CheckFile { // dosyayı kontrol eden class
    public void Check(){
        String fileName = "C:\\Users\\merve\\Desktop\\kullanicilar.txt ";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            boolean hashPresent = false;
            boolean starPresent = false;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("ELİT ÜYELER")) {
                    hashPresent = true;
                }
                if (line.contains("GENEL ÜYELER")) {
                    starPresent = true;
                }
            }

            bufferedReader.close();

            if (!hashPresent) {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.newLine();
                bufferedWriter.write("ELİT ÜYELER");

                bufferedWriter.close();
            }

            if (!starPresent) {
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.newLine();
                bufferedWriter.write("GENEL ÜYELER");

                bufferedWriter.close();
            }
        } catch (IOException ex) {
            System.out.println("dosya okunurken hata ile karşılaşıldı");
        }
    }
}

class Member  {
    String name,mail,surname;
    public void AddMember(int num)throws IOException {


        // dosyayı okuyoruz
        Scanner scn = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\merve\\Desktop\\kullanicilar.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        reader.close();
        String fileContents = sb.toString();

        // alınan bilgileri nereye ekleyeceğimizi belirliyoruz
        String find;
        int index;
        if (num == 1) {
            find = "ELİT ÜYELER";
            index = fileContents.indexOf(find) + 12;
        } else if (num == 2) {
            find = "GENEL ÜYELER";
            index = fileContents.indexOf(find) + 13;
        } else {
            System.out.println("geçersiz değer");
            return;
        }

        // dosyayı iki parçaya ayırıyoruz ki aralarına yeni aldığımız bilgileri yazabilelim
        String part1 = fileContents.substring(0, index + 1);
        String part2 = fileContents.substring(index + 1);

        // dosyaya eklenecek bilgileri alıyoruz
        System.out.print("Lütfen isminizi girin \n");
        name = scn.nextLine();
        System.out.println("lütfen soyisminizi girin");
        surname=scn.nextLine();
        System.out.println("lütfen mail adresinizi girin");
        mail= scn.nextLine();

        String newInfo = name+"        "+surname+"        "+mail;

        // dosyaya eklemek istediğimiz bilgilerle beraber yeni bir String ifade oluşturuyoruz
        String updatedContents = part1 + newInfo + System.lineSeparator() + part2;

        // son oluşturduğumuz ifadeyi dosyaya yazıyoruz
        PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\merve\\Desktop\\kullanicilar.txt"));
        writer.write(updatedContents);
        writer.close();

        System.out.println("bilgileriniz kaydedilmiştir");

    }
}


class FindMail {


    public ArrayList<String> Email() { // dosyadaki mail adreslerini bulup ArrayList olarak return eden method

        ArrayList<String> emails = new ArrayList<String>();
        String fileName ="C:\\Users\\merve\\Desktop\\kullanicilar.txt" ;

        try {
            // dosyayı okuyup mail adreslerini belirliyor ve bu mail adreslerini arrayliste kadediyoruz
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern pattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String email = matcher.group();
                    emails.add(email);

                }
            }
            reader.close();
        } catch (Exception e) { // hata ile karşılaşılırsa:
            System.err.format("'%s' okunurken hata oluştu.", fileName);
            e.printStackTrace();
        }
        // dosyadan okuduğumuz mail adreslerin kontrol ediyoruz
        System.out.println(emails.size() + " tane adres bulundu");
        for (String email : emails) {
            System.out.println(email);

        }
        return emails; }


}

    class SendMail extends FindMail {

    public void MailSender(){
        // kalıtım yoluyla aldığımız arraylist:
        ArrayList<String> emails = Email();
        Scanner scn = new Scanner(System.in);

        // host olarak kullandığımız mail adresinin bilgileri
        final String username = "mervenursarac60@gmail.com";
        final String password = "tredceszpcrijxkj"; // 2 adımlı doğrulama şifresi

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mervenursarac60@gmail.com"));

            for (String address : emails) { // kalıtım ile aldığımız arrayListteki mail adreslerini tek tek atıyoruz
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
            }

            // mailin içerik bilgisini alıyoruz
            System.out.println("göndereceğiniz mailin başlığını giriniz:");
             String subject = scn.nextLine();
            message.setSubject(subject);
            System.out.println("mailinizin içeriğini giriniz:");
            String body = scn.nextLine();
            message.setText(body);


            // maili gönderiyoruz
            Transport.send(message);

            System.out.println("Mail gönderme başarılı!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}







