package com.deco2800.potatoes.entities.enemies;

import com.badlogic.gdx.math.Vector3;
import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.TimeEvent;
import com.deco2800.potatoes.entities.trees.ResourceTree;
import com.deco2800.potatoes.managers.EventManager;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.util.WorldUtil;

import java.util.Collection;
import java.util.Optional;

/**
 * A melee attack from enemy to a target
 *
 **/
public class StealingEvent extends TimeEvent<EnemyEntity> {

    private float range = 1.5f;
    private Class target;

    /**
     * Default constructor for serialization
     */
    public StealingEvent() {
        // Blank comment for the great lord Sonar
    }

    /**
     * Constructor for melee attack event, set up to repeat an attack according to
     * attackSpeed
     *
     * @param attackSpeed
     *            the delay between shots
     *
     */
    public StealingEvent(int attackSpeed, Class target) {
        setDoReset(true);
        setResetAmount(attackSpeed);
        this.target = target;
        reset();
    }

    /**
     * Creates action as per TimeEvent shoots a projectile at small range to
     * simulate melee attack
     *
     * @param enemy
     *            The enemy that this melee attack belongs to
     */
    @Override
    public void action(EnemyEntity enemy) {
        Optional<AbstractEntity> target1 = WorldUtil.getClosestEntityOfClass(target, enemy.getPosX(), enemy.getPosY());

        // no target exists or target is out of range
        if (!target1.isPresent() || enemy.distanceTo(target1.get()) > range) {
            return;
        }


        Collection<AbstractEntity> entities = GameManager.get().getWorld().getEntities().values();
        for (AbstractEntity entitiy : entities) {
            if (entitiy instanceof ResourceTree) {
                if (((ResourceTree) entitiy).getGatherCount() > 0) {
                    ((ResourceTree) entitiy).gather(-1);
                }
            }
        }

            // GameManager.get().getWorld()
            //  .addEntity(new MeleeAttack(target,
            // new Vector3(enemy.getPosX() + 0.5f, enemy.getPosY() + 0.5f, enemy.getPosZ()),
            // new Vector3(target1.get().getPosX(), target1.get().getPosY(), target1.get().getPosZ()), 1, 4));

		/*Stop attacking if dead (deathHandler of mortal entity will eventually unregister the event).*/
            if (enemy.isDead()) {
                GameManager.get().getManager(EventManager.class).unregisterEvent(enemy, this);
                setDoReset(false);

            }

    }

    /**
     * @return a copy of this MeleeAttackEvent
     */
    @Override
    public TimeEvent<EnemyEntity> copy() {
        return new StealingEvent(getResetAmount(), this.target);
    }

    /**
     * @return string representation of melee attack
     */
    @Override
    public String toString() {
        return String.format("Steal with %d attackspeed", this.getResetAmount());
    }
}
