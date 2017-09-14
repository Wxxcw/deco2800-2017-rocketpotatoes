package com.deco2800.potatoes.entities.trees;

import com.deco2800.potatoes.entities.Damage;

public class IceTree extends Damage {
    private static final String TEXTURE = "ice_basic_tree";

    public IceTree(){

        this.damageTreeType = "IceTree";
        this.texture = TEXTURE;

    }


}
