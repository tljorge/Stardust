package com.codexi.stardust;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Asteroid {
    private Sprite sprite;
    private float health;         //base health is 100 * size
    private int speed;            //speed of the asteroid
    private float size;           //size of the asteroid, multiplier of health
    private float rotationRate;   //rate of rotation for asteroid

    //code for collision line
    private float x1Pt;           //start of collision line
    private float x2Pt;           //end of the collision line
    private float yPt;            //height of collision line


    //Base constructor for asteroid, may need more?
    public Asteroid(Sprite s,float health,int speed,float size,float rate){
        sprite = s;
        this.health = health*size;
        this.speed = speed;
        this.size = size;
        this.rotationRate = rate;
        this.getSprite().setScale(size);
        x1Pt = (this.getSprite().getX())-((this.getSprite().getWidth()*size)/2);
        x2Pt = this.getSprite().getX()+((this.getSprite().getWidth()*size)/2)+size*5;
        yPt  = this.getSprite().getY()-((this.getSprite().getHeight()*size)/2)+size*10;
    }

    public void updateCoordinates(){
        x1Pt = (this.getSprite().getX())-((this.getSprite().getWidth()*size)/2);
        x2Pt = this.getSprite().getX()+((this.getSprite().getWidth()*size)/2)+size*5;
        yPt  = this.getSprite().getY()-((this.getSprite().getHeight()*size)/2)+size*10;
    }

    //Setter methods
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setHealth (float health){
        this.health = health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSize(int size) {
        this.size = size;
    }

    //Get methods
    public float getX1Pt() {
        return x1Pt;
    }

    public float getX2Pt() {
        return x2Pt;
    }

    public float getyPt() {
        return yPt;
    }

    public float getHealth() {
        return health;
    }

    public float getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getRotationRate(){
        return rotationRate;
    }

}
