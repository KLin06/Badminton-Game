import java.util.*;
import java.io.*;
import java.util.*;

public class PlaySound {
    public static void main(String[] args) throws IOException {

    }

    public void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(new File("D:/MusicPlayer/fml.mp3").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
