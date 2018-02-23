package GameObjects;

import Graphics.Animation;
import Map.Block;
import Map.Map;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Enemy extends GameObject
{
  private int maxHealth = 30;
  private int health;
  private boolean dying;
  private boolean dead;
    
  //attacking
  private ArrayList<Bullet> bulletList;
  private boolean attacking;
  private boolean flinching;
  private boolean attackingLeft;
  private int attackDamage;
  private long flinchTimer;
  private double distanceToPlayer;
  private boolean hitPlayer;
  private boolean alreadyHitPlayer;
  private boolean facingLeft;
  
  //looking at mouse
  public static final int LOOKING_EAST = 0;
  public static final int LOOKING_NORTHEAST = 1;
  public static final int LOOKING_NORTHWEST = 2;
  public static final int LOOKING_NORTH = 3;
  public static final int LOOKING_SOUTHEAST = 4;
  public static final int LOOKING_SOUTHWEST = 5;
  public static final int LOOKING_SOUTH = 6;
  public static final int LOOKING_WEST = 7;
  private boolean[] lookingDirection;
  private int direction;
  
  //animations
  private ArrayList<BufferedImage[]> sprites;
  int[] anims = {18, 15, 8, 11, 13};
  
  //animation actions  
  public static final int ATTACKING = 0;
  public static final int DEAD = 1;
  public static final int HIT = 2;
  public static final int IDLE = 3;
  public static final int WALKING = 4;
  
  public static final int MOVING_EAST = 0;
  public static final int MOVING_NORTHEAST = 1;
  public static final int MOVING_NORTHWEST = 2;
  public static final int MOVING_NORTH = 3;
  public static final int MOVING_SOUTHEAST = 4;
  public static final int MOVING_SOUTHWEST = 5;
  public static final int MOVING_SOUTH = 6;
  public static final int MOVING_WEST = 7;
  
  public static final int ENEMY_SIZE = 32;
  
  Vector2D velocity, acceleration, heading;
  ArrayList<Enemy> enemies;
  Steering steering;
  Player player;
  
  public Enemy(Map map, Player player, ArrayList<Enemy> enemies)
  {
    super(map);
    this.player = player;
    this.enemies = enemies;
    init();
    loadCharacterSprites();
  }
  
  private void init() {
    //tested values
    width = 32;
    height = 60;
    cWidth = 8;
    cHeight = 0;
    
    speed = 0;
    angleSpeed = 0;
    moveSpeed = 0.25;
    angleMoveSpeed = 1.5;
    
    angle = 180;
    
    maxSpeed = 1.25;
    maxAngleSpeed = 3;
    stopSpeed = 0.5;
    flinchTimer = 100000;
    distanceToPlayer = 0;
    attackDamage = 10;
    
    health = 30;
    bulletList = new ArrayList();
    lookingDirection = new boolean[8];
    Arrays.fill(lookingDirection, false);
    dead = false;
    alreadyHitPlayer = false;
    
    sprites = new ArrayList();
    animation = new Animation();
    direction = 0;
    
    rect = new Rectangle((int)x, (int)y, cWidth-4, cHeight);
    collisionBox = rect;
    
    velocity = acceleration = heading = new Vector2D();
    steering = new Steering(this);
  }
  
  private void loadCharacterSprites() {
    frames = loadFramesFromFolder("Resources/Skeleton", 65);
    int frameOffset = 0;

    for (int i = 0; i < anims.length; i++) {
      BufferedImage[] tempFrames = new BufferedImage[anims[i]];

      for (int j = 0; j < tempFrames.length; j++) {
        tempFrames[j] = frames[frameOffset + j];
      }
      sprites.add(tempFrames);
      frameOffset += anims[i];
    }
  }
  
  public int getHealth() { 
    return health;
  }
  
  public void setHealth(int k) {
        health = k;
    }
  
  public void setDamage(int k) {
    attackDamage = k;
  }
  
  public void setMoveSpeed(double speed) {
    maxSpeed = speed;
  }
  
  public void update() {
    
    if (dead) {
      animation.setFrames(sprites.get(DEAD));
      animation.setFrame(14);
      animation.setDelay(-1);
      width = 0;
      height = 0;
      cWidth = 0;
      cHeight = 0;
      return;
      
    } else if (dying) {
      if (currentAction != DEAD) {
        animation.setFrames((sprites.get(DEAD)));
        currentAction = DEAD;
        animation.setDelay(100);
      }
      
      if (animation.hasPlayedOnce()) {
        dead = true;
        dying = false;
        flinching = false;
        animation.setFrames(sprites.get(DEAD));
        animation.setFrame(14);
        animation.setDelay(-1);
      }
    }
    
    if(getHealth() <= 0 && !dead) {
      dying = true;
      flinching = false;
      width = 0;
      height = 0;
      cWidth = 0;
      cHeight = 0;
    }
    
    Vector2D seekForce = steering.seek(new Vector2D(player.getRectangle().getCenterX(), player.getRectangle().getCenterY()));
		Vector2D avoidanceForce = steering.obstacleAvoidance(map);
		Vector2D separationForce = steering.separation(enemies);
		
		Vector2D steeringForce = new Vector2D(); 
		
		if(!avoidanceForce.isZero())
			seekForce = new Vector2D();	
    
		steeringForce.setX(seekForce.getX() + avoidanceForce.getX() + separationForce.getX());
		steeringForce.setY(seekForce.getY() + avoidanceForce.getY() + separationForce.getY());
		steeringForce.divideByScalar(1);
		
		steeringForce.truncate(maxSpeed);
		
		velocity.setX(velocity.getX() + steeringForce.getX());
		velocity.setY(velocity.getY() + steeringForce.getY());
		
		velocity.truncate(maxSpeed);

		Vector2D toPlayer = new Vector2D(player.getRectangle().getCenterX() - 20, player.getRectangle().getCenterY() - 20).substract(new Vector2D(x, y));
		distanceToPlayer = toPlayer.getMagnitude();
		
		if(distanceToPlayer < Player.PLAYER_SIZE * 1.10 && !dead && !dying)
		{
      if (currentAction != ATTACKING && !flinching) {
			  attacking = true;
        
        attackingLeft = (velocity.getX() < 0);
        
        animation.setFrames(sprites.get(ATTACKING));
        currentAction = ATTACKING;
      }
      
      if (animation.getFrame() == 8 && attacking && !alreadyHitPlayer) {
        hitPlayer = true;
        alreadyHitPlayer = true;
      } else {
        hitPlayer = false;
      }
      
      if (animation.getFrame() != 8) {
        alreadyHitPlayer = false;
      }
		} else {
      attacking = false;
      hitPlayer = false;
      
      if (currentAction != WALKING && !flinching && !dying) {
        animation.setFrames(sprites.get(WALKING));
        currentAction = WALKING;
      }
    }
    
    animation.setDelay(100);
    animation.update();
    
		if(attacking || flinching || dying) {
      facingLeft = velocity.getX() < 0;
			velocity.zero();
    }
		
		x += velocity.getX();
		y += velocity.getY();
		
		if(velocity.getMagnitude() > 0){
			heading = velocity.normalize();
			
			angle = Math.acos(heading.dot(new Vector2D(1,0)));
			if(heading.getY() < 0)
				angle *= -1;
		}
  }
  
  public void readjustMapPosition(double x, double y) {
    map.setPosition(x, y);
  }
  
  public void setMapArray(Block[][] bm) {
    map.setMapArray(bm);
  }
  
  public Block[][] getMapArray() {
    return map.getMapArray();
  }
  
  public Map getMapObject() {
    return map;
  }
  
  public void setMapObject(Map m) {
    map = m;
  }
  
  public void setDead(boolean d) {
      dead = d;
  }

  public boolean getDead() {
      return dead;
  }
  
  public void draw(Graphics2D g) {
    setMapPosition();
    
    if (dead)
    {
      animation.setFrames(sprites.get(DEAD));
      animation.setFrame(14);
      
      if (facingLeft)
      {
        BufferedImage flippedImage = flipImage(animation.getImage());

        g.drawImage(flippedImage,
            (int) (x + xMap - 64 / 2),
            (int) (y + yMap - 64 / 2),
            null);
        //enemy facing right
      } else
      {
        g.drawImage(animation.getImage(),
            (int) (x + xMap - 64 / 2),
            (int) (y + yMap - 64 / 2),
            null);
      }
    }
    
    else if (dying) {
      if (facingLeft)
      {
        BufferedImage flippedImage = flipImage(animation.getImage());

        g.drawImage(flippedImage,
            (int) (x + xMap - 64 / 2),
            (int) (y + yMap - 64 / 2),
            null);
        //enemy facing right
      } else
      {
        g.drawImage(animation.getImage(),
            (int) (x + xMap - 64 / 2),
            (int) (y + yMap - 64 / 2),
            null);
      }
    }
  
    //draw enemy hit
    else if (flinching) {
      if (animation.hasPlayedOnce()) {
        animation.setFrames(sprites.get(WALKING));
        currentAction = WALKING;
        
        if (facingLeft) {
          velocity.setX(-0.01);
        }
        
        flinching = false;
      } else
      {
        if (facingLeft)
        {
          BufferedImage flippedImage = flipImage(animation.getImage());
          
          g.drawImage(flippedImage,
              (int) (x + xMap - 5 - width / 2),
              (int) (y + yMap - height / 2),
              null);
          //enemy facing right
        } else
        {
          g.drawImage(animation.getImage(),
            (int) (x + xMap - 22 - width / 2),
            (int) (y + yMap - height / 2),
            null);
        }
      }
    }
    //draw enemy
    //enemy facing left
    else if (velocity.getX() < 0 || (attackingLeft && attacking)) {
      //flips image horizontally
      BufferedImage flippedImage = flipImage(animation.getImage());
      if (attackingLeft && attacking) {
        g.drawImage(flippedImage,
            (int) (x + xMap - 47 - width / 2),
            (int) (y + yMap - 10 - height / 2),
            null);
      } else {
        g.drawImage(flippedImage,
            (int) (x + xMap - width / 2),
            (int) (y + yMap - height / 2),
            null);
      }
    //enemy facing right
    } else if (!attackingLeft && attacking)
    {
      g.drawImage(animation.getImage(),
          (int) (x + xMap - width / 2),
          (int) (y + yMap - 10 - height / 2),
          null);
    } else
    {
      g.drawImage(animation.getImage(),
          (int) (x + xMap - width / 2),
          (int) (y + yMap - height / 2),
          null);
    }
  }
  
  public ArrayList<Bullet> getBulletList() {
    return bulletList;
  }
  
  public double getMoveSpeed() {
    return moveSpeed;
  }
  
  public double getMaxMoveSpeed() {
    return maxSpeed;
  }
  
  public void setZombiesList(ArrayList<Enemy> zombies) {
    this.enemies = zombies;
  }
  
  public void setPlayer(Player player) {
    this.player = player;
  }
  
  public boolean getHitPlayer() {
    return hitPlayer;
  }
  
  public void setHitPlayer(boolean n) {
    hitPlayer = n;
  }
  
  public int getDamage() {
    return attackDamage;
  }
  
  public void setFlinch() {
    if (!dead && !dying)
    {
      animation.setFrames(sprites.get(HIT));
      animation.update();
      flinching = true;
    }
  }
  
  private BufferedImage flipImage(BufferedImage image) {
    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    tx.translate(-image.getWidth(null), 0);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    image = op.filter(image, null);
    return image;
  }
}