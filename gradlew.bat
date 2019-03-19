package com.codexi.stardust;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import javax.xml.soap.Text;

public class Stardust extends ApplicationAdapter {
	//basic stuff
	private OrthographicCamera cam;
	private ShapeRenderer sr;
	private Vector3 pos;
	private SpriteBatch batch;
	private BitmapFont font;

	//ship globals
	private Texture shipTexture;
	private Sprite shipSprite;
	private Ship ship;

	//bullet globals
    private Texture bulletTexture;
    private Sprite bulletSprite;
    private Bullet bullet;

	//asteroid globals
	private Texture asteroidTexture;
	private Sprite asteroidSprite;
	private Asteroid asteroid;


	//spawning multiple asteroids
	private Array<Asteroid> asteroids;
	private long lastSpawnTime = 0;

	public void create(){
		//camera stuff
		cam = new OrthographicCamera();
		cam.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//position vector, base middle of screen
		pos = new Vector3(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2,0);

		//textures
		shipTexture = new Texture(Gdx.files.internal("ship01.png"));
		asteroidTexture = new Texture(Gdx.files.internal("asteroid.png"));

		//sprite batch, sprites
		batch = new SpriteBatch();
		shipSprite = new Sprite(shipTexture, 128,128);
		asteroidSprite = new Sprite(asteroidTexture, 32,32);

		//game objects
		ship = new Ship(shipSprite,100,500,1,5,5);
		//asteroid = new Asteroid(asteroidSprite,100,3,2);

		//Starting ship location
		ship.getSprite().setCenterY(Gdx.graphics.getHeight()/5);
		ship.getSprite().setCenterX(Gdx.graphics.getWidth()/2);

		//Data
		asteroids = new Array<Asteroid>();

		//Spawning
		spawnAsteroid();
	}

	public 