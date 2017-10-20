package com.deco2800.potatoes.entities.trees;

import java.util.LinkedList;
import java.util.List;

import com.deco2800.potatoes.entities.PropertiesBuilder;
import com.deco2800.potatoes.entities.resources.FoodResource;
import com.deco2800.potatoes.entities.resources.Resource;
import com.deco2800.potatoes.entities.resources.SeedResource;

public class SeedTree extends ResourceTree {
	
	public static final String SEED_TREE_TEXTURE = "seed_resource_tree";
	
	
	/**
	 * Constructor for creating a seed resource tree
	 * 
	 * @param posX
	 *            The x-coordinate.
	 * @param posY
	 *            The y-coordinate.
	 */
	public SeedTree(float posX, float posY) {
		super(posX, posY, new SeedResource(), 10); // Set resource to seed and capacity to 10
	}
	
	
	/**
	 * Stats for a resource tree that gathers seeds
	 * 
	 * @return the list of upgrade stats for a seed resource tree
	 */
	private static List<TreeProperties> getSeedTreeStats() {
		List<TreeProperties> result = new LinkedList<>();
		List<PropertiesBuilder<ResourceTree>> builders = new LinkedList<>();

		String texture = SEED_TREE_TEXTURE;
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(8).setBuildTime(2500).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(6000, 1)));
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(20).setBuildTime(2000).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(5500, 1)));
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(30).setBuildTime(1500).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(5000, 2)));

		for (PropertiesBuilder<ResourceTree> statisticsBuilder : builders) {
			result.add(statisticsBuilder.createTreeStatistics());
		}
		return result;
	}
	
	@Override
	public List<TreeProperties> getAllUpgradeStats() {
		this.setTexture(SEED_TREE_TEXTURE);
		return getSeedTreeStats();
	}
	
	@Override
	public String getName() {
		return "Seed Tree";
	}
	
	
	
	
}
