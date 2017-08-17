package com.deco2800.potatoes.entities;

import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.Tickable;

/**
 * A generic player instance for the game
 */
public class Peon extends AbstractEntity implements Tickable {
	
	private final static transient String TEXTURE = "spacman";

	public Peon() {
		//empty for serialization
	}


	/**
	 * Constructor for the Peon
	 * @param world
	 * @param posX
	 * @param posY
	 * @param posZ
	 */
	public Peon(float posX, float posY, float posZ) {
		super(posX, posY, posZ, 1, 1, 1, TEXTURE);
	}

	@Override
	public void onTick(long i) {

	}
}
