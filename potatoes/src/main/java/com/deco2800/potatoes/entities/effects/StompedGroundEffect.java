package com.deco2800.potatoes.entities.effects;

import java.util.Map;

import com.badlogic.gdx.math.Vector3;
import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.resources.ResourceEntity;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.managers.SoundManager;
import com.deco2800.potatoes.util.Box3D;

/**
 * A StompedGroundEffect, essentially terrain that has been "damaged" by an
 * entity in the game. Originally created for the TankEnemy, as the bear moves
 * he "stomps" the ground and damages it. This effect can be either temporary or
 * permanent. When entities walk through this effect, they will be slowed down
 * TODO Actually make them slow down.
 */
public class StompedGroundEffect extends Effect {
	// TODO Texture is a placeholder. Need to design proper artwork for stomped ground
	private static final transient String TEXTURE = "DamagedGroundTemp1";

	private boolean isTemporary;
	private Box3D effectPosition;
	private int currentTextureIndexCount = 0;
	private String[] currentTextureArray = { "DamagedGroundTemp1", "DamagedGroundTemp2", "DamagedGroundTemp3" };
	private int timer = 0;

	private static final SoundManager soundManager = new SoundManager();

	/**
	 * Empty constructor. Used for serialisation purposes
	 */
	public StompedGroundEffect() {
		// empty for serialization
	}

	/**
	 * Creates a new stomped ground effect. Effect is either temporary and will
	 * disappear, or is permanent and will affect game-play indefinitely.
	 *
	 * @param posX
	 *            x start position
	 * @param posY
	 *            y start position
	 * @param posZ
	 *            z start position
	 * @param isTemporary
	 *            boolean for whether this effect is temporary or permanent
	 */
	public StompedGroundEffect(Class<?> targetClass, float posX, float posY, float posZ, boolean isTemporary,
			float damage, float range) {
		super(targetClass, new Vector3(posX, posY, posZ), 1f, 1f, 1f, 1.2f, 1.2f, damage, range, EffectType.DAMAGED_GROUND);
		this.isTemporary = isTemporary;
		effectPosition = getBox3D();
		animate = false;
	}

	@Override
	public void onTick(long time) {
		if (isTemporary) {
			timer++;
			if (timer % 200 == 0) {
				if (currentTextureIndexCount < 3) {
					setTexture(currentTextureArray[currentTextureIndexCount]);
					currentTextureIndexCount++;
				} else {
					GameManager.get().getWorld().removeEntity(this);
				}
			}
		}
	}

	/**
	 * String representation of the damaged ground at its set position.
	 *
	 * @return String representation of the stomped ground
	 */
	@Override
	public String toString() {
		return String.format("Stomped Ground at (%d, %d)", (int) getPosX(), (int) getPosY());
	}
}
