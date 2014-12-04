package com.example.andenginesensor;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity implements
		IAccelerationListener {

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	private Camera camera;
	private Engine engine;
	Scene sahne;
	private PhysicsWorld physicsWorld;
	private FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(0f, 0f, 0f);

	private BitmapTextureAtlas texSaha, texOyuncu1, texOyuncu2, texSahaUst,
			texSahaAlt, texSahaSagUst, texSahaSagAlt, texSahaSolUst,
			texSahaSolAlt;
	private TextureRegion texRegSaha, texRegOyuncu1, texRegOyuncu2,
			texRegSahaUst, texRegSahaAlt, texRegSahaSagUst, texRegSahaSagAlt,
			texRegSahaSolUst, texRegSahaSolAlt;
	private Sprite spriteSaha, spriteOyuncu1, spriteOyuncu2, spriteSahaUst,
			spriteSahaAlt, spriteSahaSagUst, spriteSahaSagAlt,
			spriteSahaSolUst, spriteSahaSolAlt;

	private Body bodyoyuncu1, bodyoyuncu2, bodySahaUst, bodySahaAlt,
			bodySahaSagUst, bodySahaSagAlt, bodySahaSolUst, bodySahaSolAlt;
	
	

	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				camera);

		engineOptions.getUpdateThread();
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engine = new Engine(engineOptions);
		// cihazýn multitouch desteðini sorgulayan sonrada oluþturan kod bloðu
		try {
			if (MultiTouch.isSupported(this)) {

				engineOptions.getTouchOptions().setNeedsMultiTouch(true);

			} else {

				Toast.makeText(getApplicationContext(),
						"Cihazýnýz multiTouch özelliðini desteklemiyor",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		texSaha = new BitmapTextureAtlas(this.getTextureManager(), 1024, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texOyuncu1 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texOyuncu2 = new BitmapTextureAtlas(this.getTextureManager(), 128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaAlt = new BitmapTextureAtlas(this.getTextureManager(), 1024,
				512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaUst = new BitmapTextureAtlas(this.getTextureManager(), 1024, 32,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaSagAlt = new BitmapTextureAtlas(this.getTextureManager(), 32,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaSagUst = new BitmapTextureAtlas(this.getTextureManager(), 32,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaSolUst = new BitmapTextureAtlas(this.getTextureManager(), 32,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		texSahaSolAlt = new BitmapTextureAtlas(this.getTextureManager(), 32,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.texRegSaha = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texSaha, this, "Arkaplan.jpg", 0, 0);
		this.texRegOyuncu1 = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texOyuncu1, this, "kol1.png", 0, 0);
		this.texRegOyuncu2 = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texOyuncu2, this, "kol2.png", 0, 0);

		texRegSahaAlt = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.texSahaAlt, this, "duvar.png", 0, 0);
		texRegSahaUst = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.texSahaUst, this, "duvar.png", 0, 0);
		texRegSahaSagAlt = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texSahaSagAlt, this, "kaleduvari.png", 0,
						0);
		texRegSahaSagUst = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texSahaSagUst, this, "kaleduvari.png", 0,
						0);
		texRegSahaSolUst = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texSahaSolUst, this, "kaleduvari.png", 0,
						0);
		texRegSahaSolAlt = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.texSahaSolAlt, this, "kaleduvari.png", 0,
						0);

		this.texSaha.load();
		this.texOyuncu1.load();
		this.texOyuncu2.load();
		this.texSahaAlt.load();
		this.texSahaUst.load();
		this.texSahaSagAlt.load();
		this.texSahaSagUst.load();
		this.texSahaSolAlt.load();
		this.texSahaSolUst.load();
	}

	@Override
	protected Scene onCreateScene() {
		// TODO Auto-generated method stub
		this.engine.registerUpdateHandler(new FPSLogger());
		sahne = new Scene();
		physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		nesneleriOlustur();
		this.sahne.registerUpdateHandler(physicsWorld);

		return sahne;
	}

	private void nesneleriOlustur() {
		// TODO Auto-generated method stub
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		// sprite nesnelerinin oluþturulmasý....
		spriteSaha = new Sprite(0, 0, texRegSaha, vertexBufferObjectManager);
		spriteOyuncu1 = new Sprite(100, 240, texRegOyuncu1,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				if (pSceneTouchEvent.isActionMove()) {

					bodyoyuncu1
							.setTransform(
									pSceneTouchEvent.getX()
											/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
									pSceneTouchEvent.getY()
											/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
									0);

				}

				return true;
			}
		};

		spriteOyuncu2 = new Sprite(700, 240, texRegOyuncu2,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				bodyoyuncu2
						.setTransform(
								new Vector2(
										pSceneTouchEvent.getX()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
										pSceneTouchEvent.getY()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
								0);

				return true;
			}
		};

		spriteSahaUst = new Sprite(0, 5, texRegSahaUst,
				vertexBufferObjectManager);
		spriteSahaAlt = new Sprite(0, CAMERA_HEIGHT - 37, texRegSahaAlt,
				vertexBufferObjectManager);
		spriteSahaSagUst = new Sprite(CAMERA_WIDTH - 37, -93, texRegSahaSagUst,
				vertexBufferObjectManager);
		spriteSahaSagAlt = new Sprite(CAMERA_WIDTH - 37, 318, texRegSahaSagAlt,
				vertexBufferObjectManager);
		spriteSahaSolUst = new Sprite(5, -93, texRegSahaSolUst,
				vertexBufferObjectManager);
		spriteSahaSolAlt = new Sprite(5, 318, texRegSahaSolAlt,
				vertexBufferObjectManager);

		// body nesnelerinin oluþturulmasý.....
		bodyoyuncu1 = PhysicsFactory.createCircleBody(physicsWorld,
				spriteOyuncu1.getX() + 64, spriteOyuncu1.getY() + 64, 47, 0,
				BodyType.DynamicBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteOyuncu1, bodyoyuncu1, true, true));

		bodyoyuncu2 = PhysicsFactory.createCircleBody(physicsWorld,
				spriteOyuncu2.getX() + 64, spriteOyuncu2.getY() + 64, 47, 0,
				BodyType.DynamicBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteOyuncu2, bodyoyuncu2, true, true));

		bodySahaUst = PhysicsFactory.createBoxBody(physicsWorld, spriteSahaUst,
				BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaUst, bodySahaUst, true, true));

		bodySahaAlt = PhysicsFactory.createBoxBody(physicsWorld, spriteSahaAlt,
				BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaAlt, bodySahaAlt, true, true));

		bodySahaUst = PhysicsFactory.createBoxBody(physicsWorld, spriteSahaUst,
				BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaUst, bodySahaUst, true, true));

		bodySahaSagAlt = PhysicsFactory.createBoxBody(physicsWorld,
				spriteSahaSagAlt, BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaSagAlt, bodySahaSagAlt, true, true));

		bodySahaSagUst = PhysicsFactory.createBoxBody(physicsWorld,
				spriteSahaSagUst, BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaSagUst, bodySahaSagUst, true, true));

		bodySahaSolAlt = PhysicsFactory.createBoxBody(physicsWorld,
				spriteSahaSolAlt, BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaSolAlt, bodySahaSolAlt, true, true));

		bodySahaSolUst = PhysicsFactory.createBoxBody(physicsWorld,
				spriteSahaSolUst, BodyType.StaticBody, fixtureDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				spriteSahaSolUst, bodySahaSolUst, true, true));

		// body nesnelerine aðýrlýk verme iþlemi....
		MassData massoyuncu1 = bodyoyuncu1.getMassData();
		massoyuncu1.mass = 200;
		bodyoyuncu1.setMassData(massoyuncu1);

		MassData massoyuncu2 = bodyoyuncu2.getMassData();
		massoyuncu1.mass = 50;
		bodyoyuncu1.setMassData(massoyuncu2);
		// bodyOyuncu1 ve bodyoyuncu2 nesnelerinin etkileþimi
		physicsWorld.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub
				if (contact.getFixtureA().getBody() == physicsWorld
						.getPhysicsConnectorManager().findBodyByShape(
								spriteOyuncu1)) {
					if (contact.getFixtureB().getBody() == physicsWorld
							.getPhysicsConnectorManager().findBodyByShape(
									spriteOyuncu2)) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "Upps",
										Toast.LENGTH_SHORT).show();
								recreate();

							}
						});

					}

				}

			}
		});

		// sprite nesnelerinin sahneye eklenmesi....
		sahne.attachChild(spriteSaha);
		sahne.attachChild(spriteOyuncu1);
		sahne.attachChild(spriteOyuncu2);
		sahne.attachChild(spriteSahaAlt);
		sahne.attachChild(spriteSahaUst);
		sahne.attachChild(spriteSahaSagAlt);
		sahne.attachChild(spriteSahaSagUst);
		sahne.attachChild(spriteSahaSolAlt);
		sahne.attachChild(spriteSahaSolUst);

		sahne.registerTouchArea(spriteOyuncu1);
		sahne.registerTouchArea(spriteOyuncu2);

	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(),
				pAccelerationData.getY());
		this.physicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);

	}

	@Override
	public synchronized void onResumeGame() {
		// TODO Auto-generated method stub
		super.onResumeGame();
		this.enableAccelerationSensor(this);

	}

	@Override
	public synchronized void onPauseGame() {
		// TODO Auto-generated method stub
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

}
