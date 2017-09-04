package com.deco2800.potatoes.entities;

import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.entities.health.HasProgressBar;
import com.deco2800.potatoes.entities.health.MortalEntity;
import com.deco2800.potatoes.entities.health.ProgressBar;
import com.deco2800.potatoes.entities.health.ProgressBarEntity;

/**
 * Class representing the "goal/nexus" thingy that the player needs to defend.
 * Very likely this will be refactored and replaced.
 * 
 * @author leggy
 *
 */
public class GoalPotate extends MortalEntity implements HasProgressBar {
	
	private final static transient String TEXTURE = "potate";

	private static final ProgressBarEntity progressBar = new ProgressBarEntity("healthbar", 2);

	public GoalPotate() {
		// empty for serialization
	}

	/*lots of health so squirrels don't kill it so quickly*/
	public GoalPotate(float posX, float posY, float posZ) {
		super(posX, posY, posZ, 1f, 1f, 1f, TEXTURE, 1000f);
		this.setStaticCollideable(true);
	}

	@Override
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public String toString() {
		return "The goal potate";
	}

	@Override
	public int getProgress() {
		return (int) getHealth();
	}

	@Override
	public void setProgress(int p) {
		return;
	}

	@Override
	public float getProgressRatio() {
		return getHealth() / getMaxHealth();
	}

	@Override
	public int getMaxProgress() {
		return (int) getMaxHealth();
	}

	@Override
	public void setMaxProgress(int p) {
		return;
	}

	@Override
	public boolean showProgress() {
		return true;
	}


}
