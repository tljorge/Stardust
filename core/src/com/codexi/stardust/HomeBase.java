package com.codexi.stardust;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class HomeBase implements Screen {
    //Basic stuff
    final Starter game;
    OrthographicCamera cam;
    Vector3 pos;

    //star globals
    private Texture starTexture1, starTexture2, starTexture3, starTexture4;
    private Array<Star> stars;

    //base globals
    private Texture baseBackgroundTexture;
    private Sprite baseBackgroundSprite;
    private Texture wallPaneTexture01,wallPaneTexture02,wallPaneTexture03;
    private Sprite wallPaneSprite01,wallPaneSprite02,wallPaneSprite03;
    private Texture roofOutlineTexture;
    private Sprite roofOutlineSprite;
    private Texture airLockTexture;
    private Sprite airLockSprite;
    private boolean airLockStatus = false;  //false is opened, true is closed
    private Texture landingPadTexture;
    private Sprite landingPadSprite;

    //status bar
    private Texture statusBackgroundTexture, fuelBarTexture, healthBarTexture;
    private Sprite healthBar,fuelBar, statusBackground;

    //ship globals
    private Texture shipTexture;
    private Sprite shipSprite;
    private Ship ship;
    private boolean shipDrawDepth = true; //draws on top of wall pane when true, below when false
    private float shipSpeed = 7.5f; //ship speed

    //control panel globals
    private Texture controlPanelTexture;
    private Sprite controlPanelSprite;
    private Texture upgradeBtnTexture;
    private Sprite upBtn01, upBtn02; //etc... make when needed
    private boolean controlPanelUp = false; //default state control panel is not up,
                                            //true, stop updating and rendering back objects
    private BitmapFont fTitle, fMoney, fUpgrades, fWarning, fDamage, fRate, fHull, fFuelEff,
    fFuelCap, fSpeed, modsMsg, fDamageVal, fRateVal, fHullVal, fFuelCapVal, fFuelEffVal, fSpeedVal;
    //May not need all of those

    //Draw coordinates for all fonts
    float xfTitle, yfTitle;
    float xfMoney, yfMoney;
    float xfUpgrades, yfUpgrades;
    float xfWarning, yfWarning;
    float xfDamage, xyDamage;
    float xfRate, yfRate;
    float xfHull, yfHull;

    //Dynamic font messages
    String moneyMsg;
    //Continue after concept is done


    //Constructor
    public HomeBase(final Starter game){
        this.game = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        pos = new Vector3(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0);

        stars = new Array<Star>();

        initTextures();
        initGameObjects();
        initControlPanel();
        initStatus();
        initBase();
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
        updateShip();
        updateControlPanel();


        game.batch.begin();
        draw();
        game.batch.end();
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
        starTexture1.dispose();
        starTexture2.dispose();
        starTexture3.dispose();
        starTexture4.dispose();
        baseBackgroundTexture.dispose();
        baseBackgroundTexture.dispose();
        wallPaneTexture01.dispose();
        wallPaneTexture02.dispose();
        wallPaneTexture03.dispose();
        healthBarTexture.dispose();
        fuelBarTexture.dispose();
        statusBackgroundTexture.dispose();
        airLockTexture.dispose();
        shipTexture.dispose();
        landingPadTexture.dispose();

        //Control disposing, textures, fonts
        controlPanelTexture.dispose();
        fTitle.dispose(); fMoney.dispose(); fUpgrades.dispose(); fWarning.dispose(); fDamage.dispose();
        fRate.dispose(); fHull.dispose(); fFuelEff.dispose(); fFuelCap.dispose(); fSpeed.dispose();
        modsMsg.dispose(); fDamageVal.dispose(); fRateVal.dispose(); fHullVal.dispose(); fFuelCapVal.dispose();
        fFuelEffVal.dispose(); fSpeedVal.dispose();

    }

    //drawing to render
    public void draw(){
        for (int i=0;i<stars.size;i++) {
            stars.get(i).getSprite().draw(game.batch);
        }

        //basebackground, draw first
        baseBackgroundSprite.draw(game.batch);

        landingPadSprite.draw(game.batch);

        airLockSprite.draw(game.batch);
        if(!shipDrawDepth && !airLockStatus){
            closeAirLock();
        }

        //below wall pane
        if(!shipDrawDepth){
            shipSprite.draw(game.batch);
        }

        //base wall panes
        wallPaneSprite01.draw(game.batch);
        wallPaneSprite03.draw(game.batch);
        wallPaneSprite02.draw(game.batch);


        //over wall pane
        if(shipDrawDepth) {
            shipSprite.draw(game.batch);
        }

        //base roof
        roofOutlineSprite.draw(game.batch);

        //Control panel stuff
        controlPanelSprite.draw(game.batch);
        fTitle.draw(game.batch,"CONTROL PANEL",xfTitle,yfTitle);
        fMoney.draw(game.batch, moneyMsg, xfMoney,yfMoney);
        fUpgrades.draw(game.batch, "SHIP UPGRADES", xfUpgrades, yfUpgrades);
        fWarning.draw(game.batch, "WARNING: UPGRADES INCREASE FUEL COSTS", xfWarning,yfWarning);


    }

    //initializing control panel
    private void initControlPanel(){
        //Control panel background
        controlPanelSprite = new Sprite(controlPanelTexture,512,512);
        controlPanelSprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()/1.75f);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OCRAEXT.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter param1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter param2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter param3 = new FreeTypeFontGenerator.FreeTypeFontParameter();


        //Title init
        param1.size = 60; param1.color = Color.DARK_GRAY;
        fTitle = generator.generateFont(param1);

        //Money init
        param2.size = 60; param2.color = Color.FOREST;
        fMoney = generator.generateFont(param2);

        //Upgrade message init
        fUpgrades = generator.generateFont(param1);

        //Warning message init
        param3.size = 45; param3.color = Color.RED;
        fWarning = generator.generateFont(param3);


    }

    //Updates font
    public void updateControlPanel(){
        //Set font positions relative to control panel sprite
        float height = controlPanelSprite.getHeight();   //height of control panel
        float width = controlPanelSprite.getWidth();     //width of control panel
        float xPos = controlPanelSprite.getX();          //x position of control panel
        float yPos = controlPanelSprite.getY();          //y position of control panel

        //Title pos
        xfTitle = xPos + width/15f; yfTitle = yPos + height - height/15f;

        //Money pos
        xfMoney =  xPos + width/15f; yfMoney = yPos + height - (height/15f)*2.5f;
        moneyMsg = "$ " + String.valueOf(ship.getMoney());

        //Upgrades pos
        xfUpgrades = xPos + width/15f; yfUpgrades = yPos + height - (height/15f)*5f;

        //Warning pos
        xfWarning = xPos + width/15f; yfUpgrades = yPos + height - (height/15f)*5f - 70;
    }

    //initializing star objects
    private void initStars() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        for (int i = 0; i < 35; i++) {
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

    //initialize game objects
    private void initGameObjects() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        //sprite batch, sprites
        shipSprite = new Sprite(shipTexture, 100, 128);

        //game objects, change to call initializer that reads from save file
        ship = new Ship(shipSprite, 100, 300, 1, 1f, 150, 50);

        //Starting ship location
        ship.getSprite().setCenterY(0-shipSprite.getHeight());
        ship.getSprite().setCenterX(width/3);


        //status information
        healthBar = new Sprite(healthBarTexture);
        fuelBar = new Sprite(fuelBarTexture);
        statusBackground = new Sprite(statusBackgroundTexture);
    }

    //initialize textures
    private void initTextures() {
        //star textures
        starTexture1 = new Texture(Gdx.files.internal("gameres/star01.png"));
        starTexture2 = new Texture(Gdx.files.internal("gameres/star02.png"));
        starTexture3 = new Texture(Gdx.files.internal("gameres/star03.png"));
        starTexture4 = new Texture(Gdx.files.internal("gameres/star04.png"));

        //base textures
        baseBackgroundTexture = new Texture(Gdx.files.internal("baseres/baseback1.png"));
        wallPaneTexture01 = new Texture(Gdx.files.internal("baseres/wallpane01.png"));
        wallPaneTexture02 = new Texture(Gdx.files.internal("baseres/wallpane04.png"));
        wallPaneTexture03 = new Texture(Gdx.files.internal("baseres/wallpane03.png"));
        roofOutlineTexture = new Texture(Gdx.files.internal("baseres/roofoutline.png"));
        airLockTexture = new Texture(Gdx.files.internal("baseres/airlock.png"));
        landingPadTexture = new Texture(Gdx.files.internal("baseres/landingpad.png"));

        //status textures
        fuelBarTexture = new Texture(Gdx.files.internal("gameres/fuelbar.png"));
        healthBarTexture = new Texture(Gdx.files.internal("gameres/healthbar.png"));
        statusBackgroundTexture = new Texture(Gdx.files.internal("gameres/status_background.png"));

        //ship textures
        shipTexture = new Texture(Gdx.files.internal("gameres/ship01.png"));

        //control panel textures
        controlPanelTexture = new Texture(Gdx.files.internal("baseres/controlpanel.png"));

    }

    //initialize the base
    private void initBase(){
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        int originalWidth = 256;
        int originalHeight = 128;

        float startHeight = height/1.75f;

        baseBackgroundSprite = new Sprite(baseBackgroundTexture,512,512);
        baseBackgroundSprite.setSize(width,height-height/2.25f);
        baseBackgroundSprite.setPosition(0,startHeight);

        landingPadSprite = new Sprite(landingPadTexture,256,256);
        landingPadSprite.setPosition(width/2-128,height/1.25f);

        wallPaneSprite01 = new Sprite(wallPaneTexture01,originalWidth,originalHeight);
        wallPaneSprite02 = new Sprite(wallPaneTexture02,originalWidth,originalHeight);
        wallPaneSprite03 = new Sprite(wallPaneTexture03,originalWidth,originalHeight);

        wallPaneSprite02.setSize(width/3,originalHeight*1.5f);
        wallPaneSprite02.setPosition(0f,startHeight);

        wallPaneSprite01.setSize(width/3,originalHeight*1.5f);
        wallPaneSprite01.setPosition(0f+wallPaneSprite01.getWidth(), startHeight);

        wallPaneSprite03.setSize(width/3,originalHeight*1.5f);
        wallPaneSprite03.setPosition(0f+wallPaneSprite01.getWidth()*2, startHeight);

        //airlock
        airLockSprite = new Sprite(airLockTexture,128,128);
        airLockSprite.setSize(width/3,originalHeight*1.5f);
        airLockSprite.setX(wallPaneSprite01.getX()-airLockSprite.getWidth()*2);
        airLockSprite.setY(startHeight);

        roofOutlineSprite = new Sprite(roofOutlineTexture,512,512);
        roofOutlineSprite.setSize(width,height-height/1.75f);
        roofOutlineSprite.setPosition(0,wallPaneSprite01.getY()+wallPaneSprite01.getHeight());
    }

    //init fuel and health
    private void initStatus() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float hBarX = width / 20;
        float hBarY = height - (height / 31);
        float fBarX = width / 20;
        float fBarY = height - (height / 19);

        healthBar.setPosition(hBarX, hBarY);
        healthBar.setSize(width / 1.6f, height / 50);

        fuelBar.setPosition(fBarX, fBarY);
        fuelBar.setSize(width / 1.6f, height / 50);

        statusBackground.setPosition(0, height-statusBackground.getHeight()*1.8f);
        statusBackground.setSize(width, statusBackground.getHeight()*1.8f);

    }

    //update ship
    private void updateShip(){
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        checkShipDepth();
        if(shipDrawDepth==false &&
                shipSprite.getY()+shipSprite.getHeight()/2<landingPadSprite.getY()+landingPadSprite.getHeight()/2){
            shipSpeed = shipSpeed-.06f;  //ship deceleration
            shipSprite.setCenterX(width / 2);
            shipSprite.translateY(shipSpeed);
        }
        else if(shipSprite.getY()+shipSprite.getHeight()/2<landingPadSprite.getY()+landingPadSprite.getHeight()/2) {
            shipSprite.setCenterX(width / 2);
            shipSprite.translateY(shipSpeed);
        }
    }


    //update the stars
    private void updateStars(){
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

    //checks to see if ship needs to be drawn above or below wall panel
    private void checkShipDepth(){
        float circleTop = wallPaneSprite01.getY()+wallPaneSprite01.getHeight();
        float circleBottom = wallPaneSprite01.getY()+15;

        //if ship is within those two values flip the boolean so drawing in below
        if(shipSprite.getY()>circleBottom && shipSprite.getY()+shipSprite.getHeight() < circleTop){
            shipDrawDepth = false;
        }
    }

    //if ship is through airlock, close it
    private void closeAirLock(){
        airLockSprite.translateX(12);
        if(airLockSprite.getX()>(wallPaneSprite01.getX()+wallPaneSprite01.getWidth())/2){
            airLockStatus = true; //closed
        }
    }

}
