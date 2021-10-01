import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class ImageCanvas extends JPanel
{
  public static BufferedImage image;
  boolean imageSet = false;
  public ImageCanvas ()
  {
    super();    
  }
  public void paintComponent(Graphics g)
  {
    if(imageSet){
    g.drawImage(image, 0, 0, null);  
    repaint();
    }
  } 
  public void paint(BufferedImage img) {
    image = img;
    imageSet = true;
    repaint();
  }
}