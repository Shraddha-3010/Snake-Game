package snakegame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    private Image apple;
    private Image dot;
    private Image head;

    private final int ALL_DOTS = 3600;
    private final int DOT_SIZE = 10;
    private final int RANDOM_POSITION = 29;

    private final int MAX_APPLES = 5;
    private int[] apple_x = new int[MAX_APPLES];
    private int[] apple_y = new int[MAX_APPLES];
    

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;

    private int dots;
    private int score = 0;
    private int highScore = 0;

    private Timer timer;
    private JButton restartButton;

    public Board() {
        addKeyListener(new TAdapter());
        setBackground(new Color(245, 245, 220));
        setPreferredSize(new Dimension(600, 600));
        setFocusable(true);

        setLayout(null);

        restartButton = new JButton("Restart");
        restartButton.setBounds(250, 320, 100, 30);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> initGame());
        add(restartButton);

        loadImages();
        initGame();
    }

    public void loadImages() {
        dot = new ImageIcon("images/dot.png").getImage();
        apple = new ImageIcon("images/apple.png").getImage();
        head = new ImageIcon("images/head.png").getImage();
    }

    public void initGame() {
        dots = 5;
        score = 0;
        inGame = true;

        for (int i = 0; i < dots; i++) {
            y[i] = 50;
            x[i] = 50 - i * DOT_SIZE;
        }

        locateApple();

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(140, this);
        timer.start();

        restartButton.setVisible(false);
    }

    public void locateApple() {
        for (int i = 0; i < MAX_APPLES; i++) {
            int r = (int) (Math.random() * RANDOM_POSITION);
            apple_x[i] = r * DOT_SIZE;
            r = (int) (Math.random() * RANDOM_POSITION);
            apple_y[i] = r * DOT_SIZE;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (inGame) {
            for (int i = 0; i < MAX_APPLES; i++) {
                g.drawImage(apple, apple_x[i], apple_y[i], this);
            }
            
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], this);
                } else {
                    g.drawImage(dot, x[i], y[i], this);
                }
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("Score: " + score, 10, 20);

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        String msg = "Game Over!";
        String scoreMsg = "Your Score: " + score;
        String highScoreMsg = "High Score: " + highScore;
        Font font = new Font("SansSerif", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(msg, (300 - metrics.stringWidth(msg)) / 2, 250);
        g.drawString(scoreMsg, (300 - metrics.stringWidth(scoreMsg)) / 2, 280);
        g.drawString(highScoreMsg, (300 - metrics.stringWidth(highScoreMsg)) / 2, 310);

        restartButton.setVisible(true);
    }

    public void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }
        if (rightDirection) {
            x[0] += DOT_SIZE;
        }
        if (upDirection) {
            y[0] -= DOT_SIZE;
        }
        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    public void checkApple() {
        for (int i = 0; i < MAX_APPLES; i++) {
            if ((x[0] == apple_x[i]) && (y[0] == apple_y[i])) {
                dots++;
                score++;
                if (score > highScore) {
                    highScore = score;
                }
                // Reposition the eaten apple only
                int r = (int) (Math.random() * RANDOM_POSITION);                                              
                apple_x[i] = r * DOT_SIZE;
                r = (int) (Math.random() * RANDOM_POSITION);
                apple_y[i] = r * DOT_SIZE;
            }
        }
    }
    

    public void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 3) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (x[0] >= 600 || x[0] < 0 || y[0] >= 600 || y[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }

            if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }
}