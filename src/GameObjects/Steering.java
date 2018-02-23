package GameObjects;

import Map.Block;
import Map.Map;
import java.util.ArrayList;

public class Steering {
	
	private Enemy enemy;
	
	// for obstacle avoidance
	private Vector2D ahead, ahead2, enemyPos, avoidance;
	private final int MAXSEEAHEAD = 100; 
	
	private int width, height;
	
	
	public Steering(Enemy enemy){
		this.enemy = enemy;
		width = enemy.getWidth()/2;
		height  = enemy.getHeight()/2;
		
		ahead = new Vector2D();
		ahead2 = new Vector2D();
		avoidance = new Vector2D();
	}
	
	public Vector2D seek(Vector2D target){
		Vector2D desiredVelocity = target.substract(new Vector2D(enemy.getX(), enemy.getY()));
		desiredVelocity = desiredVelocity.normalize();
		desiredVelocity.multyplyByScalar(enemy.getMaxMoveSpeed());
		return desiredVelocity.substract(new Vector2D(enemy.getMoveSpeed(), enemy.getMoveSpeed()));
	}
	
	public Vector2D arrive(Vector2D target){
		Vector2D desiredVelocity = target.substract(new Vector2D(enemy.getX(), enemy.getY()));
		double distance = desiredVelocity.getMagnitude();
		desiredVelocity = desiredVelocity.normalize();
		if(distance < Enemy.ENEMY_SIZE){
			desiredVelocity = desiredVelocity.multyplyByScalar(enemy.getMaxMoveSpeed()*distance/Enemy.ENEMY_SIZE);
			
		}else{
			desiredVelocity = desiredVelocity.multyplyByScalar(enemy.getMaxMoveSpeed());
		}
		return desiredVelocity.substract(new Vector2D(enemy.getMoveSpeed(), enemy.getMoveSpeed()));
	}
	
	public Vector2D flee(Vector2D target){
		Vector2D desiredVelocity = new Vector2D(enemy.getX(), enemy.getY()).substract(target);
		desiredVelocity = desiredVelocity.normalize();
		desiredVelocity.multyplyByScalar(enemy.getMaxMoveSpeed());
		return desiredVelocity.substract(new Vector2D(enemy.getMoveSpeed(), enemy.getMoveSpeed()));
		
	}
	
	// Obstacle avoidance	
	public Vector2D obstacleAvoidance(Map map){
		
		Vector2D position = new Vector2D(enemy.getX(), enemy.getY());
		Vector2D velocity = new Vector2D(enemy.getMoveSpeed(), enemy.getMoveSpeed());
;
		velocity = velocity.normalize();
		
		ahead = new Vector2D(position.getX() + width + velocity.getX()*MAXSEEAHEAD, 
				position.getY() + height + velocity.getY()*MAXSEEAHEAD);
		
		ahead2 = new Vector2D(position.getX() + width + velocity.getX()*MAXSEEAHEAD*0.5, 
				position.getY() + height + velocity.getY()*MAXSEEAHEAD*0.5);
		
		enemyPos = new Vector2D(position.getX() + width, 
				position.getY() + height);
		
 		
		Block obstacle = findObstacle(map);
		
		avoidance = new Vector2D();
		
		if(obstacle != null)
		{
			avoidance.setX(ahead.getX() - obstacle.getRectangle().getCenterX());
			avoidance.setY(ahead.getY() - obstacle.getRectangle().getCenterY());
			
			avoidance = avoidance.normalize();
      //max force
			avoidance.multyplyByScalar(2);
			
		}
		return avoidance;
	}
	
	private double distance(Vector2D a, Vector2D b){
		double width = a.getX() - b.getX();
		double height = a.getY() - b.getY();
		
		return Math.sqrt(width*width + height*height);
	}
	
	private boolean collision(Vector2D ahead, Vector2D ahead2, Vector2D enemyPos, Block obstacle){
		Vector2D center = new Vector2D(obstacle.getRectangle().getCenterX(), obstacle.getRectangle().getCenterY());
		
		return distance(center, ahead) <= Block.BLOCK_SIZE/2 + 15 ||
				distance(center, ahead2) <= Block.BLOCK_SIZE/2 + 15
				|| distance(center, enemyPos) <= Block.BLOCK_SIZE/2 + 15;
	}
	
	private Block findObstacle(Map map){
		Block obstacle = null;
		ArrayList<Block> nonEmptyBlocks = map.getNonEmptyBlocks();
    
		for(Block block : nonEmptyBlocks)
		{
			boolean collision = collision(ahead, ahead2, enemyPos, block);
			
			if(collision && (obstacle == null || distance(new Vector2D(obstacle.getRectangle().getCenterX(),
                                                    obstacle.getRectangle().getCenterY()),
                                                    new Vector2D(enemy.getX(), enemy.getY())) 
                                         > distance(new Vector2D(block.getRectangle().getCenterX(),
                                                    block.getRectangle().getCenterY()),
                                                    new Vector2D(enemy.getX(), enemy.getY())))){
				obstacle = block;
			}
		}
		return obstacle;
	}
	
	public Vector2D separation(ArrayList zombies){
		
		ArrayList<Enemy> neighbors = getNeighbors(zombies);
		
		Vector2D separationForce = new Vector2D();
		
		for(int i = 0; i < neighbors.size(); i++)
		{
			Enemy neighbor = neighbors.get(i);
			
			Vector2D fleeForce = flee(new Vector2D(neighbor.getX(), neighbor.getY()));
			
			separationForce.setX(separationForce.getX() + fleeForce.getX());
			separationForce.setY(separationForce.getY() + fleeForce.getY());
			
		}
		return separationForce;
	}
	
	private ArrayList<Enemy> getNeighbors(ArrayList<Enemy> enemies){
		ArrayList <Enemy> neighbors = new ArrayList();
		
    if (enemies != null) {
		for(int i = 0; i < enemies.size(); i++)
		{
      Enemy en = enemies.get(i);
      
        if (enemy.equals(en))
        {
          continue;
        }
        if (en.getDead())
        {
          continue;
        }
        if (distance(new Vector2D(enemy.getX(), enemy.getY()), new Vector2D(en.getX(), en.getY())) < Enemy.ENEMY_SIZE)
        {
          neighbors.add(en);
        }
      }
    }
		return neighbors;
	}
}