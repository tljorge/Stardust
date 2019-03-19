package com.codexi.stardust;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class Ship{
    private Sprite sprite;
    private float health;            //base health is 100 * size
    private float maxHealth;
    private int speed;               //speed of the asteroid
    private int size;                //size of the asteroid, multiplier of health
    private float rate;              //fire rate of the weapons
    private float fuel;              //amount fuel ship contains
    private float maxFuel;
    private float damage;            //damage of the bullets
    private int money;
    //need to modify as needed
    //maybe need position?

    //Base constructor for asteroid, may need more?
    public Ship(Sprite s,float health,int speed,int size,float rate,float fuel,float damage){
        sprite = s;
        this.health = health;
        this.speed = speed;
        this.size = size;
        this.rate = rate;
        this.fuel = fuel;
        this.damage = damage;
        this.maxHealth = health;
        this.maxFuel = fuel;
        money = 100;
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

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    //Get methods
    public float getHealth() {
        return health;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getRate() {
        return rate;
    }

    public float getDamage() {
        return damage;
    }

    public float getFuel() {
        return fuel;
    }

    public float getMaxFuel() { return maxFuel; }

    public float getMaxHealth() { return maxHealth; }

    public int getMoney() {
        return money;
    }
}
