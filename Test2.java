import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;  
import java.io.*;
import java.net.*;  
import java.io.*;
import java.io.File;
import java.nio.file.Files;  
import java.nio.file.Paths;
import java.util.Base64;
import java.util.*;
import java.security.*;
import java.util.concurrent.ThreadLocalRandom;

public class Test2 {
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
    static double rabbin_test(int window_size){
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive        
        double large_num=12941;
        double hash = 0;
        int min = -128;
        int max = 128;
        for(int i=0;i<window_size;i++){
            int slice = ThreadLocalRandom.current().nextInt(min, max + 1);
            hash += slice*Math.pow(large_num,i);
        }
        return hash %2048;
    }
   public static void main(String args[]) throws Exception {
    double b = 3010363 %4;
    double c = 3010367 %4;
    int amount = 50000;
    int trials = 1;
    int avg = 0;
    for(int j=0;j<trials;j++){
        int count = 0;
        for(int i=0;i<amount;i++){        
        double d = rabbin_test(3);
        if(d==0){
            count +=1;
        }
        }
        avg += amount/count;
    }
    System.out.println("COUNT:");
    System.out.println(avg/trials);
    
    byte[] fileContent = Files.readAllBytes(Paths.get("./ServerFiles/aaa.jpg"));
    int window_size = 3;
    int zerocount = 0;
    
    for(int i=window_size;i<fileContent.length;i++){
        double hash = rabbin_hash(window_size,Arrays.copyOfRange(fileContent,i-window_size,i));
        if(hash==0){
            zerocount ++;
        }
    }
    System.out.println("OUT:");
    if(zerocount!=0){
        double a = fileContent.length/zerocount;
        System.out.println(a);
    }else{
        System.out.println(zerocount);
    }


    /*
    String strBuffer = Base64.getEncoder().encodeToString(fileContent);
    //String strBuffer = Files.readString(Paths.get("out.txt"));
    byte[] data = Base64.getDecoder().decode(strBuffer);
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    BufferedImage bImage2 = ImageIO.read(bis);
    ImageIO.write(bImage2, "jpg", new File("output.jpg") );
    System.out.println("image created");
    */
   }
}