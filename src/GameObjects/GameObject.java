package GameObjects;

//import Map.PowerUp;
import Graphics.Animation;
import Map.Block;
import Map.Map;
import Main.GamePanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;

public abstract class GameObject
{
  protected File dir;
  protected String[] EXTENSIONS;
  protected FilenameFilter IMAGE_FILTER;
  
  protected Image image;
  protected Map map;
  protected Rectangle rect;
  protected Shape collisionBox;
  protected Player otherPlayer;
  protected int blockSize;
  
  protected boolean intersected;
  protected boolean topInter, botInter, rightInter, leftInter;
  protected boolean useShield;
  
  //dimensions
  protected int width, height;
  
  //position
  protected double x, y;
  protected double xMap, yMap;
  
  //speed
  protected double xSpeed, ySpeed;
  
  //collision box
  protected int cWidth, cHeight;
  protected int zomWidth, zomHeight;
    
  protected double angle;
  protected double angleMoveSpeed;
  protected double angleSpeed;
  protected double maxAngleSpeed;
  
  protected double speed;
  
  //collision
  protected int curRow, curCol;
  protected double xDest, yDest, xTemp, yTemp;
  protected boolean topLeft, topRight, bottomLeft, bottomRight;
  protected boolean tLShield1, tRShield1, bLShield1, bRShield1;
  protected boolean tLShield2, tRShield2, bLShield2, bRShield2;
  
  protected int currentAction, prevAction;
  
  //movement
  protected boolean forward, turnLeft, backwards, turnRight;
  protected boolean north, east, south, west, northEast, northWest, southEast, southWest;
  protected double moveSpeed, maxSpeed, stopSpeed;
  protected BufferedImage[] frames;
  
  //animations
  protected Animation animation;
  
  public GameObject (Map map) {
    this.map = map;
    blockSize = map.getBlockSize();
  }
  
  public boolean bulletIntersects (GameObject obj) {
    Rectangle r1 = getRectangle();
    Rectangle r2 = obj.getRectangle();
    return r1.intersects(r2);
  }
  
  public boolean intersects (GameObject obj) {
     return collisionBox.intersects(obj.getCollisionBox().getBounds2D());
  }
  
  public Rectangle getRectangle() {
    return new Rectangle((int)x-width/2, (int)y-height/2, width, height);
  }
  
  public void setTransformation(int x, int y) {
    AffineTransform at = new AffineTransform();
    rect.setBounds(x-cWidth/2, y-cHeight/2, cWidth, cHeight);
    at.rotate(Math.toRadians(-angle + 6), rect.getCenterX(), rect.getCenterY());
    collisionBox = at.createTransformedShape(rect);
  }
  
  public void calculateCorners(double x, double y)
  {

    //surrounding tiles for 64pxl
    int leftTile = (int) (x - cWidth / 2) / blockSize;
    int rightTile = (int) (x + cWidth / 2 - 1) / blockSize;
    int topTile = (int) (y - cHeight / 2) / blockSize;
    int bottomTile = (int) (y + cHeight / 2 - 1) / blockSize;

    //four corners for 64pxl
    int tL = map.getType(topTile, leftTile);
    int tR = map.getType(topTile, rightTile);
    int bL = map.getType(bottomTile, leftTile);
    int bR = map.getType(bottomTile, rightTile);

    //handles objects larger than 32pxl
    if (cWidth > 32 && height > 32)
    {
      int cWidthX = 32;
      int cHeightX = 32;
      int xBuffer = 10;
      int leftTileX = (int) (x - xBuffer - blockSize + cWidthX / 2) / blockSize;
      int rightTileX = (int) (x + xBuffer + cWidthX / 2 - 1) / blockSize;
      int topTileX = (int) (y - cHeightX / 2) / blockSize;
      int bottomTileX = (int) (y + cHeightX / 2 - 1) / blockSize;
      int tLX = map.getType(topTileX, leftTileX);
      int tRX = map.getType(topTileX, rightTileX);
      int bLX = map.getType(bottomTileX, leftTileX);
      int bRX = map.getType(bottomTileX, rightTileX);

      int cWidthY = 32;
      int cHeightY = 44;
      int yBuffer = 10;
      int leftTileY = (int) (x - yBuffer + cWidthY / 2) / blockSize;
      int rightTileY = (int) (x + yBuffer + cWidthY / 2 - 1) / blockSize;
      int topTileY = (int) (y - cHeightY / 2) / blockSize;
      int bottomTileY = (int) (y + cHeightY / 2 - 1) / blockSize;
      int tLY = map.getType(topTileY, leftTileY);
      int tRY = map.getType(topTileY, rightTileY);
      int bLY = map.getType(bottomTileY, leftTileY);
      int bRY = map.getType(bottomTileY, rightTileY);

      //set boolean blocks
      topLeft = (tL != Block.EMPTY_TILE) || (tLX != Block.EMPTY_TILE) || (tLY != Block.EMPTY_TILE);
      topRight = (tR != Block.EMPTY_TILE) || (tRX != Block.EMPTY_TILE) || (tRY != Block.EMPTY_TILE);
      bottomLeft = (bL != Block.EMPTY_TILE) || (bLX != Block.EMPTY_TILE) || (bLY != Block.EMPTY_TILE);
      bottomRight = (bR != Block.EMPTY_TILE) || (bRX != Block.EMPTY_TILE) || (bRY != Block.EMPTY_TILE);
    }
    else {
      topLeft = (tL != Block.EMPTY_TILE);
      topRight = (tR != Block.EMPTY_TILE);
      bottomLeft = (bL != Block.EMPTY_TILE);
      bottomRight = (bR != Block.EMPTY_TILE);
    }
  }
  
  public void checkMapCollision() {
    curCol = (int)x / blockSize;
    curRow = (int)y / blockSize;
    
    xDest = x + xSpeed;
    yDest = y + ySpeed;
    
    xTemp = x;
    yTemp = y;
   
    //y direction movement
    calculateCorners(x, yDest);
    //moving upwards (ySpeed is negative for upwards direction)
    if (ySpeed < 0) {
      //if there is a block above, stop the speed and place
      //the object right below the block
      
      if (topLeft || topRight) {
        ySpeed = 0;
        yTemp = curRow * blockSize + cHeight/2;
      }
      else {
        yTemp += ySpeed;
      }
    }
    //moving downwards
    if (ySpeed > 0) {
      if (bottomLeft || bottomRight) {
        ySpeed = 0;
        yTemp = (curRow + 1) * blockSize - cHeight/2;
      }
      else {
        yTemp += ySpeed;
      }
    }
    
    //x direction movement
    calculateCorners(xDest, y);
    //moving left
    if (xSpeed < 0) {
      if (topLeft || bottomLeft) {
        xSpeed = 0;
        xTemp = curCol * blockSize + cWidth/2;
      }
      else {
        xTemp += xSpeed;
      }
    }
    //moving right
    if (xSpeed > 0) {
      if (topRight || bottomRight) {
        xSpeed = 0;
        xTemp = (curCol + 1) * blockSize - cWidth/2;
      }
      else {
        xTemp += xSpeed;
      }
    }   
  }
  
  public static Image makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
  
  private static BufferedImage imageToBufferedImage(Image image) {

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;
    }
  
  public BufferedImage makeBackgroundTransparent(BufferedImage image) {
    return imageToBufferedImage(makeColorTransparent(image, Color.WHITE));
  }
  
  public BufferedImage[] loadFramesFromFolder(String file, int size) {
    
    File dir = new File(file);

    EXTENSIONS = new String[]{
        "gif", "png", "jpg"
    };
    
    IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    //frames rotating 45 degrees counter clockwise
    BufferedImage[] tempFrames = new BufferedImage[size];
    int count = 0;
    
    if (dir.isDirectory())
    { 
      for (final File f : dir.listFiles(IMAGE_FILTER))
      {
        try {
          BufferedImage image = ImageIO.read(f);
          image = makeBackgroundTransparent(image);
          
          tempFrames[count++] = image;

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    return tempFrames;
  }
  
  public int getX() {
    return (int) x;
  }
  
  public int getY() {
    return (int) y;
  }
  
  public int getXMap() {
    return (int) xMap;
  }
  
  public int getYMap() {
    return (int) yMap;
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public int getCHeight() {
    return cHeight;
  }
  
  public int getCWidth() {
    return cWidth;
  }
  
   public Shape getCollisionBox() {
    return collisionBox;
  }
  
  public void setOtherPlayer(Player otherPlayer) {
    this.otherPlayer = otherPlayer;
  }
  
  public void setPosition (double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public void setSpeeds(double xSpeed, double ySpeed) {
    this.xSpeed = xSpeed;
    this.ySpeed = ySpeed;
  }
  
  public void setAngle(int a) {
    angle = a;
  }
  
  public void setMapPosition() {
    xMap = map.getX();
    yMap = map.getY();
  }
  
  public boolean notOnScreen() {
    return (x + xMap + width < 0)
        || (x + xMap - width > GamePanel.WIDTH)
        || (y + yMap + height < 0)
        || (y + yMap - height > GamePanel.HEIGHT);
  }
  
  public void setTurnRight (boolean b) {
    turnRight = b;
  } 
  
  public void setTurnLeft (boolean b) {
    turnLeft = b;
  }
  
  public void setForward (boolean b) {
    forward = b;
  }
  
  public void setBackwards (boolean b) {
    backwards = b;
  }
  
  public void setEast (boolean b) {
    east = b;
  } 
  
  public void setWest (boolean b) {
    west = b;
  }
  
  public void setNorth (boolean b) {
    north = b;
  }
  
  public void setSouth (boolean b) {
    south = b;
  }
  
  public void setNorthEast (boolean b) {
    northEast = b;
  }
  
  public void setNorthWest (boolean b) {
    northWest = b;
  }
  
  public void setSouthEast (boolean b) {
    southEast = b;
  }
  
  public void setSouthWest (boolean b) {
    southWest = b;
  }
  
  public void setCurrentAction(int k) {
    currentAction = k;
  }
  
  public void setPrivousAction(int k) {
    prevAction = k;
  }
  
  public void setUseShield(boolean b) {
    useShield = b;
  }
}

