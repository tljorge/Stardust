package com.codexi.stardust;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Starter extends Game {

    public SpriteBatch batch;

    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new HomeBase(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();

    }
}
