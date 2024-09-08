import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.*;

public class EnemyControl extends JPanel implements KeyListener, Runnable {

    // sprites
    public static BufferedImage[] hittingAnimation = new BufferedImage[6];
    public static BufferedImage[] enemyHittingAnimation = new BufferedImage[6];
    public static BufferedImage[] walkingAnimation = new BufferedImage[4];
    public static BufferedImage[] enemyWalkingAnimation = new BufferedImage[4];
    public static BufferedImage court;

    // shared variables
    public static int yVelocity = -20; // same for both characters, initial jump velocity
    public static int gravity = 2; // for player

    // player variables
    public static int playerXPos = 200;
    public static int playerYPos = 260;
    public static boolean wPressed = false;
    public static boolean aPressed = false;
    public static boolean dPressed = false;
    public static boolean spacePressed = false;
    public static int playerFrameController = 0;
    public static int playerSpriteHittingNumber = 0;
    public static int playerSpriteWalkingNumber = 0;
    public static int playerYVelocity = yVelocity;
    public static boolean isPlayerWalking = false;
    public static boolean isPlayerSwinging = false;
    public static boolean isPlayerJumping = false;
    public static int playerSwingCount = 0; // maintains full swing

    // enemy variables
    public static int enemyXPos = 600;
    public static int enemyYPos = 260;
    public static boolean upPressed = false;
    public static boolean leftPressed = false;
    public static boolean rightPressed = false;
    public static boolean downPressed = false;
    public static int enemyFrameController = 0;
    public static int enemySpriteHittingNumber = 0;
    public static int enemySpriteWalkingNumber = 0;
    public static int enemyYVelocity = yVelocity;
    public static boolean isEnemyWalking = false;
    public static boolean isEnemySwinging = false;
    public static boolean isEnemyJumping = false;
    public static int enemySwingCount = 0;

    // shuttle variables
    public static BufferedImage birdie;
    public static int shuttleSumXY = -25; // for the relationship between x and y
    public static int shuttleInitialX = -18; // initial set velocity, normal -20
    public static int shuttleInitialY = -20; // initial yVelocity
    public static int shuttleStartY = 490;
    public static int shuttleXPos = 200;
    public static int shuttleYPos = shuttleStartY;
    public static int shuttleplayerFrameController = 0;
    public static double degRotate = 0;
    public static int shuttleXVelocity = shuttleInitialX;
    public static int shuttleYVelocity = shuttleInitialY;
    public static int shuttleGravity = 1; // normal 2
    public static boolean direction = true; // true is to the right, false is to the left

    // collision detection variables
    public static int playerRacquetTopXPosition;
    public static int playerRacquetTopYPosition;
    public static int playerRacquetBottomXPosition;
    public static int playerRacquetBottomYPosition;
    public static int playerSwingTimer = 0; // counts the time since it was swung
    public static boolean playerIsHit = false; // whether the shuttle has already been hit in the swing

    public static int enemyRacquetTopXPosition;
    public static int enemyRacquetTopYPosition;
    public static int enemyRacquetBottomXPosition;
    public static int enemyRacquetBottomYPosition;
    public static int enemySwingTimer = 0;
    public static boolean enemyIsHit = false;

    // score keeping
    public static boolean inPlay = false;
    public static int playerScore = 0;
    public static int enemyScore = 0;
    public static boolean shuttleResponsibility = false; // if true, then enemy point, if false, then player point
    public static boolean hitNet = false; // prevents multicounts

    public static BufferedImage square;

    // Built in Methods
    public EnemyControl() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        setFocusable(true);
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(court, 0, 0, null);

        scoreKeeper(g);
        playerMovement(g);
        enemyMovement(g);
        shuttleMovement(g); // this line must stay last or everything becomes weird and begins rotation
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Player");
        EnemyControl panel = new EnemyControl();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

            enemyHittingAnimation[0] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation01.png"));
            enemyHittingAnimation[1] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation02.png"));
            enemyHittingAnimation[2] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation03.png"));
            enemyHittingAnimation[3] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation04.png"));
            enemyHittingAnimation[4] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation05.png"));
            enemyHittingAnimation[5] = ImageIO
                    .read(new File("C:\\Users\\emily\\Downloads\\MSS CS\\CS Game\\enemySwingAnimation06.png"));

            walkingAnimation[0] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation01.png"));
            walkingAnimation[1] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation02.png"));
            walkingAnimation[2] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation03.png"));
            walkingAnimation[3] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\moveAnimation04.png"));

            enemyWalkingAnimation[0] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemyMoveAnimation01.png"));
            enemyWalkingAnimation[1] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemyMoveAnimation02.png"));
            enemyWalkingAnimation[2] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemyMoveAnimation03.png"));
            enemyWalkingAnimation[3] = ImageIO
                    .read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\enemyMoveAnimation04.png"));

            birdie = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\birdie.png"));
            square = ImageIO.read(new File("C:\\Users\\Emily\\Downloads\\MSS CS\\CS Game\\square.png"));

        } catch (Exception e) {
            System.out.println("Something wrong with image");
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

    // Movement Methods
    public static void jump() { // to make it jump
        if (wPressed) { // to remove issue (if both players hold jump)
            isPlayerJumping = true;
        }
        if (upPressed) {
            isEnemyJumping = true;
        }
        if (isPlayerJumping) {
            playerYPos += playerYVelocity;
            playerYVelocity += gravity;
            if (playerYPos >= 260) {
                isPlayerJumping = false;
                playerYPos = 260;
                playerYVelocity = yVelocity;
            }
        }
        if (isEnemyJumping) {
            enemyYPos += enemyYVelocity;
            enemyYVelocity += gravity;
            if (enemyYPos >= 260) {
                isEnemyJumping = false;
                enemyYPos = 260;
                enemyYVelocity = yVelocity;
            }
        }
    }

    public static void shuttleMovement(Graphics g) { // animating the birdie
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate
        serve(g);
        if (inPlay) {
            g2d.rotate(Math.toRadians(degRotate), shuttleXPos + 25, shuttleYPos + 25);
            g.drawImage(birdie, shuttleXPos, shuttleYPos, null);
            if (shuttleplayerFrameController == 2) {
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
                shuttleplayerFrameController = 0;
            }
            shuttleplayerFrameController++;

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

    public static void playerMovement(Graphics g) { // animating the swing
        if (aPressed && playerXPos > 0) { // moving the sprite
            playerXPos -= 8;
        }
        if (dPressed && playerXPos < 380) {
            playerXPos += 8;
        }
        jump();
        if (spacePressed || playerSwingCount != 0) { // checking if it should be swinging
            isPlayerSwinging = true;
        } else {
            isPlayerSwinging = false;
            playerSpriteHittingNumber = 0;
        }
        if (isPlayerSwinging) {
            if (playerSwingCount == 0) {
                playerSwingCount++; // remove delay
            }
            playerFrameController++;
            playerSwingTimer++; // counts each frame (remove)
            if (playerFrameController >= 3) { // controls frame rate
                playerFrameController = 0;
                playerSpriteHittingNumber = (playerSpriteHittingNumber + 1) % 6;
                playerSwingCount++;
            }
            if (playerSwingCount == 7) { // runs until the swing hits sprite 4;
                playerSwingCount = 0;
                playerSwingTimer = 0; // (remove)
                playerIsHit = false;
            }
            if (playerSwingTimer < 10) { // doesn't swing too far, these lines must be below playerSwingCount resetter
                playerRacquetCollisionDetection(g); // detecting collisions
            }
            g.drawImage(hittingAnimation[playerSpriteHittingNumber], playerXPos, playerYPos, null);

        } else if (isPlayerWalking) {
            g.drawImage(walkingAnimation[playerSpriteWalkingNumber], playerXPos, playerYPos, null); // walking animation
                                                                                                    // when
            // a&d keys
            playerFrameController++;
            if (playerFrameController >= 6) {
                playerFrameController = 0;
                playerSpriteWalkingNumber = (playerSpriteWalkingNumber + 1) % 4;
            }
        } else {
            g.drawImage(walkingAnimation[0], playerXPos, playerYPos, null);
        }
    }

    public static void enemyMovement(Graphics g) {
        if (leftPressed && enemyXPos > 420) { // moving the sprite
            enemyXPos -= 8;
        }
        if (rightPressed && enemyXPos < 775) {
            enemyXPos += 8;
        }
        if (downPressed || enemySwingCount != 0) { // checking if it should be swinging
            isEnemySwinging = true;
        } else {
            isEnemySwinging = false;
            enemySpriteHittingNumber = 0;
        }
        if (isEnemySwinging) {
            if (enemySwingCount == 0) {
                enemySwingCount++; // remove delay
            }
            enemyFrameController++;
            enemySwingTimer++; // counts each frame (remove)
            if (enemyFrameController >= 3) { // controls frame rate
                enemyFrameController = 0;
                enemySpriteHittingNumber = (enemySpriteHittingNumber + 1) % 6;
                enemySwingCount++;
            }
            if (enemySwingCount == 7) { // runs until the swing hits sprite 4;
                enemySwingCount = 0;
                enemySwingTimer = 0; // (remove)
                enemyIsHit = false;
            }
            if (enemySwingTimer < 10) { // doesn't swing too far, these lines must be below enemySwingCount resetter
                enemyRacquetCollisionDetection(g); // detecting collisions
            }

            g.drawImage(enemyHittingAnimation[enemySpriteHittingNumber], enemyXPos, enemyYPos, null);
        } else if (isEnemyWalking) {
            g.drawImage(enemyWalkingAnimation[enemySpriteWalkingNumber], enemyXPos, enemyYPos, null); // walking
                                                                                                      // animation
                                                                                                      // when
            // a&d keys
            enemyFrameController++;
            if (enemyFrameController >= 6) {
                enemyFrameController = 0;
                enemySpriteWalkingNumber = (enemySpriteWalkingNumber + 1) % 4;
            }
        } else {
            g.drawImage(enemyWalkingAnimation[0], enemyXPos, enemyYPos, null);
        }
    }

    public static void serve(Graphics g) { // determines and animates which player serves
        if (!inPlay) {
            if (playerXPos > 235) {
                playerXPos = 235; // prevents it from going past service line
            }
            if (playerXPos < 10) {
                playerXPos = 10; // prevents it from going past doubles serve line
            }
            if (enemyXPos > 773) {
                enemyXPos = 773; // prevents it from going past service line
            }
            if (enemyXPos < 565) {
                enemyXPos = 565; // prevents it from going past doubles serve line
            }
            if (!shuttleResponsibility) { // players point
                if (isPlayerSwinging) {
                    direction = true; // to make sure if in score it ends up in the hands of the player
                    inPlay = true; // makes sure the shuttle is in play
                    shuttleXPos = playerXPos + 45; // so it starts at the swing of the playerRacquet
                    shuttleYPos = playerYPos + 45;
                    playerIsHit = true; // so serve swing does not hit
                } else {
                    g.drawImage(birdie, playerXPos + 93, playerYPos + 133, null); // drawing it in the hand of the
                                                                                  // character // character
                }
            } else { // enemy point
                if (isEnemySwinging) {
                    direction = false; // to make sure if in score it ends up in the hands of the player
                    inPlay = true; // makes sure the shuttle is in play
                    shuttleXPos = enemyXPos + 45; // so it starts at the swing of the playerRacquet
                    shuttleYPos = enemyYPos + 45;
                    enemyIsHit = true; // so serve swing does not hit
                } else {
                    g.drawImage(birdie, enemyXPos + 93, enemyYPos + 133, null);
                }
            }
        }
    }

    // Collision Detection Methods

    public static void shuttleNetCollisionDetection(Graphics g) { // detects collisions between the shuttle and net
        if ((shuttleXPos >= 503 && shuttleXPos <= 519 && shuttleYPos >= 372 && shuttleYPos <= 600)
                || (shuttleXPos >= 473 && shuttleXPos <= 499 && shuttleYPos >= 372 && shuttleYPos <= 600) && !hitNet) {
            hitNet = true;
            shuttleResponsibility = !shuttleResponsibility; // fault of whoever hit it
            scoreCheck(g);
        }
    }

    public static void playerRacquetCollisionDetection(Graphics g) { // detects collision between raquet and shuttle
        playerRacquetTopXPosition = -(int) (Math.cos(Math.toRadians(360 * playerSwingTimer / 25 + 25))
                * Math.sqrt(15000))
                + 90 + playerXPos; // calculating the coordinates of the playerRacquet top
        playerRacquetTopYPosition = -(int) (Math.sin(Math.toRadians(360 * playerSwingTimer / 25 + 25))
                * Math.sqrt(15000))
                + 145
                + playerYPos;
        playerRacquetBottomXPosition = -(int) (Math.cos(Math.toRadians(360 * playerSwingTimer / 26 + 35))
                * Math.sqrt(5000))
                + 90
                + playerXPos;
        playerRacquetBottomYPosition = -(int) (Math.sin(Math.toRadians(360 * playerSwingTimer / 26 + 35))
                * Math.sqrt(5000))
                + 160
                + playerYPos;

        // to determine if the shuttle and playerRacquet collide
        if (Math.abs(playerRacquetBottomXPosition - playerRacquetTopXPosition)
                + 30 >= Math.abs(playerRacquetBottomXPosition - shuttleXPos)
                        + Math.abs(playerRacquetTopXPosition - shuttleXPos)
                && !playerIsHit && shuttleResponsibility) {
            // can read !isShuttleResponsibility, adding a number inside this statement
            // increases the margin of error, shuttle responsibility is false to prevent
            // double hits
            if (Math.abs(playerRacquetBottomYPosition - playerRacquetTopYPosition) + 30 >= Math
                    .abs(playerRacquetBottomYPosition - shuttleYPos)
                    + Math.abs(playerRacquetTopYPosition - shuttleYPos)) {

                if (isPlayerJumping) {// smash
                    shuttleYVelocity = 4 - (int) (11 * playerSwingCount) / 2;
                    // set arbitrary values, denominator is the factor that affects the minimum
                    // yvelocity value
                    shuttleXVelocity = (int) Math.sqrt(Math.pow(shuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2)) + 4;

                } else { // normal hit
                    shuttleYVelocity = -26 + (int) (7 * playerSwingCount) / 2;
                    // set arbitrary values, denominator is the factor that affects the minimum
                    // yvelocity value
                    shuttleXVelocity = (int) Math.sqrt(Math.pow(shuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2)) + 2;
                }

                playerIsHit = true; // it is hit
                direction = true; // turns it the other way around
                // last number is arbitrary, only works at the instance it is changed
                shuttleResponsibility = false; // opponent must hit

                // int distance = (int) Math.sqrt(
                // Math.pow(playerRacquetTopXPosition - shuttleXPos, 2)
                // + Math.pow(playerRacquetTopYPosition - shuttleYPos, 2));
                // shuttleYVelocity = -24 + 2 * (int) ((distance) / 10);
                // set arbitrary values, denominator is the factor that affects the minimum
                // yvelocity value

            }
        }
    }

    public static void enemyRacquetCollisionDetection(Graphics g) {
        enemyRacquetTopXPosition = (int) (Math.cos(-Math.toRadians(360 * enemySwingTimer / 25 + 25))
                * Math.sqrt(15000))
                + 125
                + enemyXPos; // calculating the coordinates of the enemyRacquet top
        enemyRacquetTopYPosition = (int) (Math.sin(-Math.toRadians(360 * enemySwingTimer / 25 + 25))
                * Math.sqrt(15000))
                + 150
                + enemyYPos;
        enemyRacquetBottomXPosition = (int) (Math.cos(-Math.toRadians(360 * enemySwingTimer / 26 + 35))
                * Math.sqrt(5000))
                + 130
                + enemyXPos;
        enemyRacquetBottomYPosition = (int) (Math.sin(-Math.toRadians(360 * enemySwingTimer / 26 + 35))
                * Math.sqrt(5000))
                + 160
                + enemyYPos;
        if (Math.abs(enemyRacquetBottomXPosition - enemyRacquetTopXPosition)
                + 30 >= Math.abs(enemyRacquetBottomXPosition - shuttleXPos)
                        + Math.abs(enemyRacquetTopXPosition - shuttleXPos)
                && !enemyIsHit && !shuttleResponsibility) {
            // increases the margin of error, shuttle responsibility is false to prevent
            // double hits
            if (Math.abs(enemyRacquetBottomYPosition - enemyRacquetTopYPosition) + 30 >= Math
                    .abs(enemyRacquetBottomYPosition - shuttleYPos)
                    + Math.abs(enemyRacquetTopYPosition - shuttleYPos) && !enemyIsHit) {

                if (enemyRacquetTopXPosition <= 517) { // maybe this line is problematic
                    enemyIsHit = true;
                    isEnemySwinging = false;
                    enemySwingCount = 0; // to prevent isPlayerSwinging from going back to true
                    shuttleResponsibility = false; // if you hit the net it is your fault
                    scoreCheck(g);
                } else {
                     if (isEnemyJumping) {// smash
                        shuttleYVelocity = 4 - (int) (11 * enemySwingCount) / 2;
                        // set arbitrary values, denominator is the factor that affects the minimum
                        // yvelocity value
                        shuttleXVelocity = -(int) Math.sqrt(Math.pow(shuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2)) - 4;

                    } else { //normal hit
                        shuttleYVelocity = -26 + (int) (7 * enemySwingCount) / 2;
                        // set arbitrary values, denominator is the factor that affects the minimum
                        // yvelocity value
                         shuttleXVelocity = -(int) Math.sqrt(Math.pow(shuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2)) - 2;
                    }

                    enemyIsHit = true; // it is hit
                    direction = false; // turns it the other way around
                    // last number is arbitrary, only works at the instance it is changed
                    shuttleResponsibility = true; // opponent must hit
                }
            }
        }
    }

    // Score Keeping Methods
    public static void scoreKeeper(Graphics g) { // the stats for the scoreboard
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate
        Font timerFont = new Font("timerFont", Font.BOLD, 50); // to draw out the scoreboard
        g.setFont(timerFont);
        g2d.setColor(Color.RED);
        g.drawString(playerScore + " - " + enemyScore, 455, 73);
    }

    public static void scoreCheck(Graphics g) { // who should get the point
        inPlay = false;
        if (!shuttleResponsibility) {
            playerScore++; // if the player is responsible for the shuttle, enemy wins point
        } else {
            enemyScore++;
        }
        reset(g);
    }

    public static void reset(Graphics g) {
        shuttleXVelocity = shuttleInitialX;
        shuttleYVelocity = shuttleInitialY;
    }

    // Key Listener
    public void keyPressed(KeyEvent e) {
        // for player
        if (e.getKeyChar() == 'w') { // jumping
            wPressed = true;
            if (!isPlayerJumping)
                isPlayerJumping = true;
        }
        if (e.getKeyChar() == 'a') { // backwards
            aPressed = true;
            isPlayerWalking = true;
        }
        if (e.getKeyChar() == 's') { // swing
            spacePressed = true;
        }
        if (e.getKeyChar() == 'd') { // forward
            dPressed = true;
            isPlayerWalking = true;
        }

        // for enemy
        if (e.getKeyCode() == KeyEvent.VK_UP) { // jump!
            upPressed = true;
            if (!isEnemyJumping)
                isEnemyJumping = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { // backwards
            leftPressed = true;
            isEnemyWalking = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) { // swing
            downPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // forward
            rightPressed = true;
            isEnemyWalking = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            wPressed = false;
        }
        if (e.getKeyChar() == 'a') {
            aPressed = false;
            isPlayerWalking = false;
        }
        if (e.getKeyChar() == 's') { // swing
            spacePressed = false;
        }
        if (e.getKeyChar() == 'd') {
            dPressed = false;
            isPlayerWalking = false;
        }

        // for enemy
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { // backwards
            leftPressed = false;
            isEnemyWalking = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) { // swing
            downPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // forward
            rightPressed = false;
            isEnemyWalking = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}
