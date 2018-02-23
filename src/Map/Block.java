package Map;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Block{
  
  protected BufferedImage image;
  protected Rectangle rect;
  protected int type;
  
  public static final int EMPTY_TILE = 0;
  public static final int BREAKABLE = 1;
  public static final int UNBREAKABLE = 2;
  
  public static final int BLOCK_SIZE = 32;
  
  public Block(int type, int x, int y, int width, int height) {
    this.type = type;
    rect = new Rectangle(x, y, width, height);
    loadImage();
  }
  
  public BufferedImage getImage() {
    return image;
  }
  
  private void loadImage()
  {
    try
    {
      if (type == BREAKABLE)
      {
      }

      if (type == UNBREAKABLE)
      {
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public int getType() {
    return type;
  }
  
   public void setType(int k) {
    type = k;
  }
  
  public Rectangle getRectangle() {
    return rect;
  }
}
