package com.base.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Mesh 
{
	private int vbo;
	private int ibo;
	private int size;
	
	public Mesh(String fileName)
	{
		initMeshData();
		loadMesh(fileName);
	}
	
	public Mesh(Vertex[] vertices, int[] indices)
	{		
		this(vertices, indices, false);		
	}
	
	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals)
	{
		initMeshData();
		AddVertices(vertices, indices, calcNormals);
	}
	
	private void initMeshData()
	{
		vbo = glGenBuffers(); 
		ibo = glGenBuffers(); 
		size = 0;
	}
	
	private void AddVertices(Vertex[] vertices, int[] indices, boolean calcNormals) 
	{
		if (calcNormals)
			CalcNormals(vertices, indices);
		
		size = indices.length;
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Util.CreateFippedBuffer(vertices), GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.CreateFippedBuffer(indices), GL_STATIC_DRAW);		
	}
	
	public void Draw() 
	{
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
	
	private void CalcNormals(Vertex[] vertices, int[] indices)
	{
		for(int i = 0; i < indices.length; i += 3)
		{
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];
			
			Vector3f v1 = vertices[i1].GetPos().Sub(vertices[i0].GetPos());
			Vector3f v2 = vertices[i2].GetPos().Sub(vertices[i0].GetPos());
			
			Vector3f normal = v1.Cross(v2).Normalized();
			
			vertices[i0].SetNormal(vertices[i0].GetNormal().Add(normal));
			vertices[i1].SetNormal(vertices[i1].GetNormal().Add(normal));
			vertices[i2].SetNormal(vertices[i2].GetNormal().Add(normal));
		}
		
		for(int i = 0; i < vertices.length; i++)
			vertices[i].SetNormal(vertices[i].GetNormal().Normalized());
	}
	
	private Mesh loadMesh(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];
		
		if(!ext.equals("obj"))
		{
			System.err.println("Error: File format not supported for mesh data: " + ext);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		BufferedReader meshReader = null;
		
		try 
		{
			meshReader = new BufferedReader(new FileReader("./res/models/" + fileName));
			String line;
			
			while((line = meshReader.readLine()) != null) 
			{
				String[] tokens = line.split(" ");
				tokens = Util.RemoveEmptyStrings(tokens);
				
				if(tokens.length == 0 || tokens[0].equals("#"))
				{
					continue;
				}
				else if (tokens[0].equals("v"))
				{
					vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]), 
														 Float.valueOf(tokens[2]), 
														 Float.valueOf(tokens[3]))));
				}
				else if(tokens[0].equals("f"))
				{
					indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
					indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
					indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
					
					if (tokens.length > 4)
					{
						indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
					}
				}
			}
			
			meshReader.close();
			
			Vertex[] vertexData = new Vertex[vertices.size()];
			vertices.toArray(vertexData);
			
			Integer[] indexData = new Integer[indices.size()];
			indices.toArray(indexData);
			
			AddVertices(vertexData, Util.ToIntArray(indexData), true);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}