package CosmicBreaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cbreaker extends JFrame implements KeyListener {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int PLAYER_WIDTH = 20;
    public static final int PLAYER_HEIGHT = 20;
    public static final int ENEMY_WIDTH = 20;
    public static final int ENEMY_HEIGHT = 20;
    public static final int PLAYER_SPEED = 5;
    public static final int BULLET_SPEED = 5;
    public static final int ENEMY_FIRE_INTERVAL = 1000;

    public JPanel gamePanel;
    public Player player;
    public List<Enemy> enemies;
    public List<Bullet> playerBullets;
    public List<Bullet> enemyBullets;
    public Random random;
    public long lastEnemyFireTime;
    public boolean gameover;
    public int kills;

    public Cbreaker() {
        setTitle("Space Invaders");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                render(g);

            }
        };
        gamePanel.setBackground(Color.BLACK);
        add(gamePanel, BorderLayout.CENTER);
        addKeyListener(this);

        player = new Player(WIDTH / 2 - PLAYER_WIDTH / 2, HEIGHT - PLAYER_HEIGHT - 10);
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        random = new Random();
        lastEnemyFireTime = System.currentTimeMillis();
        gameover = false;
        kills = 0;
        spawnEnemies();
        setVisible(true);
        gameLoop();
    }
    public void gameLoop() {
        while (!gameover) {
            update();
            gamePanel.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void update() {
        player.update();
        for (Bullet bullet : playerBullets) {
            bullet.update();
            if (bullet.getY() < 0) {
                playerBullets.remove(bullet);
                break;
            }
            for (Enemy enemy : enemies) {
                if (bullet.intersects(enemy.getBounds())) {
                    playerBullets.remove(bullet);
                    enemies.remove(enemy);
                    kills++;
                    break;
                }
            }
        }
        for (Bullet bullet : enemyBullets) {
            bullet.update();
            if (bullet.getY() > HEIGHT) {
                enemyBullets.remove(bullet);
                break;
            }
            if (bullet.intersects(player.getBounds())) {
                endGame();
                break;
            }
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemyFireTime > ENEMY_FIRE_INTERVAL) {
            lastEnemyFireTime = currentTime;

            if (!enemies.isEmpty()) {
                Enemy randomEnemy = enemies.get(random.nextInt(enemies.size()));
                enemyBullets.add(new Bullet(randomEnemy.getX() + ENEMY_WIDTH / 2, randomEnemy.getY() + ENEMY_HEIGHT));
            }
        }
    }
    public void endGame() {
        gameover = true;
        JOptionPane.showMessageDialog(this, "Game Over!\nKills: " + kills);
        System.exit(0);
    }
    public void render(Graphics g) {
        if (gameover) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
            g.drawString("Kills: " + kills, WIDTH / 2 - 60, HEIGHT / 2 + 40);
            return;
        }
        player.draw(g);

        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Kills: " + kills, WIDTH - 120, 30);
    }
    public void spawnEnemies() {
        int startX = 50;
        int startY = 50;
        int enemyRows = 4;
        int enemyCols = 10;

        for (int row = 0; row < enemyRows; row++) {
            for (int col = 0; col < enemyCols; col++) {
                int x = startX + col * (ENEMY_WIDTH + 10);
                int y = startY + row * (ENEMY_HEIGHT + 10);
                enemies.add(new Enemy(x, y));
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            player.setVelocity(-PLAYER_SPEED, 0);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            player.setVelocity(PLAYER_SPEED, 0);
        } else if (keyCode == KeyEvent.VK_SPACE) {
            playerBullets.add(new Bullet(player.getX() + PLAYER_WIDTH / 2, player.getY()));
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            player.setVelocity(0, 0);
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    public static class Player {
        public int x;
        public int y;
        public int velocityX;
        public int velocityY;
        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void setVelocity(int velocityX, int velocityY) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }
        public void update() {
            x += velocityX;
            y += velocityY;

            if (x < 0) {
                x = 0;
            } else if (x > WIDTH - PLAYER_WIDTH) {
                x = WIDTH - PLAYER_WIDTH;
            }
        }
        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.drawString("<o>", x, y + PLAYER_HEIGHT);
        }
        public Rectangle getBounds() {
            return new Rectangle(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        }
        public boolean intersects(Rectangle rect) {
            return getBounds().intersects(rect);
        }
    }
    private static class Enemy {
        public int x;
        public int y;
        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.drawString("<[]>", x, y + ENEMY_HEIGHT);
        }
        public Rectangle getBounds() {
            return new Rectangle(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
        public boolean intersects(Rectangle rect) {
            return getBounds().intersects(rect);
        }
    }
    private static class Bullet {
        public int x;
        public int y;
        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void update() {
            y += BULLET_SPEED;
        }
        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 2, 5);
        }
        public Rectangle getBounds() {
            return new Rectangle(x, y, 2, 5);
        }
        public boolean intersects(Rectangle rect) {
            return getBounds().intersects(rect);
        }
    }
    public static void main(String[] args) {
        new Cbreaker();
    }
}
