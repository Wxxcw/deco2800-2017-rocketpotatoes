package com.deco2800.potatoes.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.deco2800.potatoes.entities.*;
import com.deco2800.potatoes.entities.enemies.Moose;
import com.deco2800.potatoes.entities.enemies.Squirrel;
import com.deco2800.potatoes.entities.enemies.TankEnemy;
import com.deco2800.potatoes.entities.trees.ResourceTree;
import com.deco2800.potatoes.managers.*;
import com.deco2800.potatoes.observers.KeyDownObserver;
import com.deco2800.potatoes.renderering.Render3D;
import com.deco2800.potatoes.screens.GameScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

import java.security.Key;
import java.util.Set;

import static com.badlogic.gdx.utils.Align.left;

public class DebugModeGui extends Gui {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugModeGui.class);


    private GameScreen screen;
    private Stage stage;

    // Buttons
    private Skin uiSkin;
    private VerticalGroup debugButtonGroup;
    private Label debugOn;
    private Label spawnCommands;
    private Label f1;
    private Label f2;
    private Label f3;
    private Label f4;
    private TextButton resetButton;
    private TextButton addResourcesButton;
    //private TextButton spawnButton;
    private TextButton immortalButton;
    private TextButton exitButton;
    private Table table;

    // State indicator
    private enum States {
        DEBUGON,
        DEBUGOFF
    }

    private States state = States.DEBUGOFF;

    public DebugModeGui(Stage stage, GameScreen screen){

        this.screen = screen;
        this.stage = stage;
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        table = new Table(uiSkin);

        // actors initialisation
        debugOn = new Label("Debug Options",uiSkin);
        resetButton = new TextButton("Reset Map", uiSkin);
        addResourcesButton = new TextButton("+10/+10 Resources", uiSkin);
        //spawnButton = new TextButton("Spawn", uiSkin);
        immortalButton = new TextButton("Immortality", uiSkin);
        spawnCommands = new Label("SPAWN COMMANDS",uiSkin);
        f1 = new Label("F1: DMG Tower",uiSkin);
        f2 = new Label("F2: Squirrel",uiSkin);
        f3 = new Label("F3: DMG Enemy",uiSkin);
        f4 = new Label("F4: RSC Seed",uiSkin);
        exitButton = new TextButton("Exit Debug", uiSkin);

        // adding actors
        debugButtonGroup = new VerticalGroup();
        debugButtonGroup.addActor(debugOn);
        debugButtonGroup.addActor(immortalButton);
        debugButtonGroup.addActor(resetButton);
        debugButtonGroup.addActor(addResourcesButton);
        //debugButtonGroup.addActor(spawnButton);
        debugButtonGroup.addActor(spawnCommands);
        debugButtonGroup.addActor(f1);
        debugButtonGroup.addActor(f2);
        debugButtonGroup.addActor(f3);
        debugButtonGroup.addActor(f4);
        debugButtonGroup.addActor(exitButton);
        table.add(debugButtonGroup);

        setupListeners();

        resetGui(stage);
        table.setVisible(false);
        //resetGui(stage);

        stage.addActor(table);


    }

    private void resetGui(Stage stage) {

        table.reset();
        table.left();
        table.setPosition(0,stage.getHeight()/2, left);
        table.add(debugButtonGroup);
        //table.setDebug(true);

    }

    private void setupListeners() {

        /* Listener for the exit debug button. */
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        /* Listener for the immortality button */
        immortalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.menuBlipSound();
                GameManager.get().getManager(PlayerManager.class).getPlayer().heal(200);
                GameManager.get().getManager(PlayerManager.class).getPlayer().addDamageScaling(0);
            }
        });

        /* Listener for the add resources button */
        addResourcesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Set<Resource> rsc = GameManager.get().getManager(PlayerManager.class).getPlayer().getInventory().getInventoryResources();
                for (Resource rscname: rsc) {
                    GameManager.get().getManager(PlayerManager.class).getPlayer().getInventory().updateQuantity(rscname, 10);
                }
            }
        });

        /* Listener for the reset button */
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map<Integer, AbstractEntity> entitiesMap = GameManager.get().getWorld().getEntities();
                System.out.println("Map: " + entitiesMap.values().toString());

                //Deletes all entities except player
                for (AbstractEntity ent: entitiesMap.values()){
                    if (!(ent instanceof Player)){
                        GameManager.get().getWorld().removeEntity(ent);
                    }
                }
            }
        });

        GameManager.get().getManager(InputManager.class).addKeyDownListener(new KeyDownObserver() {
            @Override
            public void notifyKeyDown(int keycode) {
                float x = GameManager.get().getManager(InputManager.class).getMouseX();
                float y = GameManager.get().getManager(InputManager.class).getMouseY();

                //Converting mouse coordinates to tiles
                Vector3 coords = Render3D.screenToWorldCoordiates(x,y,0);
                Vector2 coords2 = Render3D.worldPosToTile(coords.x,coords.y);

                if (state == States.DEBUGON) {
                    if (keycode == Input.Keys.F1) {
                        Tower tower = new Tower((int)coords2.x,(int)coords2.y,0);
                        tower.setProgress(0);
                        GameManager.get().getWorld().addEntity(tower);

                    }

                    if (keycode == Input.Keys.F5) {
                        ResourceTree rscTree = new ResourceTree((int)coords2.x,(int)coords2.y,0);
                        rscTree.setProgress(0);
                        GameManager.get().getWorld().addEntity(rscTree);

                    }

                    if (keycode == Input.Keys.F2) {
                        GameManager.get().getWorld().addEntity(new Squirrel(coords2.x, coords2.y,0));

                    }

                    if (keycode == Input.Keys.F3) {
                        GameManager.get().getWorld().addEntity(new TankEnemy(coords2.x, coords2.y,0));

                    }

                    if (keycode == Input.Keys.F4) {
                        SeedResource seedResource = new SeedResource();
                        GameManager.get().getWorld().addEntity(new ResourceEntity(coords2.x, coords2.y,0,seedResource));

                    }

                    if (keycode == Input.Keys.F5) { //TODO: make this appear on GUI
                        GameManager.get().getWorld().addEntity(new Moose(coords2.x, coords2.y,0));

                    }
                }
            }
        });



    }

    @Override
	public void show() {
        table.setVisible(true);
        stage.addActor(table);
        state = States.DEBUGON;
    }

    @Override
	public void hide() {
        /*((PlayerManager) GameManager.get().getManager(PlayerManager.class)).getPlayer().addDamageScaling(1f);
        float dmg = ((PlayerManager) GameManager.get().getManager(PlayerManager.class)).getPlayer().getDamageScaling();
        String dmgstr = String.valueOf(dmg);
        LOGGER.info("DAMAGE SCLAING: "+dmgstr);*/
        table.setVisible(false);
        state = States.DEBUGOFF;
    }
}
