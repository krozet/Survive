package GameObjects;

import Map.Map;
import java.awt.*;
import java.util.ArrayList;

public class Bullet extends GameObject{
  
  private double angle;
  private boolean show;
  private boolean hitEnemy;
  private ArrayList<Enemy> enemies;
  private Enemy enemyHit;

  private Explosion explosion;
  
  public Bullet(Map map, double angle, double x, double y, ArrayList<Enemy> enemies) {
    super(map);
    //add offset so shoots from front of tank, needs fine tuning
    this.x = x - 30 * Math.sin(Math.toRadians(angle));
    this.y = y - 25 * Math.cos(Math.toRadians(angle));
    
    //collision
    height = 10;
    width = 10;
    cWidth = 10;
    cHeight  = 10;
    
    //initialize explosion
    explosion = new Explosion(Explosion.NO_EXPLOSION, 0 , 0, 0, 0);
    
    show = true;
    hitEnemy = false;
    speed = 5;
    this.angle = angle;
    loadSprites();
    otherPlayer = new Player(map);
    this.enemies = enemies;
  }
  
  public void setThisP1SmallExplosion() {
    explosion = new Explosion(Explosion.SMALL_EXPLOSION,
          (int)(x + xMap - width / 2), 
          (int)(y + yMap - height / 2),
          (int)(x + otherPlayer.getXMap() - width / 2), 
          (int)(y + otherPlayer.getYMap() - height / 2));
  }
  
  public void setThisP2SmallExplosion() {
    explosion = new Explosion(Explosion.SMALL_EXPLOSION,
          (int)(x + otherPlayer.getXMap() - width / 2), 
          (int)(y + otherPlayer.getYMap() - height / 2),
          (int)(x + xMap - width / 2), 
          (int)(y + yMap - height / 2));
  }
  
  public void setThisP1LargeExplosion() {
    explosion = new Explosion(Explosion.LARGE_EXPLOSION,
          (int)(x + xMap - width / 2), 
          (int)(y + yMap - height / 2),
          (int)(x + otherPlayer.getXMap() - width / 2), 
          (int)(y + otherPlayer.getYMap() - height / 2));
  }
  
  public void setThisP2LargeExplosion() {
    explosion = new Explosion(Explosion.LARGE_EXPLOSION,
          (int)(x + otherPlayer.getXMap() - width / 2), 
          (int)(y + otherPlayer.getYMap() - height/ 2),
          (int)(x + xMap - width / 2), 
          (int)(y + yMap - height / 2));
  }
  
  public void update() {
      this.x += speed * Math.cos(Math.toRadians(angle));
      this.y -= speed * Math.sin(Math.toRadians(-angle));
      
      calculateCorners(x, y);
      
      if(topLeft || topRight || bottomLeft || bottomRight) {//if bullet hits wall
        show = false;
    } else if (!enemies.isEmpty()) {
      for (Enemy zombie : enemies)
      {
        if (bulletIntersects(zombie))
        {
          show = false;
          hitEnemy = true;
          enemyHit = zombie;
          break;
        }
      }
    }
  }
  
  public void setEnemyList(ArrayList<Enemy> enemies) {
    this.enemies = enemies;
  }
  
  private void loadSprites() {
    frames = loadFramesFromFolder("Resources/Bullet", 1);
  }
  
  public boolean getShow() {
    return this.show;
  }
  
  public Explosion getExplosion() {
    return explosion;
  }
  
  public Enemy getHitEnemy() {
    return enemyHit;
  }
  
  public boolean getBooleanHitEnemy() {
    return hitEnemy;
  }
  
  public void setHitEnemy(boolean b) {
      hitEnemy = b;
  }
  
  public void setEnemyHealth(int health) {
    enemyHit.setHealth(health);
  }
  
  public void setFlinch() {
    enemyHit.setFlinch();
  }
  
  public void draw(Graphics2D g) {
    setMapPosition();

  if (angle  < 0) {
      angle += 360;
    }
    
    g.drawImage(frames[0],
        (int)(x + xMap),
        (int)(y + yMap),
        null);
  }
}