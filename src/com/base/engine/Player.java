package com.base.engine;

import java.util.Random;

public class Player 
{
	public static final float GUN_OFFSET = -0.0875f;
	
	public static final float SCALE = 0.0625f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float) ((double) SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float START = 0;
	
	public static final float OFFSET_X = 0f;
	public static final float OFFSET_Y = 0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	private static final float MOUSE_SENSITIVITY = 0.01f;
	private static final float WALK_SPEED = 3.5f;
	private static final float RUN_SPEED = 6f;
	public static final float PLAYER_SIZE = 0.2f;
	public static final float SHOOT_DISTANCE = 1000.0f;
	private static final Vector3f zeroVector = new Vector3f(0,0,0);
	public static final int DAMAGE_MIN = 20;
	public static final int DAMAGE_MAX = 60;
	public static final int MAX_HEALTH = 100;
	
	private static Mesh mesh;
	private static Material gunMaterial;
	
	private Transform gunTransform;
	private Camera camera;
	private Random rand;
	private int health;
	
	private boolean mouseLocked = false;
	private Vector2f centerPosition = new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2);
	private Vector3f movementVector;
	private float moveSpeed = WALK_SPEED;
	
	public Player(Vector3f position)
	{
		if (mesh == null) 
		{
			Vertex[] vertices = new Vertex[] 
			{
					new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
					new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y)) 
			};

			int[] indices = new int[] 
			{ 
					0, 1, 2, 
					0, 2, 3 
			};

			mesh = new Mesh(vertices, indices);
		}
		
		if(gunMaterial == null)
		{
			gunMaterial = new Material(new Texture("PISGE0.png"));
		}
		
		camera = new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
		rand = new Random();
		health = MAX_HEALTH;
		gunTransform = new Transform();
		gunTransform.setTranslation(new Vector3f(7,0,7));
		
	}
	
	public void damage(int amt)
	{
		health -= amt;
		
		if(health > MAX_HEALTH)
			health = MAX_HEALTH;
		
		System.out.println(health);
		
		if(health <= 0)
		{
			Game.setIsRunning(false);
			System.out.println("you dead");
		}
	}
	
	public int getDamage()
	{
		return rand.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN;
	}
	
	public void input()
	{
		if(Input.GetKeyDown(Input.KEY_E))		
			Game.getLevel().openDoors(camera.getPos());
		
		if(Input.GetKey(Input.KEY_ESCAPE))
		{
			Input.SetCursor(true);
			mouseLocked = false;
		}
		
		if(Input.GetMouseDown(0))
		{
			if(!mouseLocked)
			{
				Input.SetMousePosition(centerPosition);
				Input.SetCursor(false);
				mouseLocked = true;
			}
			else
			{
				Vector2f lineStart = new Vector2f (camera.getPos().GetX(), camera.getPos().GetZ());
				Vector2f castDirection = new Vector2f (camera.getForward().GetX(), camera.getForward().GetZ()).Normalized();
				Vector2f lineEnd = lineStart.Add(castDirection.Mul(SHOOT_DISTANCE));
				
				Game.getLevel().checkIntersections(lineStart, lineEnd, true);
			}
		}
		
		moveSpeed = (Input.GetKey(Input.KEY_LSHIFT) || Input.GetKey(Input.KEY_RSHIFT)) ? RUN_SPEED : WALK_SPEED;
		
		movementVector = zeroVector;
		
		if(Input.GetKey(Input.KEY_W))
			movementVector = movementVector.Add(camera.getForward());

		if(Input.GetKey(Input.KEY_S))
			movementVector = movementVector.Sub(camera.getForward());
		
		if(Input.GetKey(Input.KEY_A))
			movementVector = movementVector.Add(camera.getLeft());
				
		if(Input.GetKey(Input.KEY_D))
			movementVector = movementVector.Add(camera.getRight());
			
		if(mouseLocked)
		{
			Vector2f deltaPos = Input.GetMousePosition().Sub(centerPosition);
			
			boolean rotY = (deltaPos.GetX() != 0);
			boolean rotX = (deltaPos.GetY() != 0);
			
			if(rotY)
				camera.rotateY(deltaPos.GetX() * MOUSE_SENSITIVITY);
			
			if(rotX)
				camera.rotateX(-deltaPos.GetY() * MOUSE_SENSITIVITY);
			
			if(rotY || rotX)
				Input.SetMousePosition(centerPosition);
		}
	}
	
	public void update()
	{
		float moveAmt = (float)(moveSpeed * Time.getDelta());
		
		movementVector.SetY(0);
		if(movementVector.Length() > 0)
			movementVector = movementVector.Normalized();
		
		Vector3f oldPos = camera.getPos();
		Vector3f newPos = oldPos.Add(movementVector.Mul(moveAmt));
		
		Vector3f collisionVector = Game.getLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_SIZE);
		movementVector = movementVector.Mul(collisionVector);
		
		if(movementVector.Length() > 0)
			camera.move(movementVector, moveAmt);
		
		// gun movement
		
		gunTransform.setTranslation(camera.getPos().Add(camera.getForward().Normalized().Mul(0.105f)));
		gunTransform.getTranslation().SetY(gunTransform.getTranslation().GetY() + GUN_OFFSET);
		
		Vector3f directionToCamera = Transform.getCamera().getPos().Sub(gunTransform.getTranslation());
		
		float angleToFaceTheCamera = (float) Math.toDegrees(Math.atan(directionToCamera.GetZ() / directionToCamera.GetX()));

		if (directionToCamera.GetX() < 0)
			angleToFaceTheCamera += 180;

		gunTransform.getRotation().SetY(angleToFaceTheCamera + 90);
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
		mesh.Draw();
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getMaxHealth()
	{
		return MAX_HEALTH;
	}
}