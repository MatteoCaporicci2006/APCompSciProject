package pooleman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Game extends JPanel implements ActionListener {
	
	private Dimension d;
    private Font smallFont = new Font("Times New Roman", Font.BOLD, 18);
    private boolean inGame = false;
    private boolean dying = false;
    private int BLOCK_SIZE = 24;
    private int N_BLOCKS = 15;
    private int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private int MAX_GHOSTS = 12;
    private int POOLEMAN_SPEED = 6;
    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
    private Image heart, ghost;
    private Image pooleman;
    private int pooleman_x, pooleman_y, poolemand_x, poolemand_y;
    private int req_dx, req_dy;
    
    private int levelData[] = {
    	19, 18, 22,  0, 19, 18, 18, 18, 18, 18, 18, 18, 26, 26, 22,
    	17, 16, 20,  0, 17, 16, 16, 16, 16, 16, 16, 20,  0,  0, 21,
    	17, 16, 20,  0, 17, 16, 16, 16, 24, 16, 16, 20,  0,  0, 21,
    	17, 16, 20,  0, 17, 16, 16, 20,  0, 17, 16, 16, 18, 18, 20,
   		17, 16, 20,  0, 17, 16, 16, 20,  0, 17, 16, 16, 16, 16, 20,
    	17, 16, 20,  0, 17, 16, 24, 28,  0, 25, 24, 24, 24, 16, 20,
    	17, 16, 28,  0, 17, 20,  0,  0,  0,  0,  0,  0,  0, 17, 20, 
    	17, 20,  0,  0, 17, 16, 18, 22,  0, 19, 18, 18, 18, 16, 20,
    	17, 16, 18, 18, 16, 16, 16, 20,  0, 17, 16, 16, 16, 16, 20,
    	17, 16, 24, 24, 16, 16, 16, 20,  0, 17, 16, 16, 16, 16, 20,
    	17, 20,  0,  0, 17, 16, 16, 16, 18, 16, 16, 24, 24, 24, 20,
    	17, 20,  0, 19, 16, 16, 16, 16, 16, 16, 20,  0,  0,  0, 21,
    	17, 20,  0, 17, 16, 16, 16, 16, 16, 16, 28,  0, 19, 18, 20,
    	17, 20,  0, 17, 16, 16, 16, 16, 16, 20,  0,  0, 17, 16, 20,
    	25, 24, 26, 24, 24, 24, 24, 24, 24, 24, 26, 26, 24, 24, 28
    };

    private int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private int maxSpeed = 6;
    private int currentSpeed = 3;
    private int[] screenData;
    private Timer timer;

  
    public Game() {
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);	
        initGame();	
    }
    
    private void loadImages() {
    	pooleman = new ImageIcon("C:/Users/Owner/eclipse-workspace/APCompSciFinal_pacman/src/images/pooleman.png").getImage();
        ghost = new ImageIcon("C:/Users/Owner/eclipse-workspace/APCompSciFinal_pacman/src/images/ghost.png").getImage();
        heart = new ImageIcon("C:/Users/Owner/eclipse-workspace/APCompSciFinal_pacman/src/images/heart.png").getImage();

    }
    
    private void initVariables() {
        screenData = new int[N_BLOCKS * N_BLOCKS];
        d = new Dimension(600, 600);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(27, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {
            death();

        } else {
            movePooleman();
            drawPooleman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {
   	   String start = "Welcome to Pooleman! Press SPACE to play!";
       g2d.setColor(Color.RED);
       g2d.drawString(start, (SCREEN_SIZE)/10, 200);
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(250, 0, 0));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 150, SCREEN_SIZE + 30);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 50 + 100, SCREEN_SIZE + 20, this);
        }
    }

    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
        	
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {
            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }
            initLevel();
        }
    }

    private void death() {
    	lives--;

        if (lives == 0) {
            inGame = false;
        }
        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }
                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }
                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }
                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }
                if (count == 0) {
                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }
                } else {
                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }
            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pooleman_x > (ghost_x[i] - 12) && pooleman_x < (ghost_x[i] + 12)
                    && pooleman_y > (ghost_y[i] - 12) && pooleman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(ghost, x, y, this);
        }

    private void movePooleman() {
        int pos;
        int ch;

        if (pooleman_x % BLOCK_SIZE == 0 && pooleman_y % BLOCK_SIZE == 0) {
            pos = pooleman_x / BLOCK_SIZE + N_BLOCKS * (int) (pooleman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (int) (ch & 15);
                score++;
            }
            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    poolemand_x = req_dx;
                    poolemand_y = req_dy;
                }
            }
            if ((poolemand_x == -1 && poolemand_y == 0 && (ch & 1) != 0)
                    || (poolemand_x == 1 && poolemand_y == 0 && (ch & 4) != 0)
                    || (poolemand_x == 0 && poolemand_y == -1 && (ch & 2) != 0)
                    || (poolemand_x == 0 && poolemand_y == 1 && (ch & 8) != 0)) {
                poolemand_x = 0;
                poolemand_y = 0;
            }
        } 
        pooleman_x = pooleman_x + POOLEMAN_SPEED * poolemand_x;
        pooleman_y = pooleman_y + POOLEMAN_SPEED * poolemand_y;
    }

    private void drawPooleman(Graphics2D g2d) {
        	g2d.drawImage(pooleman, pooleman_x + 1, pooleman_y + 1, this);
    }

    private void drawMaze(Graphics2D g2d) {
        int i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,150,250));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    private void initGame() {
    	lives = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void initLevel() {
        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {
    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pooleman_x = 7 * BLOCK_SIZE;
        pooleman_y = 11 * BLOCK_SIZE;
        poolemand_x = 0;	
        poolemand_y = 0;
        req_dx = 0;
        req_dy = 0;
        dying = false;
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
           showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_A) {
                    req_dx = -1;
                    req_dy = 0; 
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_D) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_W) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_S) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
}

	
    	@Override
    	public void actionPerformed(ActionEvent e) {
        	repaint();
    	}
	}
