package com.deco2800.potatoes.worlds;

import java.awt.Point;
import java.util.*;

import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.Selectable;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.managers.Manager;
import com.deco2800.potatoes.managers.MultiplayerManager;
import com.deco2800.potatoes.managers.TextureManager;
import com.deco2800.potatoes.managers.WorldManager;
import com.deco2800.potatoes.renderering.Renderable;
import com.deco2800.potatoes.worlds.terrain.Terrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * World is the Game World
 *
 * It provides storage for the WorldEntities and other universal world level
 * items.
 */
public class World {
	private static final Logger LOGGER = LoggerFactory.getLogger(World.class);

	/**
	 * Size of the area entites that are in will be put into the same position in the map
	 */
	private static final int GRID_SIZE = 5;

	private static final int TILE_WIDTH = 128;
	private static final int TILE_HEIGHT = 74;
	private Terrain[][] terrain;

	private Map<Integer, AbstractEntity> entities = new HashMap<>();
	private Map<Point, Map<Integer, AbstractEntity>> entitiesByPosition = new HashMap<>();
	// Current index of the hashmap i.e. the last value we inserted into, for
	// significantly more efficient insertion)
	// First 16 index's are reserved for clients
	// TODO game will likely crash/break badly when this overflows
	private int currentIndex = 17;
	protected TiledMap map;
	private int width;
	private int length;
	// Store managers for this world
	private Set<Manager> managers = new HashSet<>();

	private Drawable background;
	private Terrain backgroundTerrain;

	public World() {
		map = new TiledMap();
		map.getProperties().put("tilewidth", TILE_WIDTH);
		map.getProperties().put("tileheight", TILE_HEIGHT);
	}

	/**
	 * Returns a list of entities in this world
	 * Note that looping through all entities to find a subset of them is rather inefficient in most cases.
	 *
	 * @return All Entities in the world
	 */
	@Deprecated
	public Map<Integer, AbstractEntity> getEntities() {
		return new HashMap<>(this.entities);
	}

	/**
	 * Returns the entity associated with the given id
	 */
	public AbstractEntity getEntity(int id) {
		return entities.get(id);
	}

	/**
	 * Returns the current map for this world
	 *
	 * @return Map object for this world
	 */
	public TiledMap getMap() {
		return this.map;
	}

	/**
	 * Add's an entity into the next available id slot.
	 *
	 * In singleplayer this should work as you expect.
	 *
	 * For multiplayer, this should only be called by the master client. TODO way to
	 * for perform client side prediction.
	 *
	 * @param entity
	 */
	public void addEntity(AbstractEntity entity) {
		MultiplayerManager m = GameManager.get().getManager(MultiplayerManager.class);
		if (m.isMultiplayer()&& m.isMaster()) {
			// HashMap because I want entities to have unique ids that aren't necessarily
			// sequential
			// O(n) insertion? Sorry this is pretty hacky :(
			while (true) {
				if (entities.containsKey(currentIndex)) {
					currentIndex++;
				} else {
					// If we're in multiplayer and the master tell other entities.
					addToMaps(currentIndex++, entity);

					// Tell other clients about this entity. Note that we should always broadcast
					// master changes AFTER
					// they have actually been made. Since the server will often read the master
					// state for information.
					m.broadcastNewEntity(currentIndex - 1);
					break;
					}
				}
		}else if (m.isMultiplayer()&& !m.isMaster()) {
				throw new IllegalStateException(
						"Clients who aren't master shouldn't be adding entities when in multiplayer!");
		} else {
				// Singleplayer behaviour
				addToMaps(currentIndex++, entity);
		}
	}

	/**
	 * Add's an entity at the id (i.e. the index of the ArrayList). This will
	 * overwrite any existing entity if it is occupied.
	 *
	 * This function should probably only be called for multiplayer purposes, where
	 * entity id's must sync across clients. As such this will throw an error if
	 * you're not in multiplayer
	 *
	 * @param entity
	 * @param id
	 */
	public void addEntity(AbstractEntity entity, int id) {
		MultiplayerManager m = GameManager.get().getManager(MultiplayerManager.class);
		if (m.isMultiplayer()) {
			addToMaps(id, entity);
		} else {
			throw new IllegalStateException("Not in multiplayer, this function should only be used for multiplayer");
		}

	}

	/**
	 * Removes the entity with the given id from this world.
	 */
	public void removeEntity(int id) {
		removeFromMaps(id);

		// Tell the other clients if we're master and in multiplayer.
		MultiplayerManager m = GameManager.get().getManager(MultiplayerManager.class);
		if (m.isMultiplayer() && m.isMaster()) {
			m.broadcastEntityDestroy(id);
		}
	}

	/**
	 * Removes the given entity from this world, if it is in the world. Otherwise,
	 * nothing happens
	 */
	public void removeEntity(AbstractEntity entity) {
		for (Map.Entry<Integer, AbstractEntity> e : entities.entrySet()) {
			if (e.getValue() == entity) {
				removeFromMaps(e.getKey());

				// Tell the other clients if we're master and in multiplayer.
				MultiplayerManager m = GameManager.get().getManager(MultiplayerManager.class);
				if (m.isMultiplayer() && m.isMaster()) {
					m.broadcastEntityDestroy(e.getKey());
				}
				return;
			}
		}
	}

	/**
	 * Adds the entity and associated id to all the maps
	 */
	private void addToMaps(int id, AbstractEntity entity) {
		// Check if the nested maps exist and create them if they don't
		if (!entitiesByPosition.containsKey(getGridPoint(entity))) {
			entitiesByPosition.put(getGridPoint(entity), new HashMap<>());
		}

		entitiesByPosition.get(getGridPoint(entity)).put(id, entity);
		entities.put(id, entity);
	}

	/**
	 * Removes the entity associated with the given id from all the maps
	 */
	private void removeFromMaps(int id) {
		AbstractEntity entity = entities.get(id);
		// Check all the nested maps exist
		if (entitiesByPosition.containsKey(getGridPoint(entity))) {
			entitiesByPosition.get(getGridPoint(entity)).remove(id);
		}
		entities.remove(id);
	}

	private Point getGridPoint(AbstractEntity entity) {
		if (entity == null) {
			// Quick fix for tests, should be used normally
			return new Point(0,0);
		}
		return getGridPoint((int)entity.getPosX(), (int)entity.getPosY());
	}

	private Point getGridPoint(int x, int y) {
		// Rounds x and y to a multiple of GRID_SIZE
		return new Point(GRID_SIZE * (x / GRID_SIZE), GRID_SIZE * (y / GRID_SIZE));
	}

	/**
	 * Sets the tile int the current world at coordinated x and y to the type of the
	 * terrain given. No idea if this currently works
	 *
	 * @param x
	 * @param y
	 * @param tile
	 */
	public void setTile(int x, int y, Terrain tile) {
		if (!tile.equals(terrain[x][y])) {
			GameManager.get().getManager(WorldManager.class).setWorldCached(false);
			terrain[x][y] = tile;
			Cell cell = GameManager.get().getManager(WorldManager.class).getCell(tile.getTexture());
			((TiledMapTileLayer) map.getLayers().get(0)).setCell(x, y, cell);
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

	/**
	 * Sets the width (x size) of this world. Note this does not change the map.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Sets the length (y size) of this world. Note this does not change the map.
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Returns the x size of the world
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the x size of the world
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets the terrain grid to the given grid
	 */
	public void setTerrain(Terrain[][] newTerrain) {
		if (newTerrain.length != width || newTerrain[0].length != length || map.getLayers().getCount() == 0) {
			width = newTerrain.length;
			length = newTerrain[0].length;
			// remove all map layers
			Iterator<MapLayer> iter = map.getLayers().iterator();
			while(iter.hasNext()) {
				iter.next();
				iter.remove();
			}
			// add our new map layer
			map.getLayers().add(new TiledMapTileLayer(width, length, TILE_WIDTH, TILE_HEIGHT));
		}
		terrain = new Terrain[newTerrain.length][newTerrain[0].length];
		for (int x = 0; x < newTerrain.length; x++) {
			for (int y = 0; y < newTerrain[x].length; y++) {
				setTile(x, y, newTerrain[x][y]);
			}
		}
	}

	/**
	 * Returns the terrain of the specified location taken from the terrain grid
	 */
	public Terrain getTerrain(int x, int y) {
		if (x > 0 && y > 0 && x < width && y < length)
			return terrain[y][x];
		else
			// Makes sure terrain finding doesn't crash
			return backgroundTerrain;
	}

	/**
	 * Stores the given manager in this world
	 * @param m the manager to add
	 */
	public void addManager(Manager m) {
		managers.add(m);
	}

	/**
	 * @return the managers for this world
	 */
	public Set<Manager> getManagers() {
		return managers;
	}

	/**
	 * Returns the background for beyond the edges of the map
	 */
	public TextureRegionDrawable getBackground() {
		return (TextureRegionDrawable) background;
	}

	/**
	 * Sets the background for beyond the edges of the map
	 */
	public void setBackground(Terrain terrain) {
		// TODO fix tests so they don't throw null pointer error here
		try {
			backgroundTerrain = terrain;
			TextureRegion textureRegion = new TextureRegion(GameManager.get().getManager(TextureManager.class)
					.getTextureRegion(terrain.getTexture()));
			textureRegion.setRegionHeight(textureRegion.getRegionHeight() * WorldManager.WORLD_SIZE * 2);
			textureRegion.setRegionWidth(textureRegion.getRegionWidth() * WorldManager.WORLD_SIZE * 2);
			textureRegion.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			background = new TextureRegionDrawable(textureRegion);
		} catch (NullPointerException e) {
			background = new TextureRegionDrawable();
			LOGGER.info(e.getMessage());
		}
	}
}
