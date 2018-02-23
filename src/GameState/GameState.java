package GameState;

import java.awt.Graphics2D;

public abstract class GameState
{
  protected GameStateManager gsm;
  
  protected int winner;

  protected abstract void init();

  public abstract void update();

  public abstract void draw(Graphics2D g);

  public abstract void keyPressed(int k);

  public abstract void keyReleased(int k);
  
  public abstract void mouseDragged(int x, int y);
  
  public abstract void mouseMoved(int x, int y);
  
  public abstract void mousePressed(int button);
  
  public abstract void mouseReleased(int button);
  
  public void setWinner(int k) { winner = k; };
}