package com.base.engine;

public class Medkit 
{
	private static final float PICKUP_DISTANCE = 0.5f;
	public static final int HEAL_AMT = -25;
	
	public static final float SCALE = 0.4f;
	public static final float SIZEY = SCALE;
	public static final float SIZEX = (float) ((double) SIZEY / (0.6785714285714285714285714285714 * 4));
	public static final float START = 0;

	public static final float OFFSET_X = 0f;
	public static final float OFFSET_Y = 0f;

	public static final float TEX_MIN_X = -OFFSET_X;
	public static final float TEX_MAX_X = -1 - OFFSET_X;
	public static final float TEX_MIN_Y = -OFFSET_Y;
	public static final float TEX_MAX_Y = 1 - OFFSET_Y;
	
	private static Mesh mesh;
	private Transform transform;
	private static Material material;
	
	public Medkit(Vector3f position)
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
		
		if(material == null)
		{
			material = new Material(new Texture("MEDIA0.png"));
		}
		
		transform = new Transform();
		transform.setTranslation(position);
	}
	
	public void update() 
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().Sub(transform.getTranslation());
		
		float angleToFaceTheCamera = (float) Math.toDegrees(Math.atan(directionToCamera.GetZ() / directionToCamera.GetX()));

		if (directionToCamera.GetX() < 0)
			angleToFaceTheCamera += 180;

		transform.getRotation().SetY(angleToFaceTheCamera + 90);
		
		if(directionToCamera.Length() < PICKUP_DISTANCE)
		{
			Player player = Game.getLevel().getPlayer();
			
			if(player.getHealth() < player.getMaxHealth())
			{
				player.damage(HEAL_AMT);
				Game.getLevel().removeMedkit(this);
			}
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.Draw();
	}
}
