package Main;

import GameState.GameStateManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import java.awt.event.*;


public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

  //dimensions
  public static final int WIDTH = 1080;
  public static final int HEIGHT = 480;
  public static final int WIDTH_SCALE = 2;
  public static final double HEIGHT_SCALE = 2.5;
  public static final int FULL_WIDTH = 1080;
  public static final int FULL_HEIGHT = 480;

  //game thread
  private Thread thread;
  private boolean running;
  private final int FPS = 60;
  
  //image
  private BufferedImage screen;

  private Graphics2D gScreen;

    
  //game state manager
  private GameStateManager gsm;
  
  public GamePanel() {
    super();
    setPreferredSize(new Dimension(FULL_WIDTH * WIDTH_SCALE, (int)(FULL_HEIGHT * HEIGHT_SCALE)));
    setFocusable(true);
    requestFocus();
  }
  
  @Override
  public void addNotify() {
    super.addNotify();
    
    if(thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }
  
  private void init() {
    screen = new BufferedImage (FULL_WIDTH, FULL_HEIGHT, BufferedImage.TYPE_INT_RGB);
    gScreen = (Graphics2D) screen.getGraphics();
    running = true;
    gsm = new GameStateManager();
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    
  }
  
  //game loop
  @Override
  public synchronized void run()
  {
    init();

    final double GAME_HERTZ = 60;
    final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
    final int MAX_UPDATES_BEFORE_RENDER = 5;
    final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / FPS;

    double lastUpdateTime = System.nanoTime();
    double lastRenderTime = System.nanoTime();

    int lastSecondTime = (int) (lastUpdateTime / 1000000000);

    while (running)
    {
      double now = System.nanoTime();
      int updateCount = 0;

      if (gsm.getCurrentState() != GameStateManager.LEVELSTATE)
      {
        update();
        draw();
        drawToScreen();
      } else
      {
        while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER)
        {
          update();
          lastUpdateTime += TIME_BETWEEN_UPDATES;
          updateCount++;
        }

        if (now - lastUpdateTime > TIME_BETWEEN_UPDATES)
        {
          lastUpdateTime = now - TIME_BETWEEN_UPDATES;
        }

        draw();
        drawToScreen();
        lastRenderTime = now;

        int thisSecond = (int) (lastUpdateTime / 1000000000);
        if (thisSecond > lastSecondTime)
        {
          lastSecondTime = thisSecond;
        }

        while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
        {
          Thread.yield();
          try
          {
            Thread.sleep(1);
          } catch (Exception e)
          {
          }

          now = System.nanoTime();
        }
      }
    }
  }
  
  private synchronized void update() {
    gsm.update();
  }
  
  private synchronized void draw() {
    gsm.draw(gScreen);
  }
  
  private synchronized void drawToScreen() {
    Graphics g2 = getGraphics();

    g2.drawImage(screen, 0, 0, FULL_WIDTH * WIDTH_SCALE, (int)(FULL_HEIGHT * HEIGHT_SCALE), null);


    g2.dispose();

  }


  @Override
  public void keyTyped(KeyEvent key)
  {
    
  }

  @Override
  public void keyPressed(KeyEvent key)
  {
    gsm.keyPressed(key.getKeyCode());
  }

  @Override
  public void keyReleased(KeyEvent key)
  {
    gsm.keyReleased(key.getKeyCode());
  }

  @Override
  public void mouseClicked(MouseEvent e)
  {
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    gsm.mousePressed(e.getButton());
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    gsm.mouseReleased(e.getButton());
  }

  @Override
  public void mouseEntered(MouseEvent e)
  {
  }

  @Override
  public void mouseExited(MouseEvent e)
  {
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
    gsm.mouseDragged(e.getX(), e.getY());
  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
    gsm.mouseMoved(e.getX(), e.getY());
  }
}
