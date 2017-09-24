package com.deco2800.potatoes.entities;

import java.util.Map;
import java.util.Optional;

import com.deco2800.potatoes.entities.HasDirection.Direction;
import com.deco2800.potatoes.entities.Player.PlayerState;
import com.deco2800.potatoes.entities.animation.TimeAnimation;
import com.deco2800.potatoes.entities.enemies.EnemyEntity;
import com.deco2800.potatoes.entities.projectiles.PlayerProjectile;
import com.deco2800.potatoes.entities.projectiles.Projectile.ProjectileType;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.managers.PlayerManager;
import com.deco2800.potatoes.util.WorldUtil;

public class Wizard extends Player {
	
	
	/**
     * Creates a new Archer instance.
     *
     * @param posX The x-coordinate.
     * @param posY The y-coordinate.
     * @param posZ The z-coordinate.
     */
    public Wizard(float posX, float posY, float posZ) {
    		super(posX, posY, posZ);
    		this.movementSpeed = 0.07f;
    		this.currentDirection = Direction.SouthEast;
        this.currentState = PlayerState.idle;
        //this.currentAnimation = ;
    }
    
    private Map<Direction, TimeAnimation> wizardIdleAnimations = makePlayerAnimation("wizard", PlayerState.idle, 1, 1, null);
    private Map<Direction, TimeAnimation> wizardAttackAnimations = makePlayerAnimation("wizard", PlayerState.idle, 1, 200, new Runnable() {	//TODO: update to use attack sprites
		@Override
		public void run() {
			// Handle finishing attack
			clearState();
			updateSprites();
		}
	});
    private Map<Direction, TimeAnimation> wizardDamagedAnimations = makePlayerAnimation("wizard", PlayerState.damaged, 1, 200, new Runnable() {
		@Override
		public void run() {
			// Handle finishing death
			clearState();
			updateSprites();
		}
	});
    
    @Override
    public void updateSprites() {
    		super.updateSprites();
		switch (this.getState()) {
        case idle:
    			this.setAnimation(wizardIdleAnimations.get(this.getDirection()));
    			break;
        case damaged:
        		this.setAnimation(wizardDamagedAnimations.get(this.getDirection()));
        		break;
        case attack:
    			this.setAnimation(wizardAttackAnimations.get(this.getDirection()));
    			break;
        default:
        		this.setAnimation(wizardIdleAnimations.get(this.getDirection()));
        		break;
        }
    }
    
    @Override
    public void attack() {
	    // Archer attack
    		if (this.setState(PlayerState.attack)) {
			float pPosX = GameManager.get().getManager(PlayerManager.class).getPlayer().getPosX();
	        float pPosY = GameManager.get().getManager(PlayerManager.class).getPlayer().getPosY();
	        float pPosZ = GameManager.get().getManager(PlayerManager.class).getPlayer().getPosZ();
	        
	        Optional<AbstractEntity> target = null;
	        target = WorldUtil.getClosestEntityOfClass(EnemyEntity.class, pPosX, pPosY);
	            
	        if (target.isPresent()) {
	        		float targetPosX = target.get().getPosX();
	        		float targetPosY = target.get().getPosY();
	            
	        		switch (this.getDirection()) {
	        		case North:
	        			break;
	        		case NorthEast:
	        			pPosY -= 1;
	        			pPosX += 1.5;
	        			break;
	        		case East:
	        			pPosY -= 1;
	        			pPosX += 1.5;
	        			break;
	        		case SouthEast:
	        			pPosX += 1;
	        			break;
	        		case South:
	        			pPosX += 1.2;
	        			break;
	        		case SouthWest:
	        			pPosY += 1;
	        			pPosX += 1;
	        			break;
	        		case West:
	        			break;
	        		case NorthWest:
	        			break;
	        		default:
	        			break;
	        		}
	        		GameManager.get().getWorld().addEntity(new PlayerProjectile(target.get().getClass(), pPosX-1, pPosY, pPosZ,  1f, 100, ProjectileType.ROCKET, null,
	                        /*new ExplosionEffect(target1.get().getClass(), target1.get().getPosX() -2, target1.get().getPosY(), target1.get().getPosZ(), 0, 2f)*/null, this.getDirection().toString(),targetPosX,targetPosY));
	        } else if (!target.isPresent()) {
	            //Disable shooting when no enemies is present until new fix is found.
	        }
		}
    }
    
    @Override
    public void interact() {
	    	// Archer interact
    }

}
