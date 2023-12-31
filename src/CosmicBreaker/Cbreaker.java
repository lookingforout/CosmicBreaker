package CosmicBreaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Cbreaker extends JPanel implements KeyListener, ActionListener {
    private int playerX = 250;
    private List<Rectangle> enemies;
    private List<Rectangle> enemyProjectiles;
    private List<Rectangle> playerProjectiles;
    private boolean enemyMovingRight = true;
    private Timer timer;
    private boolean isGameOver = false;
    private boolean isGameWon = false;
    private int score = 0;
    private int time = 0;
    private final int GAME_DURATION = 60 * 1000; // 60 seconds

    public Cbreaker() {
        JFrame frame = new JFrame("Cosmic Breaker");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        enemies = createEnemies();
        enemyProjectiles = new ArrayList<>();
        playerProjectiles = new ArrayList<>();

        timer = new Timer(10, this);
        timer.start();
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Courier New", Font.BOLD, 36));
            g.drawString("GAME OVER!", 200, 200);
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.drawString("Score: " + score, 250, 250);
        } else if (isGameWon) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Courier New", Font.BOLD, 36));
            g.drawString("YOU WIN!", 220, 200);
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.drawString("Score: " + score, 250, 250);
        } else {
            g.setColor(Color.RED);
            g.setFont(new Font("Courier New", Font.PLAIN, 24));
            g.drawString("<o>", playerX, 550);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Courier New", Font.PLAIN, 24));
            for (Rectangle enemy : enemies) {
                g.drawString("<[]>", enemy.x, enemy.y);
            }

            g.setColor(Color.WHITE);
            for (Rectangle projectile : playerProjectiles) {
                g.fillRect(projectile.x, projectile.y, 5, 10);
            }

            g.setColor(Color.ORANGE);
            for (Rectangle projectile : enemyProjectiles) {
                g.fillRect(projectile.x, projectile.y, 5, 10);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Time: " + (GAME_DURATION - time) / 1000, 10, 40);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!isGameOver && !isGameWon) {
            moveEnemies();
            moveProjectiles();
            checkCollision();
            time += 10;
            if (time >= GAME_DURATION) {
                isGameOver = true;
            }
            if (enemies.isEmpty()) {
                isGameWon = true;
            }
        }
        repaint();
    }

    public void movePlayer(int direction) {
        if (direction == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= 10;
        } else if (direction == KeyEvent.VK_RIGHT && playerX < getWidth() - 50) {
            playerX += 10;
        }
    }

    public void moveEnemies() {
        if (enemyMovingRight) {
            for (Rectangle enemy : enemies) {
                enemy.x += 5;
                if (enemy.x >= getWidth() - 50) {
                    enemyMovingRight = false;
                }
            }
        } else {
            for (Rectangle enemy : enemies) {
                enemy.x -= 5;
                if (enemy.x <= 0) {
                    enemyMovingRight = true;
                }
            }
        }

        if (Math.random() < 0.01) {
            Rectangle randomEnemy = enemies.get((int) (Math.random() * enemies.size()));
            Rectangle enemyProjectile = new Rectangle(randomEnemy.x + 22, randomEnemy.y + 20, 5, 10);
            enemyProjectiles.add(enemyProjectile);
        }
    }

    public void moveProjectiles() {
        for (Iterator<Rectangle> iterator = playerProjectiles.iterator(); iterator.hasNext();) {
            Rectangle projectile = iterator.next();
            projectile.y -= 5;
            if (projectile.y <= 0) {
                iterator.remove();
            }
        }

        for (Iterator<Rectangle> iterator = enemyProjectiles.iterator(); iterator.hasNext();) {
            Rectangle projectile = iterator.next();
            projectile.y += 5;
            if (projectile.y >= getHeight()) {
                iterator.remove();
            }
        }
    }

    public void checkCollision() {
        for (Iterator<Rectangle> iterator = playerProjectiles.iterator(); iterator.hasNext();) {
            Rectangle playerProjectile = iterator.next();
            for (Iterator<Rectangle> enemyIterator = enemies.iterator(); enemyIterator.hasNext();) {
                Rectangle enemy = enemyIterator.next();
                if (playerProjectile.intersects(enemy)) {
                    iterator.remove();
                    enemyIterator.remove();
                    score += 10;
                    break;
                }
            }
        }

        for (Iterator<Rectangle> iterator = enemyProjectiles.iterator(); iterator.hasNext();) {
            Rectangle enemyProjectile = iterator.next();
            if (enemyProjectile.intersects(playerX, 550, 50, 20)) {
                iterator.remove();
                isGameOver = true;
                break;
            }
        }
    }

    public void firePlayerProjectile() {
        Rectangle projectile = new Rectangle(playerX + 22, 340, 5, 10);
        playerProjectiles.add(projectile);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            movePlayer(keyCode);
        } else if (keyCode == KeyEvent.VK_SPACE) {
            if (!isGameOver && !isGameWon) {
                firePlayerProjectile();
            } else {
                restartGame();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    private List<Rectangle> createEnemies() {
        List<Rectangle> enemies = new ArrayList<>();
        int startX = 100;
        int startY = 50;
        int enemyWidth = 50;
        int enemyHeight = 20;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int x = startX + col * 100;
                int y = startY + row * 50;
                Rectangle enemy = new Rectangle(x, y, enemyWidth, enemyHeight);
                enemies.add(enemy);
            }
        }

        // Add 10 more enemies
        int additionalEnemies = 10;
        for (int i = 0; i < additionalEnemies; i++) {
            int x = startX + (i % 5) * 100;
            int y = startY + 100 + (i / 5) * 50;
            Rectangle enemy = new Rectangle(x, y, enemyWidth, enemyHeight);
            enemies.add(enemy);
        }

        return enemies;
    }

    private void restartGame() {
        playerX = 250;
        enemies = createEnemies();
        enemyProjectiles.clear();
        playerProjectiles.clear();
        isGameOver = false;
        isGameWon = false;
        score = 0;
        time = 0;
    }

    public static void main(String[] args) {
        new Cbreaker();
    }
}
