package com.base.engine;

public class DirectionalLight 
{
	private BaseLight base;
	private Vector3f direction;
	
	public DirectionalLight(BaseLight base, Vector3f direction)
	{
		this.SetBase(base);
		this.SetDirection(direction.Normalized());
	}
	
	public BaseLight GetBase() 
	{
		return base;
	}
	
	public void SetBase(BaseLight base) 
	{
		this.base = base;
	}
	
	public Vector3f GetDirection() 
	{
		return direction;
	}
	
	public void SetDirection(Vector3f direction) 
	{
		this.direction = direction.Normalized();
	}
}