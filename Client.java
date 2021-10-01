import javax.swing.*;
import java.net.*;  
import java.awt.image.BufferedImage;
import java.io.*;  
import java.util.Base64;
import javax.imageio.ImageIO;
import java.util.*;

   class Client{
      public static void main(String args[]) throws Exception{
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
       JButton button1 = new JButton("Press");
       frame.getContentPane().add(button1);
       frame.setVisible(true);

        Socket s=new Socket("localhost",5555);  
        DataInputStream din=new DataInputStream(s.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
       BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
        String str="",str2="";
        //Seek list of files...
        dout.writeUTF("::ListFiles");
        dout.flush();  
        str2=din.readUTF();
        System.out.println("Server says: "+str2);  
        //
        while(!str.equals("stop")){
            System.out.println("Please Enter a file to retrieve");
            str=br.readLine();  
            dout.writeUTF(str);  
            dout.flush();
            //Get in separate blocks...
            int block_num = 0;
            block_num = din.readInt();
            //Get the resulting data...
            String strBuffer = "";
            System.out.println("Server:");
            for(int i=0;i <block_num;i++){
                str2=din.readUTF();
                strBuffer += str2;
            }
            byte[] decode = Base64.getDecoder().decode(strBuffer);
            ByteArrayInputStream bis = new ByteArrayInputStream(decode);
            BufferedImage bImage = ImageIO.read(bis);
            ImageIO.write(bImage, "bmp", new File("output.bmp") );
        }  
        dout.close();  
        s.close();  
      }
}