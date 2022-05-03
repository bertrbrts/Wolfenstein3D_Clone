package com.base.engine;

public class Vertex 
{
	public static final int SIZE = 8;
	
	private Vector3f pos;
	private Vector2f texCoord;
	private Vector3f normal;
		
	public Vertex(Vector3f pos) 
	{
		this(pos, new Vector2f(0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f texCoord) 
	{
		this(pos, texCoord, new Vector3f(0,0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal) 
	{
		this.SetPos(pos);
		this.SetTexCoord(texCoord);
		this.SetNormal(normal);
	}
	
	public Vector3f GetPos() 
	{
		return pos;
	}
	
	public void SetPos(Vector3f pos) 
	{
		this.pos = pos;
	}

	public Vector2f GetTexCoord() 
	{
		return texCoord;
	}

	public void SetTexCoord(Vector2f texCoord) 
	{
		this.texCoord = texCoord;
	}

	public Vector3f GetNormal() 
	{
		return normal;
	}

	public void SetNormal(Vector3f normal) 
	{
		this.normal = normal;
	}
}