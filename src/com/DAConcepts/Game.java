package com.DAConcepts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 427;
    public static final int HEIGHT = (int) (WIDTH*9f/16f);
    public static final int SCALE = 3;
    public final String TITLE = "Scuffed Doom";
    public float deltaTime;
    public static JFrame frame;
    public static boolean usingMouse = false;
    public float totalTime = 0;
    private String fpsCounter = "NaN Ticks, NaN FPS";

    private boolean running = false;
    private Thread thread;
    private BuildingScreenLayer buildingLayer;
    private int[] pixels;
    private BufferedImage img;
    private BufferedImage en1SpriteSheet;
    private BufferedImage en2SpriteSheet;
    private BufferedImage fbSpriteSheet;
    private SpriteSheet enemySpriteSheet1;
    private SpriteSheet enemySpriteSheet2;
    private SpriteSheet fireBallSpriteSheet;
    public Player player;
    private Vector2 mouseLastPos;
    private final int XMIN = 0;
    private final int YMIN = 0;
    private int XMAX;
    private int YMAX;
    private ArrayList<En1> enemies1;
    private ArrayList<En2> enemies2;
    private ArrayList<FireBall> fbs;
    public int tickCount = 0;

    private void init() {
        enemies1 = new ArrayList<En1>();
        enemies2 = new ArrayList<En2>();
        fbs = new ArrayList<FireBall>();
        // en1 sprite sheet
    	requestFocus();
        BufferedImageLoader loader = new BufferedImageLoader();
        try {
            en1SpriteSheet = loader.LoadImage("res/SpriteSheets/impSpriteSheet.png");
            en2SpriteSheet = loader.LoadImage("res/SpriteSheets/En2SpriteSheet.png");
            fbSpriteSheet = loader.LoadImage("res/SpriteSheets/FBSpriteSheet.png");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        enemySpriteSheet1 = new SpriteSheet(en1SpriteSheet);
        enemySpriteSheet2 = new SpriteSheet(en2SpriteSheet);
        fireBallSpriteSheet = new SpriteSheet(fbSpriteSheet);
        //

        // spawning entities
        player = new Player(0.5f,0.5f,0,this);
        addMouseMotionListener(new MouseAxisInput(this));
        addKeyListener(new KeyInput(this));
    }

    public synchronized void start() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(blankCursor);
        img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        buildingLayer = new BuildingScreenLayer(WIDTH,HEIGHT,this);
        pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        init();
        thread.start();
    }

    public synchronized void stop() {
        Game game = new Game();
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        frame = new JFrame(game.TITLE);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.start();
    }

    public void mouseMoved(MouseEvent e) {
        if (!usingMouse) {
            return;
        }
        if (mouseLastPos == null) {
            mouseLastPos = new Vector2(e.getX(),e.getY());
            return;
        }
        player.mouseMoved((int) mouseLastPos.x,e.getX());
        mouseLastPos = new Vector2(e.getX(),e.getY());
        if (e.getX() > WIDTH-(WIDTH/4) || e.getX() < (WIDTH/4) || e.getY() > HEIGHT-(HEIGHT/4) || e.getY() < (HEIGHT/4)) {
            Robot r = null;
            try {
                r = new Robot();
                int x = 0;
                int y = 0;
                if (e.getX() > WIDTH-(WIDTH/4) || e.getX() < (WIDTH/4)) {
                    x = WIDTH - e.getX();
                }
                if (e.getY() > HEIGHT-(HEIGHT/4) || e.getY() < (HEIGHT/4)) {
                    y = HEIGHT - e.getY();
                }
                r.mouseMove(x,y);
            } catch (AWTException ex) {
                usingMouse = false;
                System.out.println("Hmmm somethings wrong here. Try using the arrow keys instead");
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            player.setInputAxisVertical(1);
        }
        if (key == KeyEvent.VK_A) {
            player.setInputAxisHorizontal(-1);
        }
        if (key == KeyEvent.VK_S) {
            player.setInputAxisVertical(-1);
        }
        if (key == KeyEvent.VK_D) {
            player.setInputAxisHorizontal(1);
        }
        if (key == KeyEvent.VK_Q) {
            player.setInputAxisUp(-1);
        }
        if (key == KeyEvent.VK_E) {
            player.setInputAxisUp(1);
        }
        if (key == KeyEvent.VK_SHIFT) {
            player.setSprintingSpeed(player.getSprintSpeed());
        }
        if (key == KeyEvent.VK_LEFT) {
            player.setViewInputAxisHorizontal(1);
        }
        if (key == KeyEvent.VK_RIGHT) {
            player.setViewInputAxisHorizontal(-1);
        }
        if (key == KeyEvent.VK_SPACE) {
            player.setShootKey(1);
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            player.setInputAxisVertical(0);
        }
        if (key == KeyEvent.VK_A) {
            player.setInputAxisHorizontal(0);
        }
        if (key == KeyEvent.VK_S) {
            player.setInputAxisVertical(0);
        }
        if (key == KeyEvent.VK_D) {
            player.setInputAxisHorizontal(0);
        }
        if (key == KeyEvent.VK_Q) {
            player.setInputAxisUp(0);
        }
        if (key == KeyEvent.VK_E) {
            player.setInputAxisUp(0);
        }
        if (key == KeyEvent.VK_SHIFT) {
            player.setSprintingSpeed(1);
        }
        if (key == KeyEvent.VK_LEFT) {
            player.setViewInputAxisHorizontal(0);
        }
        if (key == KeyEvent.VK_RIGHT) {
            player.setViewInputAxisHorizontal(0);
        }
        if (key == KeyEvent.VK_SPACE) {
            player.setShootKey(0);
        }
    }

    public void run() {
        float deathCounter = 0;
        float spawnCounter = 0;
        long lastTime = System.nanoTime();
        long lastTick = System.nanoTime();
        final float amountOfTicks = 60f;
        float ns = 1000000000f/amountOfTicks;
        float delta = 0f;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();
        while (running) {
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                deltaTime = (System.nanoTime()-lastTick)/1000000000f;
                if (totalTime <= 10) {
                    totalTime += deltaTime;
                }
                lastTick = System.nanoTime();
                tick();
                updates++;
                delta--;
                spawnCounter += deltaTime;
                if (spawnCounter >= 4) {
                    int total = 3;
                    int maxEnemies = 10;
                    if ((enemies1.size()+enemies2.size()+fbs.size()) >= maxEnemies-4) {
                        total = Math.max(0,maxEnemies-(enemies1.size()+enemies2.size()+fbs.size()));
                    }
                    int amountOf1 = (int) (Math.random()*(total));
                    int amountOf2 = total-amountOf1;
                    for (int i = 0; i < amountOf1; i++) {
                        enemies1.add(new En1((int) (Math.random()*100),(int) (Math.random()*100),this));
                    }
                    for (int i = 0; i < amountOf2; i++) {
                        enemies2.add(new En2((int) (Math.random()*100), (int) (Math.random()*100), this));
                    }
                    spawnCounter %= 4;
                }
                if (player.dead) {
                    deathCounter += deltaTime;
                    if (deathCounter >= 1) {
                        break;
                    }
                }
                if (player.won) {
                    System.exit(1);
                }
            }
            render();
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                fpsCounter = updates+" Ticks, "+frames+" FPS";
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    public ArrayList<En1> getEnemies1() {
        return enemies1;
    }

    public ArrayList<En2> getEnemies2() {
        return enemies2;
    }

    private void tick() {
        updateFBs();
        tickCount++;
        Physics.resetColliders();
        for (En1 en : enemies1) {
            en.updateCols();
        }
        for (En2 en : enemies2) {
            en.updateCols();
        }
        player.updateCols();
        //tick
        ArrayList<En1> en1List = new ArrayList<En1>();
        for (En1 en : enemies1) {
            en.tick();
            if (!en.done) {
                en1List.add(en);
            }
        }
        enemies1 = en1List;
        ArrayList<En2> en2List = new ArrayList<En2>();
        for (En2 en : enemies2) {
            en.tick();
            if (!en.done) {
                en2List.add(en);
            }
        }
        enemies2 = en2List;
        for (FireBall en : fbs) {
            en.tick();
        }
        player.tick();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        if (!player.dead) {
            buildingLayer.render(this);
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
                pixels[i] = buildingLayer.PIXELS[i];
                //System.out.println(buildingLayer.PIXELS[i]);
            }
            g.clearRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
            g.drawImage(img, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
            g.setFont(new Font("Verdana", Font.BOLD, 15));
            g.setColor(Color.WHITE);

            for (En1 en : enemies1) {
                en.render(g);
            }
            for (En2 en : enemies2) {
                en.render(g);
            }
            for (FireBall en : fbs) {
                en.render(g);
            }
            player.render(g);

            g.drawString(fpsCounter, 10, 20);
            g.setFont(new Font("Verdana", Font.BOLD, 40));
            g.drawString("" + player.shield, 20, Game.HEIGHT * Game.SCALE - 70);
            g.drawString("" + player.health, 20, Game.HEIGHT * Game.SCALE - 30);
            g.drawString("" + player.killCount, Game.WIDTH*Game.SCALE-120, Game.HEIGHT * Game.SCALE - 30);
        }
        else {
            g.drawString("0", 10, Game.HEIGHT * Game.SCALE - 30);
            this.setBackground(Color.RED);
            g.clearRect(0,0,Game.WIDTH*Game.SCALE,Game.HEIGHT*Game.SCALE);
        }
        g.dispose();
        bs.show();
    }

    public static void main(String args[]) {
        Game game = new Game();
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        frame = new JFrame(game.TITLE);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.start();
    }

    public void addFB(FireBall fb) {
        fbs.add(fb);
    }

    public void updateFBs() {
        ArrayList<FireBall> tempFbs = new ArrayList<FireBall>();
        for (FireBall fb : fbs) {
            if (!fb.done) {
                tempFbs.add(fb);
            }
        }
        fbs = tempFbs;
    }

    public Vector2 getPlayerPos() {
        return new Vector2(player.xPos,player.yPos);
    }

    public float getPlayerHeightPos() {
        return player.heightPos;
    }

    public float getPlayerHeight() {
        return player.height;
    }

    public float getPlayerRot() {
        return player.zRot;
    }

    public Vector2 getPlayerTR() {
        return player.transformRight();
    }

    public Vector2 getPlayerTF() {
        return player.transformForward();
    }

    public SpriteSheet getEnemySpriteSheet1() {
        return this.enemySpriteSheet1;
    }

    public SpriteSheet getEnemySpriteSheet2() {
        return this.enemySpriteSheet2;
    }

    public SpriteSheet getFireBallSpriteSheet() {
        return this.fireBallSpriteSheet;
    }

    public int getYMAX() {
        return YMAX;
    }

    public int getXMAX() {
        return XMAX;
    }

    public void setYMAX(int y) {
        YMAX = y;
    }

    public void setXMAX(int x) {
        XMAX = x;
    }
}
