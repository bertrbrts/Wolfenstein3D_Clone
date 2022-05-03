package com.base.engine;

public class Door 
{
	public static final float LENGTH = 1;
	public static final float HEIGHT = 1;
	public static final float WIDTH  = 0.125F;
	public static final float START = 0;
	public static final double TIME_TO_OPEN = 1.0f;
	public static final double CLOSE_DELAY = 2.0f;
	
	private static Mesh mesh;
	private Material material;
	private Transform transform;
	
	private Vector3f openPosition;
	private Vector3f closePosition;
	
	private boolean isOpening;
	private double openingStartTime;
	private double openTime;
	private double closingStartTime;
	private double closeTime;
	
	public Door(Transform transform, Material material, Vector3f openPosition)
	{
		this.transform = transform;
		this.material = material;
		this.isOpening = false;
		this.closePosition = transform.getTranslation().Mul(1);
		this.openPosition = openPosition;
		
		if(mesh == null)
		{
			// NOTE: ADD TOP/BOTTOM FACE IF YOU SET HEIGHT LESS THAN LEVEL HEIGHT
			Vertex[] vertices = new Vertex[] {
					new Vertex(new Vector3f(START,  START,  START), new Vector2f(0.5F, 1)), 
					new Vertex(new Vector3f(START,  HEIGHT, START), new Vector2f(0.5F,0.75F)),
					new Vertex(new Vector3f(LENGTH, HEIGHT, START), new Vector2f(0.75F,0.75F)),
					new Vertex(new Vector3f(LENGTH, START,  START), new Vector2f(0.75F,1)),
					
					new Vertex(new Vector3f(START,  START,  START), new Vector2f(0.73f,1)), 
					new Vertex(new Vector3f(START,  HEIGHT, START), new Vector2f(0.73f,0.75f)),
					new Vertex(new Vector3f(START,  HEIGHT, WIDTH), new Vector2f(0.75f,0.75f)),
					new Vertex(new Vector3f(START,  START,  WIDTH), new Vector2f(0.75f,1)),
					
					new Vertex(new Vector3f(START,  START,  WIDTH), new Vector2f(0.5F, 1)), 
					new Vertex(new Vector3f(START,  HEIGHT, WIDTH), new Vector2f(0.5F,0.75F)),
					new Vertex(new Vector3f(LENGTH, HEIGHT, WIDTH), new Vector2f(0.75F,0.75F)),
					new Vertex(new Vector3f(LENGTH, START,  WIDTH), new Vector2f(0.75F,1)),
					
					new Vertex(new Vector3f(LENGTH,  START,  START), new Vector2f(0.73f,1)), 
					new Vertex(new Vector3f(LENGTH,  HEIGHT, START), new Vector2f(0.73f,0.75f)),
					new Vertex(new Vector3f(LENGTH,  HEIGHT, WIDTH), new Vector2f(0.75f,0.75f)),
					new Vertex(new Vector3f(LENGTH,  START,  WIDTH), new Vector2f(0.75f,1))};
			
			int[] indices = new int[] {	0,1,2,
										0,2,3,
										
										6,5,4,
										7,6,4,
										
										10,9,8,
										11,10,8,
										
										12,13,14,
										12,14,15};
			
			mesh = new Mesh(vertices, indices);
		}
	}
	
	public void open()
	{
		if(isOpening)
			return;
		
		openingStartTime = (double)Time.getTime()/(double)Time.SECOND;
		openTime = openingStartTime + TIME_TO_OPEN;
		closingStartTime = openTime + CLOSE_DELAY;
		closeTime = closingStartTime + TIME_TO_OPEN;
		
		isOpening = true;
	}
	
	private Vector3f VectorLerp(Vector3f startPos, Vector3f endPos, float lerpFactor)
	{
		return startPos.Add(endPos.Sub(startPos).Mul(lerpFactor));
	}
	
	public void update()
	{
		if(isOpening)
		{
			double time = (double)Time.getTime()/(double)Time.SECOND;
			
			if(time < openTime)
			{
				float lerpFactor = (float)((time - openingStartTime) / TIME_TO_OPEN);
				getTransform().setTranslation(VectorLerp(closePosition, openPosition, lerpFactor));
			}
			else if(time < closingStartTime)
			{
				getTransform().setTranslation(openPosition);
			}
			else if(time < closeTime)
			{
				float lerpFactor = (float)((time - closingStartTime) / TIME_TO_OPEN);
				getTransform().setTranslation(VectorLerp(openPosition, closePosition, lerpFactor));
			}
			else
			{
				getTransform().setTranslation(closePosition);
				isOpening = false;
			}			
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.Draw();
	}
	
	public Vector2f getDoorSize()
	{
		if(getTransform().getRotation().GetY() == 90)
			return new Vector2f(Door.WIDTH, Door.LENGTH);
		else
			return new Vector2f(Door.LENGTH, Door.WIDTH);
	}
	
	public Transform getTransform()
	{
		return transform;
	}
}