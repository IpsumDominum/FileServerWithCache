// java Program to create a list and add itemListener to it
// (program to select your birthday using lists) .
import javax.swing.event.*;
import java.awt.*;
import javax.swing.*;
class Guitest extends JFrame implements ListSelectionListener
{
     
    //frame
    static JFrame f;
     
    //lists
    static JList b,b1,b2;
     
    //label
    static JLabel l1;
  
 
    //main class
    public static void main(String[] args)
    {
        //create a new frame
        f = new JFrame("frame");                 
        //create a object
        Guitest s = new Guitest();
       
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        //create a panel
        JPanel p =new JPanel();
        p.setLayout(new CardLayout(40, 30));
        
        JPanel p2 =new JPanel();
        p2.setLayout(new CardLayout(40, 30));

        Canvas canvas = new Canvas();


        //create a new label
        JLabel l= new JLabel("Select a file to start");
        l1= new JLabel();

        //create a array for months and year
        String date[]=new String[58];
         
        //add month number and year to list
        for(int i=0;i<58;i++)
        {
            date[i]=""+(int)(i+1);
        }
        date[date.length-1] = "                       ";
         
        //create lists
        JScrollPane scrollPane = new JScrollPane();
        b= new JList(date);
        scrollPane.add(l);
        scrollPane.setViewportView(b);

        //set a selected index
        b.setSelectedIndex(2);        
        l1.setText(b.getSelectedValue()+"");

        //add item listener
        b.addListSelectionListener(s);
        
        //add list to panel
        p.add(scrollPane);
        p2.add(l1);        
        p2.add(canvas);

        mainPanel.add(p);
        mainPanel.add(p2);
  
        f.add(mainPanel);

        //set the size of frame
        f.setSize(500,500);
          
        f.show();
    }
    public void valueChanged(ListSelectionEvent e)
    {
        //set the text of the label to the selected value of lists
        l1.setText(b.getSelectedValue()+"");
         
    }
     
     
}