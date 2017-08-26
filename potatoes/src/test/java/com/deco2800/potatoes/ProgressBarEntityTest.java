package com.deco2800.potatoes;

import com.badlogic.gdx.graphics.Color;
import com.deco2800.potatoes.entities.ProgressBarEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class ProgressBarEntityTest {

	private static final List<Color> colours = Arrays.asList(Color.GREEN, Color.ORANGE, Color.RED);
	ProgressBarEntity progressBarEntity;

	private class TestableProgressBarEntity extends ProgressBarEntity {
		public TestableProgressBarEntity() {
		};

		public TestableProgressBarEntity(String texture, List<Color> colours, int height) {
			super(texture, colours, height);
		};
	}
	
	@Before
	public void setUp() throws Exception {
		progressBarEntity = new TestableProgressBarEntity("progress_bar", colours, 50);
	}
	
	// Common to all initialisation test
	private void initTestCommon() {
		assertEquals("getTexture() bad init", progressBarEntity.getTexture(), "progress_bar");
		assertEquals("getColours() bad init", progressBarEntity.getColours(), colours);
		assertEquals("getHeight() bad init", progressBarEntity.getHeight(), 50);
	}
	
	@Test
	public void initTest() {
		progressBarEntity = new TestableProgressBarEntity("progress_bar", colours, 50);
		initTestCommon();
	}
	
	@Test
	public void initTestEmpty() {
		try {
			progressBarEntity = new TestableProgressBarEntity();
		} catch (Exception E) {
			fail("No AbstractEntity serializable constructor");
		}
	}
	
	@Test
	public void getColourTest() {
		// test 3 colours
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(100, 100), Color.GREEN);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(50, 100), Color.ORANGE);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(0, 100), Color.RED);
		
		// test 1 colour
		List<Color> colours = Arrays.asList( Color.RED);
		progressBarEntity = new TestableProgressBarEntity("progress_bar", colours, 50);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(100, 100), Color.RED);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(50, 100), Color.RED);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(0, 100), Color.RED);
		
		// test 5 colour
		colours = Arrays.asList(Color.PURPLE, Color.BLUE, Color.GREEN, Color.ORANGE, Color.RED);
		progressBarEntity = new TestableProgressBarEntity("progress_bar", colours, 50);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(100, 100), Color.PURPLE);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(80, 100), Color.BLUE);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(60, 100), Color.GREEN);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(40, 100), Color.ORANGE);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(20, 100), Color.RED);
		assertEquals("Colour selected is not correct", progressBarEntity.getColour(0, 100), Color.RED);


	}

}