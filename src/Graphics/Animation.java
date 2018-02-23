package Graphics;

import java.awt.image.BufferedImage;

public class Animation {
	
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long time, lastTime;
	private long delay;
	
	private boolean playedOnce;
	
	public void Animation() {
		playedOnce = false;
	}
	
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		currentFrame = 0;
		time = 0;
    lastTime = 0;
		playedOnce = false;
	}
	
	public void setDelay(long d) { 
    delay = d; 
  }
  
	public void setFrame(int i) { 
    currentFrame = i; 
  }
	
	public void update() {
		
		if(delay == -1)
      return;
		
		time += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
    
    if(time > delay) {
			currentFrame++;
			time = 0;
		}
    
		if(currentFrame == frames.length) {
			currentFrame = 0;
      playedOnce = true;
		}
	}
	
	public int getFrame() {
    return currentFrame; 
  }
  
	public BufferedImage getImage() { 
    return frames[currentFrame]; 
  }
  
	public boolean hasPlayedOnce() { 
    return playedOnce; 
  }
  
  public void setPlayedOnce(boolean b) {
    playedOnce = b;
  }
}

