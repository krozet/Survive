package Main;

import Music.MusicPlayer;
import javax.swing.*;
import javax.swing.JFrame;

public class GameWorld extends JApplet implements Runnable{
  
  public static void main(String[] args)
  {
    GamePanel game = new GamePanel();
    ThreadPool pool = new ThreadPool(2);
    
    JFrame window = new JFrame("Survive");
    window.setContentPane(game);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.pack();
    window.setVisible(true);
    
    MusicPlayer musicPlayer = new MusicPlayer("Music.wav");
    
    pool.runTask(game);
    pool.runTask(musicPlayer);
    pool.join();//runs both thread at same time
  }

  @Override
  public void run()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}