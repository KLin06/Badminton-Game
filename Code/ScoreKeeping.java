import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ScoreKeeping extends JPanel implements KeyListener, Runnable {
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
    public static int yVelocity = -24;
    public static int gravity = 4;
    public static boolean isWalking = false;
    public static boolean isSwinging = false;
    public static boolean isJumping = false;
    public static int swingCount = 0; // maintains full swing
    public static long start; // variables for timer
    public static long finish;

    // birdie variables
    public static BufferedImage birdie;
    public static int shuttleSumXY = -25; // for the relationship between x and y
    public static int shuttleInitialX = -18; // initial set velocity, normal -20
    public static int shuttleInitialY = -18; // initial yVelocity
    public static int shuttleStartY = 490;
    public static int shuttleXPos = 200;
    public static int shuttleYPos = shuttleStartY;
    public static int shuttleFrameController = 0;
    public static double degRotate = 0;
    public static int shuttleXVelocity = shuttleInitialX;
    public static int shuttleYVelocity = shuttleInitialY;
    public static int shuttleGravity = 1; // normal 2
    public static int shuttleDimensions = 30; // height and width of the birdie
    public static boolean direction = true; // true is to the right, false is to the left

    // collision detection variables
    public static int racquetTopXPosition;
    public static int racquetTopYPosition;
    public static int racquetBottomXPosition;
    public static int racquetBottomYPosition;
    public static int swingTimer = 0; // counts the time since it was swung
    public static boolean isHit = false; // whether the shuttle has already been hit in the swing
    public static boolean hitNet = false; // whether the shittle has hit the net

    // score keeping
    public static boolean inPlay = false;
    public static int playerScore = 0;
    public static int enemyScore = 0;
    public static boolean shuttleResponsibility = true; // if true, then player, if false, then enemy

    public static Font timerFont;

    public ScoreKeeping() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
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

        scoreKeeper(g);
        characterMovement(g);
        shuttleMovement(g); // this line must stay last or everything becomes weird and begins rotation
    }

    public static void shuttleNetCollisionDetection(Graphics g) { // detects collisions between the shuttle and net,
                                                                  // still need to implement where it goes
        if (shuttleXPos >= 503 && shuttleXPos <= 519 && shuttleYPos >= 372 && shuttleYPos <= 496 && !hitNet) {
            hitNet = true;
            shuttleResponsibility = !shuttleResponsibility; // fault of whoever hit it
            scoreCheck(g);
        }
    }

    public static void jump() { // to make it jump
        if (isJumping) {
            yPos += yVelocity;
            yVelocity += gravity;
            if (yPos >= 260) {
                isJumping = false;
                yPos = 260;
                yVelocity = -24;
            }
        }
    }

    public static void scoreCheck(Graphics g) { // who should get the point
        inPlay = false;
        direction = true; // to make sure if in score it ends up in the hands of the player
        if (shuttleResponsibility) {
            enemyScore++; // if the player is responsible for the shuttle, enemy wins point
        } else {
            playerScore++;
        }
        start = System.currentTimeMillis();
    }

    public static void scoreKeeper(Graphics g) { // the stats for the scoreboard
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate

        Font timerFont = new Font("timerFont", Font.BOLD, 50); // to draw out the scoreboard
        g.setFont(timerFont);
        g2d.setColor(Color.RED);
        g.drawString(playerScore + " - " + enemyScore, 455, 73);

    }

    public static void serve(Graphics g) { // determines and animates which player serves
        if (isSwinging && !inPlay) { // serving animation for player 
            inPlay = true; // makes sure the shuttle is in play
            shuttleXPos = xPos + 45; // so it starts at the swing of the racquet
            shuttleYPos = yPos + 45;
            // isHit = true; //so serve swing does not hit
        } else if(!inPlay){
            if (xPos > 235) {
                xPos = 235; // prevents it from going past service line
            }
            if (xPos < 10) {
                xPos = 10; // prevents it from going past doubles serve line
            }
            g.drawImage(birdie, xPos + 93, yPos + 133, null); // drawing it in the hand of the character
        }
    }

    public static void racquetCollisionDetection(Graphics g) { // detects collision between raquet and shuttle
        racquetTopXPosition = -(int) (Math.cos(Math.toRadians(360 * swingTimer / 32 + 45)) * Math.sqrt(17000)) + 90
                + xPos; // calculating the coordinates of the racquet top
        racquetTopYPosition = -(int) (Math.sin(Math.toRadians(360 * swingTimer / 32 + 45)) * Math.sqrt(17000)) + 155
                + yPos;
        racquetBottomXPosition = -(int) (Math.cos(Math.toRadians(360 * swingTimer / 32 + 45)) * Math.sqrt(5000)) + 90
                + xPos;
        racquetBottomYPosition = -(int) (Math.sin(Math.toRadians(360 * swingTimer / 32 + 45)) * Math.sqrt(5000)) + 165
                + yPos;

        // to determine if the shuttle and racquet collide
        if (Math.abs(racquetBottomXPosition - racquetTopXPosition)
                + 45 >= Math.abs(racquetBottomXPosition - shuttleXPos)
                        + Math.abs(racquetTopXPosition - shuttleXPos)
                && !isHit && !shuttleResponsibility) {
            // adding a number inside this statement increases the margin of error
            // shuttle responsibility is false to prevent double hits
            if (Math.abs(racquetBottomYPosition - racquetTopYPosition) + 45 >= Math
                    .abs(racquetBottomYPosition - shuttleYPos) + Math.abs(racquetTopYPosition - shuttleYPos)) {
                isHit = true; // it is hit
                direction = true; // turns it the other way around
                int distance = (int) Math.sqrt(
                        Math.pow(racquetTopXPosition - shuttleXPos, 2)
                                + Math.pow(racquetTopYPosition - shuttleYPos, 2));
                shuttleYVelocity = -22 + 2 * (int) ((distance) / 8); // set arbitrary values, denominator is the factor
                                                                     // that affects the minimum yvelocity value
                shuttleXVelocity = (int) Math.sqrt(Math.pow(shuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2)) + 2; // last
                                                                                                                   // number
                                                                                                                   // is
                                                                                                                   // arbitrary
                shuttleResponsibility = false; // opponent must hit
            }
        }

        // to determine if the racquet and net collide
        if (racquetTopXPosition >= 503 && !isHit) {
            isHit = true;
            isSwinging = false;
            swingCount = 0; // to prevent isSwinging from going back to true
            shuttleResponsibility = true; // if you hit the net it is your fault
            scoreCheck(g);
        }
    }

    public static void shuttleMovement(Graphics g) { // animating the birdie
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate
        serve(g);
        if (inPlay) {
            g2d.rotate(Math.toRadians(degRotate), shuttleXPos + 25, shuttleYPos + 25);
            g.drawImage(birdie, shuttleXPos, shuttleYPos, null);
            if (shuttleFrameController == 2) {
                shuttleYPos += shuttleYVelocity;
                shuttleYVelocity += shuttleGravity;
                shuttleXPos += shuttleXVelocity;
                shuttleNetCollisionDetection(g); // checking collision with net
                if (shuttleYPos >= shuttleStartY) { // set as lower bound of birdie (flooring)
                    scoreCheck(g);
                    shuttleYPos = shuttleStartY; 
                    shuttleYVelocity = shuttleInitialY; // need initial y
                    shuttleXVelocity = shuttleInitialX;
                }
                shuttleFrameController = 0;
            }
            shuttleFrameController++;

            if (shuttleXPos >= 920) { // if it hits sides
                direction = false; // changes the direction
                shuttleXVelocity = -Math.abs(shuttleXVelocity);
                shuttleResponsibility = false;
            }
            if (shuttleXPos <= 30) { // if it hits sides
                direction = true; // changes the direction
                shuttleXVelocity = Math.abs(shuttleXVelocity);
                shuttleResponsibility = true;// after hitting the wall the respon. goes to the other player
            }
            if (direction) {
                shuttleXVelocity = Math.abs(shuttleXVelocity);
                degRotate = -(90 * shuttleYVelocity / shuttleInitialY) % 360; // -(90*shuttleYVelocity/shuttleInitialY)
                                                                              // % 360 for L to R,
                // (90*shuttleYVelocity/shuttleInitialY- 180) % 360 is R to L
            } else {
                degRotate = (90 * shuttleYVelocity / shuttleInitialY - 180) % 360;
                shuttleXVelocity = -Math.abs(shuttleXVelocity);
            }
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
            if (swingCount == 0) {
                swingCount++; // remove delay
            }
            frameController++;
            swingTimer++; // counts each frame (remove)
            if (frameController >= 3) { // controls frame rate
                frameController = 0;
                spriteNumber = (spriteNumber + 1) % 6;
                swingCount++;
            }
            if (swingCount == 7) { // runs until the swing hits sprite 4;
                swingCount = 0;
                swingTimer = 0; // (remove)
                isHit = false;
            }
            if (swingTimer < 16) { // doesn't swing too far, these lines must be below swingCount resetter
                racquetCollisionDetection(g); // detecting collisions
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

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Player");
        ScoreKeeping panel = new ScoreKeeping();
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

            // timerFont = Font.createFont(Font.TRUETYPE_FONT, new
            // File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS
            // Game\\TimerFont.ttf")).deriveFont(12f);
            // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // ge.registerFont(timerFont);

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
