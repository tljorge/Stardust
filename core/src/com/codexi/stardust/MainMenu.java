package com.codexi.stardust;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MainMenu implements Screen {
    final Starter game;
    OrthographicCamera cam;
    Vector3 pos;

    //Title assets
    private Texture newGameTexture, continueGameTexture, settingsTexture;
    private Sprite newGameSprite, continueGameSprite, settingsSprite;

    private Texture titleTexture;
    private Sprite titleSprite;

    //star globals
    private Texture starTexture1, starTexture2, starTexture3, starTexture4;
    private Array<Star> stars;


    //Constructor
    public MainMenu(final Starter game) {
        this.game = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        pos = new Vector3(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0);

        stars = new Array<Star>();

        initTextures();
        initGameObjects();
        initStars();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        game.batch.setProjectionMatrix(cam.combined);

        updateStars();

        game.batch.begin();
        draw();
        game.batch.end();

        //logic for switching screens
        if (Gdx.input.isTouched()) {
            pos.set(Gdx.input.getX(),Gdx.input.getY(),0);
            cam.unproject(pos);
            float x = pos.x;
            float y = pos.y;

            if(newGameSprite.getBoundingRectangle().contains(x,y)) {
                dispose();
                game.setScreen(new Stardust(game));
            }

            if(continueGameSprite.getBoundingRectangle().contains(x,y)){
                //after saves are implemented
                //different load process for ship?
            }
            if(settingsSprite.getBoundingRectangle().contains(x,y)){
                //game.setScreen(new Settings(game));
            }

        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        newGameTexture.dispose();
        continueGameTexture.dispose();
        settingsTexture.dispose();
        starTexture1.dispose();
        starTexture2.dispose();
        starTexture3.dispose();
        starTexture4.dispose();
        titleTexture.dispose();
        //game.batch.dispose();
        //game.font.dispose();
    }

    //Initializing main menu textures
    public void initTextures(){
        newGameTexture = new Texture(Gdx.files.internal("menures/new_game.png"));
        continueGameTexture = new Texture(Gdx.files.internal("menures/continue_game.png"));
        settingsTexture = new Texture(Gdx.files.internal("menures/settings.png"));
        starTexture1 = new Texture(Gdx.files.internal("gameres/star01.png"));
        starTexture2 = new Texture(Gdx.files.internal("gameres/star02.png"));
        starTexture3 = new Texture(Gdx.files.internal("gameres/star03.png"));
        starTexture4 = new Texture(Gdx.files.internal("gameres/star04.png"));
        titleTexture = new Texture(Gdx.files.internal("menures/title.png"));
    }

    //Initializing game objects
    public void initGameObjects(){
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float startY = height/2f;

        newGameSprite = new Sprite(newGameTexture,256,128);
        newGameSprite.setX(width/2-128); newGameSprite.setY(startY);
        newGameSprite.setScale(1.5f);

        float deductY = height/11 + newGameSprite.getHeight();

        continueGameSprite = new Sprite(continueGameTexture);
        continueGameSprite.setX(width/2-128); continueGameSprite.setY(startY-deductY);
        continueGameSprite.setScale(1.5f);

        settingsSprite = new Sprite (settingsTexture);
        settingsSprite.setX(width/2-128); settingsSprite.setY(startY-deductY*2);
        settingsSprite.setScale(1.5f);

        titleSprite = new Sprite(titleTexture,256,128);
        titleSprite.setX(width/2-128); titleSprite.setY(height/1.3f);
        titleSprite.setScale(3f);
    }

    //Initializing stars to background
    public void initStars(){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        for (int i = 0; i < 25; i++) {
            //rng to spawn a random star
            int starType = MathUtils.random(1, 4);
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);

            if (starType == 1) {
                Sprite starSprite = new Sprite(starTexture1, 16, 16);
                starSprite.setPosition(x, y);
                Star star = new Star(starSprite);
                stars.add(star);
            } else if (starType == 2) {
                Sprite starSprite = new Sprite(starTexture2, 16, 16);
                starSprite.setPosition(x, y);
                Star star = new Star(starSprite);
                stars.add(star);
            } else if (starType == 3) {
                Sprite starSprite = new Sprite(starTexture3, 32, 32);
                starSprite.setPosition(x, y);
                Star star = new Star(starSprite);
                stars.add(star);
            } else {
                Sprite starSprite = new Sprite(starTexture4, 32, 32);
                starSprite.setPosition(x, y);
                Star star = new Star(starSprite);
                stars.add(star);
            }
        }
    }

    //update star objects
    public void updateStars() {
        int size = stars.size;
        if (stars != null) {
            for (int i = 0; i < size; i++) {
                float speed = Gdx.graphics.getDeltaTime() * -10;
                stars.get(i).getSprite().translateY(speed); //fix

                if (stars.get(i).getSprite().getY() < -16) {
                    stars.get(i).getSprite().setY(Gdx.graphics.getHeight());
                }
            }
        }
    }

    //Drawing objects to the screen
    public void draw(){

        for (int i=0;i<stars.size;i++) {
            stars.get(i).getSprite().draw(game.batch);
        }

        titleSprite.draw(game.batch);
        newGameSprite.draw(game.batch);
        continueGameSprite.draw(game.batch);
        settingsSprite.draw(game.batch);
    }
}
