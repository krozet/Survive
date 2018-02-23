package GameState;

public class Wave
{
  private final int waveNumber;
  private final int numOfEnemies;
  private final int enemyHealth;
  private final int enemyDamage;
  private final int enemySpawnTimer;
  private final double enemySpeed;
  
  public Wave(int waveNumber, int numOfEnemies, int enemyHealth, int enemyDamage, int enemySpawnTimer, double enemySpeed) {
    this.waveNumber = waveNumber;
    this.numOfEnemies = numOfEnemies;
    this.enemyHealth = enemyHealth;
    this.enemyDamage = enemyDamage;
    this.enemySpawnTimer = enemySpawnTimer;
    this.enemySpeed = enemySpeed;
  }
  
  public int getWaveNumber() {
    return waveNumber;
  }
  
  public int getNumOfEnemies() {
    return numOfEnemies;
  }
  
  public int getEnemyHealth() {
    return enemyHealth;
  }
  
  public int getEnemyDamage() {
    return enemyDamage;
  }
  
  public double getEnemySpeed() {
    return enemySpeed;
  }
  
  public int getEnemySpawnTimer() {
    return enemySpawnTimer;
  }
}
