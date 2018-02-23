package GameState;

import GameObjects.Enemy;
import Main.GamePanel;
import Graphics.Background;
import Map.Map;
import GameObjects.Player;
import GameObjects.Explosion;
import Graphics.Health;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class LevelState extends GameState
{
  private Background bg;
  private Waves waves;
  private Player player;
  private Enemy enemy;
  
  private boolean canFire;
    
  private Map map;
  
  private boolean reset;
  private ArrayList<Explosion> explosions;
  private ArrayList<Enemy> enemies;
  private Enemy en;
  private Health hearts;
  
  private long time;
  private long lastTime;
  private long timeDrawWave;
  private long lastTimeDrawWave;
  private int enemyDelay;
  private int enemyCounter;
  private int numOfEnemies;
  private boolean allEnemiesDead;
  private boolean drawWaveTitle;
  private boolean reverseFade;
  private float alpha;
  
  public LevelState(GameStateManager gsm) {
    this.gsm = gsm;
    init();
  }

  @Override
  protected void init()
  {
    try {
       bg = new Background("Resources/BIGArena1.JPG");
     }
     catch (Exception e) {
       e.printStackTrace();
     }

    map = new Map();
    waves = new Waves();
    explosions = new ArrayList();
    enemies = new ArrayList();
    
    map.setPosition(0, 0);
    map.setTween(1);
    player = new Player(map);
    hearts = new Health(player.getHealth());

    
    player.setPosition((137*32)/(1.5) + 200, (87*32)/2);
    player.setAngle(186);
    canFire = true;
    reset = false;
    allEnemiesDead = false;
    drawWaveTitle = true;
    reverseFade = false;
    
    time = 0;
    lastTime = 0;
    timeDrawWave = 0;
    lastTimeDrawWave = 0;
    enemyDelay = 500;
    enemyCounter = 0;
    alpha = 0.0f;
  } 

  @Override
  public void update()
  {
     if(reset) {
            init();
        }
        if(!player.getDead()) {
          updatePlayer();
          
          //waves
          numOfEnemies = waves.getNumOfEnemies();
          enemyDelay = waves.getEnemySpawnTimer();
          time += System.currentTimeMillis() - lastTime;
          lastTime = System.currentTimeMillis();

          //add enemies
          if (time > enemyDelay && enemyCounter < numOfEnemies)
          {
            addEnemies();
            time = 0;
          }

          updateEnemies();
          
          if(allEnemiesDead && !waves.isComplete()) {
            waves.nextWave();
            drawWaveTitle = true;
            enemies = new ArrayList();
            enemyCounter = 0;
          }
        }
        else {
            reset = false;
            player.setDead(true);
        }

    hearts.setHealth(player.getHealth());
    updateBulletList();
    updateExplosionsList();
  }
  
  private void addEnemies() {
    Random r = new Random();
    
    //spawn zones
    switch (r.nextInt(5)) {
      //top left
      case 0:
        en = new Enemy(map, player, enemies);
        en.setHealth(waves.getEnemyHealth());
        en.setDamage(waves.getEnemyDamage());
        en.setMoveSpeed(waves.getEnemySpeed());
        en.setPosition(2970, 775);
        break;
      //top right 
      case 1:
        en = new Enemy(map, player, enemies);
        en.setHealth(waves.getEnemyHealth());
        en.setDamage(waves.getEnemyDamage());
        en.setMoveSpeed(waves.getEnemySpeed());
        en.setPosition(3555, 775);
        break;
      //right
      case 2:
        en = new Enemy(map, player, enemies);
        en.setHealth(waves.getEnemyHealth());
        en.setDamage(waves.getEnemyDamage());
        en.setMoveSpeed(waves.getEnemySpeed());
        en.setPosition(4200, 1347);
        break;
      //left
      case 3:
        en = new Enemy(map, player, enemies);
        en.setHealth(waves.getEnemyHealth());
        en.setDamage(waves.getEnemyDamage());
        en.setMoveSpeed(waves.getEnemySpeed());
        en.setPosition(2255, 1347);
        break;
      //bottom
      case 4:
        en = new Enemy(map, player, enemies);
        en.setHealth(waves.getEnemyHealth());
        en.setDamage(waves.getEnemyDamage());
        en.setMoveSpeed(waves.getEnemySpeed());
        en.setPosition(3260, 1850);
        break;
      default:
        break;
    }
    
    enemyCounter++;
    enemies.add(en);
  }
  
  public void updateEnemies() {
    int deadEnemyCounter = 0;
    
    for (int i = 0; i < enemies.size(); i++) {
      Enemy en = enemies.get(i);
      en.setPlayer(player);
      
      if (en.getHitPlayer()) {
        en = playerHit(en);
      }
     
     en.update();
     
     if(en.getDead()) {
       deadEnemyCounter++;
     }
     
     enemies.set(i, en);
    }
    
    allEnemiesDead = deadEnemyCounter == enemies.size();
  }
  
  public void updatePlayer() {

    player.setMapArray(map.getMapArray());
    player.update();

    map.setMapArray(player.getMapArray());

    player.readjustMapPosition(GamePanel.WIDTH/2 - player.getX(),
        GamePanel.HEIGHT/2 - player.getY());
    bg.setPosition(player.getMapObject().getX(), player.getMapObject().getY());
  }
  
   public void updateBulletList() {
        //Player 1 Bullets
        for (int i = 0; i < player.getBulletList().size(); i++) {
          explosions.add(player.getBulletList().get(i).getExplosion());
          //set the zombie list
            if (player.getBulletList().get(i).getShow()) {
                player.getBulletList().get(i).setEnemyList(enemies);
                player.getBulletList().get(i).update();
                //check if bullet hits zombie
                if(player.getBulletList().get(i).getBooleanHitEnemy()) {
                  enemyHit(i);
                }
            } 
            else {
                player.getBulletList().remove(i);
            }
        }
    }
   
   private void updateExplosionsList() {
    for (int i = 0; i < explosions.size(); i++) {
      if (explosions.get(i).hasPlayedOnce()) {
        explosions.remove(i);
      }
    }
   }
   
  @Override
  public void draw(Graphics2D g)
  {
    if (player.getDead()) {
      hearts.draw(g);
      drawEnd(g);
      return;
    }
    
    g.setComposite(AlphaComposite.getInstance(
          AlphaComposite.SRC_OVER, 1.0f));
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    bg.draw(g);
    player.getMapObject().draw(g);

    //stores the enemies that are infront of the player
    ArrayList<Enemy> inFrontOfPlayer = new ArrayList();
    if (!enemies.isEmpty()) {
      for (Enemy enemy : enemies) {
        if (player.getY() > enemy.getY() || enemy.getDead()) {
          enemy.draw(g);
        } else {
          inFrontOfPlayer.add(enemy);
        }
      }
    }
    //draw the player in front of the enemies above them
    player.draw(g);
    //draw the enemies in front of player above them
    for (Enemy enemy : inFrontOfPlayer) {
      enemy.draw(g);
    }

    for(int i = 0; i < player.getBulletList().size(); i++) {
      player.getBulletList().get(i).draw(g);
    }
    
    for (int i = 0; i < explosions.size(); i++) {
      explosions.get(i).draw(g);
    }
    
    //draw the Wave Count
    if (drawWaveTitle) {
      drawWave(g);
    }
    
    hearts.draw(g);
  }
  
  private void drawWave(Graphics2D g) {
    g.setFont(new Font("Impact", Font.PLAIN, 86));
      
      if (!reverseFade)
      {
        timeDrawWave += System.currentTimeMillis() - lastTimeDrawWave;
        lastTimeDrawWave = System.currentTimeMillis();
        
        //set the opacity
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, alpha));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(128, 0, 0));
        g.drawString("Wave " + (waves.getWaveNumber() + 1), 400, 100);

        //increase the opacity and repaint
        if (timeDrawWave > 15)
        {
          alpha += 0.02f;
          timeDrawWave = 0;
        }

        if (alpha >= 1.0f)
        {
          reverseFade = true;
          alpha = 1.0f;
        }
      } else {
        
        timeDrawWave += System.currentTimeMillis() - lastTimeDrawWave;
        lastTimeDrawWave = System.currentTimeMillis();
        
        //set the opacity
        g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, alpha));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(128, 0, 0));
        g.drawString("Wave " + (waves.getWaveNumber() + 1), 400, 100);

        //increase the opacity and repaint
        if (timeDrawWave > 15)
        {
          alpha -= 0.02f;
          timeDrawWave = 0;
        }

        if (alpha <= 0.0f)
        {
          reverseFade = false;
          drawWaveTitle = false;
          alpha = 0.0f;
        }
      }
  }
  
  private void drawEnd(Graphics2D g) {
    g.setFont(new Font("Impact", Font.PLAIN, 86));

    timeDrawWave += System.currentTimeMillis() - lastTime;
    lastTimeDrawWave = System.currentTimeMillis();

    //set the opacity
    g.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, alpha));
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(new Color(128, 0, 0));
    g.drawString("GAME OVER", 360, 250);

    //increase the opacity and repaint
    if (timeDrawWave > 15)
    {
      alpha += 0.02f;
      timeDrawWave = 0;
    }

    if (alpha >= 1.0f)
    {
      alpha = 1.0f;
    }
  }
  
  private void enemyHit(int position) {
    en = player.getBulletList().get(position).getHitEnemy();
    player.getBulletList().get(position).setEnemyHealth(en.getHealth() - player.getBulletDamage());
    player.getBulletList().get(position).setHitEnemy(false);
    player.getBulletList().get(position).setFlinch();
  }
  
  private Enemy playerHit(Enemy en) {
    en.setHitPlayer(false);
    player.setHealth(player.getHealth() - en.getDamage());
    player.setFlinch();
    return en;
  }
  
  @Override
  public void keyPressed(int k)
  {
    if (!player.getDead())
    {
      switch (k)
      {
        //Player 1 
        case KeyEvent.VK_A:
          player.setWest(true);
          break;
        case KeyEvent.VK_D:
          player.setEast(true);
          break;
        case KeyEvent.VK_W:
          player.setNorth(true);
          break;
        case KeyEvent.VK_S:
          player.setSouth(true);
          break;
        case KeyEvent.VK_SPACE:
          if (canFire)
          {
            player.fire(enemies);
            canFire = false;
          }
          break;
      }
    }
  }

  @Override
  public void keyReleased(int k)
  {
    if (!player.getDead())
    {
      switch (k)
      {
        //Player 1
        case KeyEvent.VK_A:
          player.setWest(false);
          break;
        case KeyEvent.VK_D:
          player.setEast(false);
          break;
        case KeyEvent.VK_W:
          player.setNorth(false);
          break;
        case KeyEvent.VK_S:
          player.setSouth(false);
          break;
        case KeyEvent.VK_SPACE:
          canFire = true;
          break;
      }
    }
  }

  @Override
  public void mouseDragged(int x, int y)
  {
    if (!player.getDead())
    {
      player.setMousePosition(x, y);
    }
  }

  @Override
  public void mouseMoved(int x, int y)
  {
    if (!player.getDead())
    {
      player.setMousePosition(x, y);
    }
  }

  @Override
  public void mousePressed(int button)
  {
    if (!player.getDead())
    {
      if (button == MouseEvent.BUTTON1)
      {
        player.setLeftMouseButton(true);
        player.fire(enemies);
      }
      if (button == MouseEvent.BUTTON3)
      {
        player.setRightMouseButton(true);
      }
    }
  }

  @Override
  public void mouseReleased(int button)
  {
    if (!player.getDead())
    {
      if (button == MouseEvent.BUTTON1)
      {
        player.setLeftMouseButton(false);
      }
      if (button == MouseEvent.BUTTON3)
      {
        player.setRightMouseButton(false);
      }
    }
  }
}
