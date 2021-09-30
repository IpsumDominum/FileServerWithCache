import java.net.*;  
import java.io.*;
import java.io.File;
import java.nio.file.Files;  
import java.nio.file.Paths;
import java.util.Base64;
import java.util.*;

class Server{

    LinkedList<String> cacheContent = new LinkedList<>();
    public static void main(String args[])throws Exception{  
    System.out.println("Loading Files");  
    File folder = new File("ServerFiles");
    File[] listOfFiles = folder.listFiles();
    byte[] fileContent = {};
    byte[] cachedFootprints = {};
    String fileList = "";
    for (int i = 0; i < listOfFiles.length; i++) {
    if (listOfFiles[i].isFile()) {
        fileContent = Files.readAllBytes(listOfFiles[i].toPath());
        fileList += listOfFiles[i].toString().substring(12,listOfFiles[i].toString().length())+ "|";
        } else if (listOfFiles[i].isDirectory()) {
        }
    }    
                

    //===============================================
    //START SERVER
    //===============================================
    System.out.println("Starting Server...");  
    ServerSocket ss=new ServerSocket(3333);
    Socket s=ss.accept();
    DataInputStream din=new DataInputStream(s.getInputStream());
    DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
    System.out.println("Server started...");  
    //===============================================
    //SERVER LISTENING
    //===============================================    

    int max_byte_size = 2048;

    String fileRequest="";
        while(!fileRequest.equals("stop")){  
            fileRequest=din.readUTF();
            System.out.println("File Request: "+fileRequest);
            boolean found = false;            
            if(fileRequest.equals("::ListFiles")){
                dout.writeUTF(fileList);
                dout.flush();
                continue;
            }
            for(int i=0;i<listOfFiles.length;i++){
                if(listOfFiles[i].toString().equals("ServerFiles\\"+fileRequest)){
                    found = true;
                    break;
                }
            }
            if(!found){
                dout.writeInt(-1);
                dout.flush();
                continue;
            }else{

            }
            fileContent = Files.readAllBytes(Paths.get("./ServerFiles/"+fileRequest));
            // byte[] to base64 encoded string
            String str2 = Base64.getEncoder().encodeToString(fileContent);
            //Send back chunks...
            if(str2.length()<=max_byte_size){
                dout.writeInt(1);
                dout.writeUTF(str2.substring(0,max_byte_size));
            }else{
                int i =1;
                LinkedList<String> blocks = new LinkedList<>();
                while(true){
                    if(i*max_byte_size>=str2.length()){
                        blocks.push(str2.substring((i-1)*max_byte_size,str2.length())); 
                        break;
                    }else{
                        blocks.push(str2.substring((i-1)*max_byte_size,i*max_byte_size)); 
                    }
                    i++;
                }
                dout.writeInt(blocks.size());
                for(i=0;i<blocks.size();i++){
                    dout.writeUTF(blocks.get(i));
                }
            }
            dout.flush();  
        }  
    //===============================================
    //CLEAN UP
    //===============================================
    din.close();  
    s.close();  
    ss.close();  
    }
}  