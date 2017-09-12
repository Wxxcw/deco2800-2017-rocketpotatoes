package com.deco2800.potatoes.worlds;

import java.util.Random;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.deco2800.potatoes.entities.Selectable;
import com.deco2800.potatoes.renderering.Renderable;

/**
 * Initial world using preset world file.
 * 
 * @author leggy
 *
 */
public class InitialWorld2 extends World {

	/**
	 * Constructor for InitialWorld
	 */
	public InitialWorld2() {
		/* Load up the map for this world */
		this.map = new TmxMapLoader().load("resources/placeholderassets/placeholder.tmx");

		/*
		 * Grab the width and length values from the map file to use as the world size
		 */
		this.setWidth(this.getMap().getProperties().get("width", Integer.class));
		this.setLength(this.getMap().getProperties().get("height", Integer.class));

		//this.addEntity(new Peon(7, 7, 0));
		//this.addEntity(new Tower(8, 8, 0));

		//this.addEntity(new GoalPotate(10, 10, 0));
		
		Random random = new Random();
		for(int i = 0; i < 5; i++) {
			//this.addEntity(new Squirrel(10 + random.nextFloat() * 10, 10 + random.nextFloat() * 10, 0));
		}
		
	}

	/**
	 * Deselects all entities.
	 */
	public void deSelectAll() {
		for (Renderable r : this.getEntities().values()) {
			if (r instanceof Selectable) {
				((Selectable) r).deselect();
			}
		}
	}
	
	// Hacky fix
	@Override
	public float getHeight(int x, int y) {
		return 0.5f;
	}
}