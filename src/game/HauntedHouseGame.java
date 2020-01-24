package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/** https://stackoverflow.com/questions/1963494/java-2d-game-graphics#1963684 */
public class HauntedHouseGame extends Thread {
    private boolean isRunning = true;
    private boolean gameLoaded = false;
    private long gameTime = 0;
    private int currentFPS = 0;
    
    private Canvas canvas;
    private BufferStrategy strategy;
    private BufferedImage background;
    private Graphics2D backgroundGraphics;
    private Graphics2D graphics;
    private JFrame frame;
    private JLabel backImage;
    private int width = 320;
    private int height = 240;
    private int scale = 2;
    private GraphicsConfiguration config =
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

    // create a hardware accelerated image
    protected final BufferedImage create(final int width, final int height,
            final boolean alpha) {
        return config.createCompatibleImage(width, height, alpha
                ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
    }

    // Setup
    public HauntedHouseGame() {
        super();
        // Background Image
        backImage = new JLabel();
        backImage.setIcon(new ImageIcon("C:\\Users\\Asus\\Desktop\\EngInformatica\\ED\\pepe.gif"));
        backImage.setLayout(new java.awt.BorderLayout(0, 0));
        // JFrame
        frame = new JFrame(config);
        frame.setLayout(new java.awt.BorderLayout(0, 0));
        frame.setResizable(false);
        frame.setTitle("HauntedHouseGame");
        // menu = new GameMenu(this, initMenu, gameMenu);
        frame.addWindowListener(new FrameClose());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(width * scale, height * scale);
        frame.setVisible(true);

        // Canvas
        canvas = new Canvas(config);
        canvas.setSize(width * scale, height * scale);
        backImage.add(canvas, 0);
        frame.add(backImage, 0);
        //frame.add(canvas, 0);

        // Background & Buffer
        background = create(width, height, false);
        canvas.createBufferStrategy(2);
        do {
            strategy = canvas.getBufferStrategy();
        } while (strategy == null);
    }

    private class FrameClose extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent e) {
            isRunning = false;
        }
    }

    // Screen and buffer stuff
    private Graphics2D getBuffer() {
        if (graphics == null) {
            try {
                graphics = (Graphics2D) strategy.getDrawGraphics();
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return graphics;
    }

    private boolean updateScreen() {
        graphics.dispose();
        graphics = null;
        try {
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
            return (!strategy.contentsLost());

        } catch (NullPointerException e) {
            return true;

        } catch (IllegalStateException e) {
            return true;
        }
    }

    @Override
    public void run() {
        backgroundGraphics = (Graphics2D) background.getGraphics();
        long fpsWait = (long) (1.0 / 30 * 1000);
        int fpsCount = 0;
        long fpsTime = 0;
        gameLoaded = true;
        while (isRunning) {
            long renderStart = System.nanoTime();
            updateGame();

            // Update Graphics
            do {
                Graphics2D bg = getBuffer();
                if (!isRunning) {
                    frame.dispose();
                    return;
                }
                renderGame(backgroundGraphics); // this calls your draw method
                if (scale != 1) {
                    bg.drawImage(background, 0, 0, width * scale, height
                            * scale, 0, 0, width, height, null);
                } else {
                    bg.drawImage(background, 0, 0, null);
                }
                bg.dispose();
            } while (!updateScreen());

            // Better do some FPS limiting here
            long renderTime = (System.nanoTime() - renderStart) / 1000000;
            try {
                Thread.sleep(Math.max(0, fpsWait - renderTime));
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
            renderTime = (System.nanoTime() - renderStart) / 1000000;
            if (gameLoaded) {
                gameTime += renderTime;
            }
            fpsTime += (System.nanoTime() - renderStart) / 1000000;
            fpsCount += 1;
            if (fpsTime > 1000 - fpsWait) {
                    currentFPS = fpsCount;
                    fpsCount = 0;
                    fpsTime = 0;
            }

        }
        gameLoaded = false;
        frame.dispose();
    }

    private void updateGame() {
        // update game logic here
    }
    
    /**
     * Renders the whole game Image through it's graphics instance.
     * 
     * @param g graphics instance for the game image
     */
    private void renderGame(Graphics2D g) {
        g.setColor(Color.GRAY);
        // Clear the board
        g.clearRect(0, 0, width, height);
        
        final Color CITY = new Color(214,217,223);
        final Color DESERT = new Color(255,204,102);
        final Color DIRT_ROAD = new Color(153,102,0);
        final Color FOREST = new Color(0,102,0);
        final Color HILLS = new Color(51,153,0);
        final Color LAKE = new Color(0,153,153);
        final Color MOUNTAINS = new Color(102,102,255);
        final Color OCEAN = new Color(0,0,153);
        final Color PAVED_ROAD = new Color(51,51,0);
        final Color PLAINS = new Color(102,153,0);

        final Color[] TERRAIN = {
            CITY,
            DESERT,
            DIRT_ROAD,
            FOREST,
            HILLS,
            LAKE,
            MOUNTAINS,
            OCEAN,
            PAVED_ROAD,
            PLAINS
        };
        java.util.Random r = new java.util.Random();
        
        int randomTerrainIndex = r.nextInt(TERRAIN.length);
        Color randomColor = TERRAIN[randomTerrainIndex];
        g.setColor(Color.GRAY);
        g.fillRect(width - 50, height - 50, 200, 200);
        g.setColor(randomColor);
        g.drawString("FPS: " + currentFPS, 20, 25);
        // g.fillRect(0, 0, width, height);
    }
    
    public float getGameTime() {
        return gameTime;
    }
    
    public int getCurrentFPS() {
        return currentFPS;
    }

    public static void main(final String args[]) {
        new HauntedHouseGame().start();
    }
}