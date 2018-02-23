package GameObjects;

import Graphics.Animation;
import Main.GamePanel;
import Map.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Player extends GameObject {

  //mouse
  private int mouseX;
  private int mouseY;
  private boolean leftMouseButton;
  private boolean rightMouseButton;
  
  public static final int PLAYER_SIZE = 64;
  
  private final static int MAX_HEALTH = 30;
  private int health;
  private boolean dead;
    
  //attacking
  private ArrayList<Bullet> bulletList;
  private boolean flinching;
  private int bulletDamage;
  private int flinchCounter;
  private long flinchTimer;
  private double xGunDisplacement;
  private double xShootPointDisplacement;
  private double yGunDisplacement;
  private double yShootPointDisplacement;
  
  //vectos
  private Vector2D shootPoint;
  private Vector2D newPoint;
  
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
  private int lastLookingDirection;
  private int direction;

  
  //animations
  private ArrayList<BufferedImage[]> sprites;
  private BufferedImage[] machineGunSprites;
  private boolean gunOnTop;
  
  //animation actions  
  public static final int MOVING_EAST = 0;
  public static final int MOVING_NORTHEAST = 1;
  public static final int MOVING_NORTHWEST = 2;
  public static final int MOVING_NORTH = 3;
  public static final int MOVING_SOUTHEAST = 4;
  public static final int MOVING_SOUTHWEST = 5;
  public static final int MOVING_SOUTH = 6;
  public static final int MOVING_WEST = 7;
  
  public Player(Map map)
  {
    super(map);
    init();

    loadCharacterSprites();
    loadMachineGunSprites();
    
    currentAction = 0;
  }
  
  private void init() {
    //tested values
    width = 68;
    height = 64;
    cWidth = 52;
    cHeight = 44;
    
    speed = 0;
    angleSpeed = 0;
    moveSpeed = 1;
    angleMoveSpeed = 1.5;
    angle = 180;
    
    maxSpeed = 2;
    maxAngleSpeed = 3;
    stopSpeed = 0.5;
    flinchTimer = 100000;
    
    health = 50;
    bulletDamage = 20;
    bulletList = new ArrayList();
    lookingDirection = new boolean[8];
    Arrays.fill(lookingDirection, false);
    dead = false;
    
    sprites = new ArrayList();
    machineGunSprites = new BufferedImage[8];
    animation = new Animation();
    direction = 0;
    
    rect = new Rectangle((int)x, (int)y, cWidth-4, cHeight);
    collisionBox = rect;
    
    shootPoint = new Vector2D();
    flinchCounter = 0;
  }
  
  private void loadCharacterSprites() {
    frames = loadFramesFromFolder("Resources/Character", 32);

    for (int i = 0; i < frames.length; i += 4) {
      BufferedImage[] tempFrames = new BufferedImage[4];

      for (int j = 0; j < tempFrames.length; j++) {
        tempFrames[j] = frames[i+j];
      }
      sprites.add(tempFrames);
    }
  }
  
  private void loadMachineGunSprites() {
    machineGunSprites = loadFramesFromFolder("Resources/Gun", 8);
  }
  
  public int getHealth() { 
    return health;
  }
  
  public void setHealth(int k) {
        health = k;
    }
  
  public void setMousePosition(int x, int y) {
    mouseX = x;
    mouseY = y;
  }
  
  public void setLeftMouseButton(boolean b) {
    leftMouseButton = b;
  }
  
  public void setRightMouseButton(boolean b) {
    rightMouseButton = b;
  }
  
  public boolean getLeftMouseButton() {
    return leftMouseButton;
  }
  
  public boolean getRightMouseButton() {
    return rightMouseButton;
  }
  
  public int getMouseX() {
    return mouseX;
  }
  
  public int getMouseY() {
    return mouseY;
  }
  
  public int getBulletDamage() {
        return bulletDamage;
    }
  
  private void getNextPosition() {
    //moving in positive x direction
    if (east || northEast || southEast) {
      xSpeed += moveSpeed;
      if (xSpeed > maxSpeed) {
        xSpeed = maxSpeed;
      }
    }
    //moving in negative x direction
    if (west || northWest || southWest) {
      xSpeed -= moveSpeed;
      if (xSpeed < -maxSpeed) {
        xSpeed = -maxSpeed;
      }
    }
    //moving in positive y direction
    if (south || southWest || southEast) {
      ySpeed += moveSpeed;
      if (ySpeed > maxSpeed) {
        ySpeed = maxSpeed;
      }
    }
    //moving in negative y direction
    if (north || northWest || northEast) {
      ySpeed -= moveSpeed;
      if (ySpeed < -maxSpeed) {
        ySpeed = -maxSpeed;
      }
    }
    //not moving in any direction
    if (isNotMoving()) {
      //stop moving in the x direction
      if (xSpeed > 0) {
        xSpeed -= stopSpeed;
        if (xSpeed < 0) {
          xSpeed = 0;
        }
      }
      else if (xSpeed < 0) {
        xSpeed += stopSpeed;
        if (xSpeed > 0) {
          xSpeed = 0;
        }
      }
      //stop moving in the y direction
      if (ySpeed > 0) {
        ySpeed -= stopSpeed;
        if (ySpeed < 0) {
          ySpeed = 0;
        }
      }
      else if (ySpeed < 0) {
        ySpeed += stopSpeed;
        if (ySpeed > 0) {
          ySpeed = 0;
        }
      }
    }
  }
  
  private boolean isNotMoving() {
    return !north && !south && !east && !west;
  }
  
  private void checkPowerUp() {
    if (useShield) {
      health += 20;
      if (MAX_HEALTH < health) {
        health = MAX_HEALTH;
      }
    }
    setUseShield(false);
  }
  
  public void update() {
    getNextPosition();
    checkMapCollision();
    checkPowerUp();
    
    setPosition(xTemp, yTemp);
    getLookingDirection();
    getNewDirection();
    
    newPoint = new Vector2D(Math.sin(Math.toRadians(angle)), Math.cos(Math.toRadians(angle)));
    
    shootPoint.setX(newPoint.getX() + x + xShootPointDisplacement - width/2);
		shootPoint.setY(newPoint.getY() + y + yShootPointDisplacement - height/2);
        
    if(getHealth() <= 0) {
      setDead(true);
    }
  }
  
  private void getLookingDirection() {
    double mWidth = mouseX - x - xMap - GamePanel.FULL_WIDTH/2;
    double mHeight = mouseY - y - yMap - GamePanel.FULL_HEIGHT/1.5 - width/1.5;
    
    angle = Math.atan(mHeight/mWidth);
    angle = Math.toDegrees(angle);
    
    //adjust angle to 0-360 degress
    if (mWidth < 0 && mHeight > 0) {
      angle = (90 - angle * -1) + 90;
    } else if (mWidth < 0 && mHeight < 0) {
      angle = angle + 180;
    } else if (mWidth > 0 && mHeight < 0) {
      angle = (90 - angle * -1) + 270;
    }
    
    //set looking direction
    if (angle <= 360 && angle >= 330) {
      lookingDirection[LOOKING_EAST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_EAST;
      gunOnTop = false;
    }
    else if (angle >= 0 && angle < 30) {
      lookingDirection[LOOKING_EAST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_EAST;
      gunOnTop = false;
    }
    else if (angle < 60 && angle >= 30) {
      lookingDirection[LOOKING_SOUTHEAST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_SOUTHEAST;
      gunOnTop = false;
    }
    else if (angle < 120 && angle >= 60) {
      lookingDirection[LOOKING_SOUTH] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_SOUTH;
      gunOnTop = true;
    }
    else if (angle < 150 && angle >= 120) {
      lookingDirection[LOOKING_SOUTHWEST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_SOUTHWEST;
      gunOnTop = false;
    }
    else if (angle < 210 && angle >= 150) {
      lookingDirection[LOOKING_WEST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_WEST;
      gunOnTop = false;
    }
    else if (angle < 240 && angle >= 210) {
      lookingDirection[LOOKING_NORTHWEST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_NORTHWEST;
      gunOnTop = false;
    }
    else if (angle < 300 && angle >= 240) {
      lookingDirection[LOOKING_NORTH] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_NORTH;
      gunOnTop = false;
    }
    else if (angle < 330 && angle >= 300) {
      lookingDirection[LOOKING_NORTHEAST] = true;
      lookingDirection[lastLookingDirection] = false;
      lastLookingDirection = LOOKING_NORTHEAST;
      gunOnTop = false;
    }
  }
  
  public void getNewDirection() {
    
    if (lookingDirection[LOOKING_NORTH]) {
      if (currentAction != MOVING_NORTH) {
      direction = MOVING_NORTH;
      animation.setFrames(sprites.get(MOVING_NORTH));
      xGunDisplacement = 36;
      yGunDisplacement = 10;
      xShootPointDisplacement = 20;
      yShootPointDisplacement = 6;
      }
    } else if (lookingDirection[LOOKING_EAST]) {
      if (currentAction != MOVING_EAST) {
      direction = MOVING_EAST;
      animation.setFrames(sprites.get(MOVING_EAST));
      xGunDisplacement = 26;
      yGunDisplacement = 38;
      xShootPointDisplacement = 71;
      yShootPointDisplacement = 62;
      }
    } else if (lookingDirection[LOOKING_SOUTH]) {
      if (currentAction != MOVING_SOUTH) {
      direction = MOVING_SOUTH;
      animation.setFrames(sprites.get(MOVING_SOUTH));
      xGunDisplacement = 8;
      yGunDisplacement = 36;
      xShootPointDisplacement = 45;
      yShootPointDisplacement = 63;
      }
    } else if (lookingDirection[LOOKING_WEST]) {
      if (currentAction != MOVING_WEST) {
      direction = MOVING_WEST;
      animation.setFrames(sprites.get(MOVING_WEST));
      xGunDisplacement = -15;
      yGunDisplacement = 37;
      xShootPointDisplacement = -17;
      yShootPointDisplacement = 14;
      }
    } else if (lookingDirection[LOOKING_NORTHEAST]) {
      if (currentAction != MOVING_NORTHEAST) {
      direction = MOVING_NORTHEAST;
      animation.setFrames(sprites.get(MOVING_NORTHEAST));
      xGunDisplacement = 28;
      yGunDisplacement = 24;
      xShootPointDisplacement = 50;
      yShootPointDisplacement = 40;
      }
    } else if (lookingDirection[LOOKING_NORTHWEST]) {
      if (currentAction != MOVING_NORTHWEST) {
      direction = MOVING_NORTHWEST;
      animation.setFrames(sprites.get(MOVING_NORTHWEST));
      xGunDisplacement = -15;
      yGunDisplacement = 24;
      xShootPointDisplacement = -35;
      yShootPointDisplacement = 5;
      }
    } else if (lookingDirection[LOOKING_SOUTHEAST]) {
      if (currentAction != MOVING_SOUTHEAST) {
      direction = MOVING_SOUTHEAST;
      animation.setFrames(sprites.get(MOVING_SOUTHEAST));
      xGunDisplacement = 24;
      yGunDisplacement = 32;
      xShootPointDisplacement = 84;
      yShootPointDisplacement = 64;
      }
    } else if (lookingDirection[LOOKING_SOUTHWEST]) {
      if (currentAction != MOVING_SOUTHWEST) {
      direction = MOVING_SOUTHWEST;
      animation.setFrames(sprites.get(MOVING_SOUTHWEST));
      xGunDisplacement = -12;
      yGunDisplacement = 32;
      xShootPointDisplacement = 6;
      yShootPointDisplacement = 29;
      }
    }
    
    if (isNotMoving()) {
      animation.setDelay(-1);
    } else {
      animation.setDelay(100);
    }
    
    currentAction = direction;
    animation.update();
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
    
    //draw player
    if (flinching) {
      long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
      if (elapsed / 100 % 2 == 0 && flinchCounter < 20) {
        flinchCounter++;
        return;
      }
      
      if (flinchCounter >= 20) {
        flinchCounter = 0;
        flinching = false;
      }
    }
    
    if (!gunOnTop) {
    g.drawImage(machineGunSprites[currentAction],
        (int)(x + xGunDisplacement + xMap - width / 2),
        (int)(y + yGunDisplacement + yMap - height / 2),
        null);
          
    g.drawImage(animation.getImage(),
        (int)(x + xMap - width / 2),
        (int)(y + yMap - height / 2),
        null);
    } else {
      g.drawImage(animation.getImage(),
        (int)(x + xMap - width / 2),
        (int)(y + yMap - height / 2),
        null);
    
    g.drawImage(machineGunSprites[currentAction],
        (int)(x + xGunDisplacement + xMap - width / 2),
        (int)(y + yGunDisplacement + yMap - height / 2),
        null);
    }
  }
  
  public void fire(ArrayList<Enemy> zombies) {
      Bullet b = new Bullet(map, angle, shootPoint.getX(), shootPoint.getY(), zombies);
      bulletList.add(b);
  }
  
  public ArrayList<Bullet> getBulletList() {
    return bulletList;
  }
  
  public void playerHealth(Graphics2D hp, int x, int y) {
            hp.setColor(Color.RED);
            hp.fillRect(x, y, 200, 50);//(x, y, HP bar length, HP bar height)
            
            hp.setColor(Color.GREEN);
            hp.fillRect(x,
                    y,
                    getHealth(),
                    50);
            hp.setColor(Color.WHITE);
            hp.drawRect(x, y, 200, 50);
        
    }

  public void setFlinch() {
    flinching = true;
    flinchTimer = System.nanoTime();
  }
}
