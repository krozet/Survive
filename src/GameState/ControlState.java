package GameState;

import Graphics.Background;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ControlState extends GameState {

    private Background bg;
    private Font font;
    private String[] controls = {"Up", "Down", "Left", "Right", "Shoot"};
    private String[] player1Controls = {"W Key", "S Key", "A Key", "D Key", "Left Click or Space"};

    public ControlState(GameStateManager gsm) {
        this.gsm = gsm;

        try {
            bg = new Background("Resources/Zombie_Background.jpg");
            font = new Font("Arial", Font.PLAIN, 28);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
//    public void draw(Graphics2D gControlScreen, Graphics2D nullValue, Graphics2D nullValue2, Graphics2D nullValue3) {
        bg.draw(g);
        g.setColor(Color.BLACK);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        //player one
        for (int i = 0; i < 5; i++) {
            g.drawString(controls[i] + ": ", 300, 80 + i * 35);
            g.drawString(player1Controls[i], 370, 80 + i * 35);
        }
        
        //back button settings
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString("Back", 300, 300);
        
    }

    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_ENTER) {
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
