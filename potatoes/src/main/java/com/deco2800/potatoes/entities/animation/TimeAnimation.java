package com.deco2800.potatoes.entities.animation;

import com.deco2800.potatoes.entities.Tickable;
import com.deco2800.potatoes.entities.TimeEvent;

import java.util.Random;

/**
 * An traditional animation where the frame changes over time <br>
 * Feel free to modify this class in any way. This could be changed to
 * com.badlogic.gdx.graphics.g2d.Animation
 */
public class TimeAnimation extends TimeEvent<Tickable> implements Animation {

	private final transient Animation[] frames;
	private static Random generator = new Random();
	/**
	 * Construction for serialization
	 */
	public TimeAnimation() {
		frames = new Animation[] {};
	}

	/**
	 * Creates time animation where the frames are distributed between. Must be
	 * registered with the event manager to function properly.
	 * 
	 * @param maxAnimationTime
	 * @param frames
	 */
	public TimeAnimation(int maxAnimationTime, Animation[] frames) {
		this.frames = frames;
		setResetAmount(maxAnimationTime);
		setDoReset(true);
		reset();
	}

	@Override
	public void action(Tickable param) {
		// Nothing to do here
	}

	/**
	 * Returns the frame based on the progress this TimeEvent. The frames are evenly
	 * distributed between the max time and 0
	 */
	@Override
	public String getFrame() {
		return getAnimation().getFrame();
	}

	@Override
	public Animation getAnimation() {
		return frames[(int)generator.nextInt(frames.length)];
		//dosen't work
//		return frames[Math.round((frames.length - 1) * (1 - (float) getProgress() / getResetAmount()))];
	}

}
