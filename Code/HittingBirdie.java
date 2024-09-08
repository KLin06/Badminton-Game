import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HittingBirdie extends JPanel implements KeyListener, Runnable {
    // player variables
    public static BufferedImage[] hittingAnimation = new BufferedImage[6];
    public static BufferedImage[] walkingAnimation = new BufferedImage[4];
    public static BufferedImage court;
    public static int xPos = 200;
    public static int yPos = 260;
    public static boolean wPressed = false;
    public static boolean aPressed = false;
    public static boolean dPressed = false;
    public static boolean spacePressed = false;
    public static int frameController = 0;
    public static int spriteNumber = 0;
    public static int spriteWalkingNumber = 0;
    public static int yVelocity = -18;
    public static int gravity = 2;
    public static boolean isWalking = false;
    public static boolean isSwinging = false;
    public static boolean isJumping = false;
    public static int swingCount = 0; // maintains full swing
    public static long start;
    public static long finish;

    // birdie variables
    public static BufferedImage birdie;
    public static int shuttleInitialY = -26; // initial set velocity, normal -20
    public static int shuttleStartY = 450;
    public static int shuttleXPos = 200;
    public static int shuttleYPos = 550;
    public static int shuttleFrameController = 0;
    public static double degRotate = 0;
    public static int shuttleXVelocity = 35; // normal 30
    public static int shuttleYVelocity = shuttleInitialY;
    public static int shuttleGravity = 2; // normal 2
    public static int shuttleDimensions = 30; // height and width of the birdie
    public static boolean direction = true; // true is to the right, false is to the left

    public static int swingTimer = 0; // counts the time since it was swung

    // collision detection variables
    public static int raquetTopXPosition;
    public static int raquetTopYPosition;
    public static int raquetBottomXPosition;
    public static int raquetBottomYPosition;
    public static boolean isHit = false; //whether the shuttle has already been hit in the swing
    

    public HittingBirdie() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public static void jump() { // to make it jump
        if (isJumping) {
            yPos += yVelocity;
            yVelocity += gravity;
            if (yPos >= 260) {
                isJumping = false;
                yPos = 260;
                yVelocity = -18;
            }
        }
    }

    public static void racquetShuttleCollisionDetection(Graphics g) {
        raquetTopXPosition = -(int) (Math.cos(Math.toRadians(360 * swingTimer / 42 + 45)) * Math.sqrt(15000)) + 90
                + xPos;
        raquetTopYPosition = -(int) (Math.sin(Math.toRadians(360 * swingTimer / 42 + 45)) * Math.sqrt(15000)) + 155
                + yPos;
        raquetBottomXPosition = -(int) (Math.cos(Math.toRadians(360 * swingTimer / 43 + 45)) * Math.sqrt(5000)) + 90
                + xPos;
        raquetBottomYPosition = -(int) (Math.sin(Math.toRadians(360 * swingTimer / 43 + 45)) * Math.sqrt(5000)) + 165
                + yPos;
        if (Math.abs(raquetBottomXPosition - raquetTopXPosition) + 35 >= Math.abs(raquetBottomXPosition - shuttleXPos)
                + Math.abs(raquetTopXPosition - shuttleXPos) && !isHit) {
            // adding a number inside this statement increases the margin of error
            if (Math.abs(raquetBottomYPosition - raquetTopYPosition) + 45 >= Math
                    .abs(raquetBottomYPosition - shuttleYPos) + Math.abs(raquetTopYPosition - shuttleYPos)) {
                isHit = true; //it is hit
                direction = true; //turns it the other way around
                int distance = (int) Math.sqrt(
                        Math.pow(raquetTopXPosition - shuttleXPos, 2) + Math.pow(raquetTopYPosition - shuttleYPos, 2));
                shuttleYVelocity = -24 + 2 * (int)((distance)/8); //set arbitrary values, denominator is the factor that affects the minimum yvelocity value
            }
        }
    }

    public static void shuttleMovement(Graphics g) { // animating the birdie
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate
        g2d.rotate(Math.toRadians(degRotate), shuttleXPos + 25, shuttleYPos + 25);
        g.drawImage(birdie, shuttleXPos, shuttleYPos, null);
        if (shuttleFrameController == 4) {
            shuttleYPos += shuttleYVelocity;
            shuttleYVelocity += shuttleGravity;
            shuttleXPos += shuttleXVelocity;
            if (shuttleYPos >= shuttleStartY) { // set as lower bound of birdie (flooring)
                shuttleYPos = shuttleStartY;
                shuttleYVelocity = shuttleInitialY; // need initial y
            }
            shuttleFrameController = 0;
        }
        shuttleFrameController++;
        if (shuttleXPos >= 920) { // if its hits sides
            direction = false; // changes the direction
            shuttleXVelocity = -Math.abs(shuttleXVelocity);
        }
        if (shuttleXPos <= 30) { // if its hits sides
            direction = true; // changes the direction
            shuttleXVelocity = Math.abs(shuttleXVelocity);
        }
        if (direction) {
            shuttleXVelocity = Math.abs(shuttleXVelocity);
            degRotate = -(90 * shuttleYVelocity / shuttleInitialY) % 360; // -(90*shuttleYVelocity/shuttleInitialY) %
                                                                          // 360 for L to R,
            // (90*shuttleYVelocity/shuttleInitialY- 180) % 360 is R to L
        } else {
            degRotate = (90 * shuttleYVelocity / shuttleInitialY - 180) % 360;
            shuttleXVelocity = -Math.abs(shuttleXVelocity);
        }
    }

    public static void characterMovement(Graphics g) { // animating the swing
        jump();
        if (spacePressed || swingCount != 0) { // checking if it should be swinging
            isSwinging = true;
        } else {
            isSwinging = false;
            spriteNumber = 0;
        }
        if (isSwinging) {
            if (swingTimer < 16) { // doesn't swing too far
                racquetShuttleCollisionDetection(g); // detecting collisions
            }
            if (swingCount == 0) {
                swingCount++; // remove delay
            }
            frameController++;
            swingTimer++; // counts each frame (remove)
            if (frameController >= 4) { // controls frame rate
                frameController = 0;
                spriteNumber = (spriteNumber + 1) % 6;
                swingCount++;
            }
            if (swingCount == 7) { // runs until the swing hits sprite 4;
                swingCount = 0;
                swingTimer = 0; // (remove)
                isHit = false;
            }
            g.drawImage(hittingAnimation[spriteNumber], xPos, yPos, null);

        } else if (isWalking) {
            g.drawImage(walkingAnimation[spriteWalkingNumber], xPos, yPos, null); // walking animation when a&d keys
            frameController++;
            if (frameController >= 6) {
                frameController = 0;
                spriteWalkingNumber = (spriteWalkingNumber + 1) % 4;
            }
        } else {
            g.drawImage(walkingAnimation[0], xPos, yPos, null);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(court, 0, 0, null);
        if (aPressed && xPos > 0) { // moving the sprite
            xPos -= 6;
        }
        if (dPressed && xPos < 400) {
            xPos += 6;
        }

        characterMovement(g);
        shuttleMovement(g); // this line must stay last or everything becomes weird and begines rotation

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Player");
        HittingBirdie panel = new HittingBirdie();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*
         * long start = System.currentTimeMillis();
         * long finish = System.currentTimeMillis();
         * long timeElapsed = finish - start;
         */
        try {
            court = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\gamestate3.png"));
            hittingAnimation[0] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\swingAnimation01.png"));
            hittingAnimation[1] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\swingAnimation02.png"));
            hittingAnimation[2] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\swingAnimation03.png"));
            hittingAnimation[3] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\swingAnimation04.png"));
            hittingAnimation[4] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\swingAnimation05.png"));
            hittingAnimation[5] = ImageIO
                    .read(new File("C:\\Users\\emily\\Downloads\\MSS CS\\CS Game\\swingAnimation06.png"));

            walkingAnimation[0] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation01.png"));
            walkingAnimation[1] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation02.png"));
            walkingAnimation[2] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation03.png"));
            walkingAnimation[3] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation04.png"));

            birdie = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\birdie.png"));
        } catch (Exception e) {
            System.out.println("Something wrong with image");
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w') { // jumping
            wPressed = true;
            if (!isJumping)
                isJumping = true;
        }
        if (e.getKeyChar() == 'a') { // backwards
            aPressed = true;
            isWalking = true;
        }
        if (e.getKeyChar() == ' ') { // swing
            spacePressed = true;
        }
        if (e.getKeyChar() == 'd') { // forward
            dPressed = true;
            isWalking = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            wPressed = false;
        }
        if (e.getKeyChar() == 'a') {
            aPressed = false;
            isWalking = false;
        }
        if (e.getKeyChar() == 'd') {
            dPressed = false;
            isWalking = false;
        }
        if (e.getKeyChar() == ' ') { // swing
            spacePressed = false;
        }
    }

    public void run() {
        while (true) {
            try {
                repaint();
                Thread.sleep(20); // 20 is 50 fps, 17 is approx 60

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}
