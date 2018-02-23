
package GameState;

//import GameObjects.Player;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class EndState extends GameState{
    
    private Font font;
    
    
    public EndState(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    @Override
    protected void init() {
        font = new Font("Arial", Font.PLAIN, 40);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
       g.setFont(font);

//       if (winner == Player.FIRST_PLAYER) {
//        g.drawString("Player 1 Wins!", 400, 200);
//       } else if (winner == Player.SECOND_PLAYER) {
//        g.drawString("Player 2 Wins!", 400, 200);
//       } else {
//        g.drawString("Nobody Wins!", 400, 200);
//       }
       g.drawString("Press Escape to go back to the menu", 200, 300);
    }

    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_ESCAPE) {
            gsm.setState(GameStateManager.MENUSTATE);
        }
    }

    @Override
    public void keyReleased(int k) {
    }

  @Override
  public void mouseDragged(int x, int y)
  {
  }

  @Override
  public void mouseMoved(int x, int y)
  {
  }

  @Override
  public void mousePressed(int button)
  {
  }

  @Override
  public void mouseReleased(int button)
  {
  }
}
