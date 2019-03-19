package com.codexi.stardust;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Particle {
    private Sprite sprite;
    private int xSpeed;
    private int ySpeed;
    private float despawnLocation;           //travel distance in y direction

    public Particle(Sprite sprite){
        this.sprite = sprite;
        int direction = MathUtils.random(0,1);
        if(direction==0) xSpeed = MathUtils.random(0,2);
        if(direction==1) xSpeed = -1*MathUtils.random(0,2);
        ySpeed = -1*MathUtils.random(5,10);
        despawnLocation = MathUtils.random(Gdx.graphics.getHeight()/9f,0);
    }
    public void randomizeSpeed(){
        int direction = MathUtils.random(0,1);
        if(direction==0) xSpeed = MathUtils.random(0,2);
        if(direction==1) xSpeed = -1*MathUtils.random(0,2);
        ySpeed = -1*MathUtils.random(5,10);
    }
    public Sprite getSprite() {
        return sprite;
    }

    public int getxSpeed(){
        return xSpeed;
    }

    public int getySpeed() {
        return ySpeed;
    }

    public float getDespawnLocation() {
        return despawnLocation;
    }
}
