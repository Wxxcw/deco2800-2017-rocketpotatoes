package com.deco2800.potatoes.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.deco2800.potatoes.entities.Enemies.EnemyEntity;
import com.deco2800.potatoes.entities.Enemies.Squirrel;
import com.deco2800.potatoes.entities.trees.AbstractTree;
import com.deco2800.potatoes.entities.trees.ResourceTree;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.managers.Inventory;
import com.deco2800.potatoes.managers.SoundManager;
import com.deco2800.potatoes.renderering.Render3D;
import com.deco2800.potatoes.util.Box3D;
import com.deco2800.potatoes.util.WorldUtil;

/**
 * Entity for the playable character.
 *
 * @author leggy
 *
 */
public class Player extends MortalEntity implements Tickable, HasProgressBar {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(Player.class);

	private final static transient float HEALTH = 200f;
	private static final transient String TEXTURE_RIGHT = "spacman_blue";
	private static final transient String TEXTURE_LEFT = "spacman_blue_2";

	private float movementSpeed;
	private float speedx;
	private float speedy;
	private int direction; // facing left=0, right=1

	private Inventory inventory;

	/*
	private static final List<Color> colours = Arrays.asList(Color.valueOf("ed1c24"),
			Color.valueOf("ed184a"), Color.valueOf("f47721"), Color.valueOf("fcb315"),
			Color.valueOf("fff200"), Color.valueOf("b7d432"), Color.valueOf("5dbb46"), 
			Color.valueOf("00a651"));
			*/
	private static final List<Color> colours = Arrays.asList(Color.WHITE);
	private static final ProgressBarEntity progressBar = new ProgressBarEntity("healthbar", 
				colours, 50, 5);

	/**
	 * Default constructor for the purposes of serialization
	 */
	public Player() {
		super(0, 0, 0, 0.30f, 0.30f, 0.30f, 0.48f,
				0.48f, TEXTURE_RIGHT, HEALTH);
	}

	/**
	 * Creates a new Player instance.
	 *
	 * @param posX
	 *            The x-coordinate.
	 * @param posY
	 *            The y-coordinate.
	 * @param posZ
	 *            The z-coordinate.
	 */
	public Player(float posX, float posY, float posZ) {
		super(posX, posY, posZ, 0.30f, 0.30f, 0.30f, 0.48f,
				0.48f, TEXTURE_RIGHT, HEALTH);
		movementSpeed = 0.075f;
		this.speedx = 0.0f;
		this.speedy = 0.0f;
		this.direction = 1;

		HashSet<Resource> startingResources = new HashSet<Resource>();
		startingResources.add(new SeedResource());
		startingResources.add(new FoodResource());
		this.inventory = new Inventory(startingResources);

		// this.setTexture("spacman_blue");
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	/**
	 * Returns the string representation of which way the player is facing.
	 */
	public String getPlayerDirection() {
		return (direction == 0) ? "left" : "right";
	}

	@Override
	public void onTick(long arg0) {
		float newPosX = this.getPosX();
		float newPosY = this.getPosY();

		newPosX += speedx;
		newPosY += speedy;

		Box3D newPos = getBox3D();
		newPos.setX(newPosX);
		newPos.setY(newPosY);

		Map<Integer, AbstractEntity> entities = GameManager.get().getWorld().getEntities();
		boolean collided = false;
		for (AbstractEntity entity : entities.values()) {
			if (!this.equals(entity) && !(entity instanceof Squirrel) && !(entity instanceof Projectile)
					&& newPos.overlaps(entity.getBox3D())) {
				LOGGER.info(this + " colliding with " + entity);
				collided = true;

			}

			if (!this.equals(entity) && (entity instanceof EnemyEntity)
					&& newPos.overlaps(entity.getBox3D())) {
				collided = true;

			}
		}

		if (!collided) {
			this.setPosX(newPosX);
			this.setPosY(newPosY);
		}
	}

	/**
	 * Handle movement when wasd keys are pressed down
	 *
	 * @param keycode
	 */
	public void handleKeyDown(int keycode) {

		switch (keycode) {
		case Input.Keys.W:
			speedy -= movementSpeed;
			speedx += movementSpeed;
			break;
		case Input.Keys.S:
			speedy += movementSpeed;
			speedx -= movementSpeed;
			break;
		case Input.Keys.A:
			// changes the sprite so that the character is facing left
			this.setTexture(TEXTURE_LEFT);
			direction = 0;
			speedx -= movementSpeed;
			speedy -= movementSpeed;
			break;
		case Input.Keys.D:
			// changes the sprite so that the character is facing right
			this.setTexture(TEXTURE_RIGHT);
			direction = 1;
			speedx += movementSpeed;
			speedy += movementSpeed;
			break;
		case Input.Keys.T:
			tossItem(new SeedResource());
			break;
		case Input.Keys.F:
			tossItem(new FoodResource());
			break;
		case Input.Keys.E:
			harvestResources();
			break;
		case Input.Keys.NUM_1:
            if (!WorldUtil.getEntityAtPosition(getCursorCoords().x, getCursorCoords().y).isPresent()) {
                AbstractTree.constructTree(new Tower(getCursorCoords().x, getCursorCoords().y, 0));
            }
            break;
		case Input.Keys.NUM_2:
            if (!WorldUtil.getEntityAtPosition(getCursorCoords().x, getCursorCoords().y).isPresent()) {
            	AbstractTree.constructTree(new ResourceTree(getCursorCoords().x, getCursorCoords().y, 0));
            }
            break;
		case Input.Keys.NUM_3:
            if (!WorldUtil.getEntityAtPosition(getCursorCoords().x, getCursorCoords().y).isPresent()) {
            	AbstractTree.constructTree(new ResourceTree(getCursorCoords().x, getCursorCoords().y, 0, new FoodResource(), 8));
            }
            break;
		default:
			break;
		}
	}
	
	private Vector2 getCursorCoords() {
		Vector3 worldCoords = Render3D.screenToWorldCoordiates(Gdx.input.getX(), Gdx.input.getY(), 0);
		Vector2 coords = Render3D.worldPosToTile(worldCoords.x, worldCoords.y);
        return new Vector2((int) Math.floor(coords.x), (int) Math.floor(coords.y));
    }
	
	/**
	 * Handles removing an item from an inventory and placing it on the map.
	 * 
	 * @param item
	 * 			The resource to be thrown.
	 */
	private void tossItem(Resource item) {
		// tosses a item in front of player
		float x = this.getPosX();
		float y = this.getPosY();
		float z = this.getPosZ();

		x = (direction == 0) ? x - 1 : x + 1;
		y = (direction == 0) ? y - 2 : y + 2;

		// only toss an item if there are items to toss
		if (this.getInventory().updateQuantity(item, -1) == 1) {
			GameManager.get().getWorld().addEntity(new ResourceEntity(x, y, z, item));
		}

	}
	
	/**
	 * Handles harvesting resources from resource tree that are in 
	 * range. Resources are added to the player's inventory.
	 */
	private void harvestResources() {
		double interactRange = 3f; // TODO: Could this be a class variable?
		Collection<AbstractEntity> entities = GameManager.get().getWorld().getEntities().values();
		boolean didHarvest = false;
		for (AbstractEntity entitiy : entities) {
			if (entitiy instanceof ResourceTree && entitiy.distance(this)  <= interactRange) {
				if(((ResourceTree) entitiy).getGatherCount() >0) {
					didHarvest = true;
					((ResourceTree) entitiy).transferResources(this.inventory);
				}
			}
		}
		if (didHarvest) {
			((SoundManager) GameManager.get().getManager(SoundManager.class)).playSound("harvesting.mp3");
		}
	}

	/**
	 * Handle movement when wasd keys are released
	 *
	 * @param keycode
	 */
	public void handleKeyUp(int keycode) {
		switch (keycode) {
		case Input.Keys.W:
			speedy += movementSpeed;
			speedx -= movementSpeed;
			break;
		case Input.Keys.S:
			speedy -= movementSpeed;
			speedx += movementSpeed;
			break;
		case Input.Keys.A:
			speedx += movementSpeed;
			speedy += movementSpeed;
			break;
		case Input.Keys.D:
			speedx -= movementSpeed;
			speedy -= movementSpeed;
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		return "The player";
	}

	@Override
	public int getProgress() {
		return (int) getHealth();
	}

	@Override
	public void setProgress(int p) {
		return;
	}

	@Override
	public float getProgressRatio() {
		return getHealth() / getMaxHealth();
	}

	@Override
	public int getMaxProgress() {
		return (int) getMaxHealth();
	}

	@Override
	public void setMaxProgress(int p) {
		return;
	}

	@Override
	public boolean showProgress() {
		return true;
	}

	@Override
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public void deathHandler() {
		LOGGER.info(this + " is dead.");
		this.heal(maxHealth);
		this.setPosition(5, 10, 0);
	}
	
}
