package com.base.engine;

public class Camera 
{
	public static final Vector3f yAxis = new Vector3f(0,1,0);
	
	private Vector3f pos;
	private Vector3f forward;
	private Vector3f up;
	
	public Camera()
	{
		this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
	}
	
	public Camera(Vector3f pos, Vector3f forward, Vector3f up)
	{
		this.setPos(pos);
		this.setForward(forward);
		this.setUp(up);
		
		this.forward = forward.Normalized();
		this.up = up.Normalized();
	}
	
	boolean mouseLocked = false;
	Vector2f centerPosition = new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2);
	
	public void input()
	{
		float sensitivity = 0.01f;
		float moveAmt = (float)(10 * Time.getDelta());
		
		if(Input.GetKey(Input.KEY_ESCAPE))
		{
			Input.SetCursor(true);
			mouseLocked = false;
		}
		
		if(Input.GetMouseDown(0))
		{
			Input.SetMousePosition(centerPosition);
			Input.SetCursor(false);
			mouseLocked = true;
		}
		
		if(Input.GetKey(Input.KEY_W))
		{
			move(getForward(), moveAmt);
		}
		
		if(Input.GetKey(Input.KEY_S))
		{
			move(getForward(), -moveAmt);
		}
		
		if(Input.GetKey(Input.KEY_A))
		{
			move(getLeft(), moveAmt);
		}
				
		if(Input.GetKey(Input.KEY_D))
		{
			move(getRight(), moveAmt);
		}		
	}
	
	public void move(Vector3f dir, float amt)
	{
		pos = pos.Add(dir.Mul(amt));
	}
	
	public void rotateY(float angle)
	{
		Vector3f Haxis = yAxis.Cross(forward).Normalized();
		
		forward = forward.Rotate(yAxis, angle).Normalized();
		
		up = forward.Cross(Haxis).Normalized();
	}
	
	public void rotateX(float angle)
	{
		Vector3f Haxis = yAxis.Cross(forward).Normalized();
		
		forward = forward.Rotate(Haxis, angle).Normalized();
		
		up = forward.Cross(Haxis).Normalized();
	}
	
	public Vector3f getLeft()
	{
		return forward.Cross(up).Normalized();
	}
	
	public Vector3f getRight()
	{
		return up.Cross(forward).Normalized();
	}

	public Vector3f getPos() 
	{
		return pos;
	}

	public void setPos(Vector3f pos) 
	{
		this.pos = pos;
	}

	public Vector3f getForward() 
	{
		return forward;
	}

	public void setForward(Vector3f forward) 
	{
		this.forward = forward;
	}

	public Vector3f getUp() 
	{
		return up;
	}

	public void setUp(Vector3f up) 
	{
		this.up = up;
	}
}