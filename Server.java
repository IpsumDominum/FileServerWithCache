import java.net.*;  
import java.io.*;
import java.io.File;
import java.nio.file.Files;  
import java.nio.file.Paths;
import java.util.Base64;
import java.util.*;

class Server{

    LinkedList<String> cacheContent = new LinkedList<>();

    static double rabbin_hash(int window_size,byte[] byte_slice){
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive        
        double large_num=12941;
        double hash = 1;
        //double p = 3010367;
        //double q = 3010363;
        for(int i=0;i<window_size;i++){
            int slice = byte_slice[i];
            hash += slice*Math.pow(large_num,i);
        }
        return hash %2048;
    }    
    
    public static void main(String args[])throws Exception{  
    System.out.println("Loading Files");  
    File folder = new File("ServerFiles");
    byte[] fileContent = {};
    byte[] cachedFootprints = {};
    File[] listOfFiles = folder.listFiles();        
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
    String fileRequest="";
        while(!fileRequest.equals("stop")){  
            fileRequest=din.readUTF();
            System.out.println("File Request: "+fileRequest);
            boolean found = false;            
            if(fileRequest.equals("::ListFiles")){
                //===============================================
                //SEND BACK FILE LIST
                //===============================================    
                System.out.println("Sending Filelist...");
                dout.writeUTF(fileList);
                dout.flush();
                continue;
            }else{
                //===============================================
                //SEND BACK REQUESTED FILE
                //===============================================    
                for(int i=0;i<listOfFiles.length;i++){
                if(listOfFiles[i].toString().equals("ServerFiles\\"+fileRequest)){
                    found = true;
                    break;
                }
            }
            //-------------------
            //FILE NOT FOUND
            //-------------------
            if(!found){
                dout.writeInt(-1);
                dout.flush();
                continue;
            }else{
                //-------------------
                //FILE FOUND.Pass.
                //-------------------
            }
            //Window size of 3;
            int window_size = 3;
            fileContent = Files.readAllBytes(Paths.get("./ServerFiles/"+fileRequest));
            if(fileContent.length<window_size){
                //If file content less than window size, just send it back.
                dout.writeInt(1); // Write number of blocks to expect.
                dout.writeInt(fileContent.length); // Write length of the message
                dout.write(fileContent);
                continue;                
            }
            // byte[] to base64 encoded string
            //String str2 = Base64.getEncoder().encodeToString(fileContent);
            //Send back chunk if chunk is small.
            int prev_boundary = 0;
            int boundary = window_size;
            //Encode each block with the Rabin scheme
            //For n blocks in window size, if the multiple of the blocks
            //multiplied by a large number seed % 2048 = 0, then send buffer
            //Otherwise keep building buffer.
            ArrayList<byte[]> byteBuffer = new ArrayList<byte[]>();
            //Get all frags to return.
            for(int i=window_size;i<fileContent.length;i++){
                double hash = rabbin_hash(window_size,Arrays.copyOfRange(fileContent,i-window_size,i));
                if((hash==0 || i==fileContent.length-1) && prev_boundary<fileContent.length){
                    //Get new frag. Check if frag exists in current cache.
                    byteBuffer.add(Arrays.copyOfRange(fileContent,prev_boundary,i));
                    prev_boundary = i+1;
                    //
                }
            }
            /*Test*/
            //Rebuild filecontent after hash blocks...
            

            //Return the frags
            dout.writeInt(byteBuffer.size());
                for(int i=0;i<byteBuffer.size();i++){
                    byte[] message = byteBuffer.get(i);
                    dout.writeInt(message.length); // write length of the message
                    dout.write(message);  
                }
                dout.flush();
            }
            
        }  
    //===============================================
    //CLEAN UP
    //===============================================
    din.close();  
    s.close();  
    ss.close();  
    }
}  