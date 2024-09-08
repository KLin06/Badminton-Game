import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class monk extends JPanel{
    public static BufferedImage background;
    public static String filePath = "";
    public Backgrounds() { // change JPanel Settings
        setPreferredSize(new Dimension(100,600));
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        g.drawImage(background, 150, 70 , null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lesson 2");
        Backgrounds panel = new Backgrounds();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();

        try{
            background = ImageIO.read(new File("C:\\Users\\klin0\\Desktop\\Coding\\CS Game\\Pictures\\gamestate12.png"));
        } catch (Exception e){
            System.out.println("Something wrong with image");
        }
    }
}
