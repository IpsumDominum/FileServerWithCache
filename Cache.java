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
import javax.swing.event.*;

class Cache{
static ArrayList<ArrayList<byte[]>> cacheContent = new ArrayList<ArrayList<byte[]>>();

static JPanel mainPanel,p,p2;
static JLabel l1;
static JList b,b2;
static JScrollPane scrollPane,scrollPane2;
static ArrayList<String> logs = new ArrayList<String>();
static DataInputStream server_din;
static DataOutputStream server_dout;
static DataInputStream client_din;
static DataOutputStream client_dout;
public static boolean cacheContainsKey(byte[] key){
    boolean found = false;
    for(int i=0;i<cacheContent.size();i++){
        if(Arrays.equals(cacheContent.get(i).get(0),key)){
            found = true;
            break;
        }
    }
    return found;
}
public static byte[] getCachedMessage(byte[] key){
    byte[] result = {};
    for(int i=0;i<cacheContent.size();i++){
        if(Arrays.equals(cacheContent.get(i).get(0),key)){
            result = cacheContent.get(i).get(1);
            break;
        }
    }
    return result;
}
public static void updateDigestList() throws IOException{
    DefaultListModel model = new DefaultListModel();
    String str = "";
    for (int i=0;i<cacheContent.size();i++) {
        //Digest
        byte[] digest = cacheContent.get(i).get(0);
        String encoded = Base64.getEncoder().encodeToString(digest);
        model.addElement(encoded);
        str += encoded+"\n";
    }
    BufferedWriter writer = new BufferedWriter(new FileWriter("Cache_fingerprints"));
    writer.write(str);

    writer.close();
    b.setModel(model);
}
public static void updateLogDisplay(){
    DefaultListModel model = new DefaultListModel();
    for (int i=0;i<logs.size();i++) {
        model.addElement(logs.get(i));
    }
    b2.setModel(model);
}
public static void main(String args[])throws IOException{
    //create a new frame
        JFrame f = new JFrame("frame");                 
        //create a object
       
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        //create a panel
        p =new JPanel();
        p.setLayout(new FlowLayout());
        
        
        p2 =new JPanel();
        //p2.setLayout(new CardLayout(40, 30));
        //p2.setSize(200,200);

                       

        //create a array for months and year
        DefaultListModel model = new DefaultListModel();
        //jList1 = new JList(model);
        model.addElement("CACHED DATA:::");
         DefaultListModel model2 = new DefaultListModel();
        //jList1 = new JList(model);
        model2.addElement("LOG:");
        
        //create lists
        JScrollPane scrollPane = new JScrollPane();        
        b = new JList();
        b.setModel(model);
        //b.setPreferredSize(new Dimension(200, 200));
        scrollPane.setViewportView(b);

        //Log
        JScrollPane scrollPane2 = new JScrollPane();        
        b2 = new JList();
        b2.setModel(model2);
        //b2.setPreferredSize(new Dimension(200, 200));
        scrollPane2.setViewportView(b2);

        //Show Data Region
        JScrollPane scrollPane3 = new JScrollPane();        
        l1= new JLabel();   
        //l1.setPreferredSize(new Dimension(200, 200));
        scrollPane3.setViewportView(l1);
        l1.setText("hihihiihasldjalskjdlasjdlkajsdlajsldajlsdjkl");
        //add item listener
        b.addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e){                              
              String query = b.getSelectedValue()+"";
              byte[] decode = Base64.getDecoder().decode(query);
              byte[] cachedMessage = getCachedMessage(decode);
              String encode = Base64.getEncoder().encodeToString(cachedMessage);
              l1.setText(encode);
          }
        });

        //add list to panel
        
        //p2.add(scrollPane2);        
        //mainPanel.add(p,BorderLayout.NORTH);
        JButton button = new JButton("Clear Cache");
        button.setBounds(50,100,95,30);  
        button.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent e) {  
            try{            
            Date date=java.util.Calendar.getInstance().getTime();
            logs.add("COMMAND : ::ClearCache @ "+date.toString());
            //Relay request to server
            server_dout.writeUTF("::ClearCache");
            server_dout.flush();
            cacheContent = new ArrayList<ArrayList<byte[]>>();
            String str_response=server_din.readUTF();
            //Relay result back to client.
            logs.add("response: "+str_response);
            updateLogDisplay();
            updateDigestList();
            }catch(IOException error){
                error.printStackTrace();
            }
        }
        });
        

        mainPanel.add(button,BorderLayout.NORTH);
        mainPanel.add(scrollPane2,BorderLayout.SOUTH);
        mainPanel.add(scrollPane,BorderLayout.WEST); 
        mainPanel.add(scrollPane3,BorderLayout.EAST);        
  
        f.add(mainPanel);
        
        //set the size of frame
        f.setSize(500,500);

        f.show();
    
        
        

        Socket s=new Socket("localhost",3333);
        server_din=new DataInputStream(s.getInputStream());  
        server_dout=new DataOutputStream(s.getOutputStream());  
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
        System.out.println("Cache Connected to File Server...");  
        //Server socket        
        ServerSocket client_ss=new ServerSocket(5555);
        Socket client_s=client_ss.accept();
        client_din=new DataInputStream(client_s.getInputStream());
        client_dout=new DataOutputStream(client_s.getOutputStream());  
        System.out.println("Cache Server started...");  
        //===============================================
        //SERVER LISTENING
        //===============================================    

        int max_byte_size = 2048;

        String fileRequest="",str_response="";
        while(!fileRequest.equals("stop")){
            //Wait for client input
            fileRequest= client_din.readUTF();
            Date date=java.util.Calendar.getInstance().getTime();
            logs.add("user request:" + fileRequest + " @ "+date.toString());
            //Relay request to server
            server_dout.writeUTF(fileRequest);
            server_dout.flush();
            //Wait for server response;
            int block_num = 0;
            if(fileRequest.equals("::ListFiles")){
                
                str_response=server_din.readUTF();
                //Relay result back to client.
                logs.add("response: "+str_response);
                updateLogDisplay();
                client_dout.writeUTF(str_response);
                client_dout.flush();
            }else{
                //===============================================
                //RECEIVE BLOCKS FROM SERVER
                //===============================================    
                block_num = server_din.readInt();
                //Get the resulting data...
                ArrayList<byte[]> byteBuffer = new ArrayList<byte[]>();
                String buffer = "";                    
                int hit_num = 0;
                for(int i=0;i <block_num;i++){
                    int byte_num = server_din.readInt();
                    //Read the returned frags.                    
                    if(byte_num>0) {
                        byte[] byte_frag = new byte[byte_num];
                        server_din.readFully(byte_frag, 0, byte_num); // read the message
                        //TODO, check if frag is cached, if so, choose from cached frags to make up for it.
                        int header = byte_frag[0];
                        //////////////////////////
                        //CHECK SERVER RESPONSE TYPE
                        //////////////////////////

                    if(header==32){
                        //Read digest, store in cache...
                        //Key is digest
                        //Value is frag value                        
                        ArrayList<byte[]> pair = new ArrayList<byte[]>();
                        pair.add(Arrays.copyOfRange(byte_frag, 1, header+1));
                        pair.add(Arrays.copyOfRange(byte_frag, 1+header,byte_frag.length));
                        cacheContent.add(
                            pair                            
                        );
                        byteBuffer.add(Arrays.copyOfRange(byte_frag, 1+header, byte_frag.length));                        
                    }else if(header==0){
                        //Check cache...
                        byte[] digest = Arrays.copyOfRange(byte_frag, 1, byte_frag.length);
                        if(cacheContainsKey(digest)){
                            //System.out.println("Cache hit.");
                            hit_num +=1;
                            byte[] cachedMessage = getCachedMessage(digest);
                            byteBuffer.add(cachedMessage);                                                    
                        }else{
                            //Query Server for key...
                            /*Cache missed... Server and Cache not in sync, send Server Cache digest list.*/
                            System.out.println("Cache and Server not in sync...");
                        }
                    }else{
                        System.out.println("ERROR::Unexpected block header bit.");
                    }
                        //String encoded = Base64.getEncoder().encodeToString(byte_frag);
                        //buffer += encoded;
                    }
                }
                updateDigestList();
                //===============================================
                //SEND BACK TO CLIENT (AFTER ASSEMBLY)
                //===============================================    
                client_dout.writeInt(byteBuffer.size());
                //System.out.println("Sending "+byteBuffer.size()+" amount of blocks...");
                for(int i=0;i<byteBuffer.size();i++){
                    byte[] message = byteBuffer.get(i);
                    //System.out.println("Sending "+message.length+" amount of bytes...");
                    client_dout.writeInt(message.length); // write length of the message                    
                    client_dout.write(message);
                }
                double percentage = ((double)hit_num/(double)block_num)*100;
                logs.add("response: "+ percentage+"% of file "+fileRequest +" was constructed with the cached data");
                updateLogDisplay();
                System.out.println("Bytes Sent.");
                client_dout.flush();
            }
        }
        client_din.close();
        client_dout.close();
        server_din.close();   
        server_dout.close();   
    }
}