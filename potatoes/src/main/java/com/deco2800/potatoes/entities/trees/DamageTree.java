package com.deco2800.potatoes.entities.trees;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.deco2800.potatoes.entities.Damage;
import com.deco2800.potatoes.entities.StatisticsBuilder;
import com.deco2800.potatoes.entities.Tickable;
import com.deco2800.potatoes.entities.animation.Animation;
import com.deco2800.potatoes.entities.animation.AnimationFactory;
import com.deco2800.potatoes.entities.animation.SingleFrameAnimation;



public class DamageTree extends AbstractTree implements Tickable {
	private static final List<TreeStatistics> ICE_TREE_STATS = generateTree("ice_basic_tree",
			x -> new SingleFrameAnimation("ice_basic_tree"));
	private static final List<TreeStatistics> ACORN_TREE_STATS = generateTree("acorn_tree",
			x -> new SingleFrameAnimation("acorn_tree"));
	private static final List<TreeStatistics> LIGHTNING_TREE_STATS = generateTree("lightning_tree1",
			x -> AnimationFactory.createSimpleTimeAnimation(100,
					new String[] {
            "lightning_tree1",
            "lightning_tree2",
            "lightning_tree3",
            "lightning_tree4",
            "lightning_tree5",
            "lightning_tree6",
            "lightning_tree7",
            "lightning_tree8",
            "lightning_tree9",
    }));
	
	private static final List<TreeStatistics> FIRE_TREE_STATS=generateTree("fire_tree",x->AnimationFactory.createSimpleTimeAnimation(100,new String[]{
			       "fire_tree1",
			       "fire_tree2",
			       "fire_tree3",
			       "fire_tree4",
			 
			 }));
    private Damage damageTreeType;
    /**
     * Static field to store information about upgrades
     */


    /**
     * Default constructor for serialization
     */
    public DamageTree() {
        //Default constructor
    }
    /**
     * Base Constructor
     */

    public DamageTree(float posX, float posY, float posZ) {

        super(posX, posY, posZ, 1f, 1f, 1f, null);

        damageTreeType=new LightningTree();


        this.resetStats();

    }

    public DamageTree(float posX, float posY, float posZ, Damage texture) {
        super(posX, posY, posZ, 1f, 1f, 1f, null);
        if(null==texture){
            damageTreeType=new LightningTree();
        }else{
            damageTreeType=texture;
        }


        this.resetStats();

    }


    public DamageTree(float posX, float posY, float posZ, Damage texture, float maxHealth,float demage) {
        super(posX, posY, posZ, 1f, 1f, 1f, null);
    }

    @Override
    public DamageTree clone() {
    	return new DamageTree(this.getPosX(), this.getPosY(), this.getPosZ(), this.getDamageTreeType());
    }



    @Override
    public List<TreeStatistics> getAllUpgradeStats() {
        if(damageTreeType instanceof IceTree){
            this.setTexture("ice_basic_tree");
            return ICE_TREE_STATS;
        }else if(damageTreeType instanceof AcornTree){
            this.setTexture("acorn_tree");
            return ACORN_TREE_STATS;
        } else if(damageTreeType instanceof FireTree){
            this.setTexture("fire_tree");
            return FIRE_TREE_STATS;
        }
        this.setTexture("lightning_tree1");
        return LIGHTNING_TREE_STATS;
    }

    /**
     * Static method to create the list of upgrades
     */
	private static List<TreeStatistics> generateTree(String texture, Function<AbstractTree, Animation> animation) {
		List<TreeStatistics> result = new LinkedList<>();
		/*
		 * UpgradeStats(Health, Shooting Time, Shooting Range, Construction/Upgrade
		 * Time, events, events, texture)
		 */

			result.add(new StatisticsBuilder<AbstractTree>().setHealth(10).setAttackRange(8f).setBuildTime(5000)
					.setBuildCost(1).setAnimation(animation).addEvent(new LightningShootEvent(250))
					.createTreeStatistics());



		return result;
	}

    /**
     * test purpose only
     * @return Damage
     */
    public Damage getDamageTreeType(){
        return damageTreeType;
    }



}
