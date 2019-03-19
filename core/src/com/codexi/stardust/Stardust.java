package com.codexi.stardust;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import java.sql.Time;


public class Stardust implements Screen {
    final Starter game;

    //basic stuff
    private OrthographicCamera cam;
    private Vector3 pos;
    private FPSLogger logger;

    //ship globals
    private Texture shipTexture;
    private Sprite shipSprite;
    private Ship ship;

    //star globals
    private Texture starTexture1, starTexture2, starTexture3, starTexture4;

    //fuel and health bar globals
    private Texture fuelBarTexture, healthBarTexture, statusBackgroundTexture;
    private Sprite fuelBar, healthBar, statusBackground;

    //bullet globals
    private Texture bulletTexture;

    //asteroid globals
    private Texture asteroidTexture;

    //particle globals
    private Texture particleTexture1, particleTexture2, particleTexture3, particleTexture4;
    private Sprite particleSprite1, particleSprite2, particleSprite3, particleSprite4;
    private Particle particle1, particle2, particle3, particle4;

    //data
    private Array<Asteroid> asteroids;
    private Array<Star> stars;
    private Array<Bullet> bullets;
    private Array<Particle> particles;

    //time variables(TimeUtils)
    private long lastSpawnTime = 0;
    private long lastFireTime = 0;
    private long lastParticleTime = 0;
    private long fuelTimer = 0;
    private long oneSecond = 1000000000;
    private long entryTime = 0;
    private int particleCt=0;

    //scalars
    private float spawnRate = 8f;

    //bullet explosion animation
    private static final int FRAME_COLS = 4, FRAME_ROWS = 2;
    Animation<TextureRegion> bulletanimation1;
    Texture bulletSheet;
    float stateTime;
    float collisionX, collisionY;
    boolean collisionDetector;

    //asteroid explosion animation, scale animation to size?
    Animation<TextureRegion> asteroidAnimation;
    Texture asteroidSheet;
    float stateTime1;
    float asteroidCenterX, asteroidCenterY;
    boolean asteroidExplosion;

    //Entry message
    boolean hasEntered;
    Texture entryTexture;
    Sprite entrySprite;

    //Exit message
    boolean exiting;
    Texture exitTexture;
    Sprite exitSprite;
    long exitTimer = 0;


    //Save state of game
    FileHandle handle;


    //LIBGDX GAME METHODS
    public Stardust(final Starter game) {
        this.game = game;

        //camera stuff
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        logger = new FPSLogger();
        handle = Gdx.files.internal("data/savedata.txt");
        hasEntered = false;
        entryTime = TimeUtils.nanoTime();
        exiting = false;

        //position vector, base middle of screen
        pos = new Vector3(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);

        //textures
        initTextures();

        //Data
        asteroids = new Array<Asteroid>();
        bullets = new Array<Bullet>();
        stars = new Array<Star>();
        particles = new Array<Particle>();

        //Initializing
        initGameObjects();
        initStatus();
        initStars();


        //Animations
        initBulletAnimation();
        initAsteroidAnimation();

    }

    @Override
    public void render(float v) {
        //drawing background, switch to image?
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();

        game.batch.setProjectionMatrix(cam.combined);

        logger.log();

        //draw
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();

        //entry message
        if(!hasEntered) {
            initEntry();
        }

        //drawing game
        draw();
        game.batch.end();

        //updating objects, checking for events
        checkCollision();
        updateStars();
        updateShip();
        updateParticles();
        if(hasEntered && !exiting) {
            updateBullet();
            updateAsteroid();

            //calculate fire rate for bullet spawn
            if (TimeUtils.nanoTime() - fuelTimer > (oneSecond / 4) * .2f) updateFuel();

            if (TimeUtils.nanoTime() - lastFireTime > oneSecond * ship.getRate()) spawnBullet();

            if (TimeUtils.nanoTime() - lastSpawnTime > oneSecond * spawnRate) spawnAsteroid();
        }
        //limits particle creation in render, reuses already created particles
        if ((TimeUtils.nanoTime() - lastParticleTime > oneSecond /5)&& particleCt<2){
            spawnParticle();
            particleCt++;
        }
    }

    @Override
    public void show() {

    }

    public void resize(int width, int height) {

    }

    public void pause() {
    }

    public void resume() {

    }

    @Override
    public void hide() {

    }

    public void dispose() {

        shipTexture.dispose();
        asteroidTexture.dispose();
        bulletTexture.dispose();
        bulletSheet.dispose();
        asteroidSheet.dispose();
        starTexture1.dispose();
        starTexture2.dispose();
        starTexture3.dispose();
        starTexture4.dispose();
        fuelBarTexture.dispose();
        healthBarTexture.dispose();
        statusBackgroundTexture.dispose();
        particleTexture1.dispose();
        particleTexture2.dispose();
        particleTexture3.dispose();
        particleTexture4.dispose();
        exitTexture.dispose();
        entryTexture.dispose();

    }
    //END LIBGDX GAME METHODS


    //Draw method for rendering
    public void draw() {
        //drawing here
        //drawing stars
        if (exiting){
            exitSprite.draw(game.batch);
            if(TimeUtils.timeSinceNanos(exitTimer) > oneSecond*3){
                dispose();
                game.setScreen(new HomeBase(game));
            }
        }

        for (int i=0;i<stars.size;i++) {
            stars.get(i).getSprite().draw(game.batch);
        }


        //drawing particles
        for (int i=0;i<particles.size;i++) {
            particles.get(i).getSprite().draw(game.batch);
        }

        //drawing ship
        ship.getSprite().draw(game.batch);

        //drawing asteroids
        for (int i=0;i<asteroids.size;i++) {
            asteroids.get(i).getSprite().draw(game.batch);
        }

        //drawing bullets
        for (int i=0;i<bullets.size;i++) {
            bullets.get(i).getSprite().draw(game.batch);
        }

        //drawing bullet collision animation
        if (collisionDetector) {
            game.batch.draw(bulletanimation1.getKeyFrame(stateTime), collisionX, collisionY);
            stateTime += Gdx.graphics.getDeltaTime();
        }
        if (bulletanimation1.isAnimationFinished(stateTime)) {
            collisionDetector = false;
            stateTime = 0;
        }

        //drawing asteroid animation
        if (asteroidExplosion) {
            game.batch.draw(asteroidAnimation.getKeyFrame(stateTime1), asteroidCenterX, asteroidCenterY);
            stateTime1 += Gdx.graphics.getDeltaTime();
        }
        if (asteroidAnimation.isAnimationFinished(stateTime1)) {
            asteroidExplosion = false;
            stateTime1 = 0;
        }


        //drawing UI
        statusBackground.draw(game.batch);
        healthBar.draw(game.batch);
        fuelBar.draw(game.batch);
        //end drawing
    }

    //Initialize game objects
    public void initGameObjects() {
        //sprite batch, sprites
        shipSprite = new Sprite(shipTexture, 100, 128);
        healthBar = new Sprite(healthBarTexture);
        fuelBar = new Sprite(fuelBarTexture);
        statusBackground = new Sprite(statusBackgroundTexture);

        //game objects
        ship = new Ship(shipSprite, 100, 300, 1, 1f, 150, 50);

        //Starting ship location
        ship.getSprite().setCenterY(Gdx.graphics.getHeight() / 5f);
        ship.getSprite().setCenterX(Gdx.graphics.getWidth() / 2 + shipSprite.getWidth() / 2);

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        entrySprite = new Sprite(entryTexture,256,128);

        entrySprite.setPosition(width/2-entrySprite.getWidth()/2,height/2);
        entrySprite.setScale(2.25f);
    }

    //Initializes textures
    public void initTextures() {
        //game textures
        shipTexture = new Texture(Gdx.files.internal("gameres/ship01.png"));
        asteroidTexture = new Texture(Gdx.files.internal("gameres/asteroid.png"));
        bulletTexture = new Texture(Gdx.files.internal("gameres/bullet.png"));

        //star textures
        starTexture1 = new Texture(Gdx.files.internal("gameres/star01.png"));
        starTexture2 = new Texture(Gdx.files.internal("gameres/star02.png"));
        starTexture3 = new Texture(Gdx.files.internal("gameres/star03.png"));
        starTexture4 = new Texture(Gdx.files.internal("gameres/star04.png"));

        //status textures
        fuelBarTexture = new Texture(Gdx.files.internal("gameres/fuelbar.png"));
        healthBarTexture = new Texture(Gdx.files.internal("gameres/healthbar.png"));
        statusBackgroundTexture = new Texture(Gdx.files.internal("gameres/status_background.png"));

        //particle textures
        particleTexture1 = new Texture(Gdx.files.internal("gameres/particle01.png"));
        particleTexture2 = new Texture(Gdx.files.internal("gameres/particle02.png"));
        particleTexture3 = new Texture(Gdx.files.internal("gameres/particle03.png"));
        particleTexture4 = new Texture(Gdx.files.internal("gameres/particle04.png"));

        //text textures
        entryTexture = new Texture(Gdx.files.internal("textres/entrytext.png"));
        exitTexture = new Texture(Gdx.files.internal("textres/lowfuel.png"));
    }

    //Initialize bullet animation
    public void initBulletAnimation() {
        //Animation stuff bullets
        bulletSheet = new Texture(Gdx.files.internal("gameres/bulletanimation1.png"));
        TextureRegion[][] tmp = TextureRegion.split(bulletSheet,
                bulletSheet.getWidth() / FRAME_COLS,
                bulletSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] bulletFrames = new TextureRegion[FRAME_ROWS * FRAME_COLS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                bulletFrames[index++] = tmp[i][j];
            }
        }

        bulletanimation1 = new Animation<TextureRegion>(.045f, bulletFrames);
        bulletanimation1.setPlayMode(Animation.PlayMode.NORMAL);

        stateTime = 0f;
        collisionX = 0;
        collisionY = 0;
        collisionDetector = false;
        //End of animation stuff
    }

    //Initialize asteroid animation
    public void initAsteroidAnimation() {
        //Animation stuff asteroids
        asteroidSheet = new Texture(Gdx.files.internal("gameres/asteroidanimation.png"));
        TextureRegion[][] tmp1 = TextureRegion.split(asteroidSheet,
                asteroidSheet.getWidth() / FRAME_COLS,
                asteroidSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] asteroidFrames = new TextureRegion[FRAME_ROWS * FRAME_COLS];
        int idx1 = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                asteroidFrames[idx1++] = tmp1[i][j];
            }

        }

        asteroidAnimation = new Animation<TextureRegion>(.05f, asteroidFrames);
        asteroidAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        asteroidCenterX = 0;
        asteroidCenterY = 0;
        stateTime1 = 0f;

    }

    //initializes screen with stars
    public void initStars() {
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

    //init fuel and health
    public void initStatus() {
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

    //initialize entry message
    public void initEntry() {
        entrySprite.draw(game.batch);
        if (TimeUtils.timeSinceNanos(entryTime) > oneSecond*3) {
            hasEntered=true;
            entryTexture.dispose();
        }
    }

    //spawning asteroids
    public void spawnAsteroid() {
        //Asteroid data
        float xSpawnPos = MathUtils.random(128, Gdx.graphics.getWidth() - 128);
        float ySpawnPos = Gdx.graphics.getHeight();
        float size = MathUtils.random(2f, 4f);
        int speed = MathUtils.random(25, 100);
        float rate = MathUtils.random(.5f, 1.5f);


        //Determining rotation direction (CW/CCW)
        int dirBool = MathUtils.random(1, 2);
        //noinspection UnusedAssignment
        int rotateDirection = 0;
        if (dirBool == 1) {
            rotateDirection = 1;
        } else {
            rotateDirection = -1;
        }

        //Creating asteroids
        Sprite asteroidSprite = new Sprite(asteroidTexture, 32, 32);
        asteroidSprite.setPosition(xSpawnPos, ySpawnPos);
        Asteroid asteroid = new Asteroid(asteroidSprite, 100, speed, size, rate * rotateDirection);

        //Adding to list and logging time
        asteroids.add(asteroid);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    //spawning particles at the back of the ship
    public void spawnParticle() {
        for (int i = 0; i < 3; i++) {
            float xSpawnPos = (MathUtils.random(shipSprite.getX() + 40, shipSprite.getX() + shipSprite.getWidth() - 55));
            float ySpawnPos = (shipSprite.getY()-15);

            int particlePicker = MathUtils.random(1, 4);

            switch (particlePicker) {
                case 1:
                    particleSprite1 = new Sprite(particleTexture1, 16, 16);
                    particleSprite1.setPosition(xSpawnPos, ySpawnPos);
                    particle1 = new Particle(particleSprite1);
                    particles.add(particle1);
                case 2:
                    particleSprite2 = new Sprite(particleTexture2, 16, 16);
                    particleSprite2.setPosition(xSpawnPos, ySpawnPos);
                    particle2 = new Particle(particleSprite2);
                    particles.add(particle2);
                case 3:
                    particleSprite3 = new Sprite(particleTexture3, 16, 16);
                    particleSprite3.setPosition(xSpawnPos, ySpawnPos);
                    particle3 = new Particle(particleSprite3);
                    particles.add(particle3);
                case 4:
                    particleSprite4 = new Sprite(particleTexture4, 16, 16);
                    particleSprite4.setPosition(xSpawnPos, ySpawnPos);
                    particle4 = new Particle(particleSprite4);
                    particles.add(particle4);
            }
        }

        lastParticleTime = TimeUtils.nanoTime();

    }

    //spawning bullets at the front of the ship
    public void spawnBullet() {
        //Bullet data
        float xSpawnPos = ship.getSprite().getX() + 42;
        float ySpawnPos = ship.getSprite().getY() + 128;

        //Creating bullets
        Sprite bulletSprite = new Sprite(bulletTexture, 16, 16);
        Bullet bullet = new Bullet(bulletSprite, 500);
        bulletSprite.setPosition(xSpawnPos, ySpawnPos);

        //Adding to list and logging time
        bullets.add(bullet);
        lastFireTime = TimeUtils.nanoTime();
    }

    //movement for the ship
    public void updateShip() {
        if (Gdx.input.isTouched()) {
            pos.set(Gdx.input.getX() -64, Gdx.input.getY(), 0);
            cam.unproject(pos);
            //need directional movement functionality
            //direction of movement
            float dx = pos.x - ship.getSprite().getX();
            @SuppressWarnings("UnusedAssignment") int dirX = 0;
            if (dx > 20) {
                dirX = 1;
            } else if (dx < -20) {
                dirX = -1;
            } else {
                dirX = 0;
            }
            float xTrans = (dirX * 1f) * Gdx.graphics.getDeltaTime() * ship.getSpeed();

            ship.getSprite().setX(ship.getSprite().getX()+xTrans);
        }
    }

    //updating particles
    public void updateParticles() {
        if (particles != null) {
            for (int i=0;i<particles.size;i++) {
                float xSpawnPos = (MathUtils.random(shipSprite.getX() + 40, shipSprite.getX() + shipSprite.getWidth() - 55));
                float ySpawnPos = (shipSprite.getY()-10);

                particles.get(i).getSprite().translateY(particles.get(i).getySpeed());
                particles.get(i).getSprite().translateX(particles.get(i).getxSpeed());

                if (particles.get(i).getSprite().getY() < particles.get(i).getDespawnLocation()) {
                    particles.get(i).getSprite().setY(ySpawnPos);
                    particles.get(i).getSprite().setX(xSpawnPos);
                    particles.get(i).randomizeSpeed();
                }
                ;
            }
        }
    }

    //moving asteroids
    public void updateAsteroid() {
        for (int i=0;i<asteroids.size;i++) {
            float speed = -1 * Gdx.graphics.getDeltaTime() * asteroids.get(i).getSpeed();
            asteroids.get(i).getSprite().setY(asteroids.get(i).getSprite().getY()+speed);
            float rate = asteroids.get(i).getRotationRate();
            asteroids.get(i).getSprite().rotate(rate);

            asteroids.get(i).updateCoordinates();

            if (asteroids.get(i).getSprite().getY() < -100) asteroids.removeValue(asteroids.get(i),true);
        }
    }

    //updating bullets
    public void updateBullet() {
        if(bullets!=null) {
            for (int i = 0; i < bullets.size; i++) {
                float speed = Gdx.graphics.getDeltaTime() * bullets.get(i).getSpeed();
                bullets.get(i).getSprite().setY(bullets.get(i).getSprite().getY() + speed); //fix

                bullets.get(i).updateCoordinates();

                if (bullets.get(i).getSprite().getY() > Gdx.graphics.getHeight()) {
                    bullets.removeValue(bullets.get(i), true);
                }
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

    //update fuel
    public void updateFuel() {
        if(ship.getFuel()>=0) {
            float tmpFuel = ship.getFuel();
            ship.setFuel(tmpFuel - .25f);
            float fuelReductionValue = ((1 - (ship.getFuel() / tmpFuel)) * fuelBar.getWidth());

            fuelBar.setSize(fuelBar.getWidth() - fuelReductionValue, fuelBar.getHeight());
            fuelTimer = TimeUtils.nanoTime();
        } else {
            exitSprite = new Sprite(exitTexture);
            exitSprite.setPosition(Gdx.graphics.getWidth()/2-128,Gdx.graphics.getHeight()/2);
            exitSprite.setScale(2f);
            exiting = true;
            exitTimer = TimeUtils.nanoTime();
        }
    }

    //update health
    public void updateHealth(float damage){
        if(ship.getHealth()>=0){
            float tmpHealth = ship.getHealth();
            ship.setHealth(ship.getHealth()-damage);
            float healthReductionValue = ((1 - (ship.getHealth() / tmpHealth)) *healthBar.getWidth());
            healthBar.setSize(healthBar.getWidth() - healthReductionValue, healthBar.getHeight());
        }
    }

    //collision checking for all bullets and asteroids/asteroids and ship
    public void checkCollision() {
        for (int i = 0; i < asteroids.size; i++) {
            if (asteroids.get(i).getSprite().getBoundingRectangle().overlaps(shipSprite.getBoundingRectangle())){
                Rectangle asteroidBounds = asteroids.get(i).getSprite().getBoundingRectangle();
                asteroidCenterX = (asteroidBounds.getX() + asteroidBounds.getWidth() / 2) - 64;
                asteroidCenterY = (asteroidBounds.getY() + asteroidBounds.getHeight() / 2) - 64;
                asteroidExplosion=true;
                updateHealth(asteroids.get(i).getHealth()/10);
                asteroids.removeValue(asteroids.get(i),true);
            }
            for (int j = 0; j < bullets.size; j++) {
                //System.out.println("Bullet pts: ("+bullets.get(j).getxPt()+","+bullets.get(j).getyPt()+")");
                //System.out.println("asteroids pts: ("+asteroids.get(i).getX1Pt()+","+asteroids.get(i).getyPt()+")");
                if (bullets.get(j).getxPt() >= asteroids.get(i).getX1Pt()
                        && bullets.get(j).getxPt() <= asteroids.get(i).getX2Pt()
                        && bullets.get(j).getyPt() >= asteroids.get(i).getyPt()
                        && bullets.get(j).getyPt() <= asteroids.get(i).getyPt() +
                        asteroids.get(i).getSprite().getHeight()) {

                    float dy = asteroids.get(i).getSize() * asteroids.get(i).getSpeed() / 10;
                    collisionX = bullets.get(j).getxPt() - 12;
                    collisionY = bullets.get(j).getyPt() - dy;
                    collisionDetector = true;

                    //Removing health from hit asteroids
                    asteroids.get(i).setHealth(asteroids.get(i).getHealth() - ship.getDamage());

                    if (asteroids.get(i).getHealth() <= 0) {
                        Rectangle asteroidBounds = asteroids.get(i).getSprite().getBoundingRectangle();

                        asteroidCenterX = (asteroidBounds.getX() + asteroidBounds.getWidth() / 2) - 64;
                        asteroidCenterY = (asteroidBounds.getY() + asteroidBounds.getHeight() / 2) - 64;
                        asteroidExplosion = true;
                        asteroids.removeIndex(i);
                        spawnAsteroid();

                    }

                    //System.out.println("Bullet pts: ("+bullets.get(j).getxPt()+","+bullets.get(j).getyPt()+")");
                    bullets.removeIndex(j);
                }
            }
        }
    }

}

