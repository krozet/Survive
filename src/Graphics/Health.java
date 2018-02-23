package Graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;

public class Health
{
  private File dir;
  private String[] EXTENSIONS;
  private FilenameFilter IMAGE_FILTER;
  private BufferedImage heart;
  
  private int numOfHearts;
  
  public Health(int health) {
    heart = loadFramesFromFolder("Resources/Heart");
    numOfHearts = health / 10;
  }
  
  public void setHealth(int health) {
    numOfHearts = health / 10;
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
  
  public BufferedImage loadFramesFromFolder(String file) {
    
    File dir = new File(file);
    BufferedImage image = null;

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

    if (dir.isDirectory())
    { 
      for (final File f : dir.listFiles(IMAGE_FILTER))
      {
        try {
          image = makeBackgroundTransparent(ImageIO.read(f));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return image;
  }
  
  public void draw(Graphics2D g) {
    int xDisplacement = 0;
    
    g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 1.0f));
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    for (int i = 0; i < numOfHearts; i++) {
      g.drawImage(heart, 850 + xDisplacement, 5, null);
      xDisplacement += 35;
      }
  }
}
