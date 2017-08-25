package com.deco2800.potatoes.entities.trees;

import java.util.LinkedList;
import java.util.List;

import com.deco2800.potatoes.entities.Resource;
import com.deco2800.potatoes.entities.SeedResource;
import com.deco2800.potatoes.entities.Tickable;
import com.deco2800.potatoes.entities.TimeEvent;
import com.deco2800.potatoes.managers.Inventory;

/**
 * Resource tree offer a means to collect resources over time.
 * 
 */
public class ResourceTree extends AbstractTree implements Tickable {
	
	/* Resource Tree Attributes */
	private int resourceCount;	// Number of resources currently gathered
	private Resource resourceType;	// Type of resource gathered by the tree
	
	// Maximum amount of resources held by any resource  
	// tree at any given instance. Set to -1 for infinite.
	public static final int MAX_RESOURCE_COUNT = 99;	
	
	/* Stats that apply to all resource trees */
	public static final int HP = 8; // Health of the tree
	public static final int RATE = 5000; // Rate resources are earned
	public static final float AMOUNT = 1f; // Number of resourced earned per gather
	public static final int CONSTRUCTION_TIME = 2500; // Construction time
	public static final String TEXTURE = "tree_selected"; // Texture name
	
	public static final List<UpgradeStats> STATS = initStats();

	/**
	 * Default constructor for serialization
	 */
	public ResourceTree() {
	}
	
	/**
	 * Constructor for creating a basic resource tree at a given 
	 * coordinate. The resource produced by the tree will default to
	 * the seed resource.
	 * 
	 * @param posX
	 *            The x-coordinate.
	 * @param posY
	 *            The y-coordinate.
	 * @param posZ
	 *            The z-coordinate.
	 */
	public ResourceTree(float posX, float posY, float posZ) {
		super(posX, posY, posZ, 1f, 1f, 1f, null, 0);
		this.resourceCount = 0;
		this.resourceType = new SeedResource();
	}
	
	/**
	 * Constructor for creating a resource tree at a given 
	 * coordinate that gathers a specified resource.
	 * 
	 * @param posX
	 *            The x-coordinate.
	 * @param posY
	 *            The y-coordinate.
	 * @param posZ
	 *            The z-coordinate.
	 * @param resourceType
	 * 			The type of resource gathered by the tree.
	 */
	public ResourceTree(float posX, float posY, float posZ, Resource resourceType) {
		super(posX, posY, posZ, 1f, 1f, 1f, null, 0);
		this.resourceCount = 0;
		this.resourceType = resourceType;
	}
	
	@Override
	public List<UpgradeStats> getAllUpgradeStats() {
		return STATS;
	}
	
	/**
	 * Configures the stats for the resource tree. Each stat level has a 
	 * normal event action for gathering resources based on the level's
	 * speed stat.
	 */
	private static List<UpgradeStats> initStats() {
		List<UpgradeStats> result = new LinkedList<>();
		List<TimeEvent<AbstractTree>> normalEvents = new LinkedList<>();
		List<TimeEvent<AbstractTree>> constructionEvents = new LinkedList<>();
		
		// Base State
		result.add(new UpgradeStats(HP, RATE, AMOUNT, CONSTRUCTION_TIME, normalEvents, constructionEvents, TEXTURE)); 
		// Upgrade 1
		result.add(new UpgradeStats(HP+12, RATE-1000, AMOUNT+1, CONSTRUCTION_TIME-500, normalEvents, constructionEvents, TEXTURE)); 
		// Upgrade 2
		result.add(new UpgradeStats(HP+22, RATE-1500, AMOUNT+2, CONSTRUCTION_TIME-1000, normalEvents, constructionEvents, TEXTURE)); 
		
		// Add ResourceGatherEvent to each upgrade level
		for (UpgradeStats upgradeStats : result) {
			upgradeStats.getNormalEventsReference().add(new ResourceGatherEvent(upgradeStats.getSpeed(), 
					Math.round(upgradeStats.getRange())));
		}

		return result;
	}

	/**
	 * Returns the number of resources gathered by the tree.
	 * 
	 *	@return the resourceCount
	 */
	public int getResourceCount() {
		return resourceCount;
	}
	
	/**
	 * Returns the resource type that is gathered by the tree.
	 * 
	 *	@return the resourceType
	 */
	public Resource getResourceType() {
		return resourceType;
	}
	
	/**
	 * Adds the specified amount to the tree's current resource gather
	 * count. Resource count will not exceed MAX_RESOURCE_COUNT.
	 * 
	 * 	@param amount of resources to add
	 */
	public void addResources(int amount) {
		if (ResourceTree.MAX_RESOURCE_COUNT == -1) {
			this.resourceCount += amount; // No limit so add without checking
		} else {
			if ((this.resourceCount + amount) <= ResourceTree.MAX_RESOURCE_COUNT) {
				this.resourceCount += amount;
			} else {
				this.resourceCount = ResourceTree.MAX_RESOURCE_COUNT; // Resource tree is full
			}
		}
	}
	
	/**
	 * Removes the specified amount from the tree's current resource 
	 * gather count.
	 * 
	 * @param amount
	 */
	public void removeResources(int amount) {
		if ((this.resourceCount - amount) < 0) {
			this.resourceCount = 0; // Cannot have less than zero resources
		} else {
			this.resourceCount -= amount;
		}
	}

	/**
	 * Transfers the resources gathered from the tree
	 * into the specified inventory. Once resources
	 * are transfered, the resourceCount of the tree will 
	 * reset to zero.
	 * 
	 * 	@param inventory
	 * 		The inventory of the player to receive gathered resources
	 */
	public void transferResources(Inventory inventory) {
		if (inventory.updateQuantity(this.resourceType, resourceCount) == 1 ) {
			resourceCount = 0;
		}
		
	}
	
	@Override
	public String toString() {
		String pos = this.getPosX() + ", " + this.getPosY() + ", " + this.getPosZ();
		return "Resource Tree at (" + pos + ") has " + this.resourceCount 
				+ " " + this.resourceType.toString() + " resources.";
	}
}