package com.deco2800.potatoes.managers;

import com.badlogic.gdx.graphics.Texture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Texture manager acts as a cache between the file system and the renderers.
 * This allows all textures to be read into memory at the start of the game saving
 * file reads from being completed during rendering.
 *
 * With this in mind don't load textures you're not going to use.
 * Textures that are not used should probably (at some point) be removed
 * from the list and then read from disk when needed again using some type
 * of reference counting
 * @Author Tim Hadwen
 */
public class TextureManager extends Manager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextureManager.class);

    /**
     * A HashMap of all textures with string keys
     */
    private Map<String, Texture> textureMap = new HashMap<String, Texture>();

    /**
     * Constructor
     * Currently loads up all the textures but probably shouldn't/doesn't
     * need to.
     */
    public TextureManager() {
        textureMap.put("grass", new Texture("grass.png"));
        textureMap.put("grass2", new Texture("grass2.png"));
        textureMap.put("tree", new Texture("spacman.png"));
        textureMap.put("real_tree", new Texture("tree.png"));
        textureMap.put("ground_1", new Texture("ground-1.png"));
        textureMap.put("spacman", new Texture("spacman.png"));
        textureMap.put("spacman_red", new Texture("spacman_red.png"));
        textureMap.put("spacman_blue", new Texture("spacman_blue.png"));
        textureMap.put("spacman_green", new Texture("spacman_green.png"));
        textureMap.put("spacman_ded", new Texture("spacman_ded.png"));
        textureMap.put("selected", new Texture("selected.png"));
        textureMap.put("selected_black", new Texture("selected_black.png"));

    }

    /**
     * Gets a texture object for a given string id
     * @param id Texture identifier
     * @return Texture for given id
     */
    public Texture getTexture(String id) {
        if (textureMap.containsKey(id)) {
            return textureMap.get(id);
        } else {
            return textureMap.get("spacman_ded");
        }

    }

    /**
     * Saves a texture with a given id
     * @param id Texture id
     * @param filename Filename within the assets folder
     */
    public void saveTexture(String id, String filename) {
        LOGGER.info("Saving texture" + id + " with Filename " + filename);
        if (!textureMap.containsKey(id)) {
            textureMap.put(id, new Texture(filename));
        }
    }
}