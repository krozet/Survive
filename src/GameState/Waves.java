package GameState;

import java.util.ArrayList;

public class Waves
{
  private final static int WAVE_ONE = 0;
  private final static int WAVE_TWO = 1;
  private final static int WAVE_THREE = 2;
  private final static int WAVE_FOUR = 3;
  private final static int WAVE_FIVE = 4;
  private final static int WAVE_SIX = 5;
  private final static int WAVE_SEVEN = 6;
  private final static int WAVE_EIGHT = 7;
  private final static int WAVE_NINE = 8;
  private final static int WAVE_TEN = 9;
  private final int numOfWaves;
  
  private int currentWave;
  
  ArrayList<Wave> waves; 
  
  public Waves() {
    currentWave = WAVE_ONE;
    waves = new ArrayList();
    init();
    
    numOfWaves = waves.size();
  }
  
  private void init() {
    //waveNum, numOfEnemies, enemyHealth, enemyDamage, enemySpawnTimer
    waves.add(new Wave(WAVE_ONE, 50, 30, 10, 500, 1.25));
    waves.add(new Wave(WAVE_TWO, 75, 30, 10, 400, 1.35));
    waves.add(new Wave(WAVE_THREE, 100, 30, 10, 400, 1.45));
    waves.add(new Wave(WAVE_FOUR, 100, 45, 10, 300, 1.5));
    waves.add(new Wave(WAVE_FIVE, 100, 45, 10, 250, 1.6));
    waves.add(new Wave(WAVE_SIX, 100, 45, 20, 200, 1.75));
    waves.add(new Wave(WAVE_SEVEN, 100, 45, 20, 100, 1.8));
    waves.add(new Wave(WAVE_EIGHT, 100, 45, 20, 50, 1.85));
    waves.add(new Wave(WAVE_NINE, 100, 60, 20, 50, 1.9));
    waves.add(new Wave(WAVE_TEN, 100, 75, 20, 50, 2.0));
  }
  
  public void nextWave() {
    currentWave++;
  }
  
  public boolean isComplete() {
    return currentWave >= numOfWaves;
  }
  
  public Wave getWave() {
    if (!isComplete()) {
      return waves.get(currentWave);
    }
    return null;
  }
  
  public int getWaveNumber() {
    if (!isComplete()) {
      return waves.get(currentWave).getWaveNumber();
    }
    return -1;
  }
  
  public int getNumOfEnemies() {
    if (!isComplete()) {
      return waves.get(currentWave).getNumOfEnemies();
    }
    return -1;
  }
  
  public int getEnemyHealth() {
    if (!isComplete()) {
      return waves.get(currentWave).getEnemyHealth();
    }
    return -1;
  }
  
  public int getEnemyDamage() {
    if (!isComplete()) {
      return waves.get(currentWave).getEnemyDamage();
    }
    return -1;
  }
  
  public double getEnemySpeed() {
    if (!isComplete()) {
      return waves.get(currentWave).getEnemySpeed();
    }
    return -1;
  }
  
  public int getEnemySpawnTimer() {
    if (!isComplete()) {
      return waves.get(currentWave).getEnemySpawnTimer();
    }
    return -1;
  }
}
