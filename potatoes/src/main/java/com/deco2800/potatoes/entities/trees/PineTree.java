package com.deco2800.potatoes.entities.trees;

import java.util.LinkedList;
import java.util.List;
import com.deco2800.potatoes.entities.PropertiesBuilder;
import com.deco2800.potatoes.entities.resources.PineconeResource;

public class PineTree extends ResourceTree {
	
	/**
	 * Constructor for creating a food resource tree
	 * 
	 * @param posX
	 *            The x-coordinate.
	 * @param posY
	 *            The y-coordinate.
	 */
	public PineTree(float posX, float posY) {
		super(posX, posY, new PineconeResource(), 12); // Set resource to pine and capacity to 12
		this.defaultTexture = "pine_resource_tree";
	}
	
	/**
	 * Stats for a resource tree that gathers food
	 * 
	 * @return the list of upgrade stats for a food resource tree
	 */
	private static List<TreeProperties> getPineTreeStats() {
		List<TreeProperties> result = new LinkedList<>();
		List<PropertiesBuilder<ResourceTree>> builders = new LinkedList<>();

		String texture = "pine_resource_tree";
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(20).setBuildTime(5000).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(6000, 1)));
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(10).setBuildTime(7000).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(5500, 1)));
		builders.add(new PropertiesBuilder<ResourceTree>().setHealth(15).setBuildTime(6500).setBuildCost(1)
				.setTexture(texture).addEvent(new ResourceGatherEvent(5000, 2)));

		for (PropertiesBuilder<ResourceTree> statisticsBuilder : builders) {
			result.add(statisticsBuilder.createTreeStatistics());
		}
		return result;
	}
	
	@Override
	public List<TreeProperties> getAllUpgradeStats() {
		this.setTexture("pine_resource_tree");
		return getPineTreeStats();
	}
	
	@Override
	public ResourceTree createCopy() {
		return new PineTree(this.getPosX(), this.getPosY());
	}
	
	@Override
	public String getName() {
		return "Pine Tree";
	}

}