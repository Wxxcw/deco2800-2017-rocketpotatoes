package com.deco2800.potatoes.entities.enemies;

import com.badlogic.gdx.math.Vector3;
import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.TimeEvent;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.util.WorldUtil;

import java.util.Optional;

/**
 * A melee attack from enemy to a target
 *
 * -Implementation inspired by "../trees/TreeProjectileShootEvent" - ty trees
 * team
 **/
public class MeleeAttackEvent extends TimeEvent<EnemyEntity> {

	private float range = 1.5f;
	private Class target;

	/**
	 * Default constructor for serialization
	 */
	public MeleeAttackEvent() {
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
	public MeleeAttackEvent(int attackSpeed, Class target) {
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
		if (!target1.isPresent() || enemy.distance(target1.get()) > range) {
			return;
		}

		GameManager.get().getWorld()
				.addEntity(new MeleeAttack(target,
						new Vector3(enemy.getPosX() + 0.5f, enemy.getPosY() + 0.5f, enemy.getPosZ()),
						new Vector3(target1.get().getPosX(), target1.get().getPosY(), target1.get().getPosZ()), 1, 4));

		/*
		 * If the enemy this attack event belongs to, stop firing !DOES NOT REMOVE
		 * EVENT, JUST STOPS REPEATING IT!
		 */
		if (enemy.isDead()) {
			setDoReset(false);
		}
	}

	/**
	 * @return a copy of this MeleeAttackEvent
	 */
	@Override
	public TimeEvent<EnemyEntity> copy() {
		return new MeleeAttackEvent(getResetAmount(), this.target);
	}

	/**
	 * @return string representation of melee attack
	 */
	@Override
	public String toString() {
		return String.format("Melee attack with %d attackspeed", this.getResetAmount());
	}
}
