import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

public class CSBadmintonGame extends JPanel implements Runnable, KeyListener, MouseListener {

    // variables
    public static BufferedImage[] stateImage = new BufferedImage[12];
    public static BufferedImage[] levelImage = new BufferedImage[6];
    public static BufferedImage[] hittingAnimation = new BufferedImage[6];
    public static BufferedImage[] enemyHittingAnimation = new BufferedImage[6];
    public static BufferedImage[] walkingAnimation = new BufferedImage[4];
    public static BufferedImage[] enemyWalkingAnimation = new BufferedImage[4];
    public static BufferedImage[] bandanas = new BufferedImage[6];

    // other sprites
    public static BufferedImage lock;
    public static BufferedImage podium;
    public static BufferedImage stickFigure;
    public static BufferedImage trophy;
    public static BufferedImage crown;
    public static BufferedImage player1win;
    public static BufferedImage player2win;
    public static BufferedImage championwin;

    // game state variables
    public static int gameState = 0; // which game state number
    public static int levelState = 0; // which game state number
    public static int levelUnlocked = 0; // which levels are unlocked
    public static int playerCount = 1; // 1 is single, 2 is double
    public static int winner = 0; // which player wins

    // shared variables
    public static int yVelocity = -20; // same for both characters, initial jump velocity
    public static int gravity = 2; // for player

    // player variables
    public static int playerXPos = 200; // coordinates of player
    public static int playerYPos = 260;
    public static boolean wPressed = false; // key pressed variables
    public static boolean aPressed = false;
    public static boolean dPressed = false;
    public static boolean spacePressed = false;
    public static int playerFrameController = 0; // to limit animation speed
    public static int playerSpriteHittingNumber = 0; // sprite animation
    public static int playerSpriteWalkingNumber = 0;
    public static int playerYVelocity = yVelocity;
    public static boolean isPlayerWalking = false; // is the player swinging
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
    public static int singleShuttleSumXY = -28;
    public static int doubleShuttleSumXY = -20 - (int)2.5 * levelState; // for the relationship between x and y
    public static int shuttleInitialX = 18; // initial set velocity, normal 20
    public static int shuttleInitialY = -20; // initial yVelocity
    public static int shuttleStartY = 530;
    public static int shuttleXPos = 200;
    public static int shuttleYPos = shuttleStartY;
    public static int shuttleplayerFrameController = 0;
    public static double degRotate = 0; // rotation of the birdie (for G2D)
    public static int shuttleXVelocity = shuttleInitialX;
    public static int shuttleYVelocity = shuttleInitialY;
    public static int shuttleGravity = 1; // normal 2
    public static boolean direction = true; // true is to the right, false is to the left
    public static boolean isBounce = false;

    // collision detection variables
    public static int playerRacquetTopXPosition; // coordinates of points of racquet
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
    public static boolean inPlay = false; // if the bird is in play
    public static int playerScore = 0; // counting score
    public static int enemyScore = 0;
    public static boolean shuttleResponsibility = false; // if true, then enemy point, if false, then player point
    public static boolean hitNet = false; // prevents multicounts

    // bot calculation variables
    public static double airTime = 0; // how much time before it reaches y2
    public static int hitYPos = 0; // what y value it was hit
    public static int targetYPos = 356; // what y value to try to hit it
    public static int targetXPos; // where it will land
    public static int hitYVelocity = shuttleInitialY; // what was the velocity when it was hit
    public static int hitXVelocity = shuttleInitialX; // because flipping directions will mess it up
    public static int hitXPos = 0; // what xPosition it was hit
    public static boolean shuttleAhead = false;
    public static boolean enemyIsServe = false;
    public static long start; // starting time
    public static long finish; // ending time

    // transition variables
    public static int podiumYPos = 450;
    public static int winFrameCounter = 0; // counts frames
    public static int podiumYMovement = 0;
    public static boolean isFinalStage = false; // if rematching in gamestate 3

    // game stats
    public static int[] highScores = new int[3]; // for overall high score
    public static int[] currentHighScores = new int[3]; // for current game high score
    public static int fastestSmash = 0; // fastest smash
    public static int rallyLength = 0;
    public static int longestRally = 0; // longest rally length
    public static double timeToBeat = 0; // how long does it take to beat bot 7

    // other variables
    public static String filePath = "Pictures/"; // path
    // if only relative path is necessary, remove this :)

    public CSBadmintonGame() { // change JPanel Settings
        setPreferredSize(new Dimension(1000, 600));
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        Thread t = new Thread(this); // initiate timer
        t.start();
    }

    public void paintComponent(Graphics g) { // the actual game functioning
        super.paintComponent(g);
        g.drawImage(stateImage[gameState], 0, 0, null);

        // telling what the game to do in the gamestates
        if (gameState == 1) {
            levelUnlock(g);
        } else if (gameState == 2 || gameState == 3) {
            gameplay(g);
        } else if (gameState == 4) {
            winScreenAnimation(g);
        } else if (gameState == 11) {
            highScoreBoard(g);
        }

    }

    public static void main(String[] args) throws IOException { //mostly importations
        JFrame frame = new JFrame("CSBadmintonGame");
        CSBadmintonGame panel = new CSBadmintonGame();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        levelStateFileCollector(); // to get progress
        highScoreCollector(); // to get high scores

        try { // importation of images
              // game states
            stateImage[0] = ImageIO.read(new File(filePath + "gamestate1.png"));
            stateImage[1] = ImageIO.read(new File(filePath + "gamestate2.png"));
            stateImage[2] = ImageIO.read(new File(filePath + "gamestate3.png"));
            stateImage[3] = ImageIO.read(new File(filePath + "gamestate4.png"));
            stateImage[4] = ImageIO.read(new File(filePath + "gamestate5.png"));
            stateImage[5] = ImageIO.read(new File(filePath + "gamestate6.png"));
            stateImage[7] = ImageIO.read(new File(filePath + "gamestate8.png"));
            stateImage[8] = ImageIO.read(new File(filePath + "gamestate9.png"));
            stateImage[9] = ImageIO.read(new File(filePath + "gamestate10.png"));
            stateImage[10] = ImageIO.read(new File(filePath + "gamestate11.png"));
            stateImage[11] = ImageIO.read(new File(filePath + "gamestate12.png"));

            // hitting animations
            hittingAnimation[0] = ImageIO.read(new File(filePath + "swingAnimation01.png"));
            hittingAnimation[1] = ImageIO.read(new File(filePath + "swingAnimation02.png"));
            hittingAnimation[2] = ImageIO.read(new File(filePath + "swingAnimation03.png"));
            hittingAnimation[3] = ImageIO.read(new File(filePath + "swingAnimation04.png"));
            hittingAnimation[4] = ImageIO.read(new File(filePath + "swingAnimation05.png"));
            hittingAnimation[5] = ImageIO.read(new File(filePath + "swingAnimation06.png"));

            // for enemy
            enemyHittingAnimation[0] = ImageIO.read(new File(filePath + "enemySwingAnimation01.png"));
            enemyHittingAnimation[1] = ImageIO.read(new File(filePath + "enemySwingAnimation02.png"));
            enemyHittingAnimation[2] = ImageIO.read(new File(filePath + "enemySwingAnimation03.png"));
            enemyHittingAnimation[3] = ImageIO.read(new File(filePath + "enemySwingAnimation04.png"));
            enemyHittingAnimation[4] = ImageIO.read(new File(filePath + "enemySwingAnimation05.png"));
            enemyHittingAnimation[5] = ImageIO.read(new File(filePath + "enemySwingAnimation06.png"));

            // walking animations
            walkingAnimation[0] = ImageIO.read(new File(filePath + "moveAnimation01.png"));
            walkingAnimation[1] = ImageIO.read(new File(filePath + "moveAnimation02.png"));
            walkingAnimation[2] = ImageIO.read(new File(filePath + "moveAnimation03.png"));
            walkingAnimation[3] = ImageIO.read(new File(filePath + "moveAnimation04.png"));

            // for enemy
            enemyWalkingAnimation[0] = ImageIO.read(new File(filePath + "enemyMoveAnimation01.png"));
            enemyWalkingAnimation[1] = ImageIO.read(new File(filePath + "enemyMoveAnimation02.png"));
            enemyWalkingAnimation[2] = ImageIO.read(new File(filePath + "enemyMoveAnimation03.png"));
            enemyWalkingAnimation[3] = ImageIO.read(new File(filePath + "enemyMoveAnimation04.png"));

            // other sprites
            birdie = ImageIO.read(new File(filePath + "birdie.png"));
            lock = ImageIO.read(new File(filePath + "transparentlock.png"));
            podium = ImageIO.read(new File(filePath + "podium.png"));
            stickFigure = ImageIO.read(new File(filePath + "standingStickFigure.png"));
            trophy = ImageIO.read(new File(filePath + "trophy.png"));
            crown = ImageIO.read(new File(filePath + "crown.png"));
            player1win = ImageIO.read(new File(filePath + "player1win.png"));
            player2win = ImageIO.read(new File(filePath + "player2win.png"));
            championwin = ImageIO.read(new File(filePath + "worldchampion.png"));

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

    // GAME METHODS
    public static void levelUnlock(Graphics g) {
        if (levelUnlocked < 5) { // if locks are there
            g.drawImage(lock, 738, 400, null);
        }
        if (levelUnlocked < 4) {
            g.drawImage(lock, 438, 400, null);
        }
        if (levelUnlocked < 3) {
            g.drawImage(lock, 138, 400, null);
        }
        if (levelUnlocked < 2) {
            g.drawImage(lock, 738, 195, null);
        }
        if (levelUnlocked < 1) {
            g.drawImage(lock, 438, 195, null);
        }
    }

    public static void gameplay(Graphics g) {

        // game functions
        scoreKeeper(g);
        playerMovement(g);
        if (playerCount == 1) {
            singleEnemyMovement(g);
        } else {
            doubleEnemyMovement(g);
        }
        shuttleMovement(g); // this line must stay last or everything becomes weird and begins rotation
    }

    // transitions methods
    public static void winScreenAnimation(Graphics g) {
        // game resetting
        shuttleResponsibility = false;

        // podium animation
        winFrameCounter++;
        if (winFrameCounter < 150) { // moving it up
            podiumYPos -= winFrameCounter / 50;
        }

        // win text
        if (playerCount == 1) { // single player win screen
            g.drawImage(championwin, 400, 120, null);
        } else if (playerCount == 2) { // for 2 player
            if (winner == 1) {
                g.drawImage(player1win, 400, 100, null);
            } else if (winner == 2) {
                g.drawImage(player2win, 400, 100, null);
            }
        }

        // other sprites
        g.drawImage(podium, 35, podiumYPos, null);
        g.drawImage(stickFigure, 180, podiumYPos - 170, null);
        g.drawImage(trophy, 245, podiumYPos - 110, null);
        g.drawImage(crown, 195, podiumYPos - 200, null);

    }

    public static void highScoreBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; // only type of graphics are used to rotate
        Font timerFont = new Font("timerFont", Font.BOLD, 40); // to draw out the scoreboard
        g.setFont(timerFont);
        g2d.setColor(Color.BLACK);

        // fastest smash
        g.drawString(highScores[0] + " km/h", 465, 310);
        g.drawString(currentHighScores[0] + " km/h", 715, 310);

        // longest rally
        g.drawString(highScores[1] + "", 465, 415);
        g.drawString(currentHighScores[1] + "", 715, 415);

        // total hits
        g.drawString(highScores[2] + "", 465, 520);
        g.drawString(currentHighScores[2] + "", 715, 520);
    }

    public static void totalReset() { // after game ends
        // all variables must be reset to prevent weird glitches
        isPlayerWalking = false;
        isPlayerJumping = false;
        isPlayerSwinging = false;
        playerScore = 0;

        isEnemyWalking = false;
        isEnemyJumping = false;
        isEnemySwinging = false;
        enemyScore = 0;

        shuttleResponsibility = false;
        hitNet = false;
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
            // rotation the birdie with math
            g2d.rotate(Math.toRadians(degRotate), shuttleXPos + 25, shuttleYPos + 25);
            g.drawImage(birdie, shuttleXPos, shuttleYPos, null);

            // moving the birdie in a parabola
            if (shuttleplayerFrameController == 2) {
                shuttleYPos += shuttleYVelocity;
                shuttleYVelocity += shuttleGravity;
                shuttleXPos += shuttleXVelocity;
                shuttleplayerFrameController = 0;
            }
            shuttleplayerFrameController++;

            hitGround(g);
            shuttleNetCollisionDetection(g); // checking collision with net

            if (shuttleXPos >= 920) { // if it hits sides
                direction = false; // changes the direction
                isBounce = true;
            }
            if (shuttleXPos <= 30) { // if it hits sides
                direction = true; // changes the direction
                isBounce = true;
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
            playerXPos -= 6;
        }
        if (dPressed && playerXPos < 380) {
            playerXPos += 6;
        }
        jump();

        //hitting stuff
        if (spacePressed || playerSwingCount != 0) { // checking if it should be swinging
            isPlayerSwinging = true;
        } else {
            isPlayerSwinging = false; // resets swing animation
            playerSpriteHittingNumber = 0;
        }
        if (isPlayerSwinging) {
            if (playerSwingCount == 0) { // if the raquet is swinging, isSwinging remaints true
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
            if (playerSwingTimer < 9) { // doesn't swing too far, these lines must be below playerSwingCount resetter
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

    public static void singleEnemyMovement(Graphics g) { // enemy movememt code for single player

        enemyShuttleDetection(g);

        if (shuttleAhead || enemyIsServe || enemySwingCount != 0) { // checking if it should be swinging
            isEnemySwinging = true;
            shuttleAhead = false;
            enemyIsServe = false;
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
            if (enemySwingTimer < 9) { // doesn't swing too far, these lines must be below enemySwingCount resetter
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

    public static void doubleEnemyMovement(Graphics g) {
        if (leftPressed && enemyXPos > 420) { // moving the sprite
            enemyXPos -= 6;
        }
        if (rightPressed && enemyXPos < 775) {
            enemyXPos += 6;
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
            if (enemySwingTimer < 9) { // doesn't swing too far, these lines must be below enemySwingCount resetter
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

                    // for bot calculations
                    hitXPos = shuttleXPos;
                    hitYPos = shuttleYPos;
                    coordinateCalculation(g);

                    // high score
                    if (playerCount == 1) {
                        currentHighScores[2] = currentHighScores[2] + 1;
                    }

                } else {
                    g.drawImage(birdie, playerXPos + 93, playerYPos + 133, null); // drawing it in the hand of the
                                                                                  // character // character
                }
            } else { // enemy point
                if (playerCount == 1) {
                    botServe(g);
                }
                if (isEnemySwinging) {
                    direction = false; // to make sure if in score it ends up in the hands of the player
                    inPlay = true; // makes sure the shuttle is in play
                    shuttleXPos = enemyXPos + 45; // so it starts at the swing of the playerRacquet
                    shuttleYPos = enemyYPos + 45;
                    enemyIsHit = true; // so serve swing does not hit
                    shuttleAhead = false;

                    // sound
                    try {
                        playHit();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    g.drawImage(birdie, enemyXPos + 93, enemyYPos + 133, null);
                }
            }
        }
    }

    // Collision Detection Methods

    public static void hitGround(Graphics g) {

        if (shuttleYPos >= shuttleStartY) {
            if (shuttleXPos <= 477) { // which side of the thing lands on
                shuttleResponsibility = true;
            } else if (shuttleXPos >= 477) {
                shuttleResponsibility = false;
            }
            scoreCheck(g);
        }
    }

    public static void shuttleNetCollisionDetection(Graphics g) { // detects collisions between the shuttle and net
        if (shuttleXPos >= 500 && shuttleXPos <= 519 && shuttleYPos >= 340 && !hitNet) {
            if (!isBounce) {
                shuttleResponsibility = !shuttleResponsibility; // fault of whoever hit it
            }
            reset(g);
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
                + playerYPos; // to move it with the character
        playerRacquetBottomXPosition = -(int) (Math.cos(Math.toRadians(360 * playerSwingTimer / 26 + 35))
                * Math.sqrt(5000)) // just math
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
                && !playerIsHit && shuttleResponsibility && playerSwingCount != 0) {
            // can read !isShuttleResponsibility, adding a number inside this statement
            // increases the margin of error, shuttle responsibility is false to prevent
            // double hits
            if (Math.abs(playerRacquetBottomYPosition - playerRacquetTopYPosition) + 30 >= Math
                    .abs(playerRacquetBottomYPosition - shuttleYPos) // if it is in the rectangle between
                    + Math.abs(playerRacquetTopYPosition - shuttleYPos)) {

                isBounce = false;

                if (playerRacquetTopXPosition >= 503) { // if they hit the birdie in front of the net
                    playerIsHit = true;
                    isPlayerSwinging = false;
                    playerSwingCount = 0; // to prevent isPlayerSwinging from going back to true
                    shuttleResponsibility = true; // if you hit the net it is your fault
                    scoreCheck(g);
                } else {
                    if (isPlayerJumping) {// smash
                        shuttleYVelocity = 4 - (int) (11 * playerSwingCount) / 2;
                        // set arbitrary values, denominator is the factor that affects the minimum
                        // yvelocity value
                        if (playerCount == 1) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(singleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 5;
                        } else if (playerCount == 2) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(doubleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 5;
                        }

                    } else { // normal hit
                        shuttleYVelocity = -26 + (int) (7 * playerSwingCount) / 2;
                        // set arbitrary values, denominator is the factor that affects the minimum
                        // yvelocity value
                        if (playerCount == 1) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(singleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 2;
                        } else if (playerCount == 2) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(singleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 2;
                        }
                    }

                    playerIsHit = true; // it is hit
                    direction = true; // turns it the other way around
                    // last number is arbitrary, only works at the instance it is changed
                    shuttleResponsibility = false; // opponent must hit

                    // sound
                    try {
                        if (shuttleYVelocity > -8) {
                            playSmash();
                        } else {
                            playHit();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (playerCount == 1) { // to tell the bot where to move
                        hitYPos = shuttleYPos;
                        hitXPos = shuttleXPos;
                        hitYVelocity = shuttleYVelocity;
                        hitXVelocity = shuttleXVelocity;
                        coordinateCalculation(g);
                    }

                    if (playerCount == 1) { // game stats
                        rallyLength++;
                        currentHighScores[2] = currentHighScores[2] + 1;
                        if (shuttleXVelocity * 4 > currentHighScores[0]) {
                            // changing fastest smash to current
                            currentHighScores[0] = shuttleXVelocity * 4;
                        }
                    }
                }
            }
        }
    }

    public static void enemyRacquetCollisionDetection(Graphics g) { 
        // coordinates of the racquet
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

        // checking collision
        if (Math.abs(enemyRacquetBottomXPosition - enemyRacquetTopXPosition)
                + 30 >= Math.abs(enemyRacquetBottomXPosition - shuttleXPos)
                        + Math.abs(enemyRacquetTopXPosition - shuttleXPos)
                && !enemyIsHit && !shuttleResponsibility && enemySwingCount != 0) {
            // increases the margin of error, shuttle responsibility is false to prevent
            // double hits
            if (Math.abs(enemyRacquetBottomYPosition - enemyRacquetTopYPosition) + 30 >= Math
                    .abs(enemyRacquetBottomYPosition - shuttleYPos)
                    + Math.abs(enemyRacquetTopYPosition - shuttleYPos) && !enemyIsHit) {

                isBounce = false;

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

                        if (playerCount == 1) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(singleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 5;
                        } else if (playerCount == 2) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(doubleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 5;
                        }

                    } else { // normal hit
                        shuttleYVelocity = -26 + (int) (7 * enemySwingCount) / 2;
                        // set arbitrary values, denominator is the factor that affects the minimum
                        // yvelocity value
                        if (playerCount == 1) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(singleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 2;
                        } else if (playerCount == 2) {
                            shuttleXVelocity = (int) Math
                                    .sqrt(Math.pow(doubleShuttleSumXY, 2) - Math.pow(shuttleYVelocity, 2))
                                    + 2;
                        }
                    }

                    enemyIsHit = true; // it is hit
                    direction = false; // turns it the other way around
                    // last number is arbitrary, only works at the instance it is changed
                    shuttleResponsibility = true; // opponent must hit

                    // play sound
                    try {
                        if (shuttleYVelocity > -8) {
                            playSmash();
                        } else {
                            playHit();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // alternate hit mechanic
                    // int distance = (int) Math.sqrt( Math.pow(enemyRacquetTopXPosition -
                    // shuttleXPos, 2) + Math.pow(enemyRacquetTopYPosition - shuttleYPos, 2));
                    // shuttleYVelocity = -24 + 2 * (int) ((distance) / 10);
                    // set arbitrary values, denominator is the factor that affects the minimum
                    // yvelocity value

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
            start = (long) System.currentTimeMillis() / 1000;
        }
        if (playerCount == 1) {
            if (playerScore == 7 && gameState == 2) { // to change to win screen
                gameState = 7;
                if (levelUnlocked < levelState + 1) {
                    levelUnlocked = levelState + 1;
                }

                totalReset();

                try { // to input it into the file
                    levelStateFileChanger();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else if (enemyScore == 7 && gameState == 2) { // to change to lose screen
                gameState = 8;
                totalReset();
            } else if (enemyScore == 7 && gameState == 3) {
                isFinalStage = true;
                gameState = 8;
                totalReset();
            } else if (playerScore == 7 && gameState == 3) { // win animation
                totalReset();
                gameState = 4;
            }
        } else if (playerCount == 2) {
            if (playerScore == 7) {
               totalReset();
                winner = 1;
                gameState = 4;
            } else if (enemyScore == 7) {
                totalReset();
                winner = 2;
                gameState = 4;
            }
        }
        reset(g); // reset the play
    }

    public static void reset(Graphics g) {
        // resetting the birdie back to its initial stats
        shuttleXVelocity = shuttleInitialX;
        shuttleYVelocity = shuttleInitialY;
        hitYVelocity = shuttleInitialY;
        hitXVelocity = shuttleInitialX;

        hitNet = false;

        // to reset rally
        if (rallyLength > currentHighScores[1]) {
            currentHighScores[1] = rallyLength;
        }
        rallyLength = 0;
    }

    // Bot Movement Methods
    public static void coordinateCalculation(Graphics g) { // to detect where the bird will be
        if (inPlay && !shuttleResponsibility) { // if the player has hit it
            airTime = (-hitYVelocity
                    + Math.sqrt(Math.pow(hitYVelocity, 2) + 2 * shuttleGravity * (targetYPos - hitYPos)))
                    / (shuttleGravity); // to calculate how long it takes before it reaches a target y value

            targetXPos = (int) (hitXVelocity * airTime) + hitXPos + randomIntGen(200 - levelState * 40);
            // xRandomizer changes difficulty

            if (targetXPos >= 920) {
                targetXPos = 1840 - targetXPos; // if it hits the wall
            }
        }
    }

    public static void enemyShuttleDetection(Graphics g) {
        if (inPlay && !shuttleResponsibility) { // conditions for if the bot should move
            if (targetXPos >= 519 && Math.abs(enemyXPos + 122 - targetXPos) > 20) {
                if (targetXPos > enemyXPos + 122 && enemyXPos <= 775) {
                    enemyXPos += 5;
                } else if (targetXPos < enemyXPos + 122 && enemyXPos >= 420) {
                    enemyXPos -= 5;
                }
                isEnemyWalking = true;
            } else {
                isEnemyWalking = false;
            }

            if (shuttleYPos > 300 - randomIntGen(40 - levelState * 8) && shuttleXPos >= 519 && shuttleYPos < 425
                    && inPlay
                    && !shuttleResponsibility) {
                shuttleAhead = true;
            } else {
                shuttleAhead = false;
            }
        }
    }

    public static void botServe(Graphics g) { // telling the bot to serve
        finish = (long) System.currentTimeMillis() / 1000;
        if (finish - start > 1) { // 2 second delay{
            enemyIsServe = true; // the bot is serving
        }
    }

    public static int randomIntGen(int upperLimit) { // returns +- integer in range
        return (int) (2 * upperLimit * Math.random()) + 1 - upperLimit;
    }

    // Text File Streaming Methods
    public static void levelStateFileCollector() throws IOException {
        Scanner in = new Scanner(new File("Textfiles/progress.txt")); // level states
        levelUnlocked = in.nextInt();
        in.close();
    }

    public static void levelStateFileChanger() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("Textfiles/progress.txt"));
        out.println(levelUnlocked);
        out.close();
    }

    public static void highScoreCollector() throws IOException {
        Scanner in = new Scanner(new File("textFiles/highScore.txt")); // level states
        highScores[0] = in.nextInt(); // fastest smash
        highScores[1] = in.nextInt(); // longest rally
        highScores[2] = in.nextInt(); // total birds hit
        in.close();
    }

    public static void highScoreChanger() throws IOException { // putting the high score in the text file
        PrintWriter out = new PrintWriter(new FileWriter("textFiles/highScore.txt"));
        for (int i = 0; i < 3; i++) {
            if (highScores[i] < currentHighScores[i]) {
                out.print(currentHighScores[i] + " ");
            } else {
                out.print(highScores[i] + " ");
            }
        }
        out.close();
    }

    // audio
    public static void playHit() throws IOException {
        try {
            // hit sound
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("Sounds/hitsound.wav"));
            Clip hitSound = AudioSystem.getClip();
            hitSound.open(audioIn);
            hitSound.start();

        } catch (Exception e) {
            System.out.println("issue with sound importation");
        }
    }

    public static void playSmash() throws IOException {
        try {
            // smash sound
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("Sounds/smashsound.wav"));
            Clip smashSound = AudioSystem.getClip();
            smashSound.open(audioIn);
            smashSound.start();

        } catch (Exception e) {
            System.out.println("issue with sound importation");
        }
    }

    public static void playCrowd() throws IOException { // useless right now
        if (gameState == 3) {
            try {
                // smash sound
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("Sounds/crowdsound.wav"));
                Clip crowdSound = AudioSystem.getClip();
                crowdSound.open(audioIn);
                crowdSound.start();
                crowdSound.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                System.out.println("issue with sound importation");
            }
        }
    }

    // Key Listener
    public void keyPressed(KeyEvent e) {
        // for player
        if (gameState == 2 || gameState == 3) {
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
            if (playerCount == 2) {
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
        }
    }

    public void keyReleased(KeyEvent e) {

        if (gameState == 2 || gameState == 3) {
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

            if (playerCount == 2) {
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
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        // on gamestate 1, main screen
        if (gameState == 0) {
            if (e.getX() >= 310 && e.getX() <= 685 && e.getY() >= 195 && e.getY() <= 305) {
                playerCount = 1; // to 1 player
                gameState = 1;
            }
            if (e.getX() >= 310 && e.getX() <= 685 && e.getY() >= 321 && e.getY() <= 426) {
                gameState = 10; // to two player
                playerCount = 2;
            }
            if (e.getX() >= 310 && e.getX() <= 685 && e.getY() >= 448 && e.getY() <= 553) {
                gameState = 11; // to top scores
            }
        }
        // on gamestate 2, 1 player screen
        else if (gameState == 1) { // back to main menu
            try { // to input score
                highScoreChanger();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (Math.pow(e.getX() - 104, 2) + Math.pow(e.getY() - 87, 2) <= Math.pow(68, 2)) {
                gameState = 0;
            } else if (Math.pow(e.getX() - 962, 2) + Math.pow(e.getY() - 40, 2) <= Math.pow(24, 2)) {
                levelUnlocked = 0; // restarts progress
                try {
                    levelStateFileChanger();
                } catch (IOException e1) {
                }
            }
            // boxes
            else if (e.getX() >= 70 && e.getX() <= 320 && e.getY() >= 175 && e.getY() <= 340) {
                levelState = 0;
                gameState = 9;
            } else if (e.getX() >= 370 && e.getX() <= 620 && e.getY() >= 175 && e.getY() <= 340 && levelUnlocked >= 1) {
                levelState = 1;
                gameState = 2;
            } else if (e.getX() >= 670 && e.getX() <= 920 && e.getY() >= 175 && e.getY() <= 340 && levelUnlocked >= 2) {
                levelState = 2;
                gameState = 2;
            } else if (e.getX() >= 70 && e.getX() <= 320 && e.getY() >= 375 && e.getY() <= 540 && levelUnlocked >= 3) {
                levelState = 3;
                gameState = 2;
            } else if (e.getX() >= 370 && e.getX() <= 620 && e.getY() >= 375 && e.getY() <= 540 && levelUnlocked >= 4) {
                levelState = 4;
                gameState = 2;
            } else if (e.getX() >= 670 && e.getX() <= 920 && e.getY() >= 375 && e.getY() <= 540 && levelUnlocked >= 5) {
                levelState = 5;
                gameState = 3;

                try { // crowd sounds
                    playCrowd();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        } else if (gameState == 4) { // gamestate 5, champion screen
            gameState = 5;

            // reset transition variables
            winFrameCounter = 0;
            podiumYPos = 450;
        } else if (gameState == 5) { // gamestate 6, credit screen
                gameState = 0;
        } else if (gameState == 7) { // gamestate 8, win screen
            if (e.getX() >= 94 && e.getX() <= 459 && e.getY() >= 231 && e.getY() <= 364) {
                levelState++;
                if (levelState == 5) { // making sure at final level they get send to the stadium
                    gameState = 3;
                } else {
                    gameState = 2;
                }
            } else if (e.getX() >= 527 && e.getX() <= 892 && e.getY() >= 231 && e.getY() <= 364) {
                gameState = 1;
            }
        } else if (gameState == 8) { // gamestate 9, loss screen
            if (e.getX() >= 94 && e.getX() <= 459 && e.getY() >= 231 && e.getY() <= 364) {
                if (isFinalStage) {
                    gameState = 3;
                    isFinalStage = false;
                } else {
                    gameState = 2;
                }
            } else if (e.getX() >= 527 && e.getX() <= 892 && e.getY() >= 231 && e.getY() <= 364) {
                gameState = 1;
            }
        } else if (gameState == 9) {
            gameState = 2;
        } else if (gameState == 10) {
            gameState = 2;
        } else if (gameState == 11) {
            if (Math.pow(e.getX() - 104, 2) + Math.pow(e.getY() - 87, 2) <= Math.pow(68, 2)) {
                gameState = 0;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
