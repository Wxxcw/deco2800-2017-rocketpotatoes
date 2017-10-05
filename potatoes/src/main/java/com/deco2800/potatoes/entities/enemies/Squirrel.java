package com.deco2800.potatoes.entities.enemies;

import com.deco2800.potatoes.collisions.Shape2D;
import com.deco2800.potatoes.collisions.Circle2D;
import com.deco2800.potatoes.entities.*;
import com.deco2800.potatoes.entities.health.HasProgress;
import com.deco2800.potatoes.entities.health.ProgressBarEntity;
import com.deco2800.potatoes.entities.player.Archer;
import com.deco2800.potatoes.entities.player.Caveman;
import com.deco2800.potatoes.entities.player.Player;
import com.deco2800.potatoes.entities.player.Wizard;
import com.deco2800.potatoes.entities.portals.BasePortal;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.managers.PathManager;
import com.deco2800.potatoes.managers.PlayerManager;
import com.deco2800.potatoes.util.WorldUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * The standard & most basic enemy in the game - a squirrel. Currently attacks and follows player.
 */
public class Squirrel extends EnemyEntity implements Tickable, HasProgress {

	private static final transient String TEXTURE_LEFT = "squirrel";
	private static final transient float HEALTH = 100f;
	private static final transient float ATTACK_RANGE = 8f;
	private static final transient int ATTACK_SPEED = 500;
	private static final EnemyProperties STATS = initStats();
	private static final String enemyType = "squirrel";

	private static final float SPEED = 0.05f;

	private static Class<?> goal = Player.class;

	private static EnemyTargets targets = initTargets();



	private AbstractEntity target = null;
	private Shape2D targetNode = null;

	private static final ProgressBarEntity PROGRESS_BAR = new ProgressBarEntity();

	private Direction currentDirection; // The direction the enemy faces
	//public enum PlayerState {idle, walk, attack, damaged, death}  // useful for when sprites for different states become available

	/***
	 * Default constructor for serialization
	 */
	public Squirrel() {
		this(0, 0);
	}

	/**
	 * Constructs a new Squirrel entity with pre-defined size and rendering lengths to match.
	 *
	 * @param posX The x coordinate the created squirrel will spawn from
	 * @param posY The y coordinate the created squirrel will spawn from
	 */
	public Squirrel(float posX, float posY) {
        super(new Circle2D(posX, posY, 0.665f), 0.60f, 0.60f, TEXTURE_LEFT, HEALTH, SPEED, goal);


		Map<Integer, AbstractEntity> entities = GameManager.get().getWorld().getEntities();

		for (AbstractEntity entity : entities.values()) {
			if (entity instanceof Player) {					//(entity.getClass().isAssignableFrom(goal)) {
				this.target = entity;
				break;
			}
		}
	}


	/**
	 * Squirrel follows it's path.
	 * Requests a new path whenever it collides with a staticCollideable entity
	 * moves directly towards the player once it reaches the end of it's path
	 *
	 * @param i The current game tick
	 */
	@Override
	public void onTick(long i) {
		//PlayerManager playerManager = GameManager.get().getManager(PlayerManager.class);
		PathManager pathMan = GameManager.get().getManager(PathManager.class);

		pathMan.initialise();

		AbstractEntity relevantTarget = mostRelevantTarget();

		if (relevantTarget != null) {

			targetNode = pathMan.getTargetNode(this, target, targetNode);

			float deltaX = target.getPosX() - getPosX();
			float deltaY = target.getPosY() - getPosY();

			super.setMoveAngle(Direction.getRadFromCoords(deltaX, deltaY));
			super.onTickMovement();

			super.updateDirection();
		}
	}

	/*Find the most relevant target to go to according to its EnemyTargets
	*
	* This is likely to get EnemyEntity, squirrel is being used for testing aggro at the moment
	* */
	private AbstractEntity mostRelevantTarget() {
		Map<Integer, AbstractEntity> entities = GameManager.get().getWorld().getEntities();
		/*Is a sight aggro-able target within range of enemy - if so, return as a target*/
		for (AbstractEntity entity : entities.values()) {
			for (Class sightTarget : targets.getSightAggroTargets()) {
				if (entity.getClass().isAssignableFrom(sightTarget)) {
					float distance = WorldUtil.distance(this.getPosX(), this.getPosY(), entity.getPosX(), entity.getPosY());
					if (distance < 10) {
						return entity;
					}
				}
			}
		}
		/*If no aggro, return 'ultimate' target*/
		for (AbstractEntity entity : entities.values()) {
			for (Class mainTarget : targets.getMainTargets()) {
				if (entity.getClass().isAssignableFrom(mainTarget)) {
					return entity;
				}
			}
		}
		return null;
	}

	/**
	 *	@return the current Direction of squirrel
	 * */
	//@Override
	public Direction getDirection() { return currentDirection; }

	/**
	 * @return String of this type of enemy (ie 'squirrel').
	 * */
	@Override
	public String getEnemyType() {
		return enemyType;
	}

	/**
	 * @return string representation of this class including its enemytype and x,y coordinates
	 */
	@Override
	public String toString() {
		return String.format("%s at (%d, %d)", getEnemyType(), (int) getPosX(), (int) getPosY());
	}

	/***
	 * Gets the progress bar that corresponds to the health of this enemy
	 * @return ProgressBarEntity corresponding to enemy's health
	 */
	@Override
	public ProgressBarEntity getProgressBar() {
		return PROGRESS_BAR;
	}

	/***
	 * Initialise EnemyStatistics belonging to this enemy which is referenced by other classes to control
	 * enemy.
	 *
	 * @return
	 */
	private static EnemyProperties initStats() {
		return new PropertiesBuilder<>().setHealth(HEALTH).setSpeed(SPEED)
				.setAttackRange(ATTACK_RANGE).setAttackSpeed(ATTACK_SPEED).setTexture(TEXTURE_LEFT)
				.addEvent(new MeleeAttackEvent(ATTACK_SPEED, BasePortal.class)).createEnemyStatistics();
	}

	private static EnemyTargets initTargets() {
		/*Enemy will move to these (in order) if no aggro*/
		ArrayList<Class> mainTargets = new ArrayList<>();
		mainTargets.add(BasePortal.class);
		mainTargets.add(Archer.class);
		mainTargets.add(Caveman.class);
		mainTargets.add(Wizard.class);

		/*if enemy can 'see' these, then enemy aggros to these*/
		ArrayList<Class> sightAggroTargets = new ArrayList<>();
		sightAggroTargets.add(Archer.class);
		sightAggroTargets.add(Caveman.class);
		sightAggroTargets.add(Wizard.class);

		/*Not yet implemented - concept: if enemy is attacked by these, then enemy aggros to these*/
		ArrayList<Class> damageAggroTargets = new ArrayList<>();
		damageAggroTargets.add(Archer.class);
		damageAggroTargets.add(Caveman.class);
		damageAggroTargets.add(Wizard.class);

		EnemyTargets targets = new EnemyTargets(mainTargets, sightAggroTargets, damageAggroTargets);
		return targets;
	}

	/***
	 * @return the EnemyStatistics of enemy which contain various governing stats of this enemy
	 */
	@Override
	public EnemyProperties getBasicStats() {
		return STATS;
	}

}
