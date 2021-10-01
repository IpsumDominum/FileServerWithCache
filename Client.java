import java.net.*;  
import java.awt.image.BufferedImage;
import java.io.*;  
import java.util.Base64;
import javax.imageio.ImageIO;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent; 

class Client{
      static JList b;
      static JLabel l1;
      static DataInputStream din;
      static DataOutputStream dout;
      static JPanel p;
      static JPanel p2;
      static String str_response;
      static boolean request_handling = false;
      static JPanel canvas;
      static JLabel pic;
      static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
          Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
          BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
          outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
          return outputImage;
      }
      public static void main(String args[]) throws Exception{

        //create a new frame
        JFrame f = new JFrame("frame");                 
        //create a object
       
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        //create a panel
        p =new JPanel();
        p.setLayout(new CardLayout(40, 30));
        
        p2 =new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));


        //create a new label
        JLabel l= new JLabel("Select a file to start");
        l1= new JLabel();
        JButton button = new JButton("Request");  
          button.setBounds(50,100,95,30);  

          button.addActionListener(new ActionListener(){  
          public void actionPerformed(ActionEvent e){  
                try{
              if(request_handling || (b.getSelectedValue()+"").equals("null")){
                return;
              }else{
                
              }   
                  l1.setText("Handling Request...");
                  String requestFileName = b.getSelectedValue()+"";
                  if(request_handling==false){
                    dout.writeUTF(requestFileName);  
                    dout.flush();  
                  }else{
                    request_handling = true;
                    return;
                  }
                  
                  //Get in separate blocks...
                  int block_num = 0;
                  block_num = din.readInt();
                  //Get the resulting data...
                  ArrayList<byte[]> byteBuffer = new ArrayList<byte[]>();
                  System.out.println("Server:");
                  String buffer = "";
                  System.out.println("Waiting for blocks...");
                  System.out.println("Fetching "+block_num+" number of blocks...");
                  int total_bytes = 0;
                  for(int i=0;i <block_num;i++){
                      int byte_num = din.readInt();
                      //System.out.println("Fetching "+byte_num+" number of bytes...");
                      //Read the returned frags.
                      if(byte_num>0) {
                          byte[] byte_frag = new byte[byte_num];
                          din.readFully(byte_frag, 0, byte_num); // read the message
                          //TODO, check if frag is cached, if so, choose from cached frags to make up for it.
                          byteBuffer.add(byte_frag);
                          String encoded = Base64.getEncoder().encodeToString(byte_frag);
                          buffer += encoded;
                          total_bytes +=byte_num;
                      }
                  }
                  byte[] finalJoined = new byte[total_bytes];
                  int prev_index = 0;
                  for(int i=0;i<byteBuffer.size();i++){
                    System.arraycopy(byteBuffer.get(i), 0, finalJoined, prev_index, byteBuffer.get(i).length);  
                    prev_index += byteBuffer.get(i).length;
                  }
                  System.out.println("Done.");
                  //byte[] decode = Base64.getDecoder().decode(strBuffer);
                  ByteArrayInputStream bis = new ByteArrayInputStream(finalJoined);
                  BufferedImage img = ImageIO.read(bis);
                  //ImageIO.write(bImage, "bmp", new File(requestFileName) );
                  img = resizeImage(img,300,300);
                  p2.remove(pic);
                  pic = new JLabel(new ImageIcon(img));
                  p2.add(pic);
                  p2.revalidate();
                  p2.repaint(); 
                  l1.setText("Ready to request next file.");
                  request_handling = false;
            }catch(Exception error){
              request_handling = false;
              error.printStackTrace();
            }
              }  
          });  
          p2.add(button);

        //create a array for months and year
        DefaultListModel model = new DefaultListModel();
        //jList1 = new JList(model);
        for (int i = 0; i < 15; i++) {
            model.addElement("");
        }
        //create lists
        JScrollPane scrollPane = new JScrollPane();        
        b = new JList();
        b.setModel(model);
        scrollPane.add(l);
        scrollPane.setViewportView(b);

        //set a selected index
        b.setSelectedIndex(2);        
        l1.setText(b.getSelectedValue()+"");

        pic = new JLabel();
        p2.add(pic);
        p2.repaint(); 
        /*
        try {
            //BufferedImage img = ImageIO.read(new File("C:\\Users\\sophi\\OneDrive\\Desktop\\Chen\\FileServerWithCache\\ServerFiles\\test1.bmp"));
            //img = resizeImage(img,300,300);
            pic = new JLabel(new ImageIcon(img));
            p2.add(pic);
            p2.repaint(); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        */
        //add item listener
        b.addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e){                
          }
        });


        //add list to panel
        p.add(scrollPane);
        p2.add(l1);        
        

        mainPanel.add(p);
        mainPanel.add(p2);
  
        f.add(mainPanel);
        
        //set the size of frame
        f.setSize(500,500);

        f.show();
          
        Socket s=new Socket("localhost",5555);  
        din=new DataInputStream(s.getInputStream());  
        dout=new DataOutputStream(s.getOutputStream());  

        //Seek list of files...
        dout.writeUTF("::ListFiles");
        dout.flush();  
        str_response=din.readUTF();
        String[] fileList = str_response.split("::");
        model = new DefaultListModel();
        for(int i=0;i<fileList.length;i++){
            model.addElement(fileList[i]);
        }
        b.setModel(model);
        //
        
        //dout.close();  
        //s.close();  
      }
}