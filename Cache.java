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

        String fileRequest="",str_response="";
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
                str_response=server_din.readUTF();
                //Relay result back to client.
                client_dout.writeUTF(str_response);
                client_dout.flush();
            }else{
                block_num = server_din.readInt();
                client_dout.writeInt(block_num);
                //Get the resulting data...
                for(int i=0;i <block_num;i++){
                    int byte_num = server_din.readInt();
                    //Read the returned frags.                    
                    String buffer = "";
                    if(byte_num>0) {
                        byte[] byte_frag = new byte[byte_num];
                        server_din.readFully(byte_frag, 0, byte_num); // read the message
                        String encoded = Base64.getEncoder().encodeToString(byte_frag);
                        buffer += encoded;
                    }
                    //TODO, check if string is a hash, if so, choose from cached frags to make up for it.
                    client_dout.writeUTF(buffer);
                    client_dout.flush();
                }
            }
        }
        client_din.close();
        client_dout.close();
        server_din.close();   
        server_dout.close();   
    }
}