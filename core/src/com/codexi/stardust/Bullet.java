package com.codexi.stardust;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

//Class for bullets, will be applied to ship class
public class Bullet {
    private Sprite sprite;
    private int speed;
    private float xPt;        //x coordinate for bullet point
    private float yPt;        //y coordinate for bullet point

    //Bullet constructor
    public Bullet (Sprite s,int speed){
        this.sprite=s;
        this.speed=speed;
        xPt = this.getSprite().getX()+8;    //middle of bullet
        yPt = this.getSprite().getY()+16;   //should be top of bullet
    }

    public float getxPt(){
        return this.xPt;
    }

    public float getyPt(){
        return this.yPt;
    }

    public void updateCoordinates(){
        xPt = this.getSprite().getX()+8;    //middle of bullet
        yPt = this.getSprite().getY()+16;   //should be top of bullet
    }

    //Getter and setter methods
    public Sprite getSprite() {
        return sprite;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
