package com.deco2800.potatoes.entities.portals;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.Player;
import com.deco2800.potatoes.entities.ResourceEntity;
import com.deco2800.potatoes.entities.Tickable;
import com.deco2800.potatoes.entities.health.MortalEntity;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.util.Box3D;

/**
 * A class that can create portals which are not the base portal. Because these
 * are not in the first world, there are no enemies and therefore these portals
 * do not have health. AbstractPortals need to be instantiated with an
 * appropriate texture.
 * 
 * @author Jordan Holder, Katie Gray
 *
 */
public class AbstractPortal extends AbstractEntity implements Tickable {
	/*
	 * Logger for all info/warning/error logs
	 */
	private static final transient Logger LOGGER = LoggerFactory.getLogger(ResourceEntity.class);
	/*
	 * The radius of which a collision can be detected
	 */
	private final float change = (float) 0.2;
	/*
	 * The array of positions where a collision needs to be checked
	 */
	private final float[][] positions = { { change, 0 }, { change, change }, { 0, change }, { -change, change },
			{ -change, 0 }, { -change, -change }, { 0, -change }, { -change, -change } };

	/**
	 * This instantiates an AbstractPortal given the appropriate parameters.
	 * 
	 * @param posX
	 *            the x coordinate of the spite
	 * @param posY
	 *            the y coordinate of the sprite
	 * @param posZ
	 *            the z coordinate of the sprite
	 * @param texture
	 *            the texture which represents the portal
	 */
	public AbstractPortal(float posX, float posY, float posZ, String texture) {
		super(posX, posY, posZ, 3, 3, 3, texture);
	}


	@Override
	public void onTick(long time) {
		float xPos = getPosX();
		float yPos = getPosY();
		boolean collided = false;

		Box3D newPos = getBox3D();
		newPos.setX(xPos);
		newPos.setY(yPos);

		Map<Integer, AbstractEntity> entities = GameManager.get().getWorld().getEntities();
		// Check surroundings
		for (AbstractEntity entity : entities.values()) {
			if (entity instanceof Player) {
				// Player detected
				for (int i = 0; i < 8; i++) {
					newPos.setX(xPos + positions[i][0]);
					newPos.setY(yPos + positions[i][1]);
					// Player next to this resource
					if (newPos.overlaps(entity.getBox3D())) {
						collided = true;

					}
				}
			}
		}

		// remove from game world and add to inventory if a player has collided with
		// this resource
		if (collided) {
			try {
				LOGGER.info("Entered portal");
				// Bring up portal interface
			} catch (Exception e) {
				LOGGER.warn("Issue entering portal");
			}

		}
	}

}
