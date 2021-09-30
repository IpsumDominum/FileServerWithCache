import java.net.*;  
import java.io.*;  
import java.util.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

class Cache{

public static void main(String args[])throws IOException{
        Socket s=new Socket("localhost",3333);
        DataInputStream server_din=new DataInputStream(s.getInputStream());  
        DataOutputStream server_dout=new DataOutputStream(s.getOutputStream());  
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
        System.out.println("Cache Connected to File Server...");  
        //Server socket
        ServerSocket client_ss=new ServerSocket(5555);
        Socket client_s=client_ss.accept();
        DataInputStream client_din=new DataInputStream(client_s.getInputStream());
        DataOutputStream client_dout=new DataOutputStream(client_s.getOutputStream());  
        System.out.println("Cache Server started...");  
        //===============================================
        //SERVER LISTENING
        //===============================================    

        int max_byte_size = 2048;

        String fileRequest="",str2="";
        while(!fileRequest.equals("stop")){  
            //Wait for client input
            fileRequest= client_din.readUTF();
            System.out.println("Client request: "+fileRequest);
            //Relay request to server
            server_dout.writeUTF(fileRequest);
            server_dout.flush();
            //Wait for server response;
            int block_num = 0;
            if(fileRequest.equals("::ListFiles")){
                block_num = 1;
            }else{
                block_num = server_din.readInt();
            }
            //Get the resulting data...
            String strBuffer = "";
            System.out.println("Server:");
            for(int i=0;i <block_num;i++){
                str2=server_din.readUTF();
                //TODO, check if string is a hash, if so, choose from cached frags to make up for it.
                strBuffer += str2;
            }
            if(!fileRequest.equals("::ListFiles")){
                byte[] decode = Base64.getDecoder().decode(strBuffer);
            }
            //Relay result back to client.
            client_dout.writeUTF(strBuffer);
            client_dout.flush();
        }
            
        client_din.close();
        client_dout.close();
        server_din.close();   
        server_dout.close();   
}
}