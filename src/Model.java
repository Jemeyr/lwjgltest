import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class Model {
	public float[] vertices;
	public int[] elements;
	public float[] normals;
	public float[] texCoords;
	
	
	public Model(String fileName)
	{
		List<Vector3f> init_vertices = new ArrayList<Vector3f>();
		List<Vector3f> init_normals = new ArrayList<Vector3f>();
		List<Vector2f> init_texCoords = new ArrayList<Vector2f>();
		
		List<Face> init_faces = new ArrayList<Face>();
		
		
		List<Vert> unique_verts = new ArrayList<Vert>();
		List<Integer> unique_indices = new ArrayList<Integer>();
		
		List<Vector3f> output_vertices = new ArrayList<Vector3f>();
		List<Vector3f> output_normals = new ArrayList<Vector3f>();
		List<Vector2f> output_texCoords = new ArrayList<Vector2f>();

		
		String[] tokens = new String[4];
		
		Scanner fileScanner = null;
		
		
		try
		{
			fileScanner = new Scanner(new File(fileName));
		}catch (Exception e){System.out.println("your file is shit, dude");}
		
		if(fileScanner == null)
		{
			System.out.println("your file is shit, dude");
			return;
		}

		String currLine = "";
	
		while(fileScanner.hasNext())
		{
			currLine = fileScanner.nextLine();
			
			
			if(currLine.startsWith("v "))
			{
				tokens = currLine.split(" ");
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				float z = Float.parseFloat(tokens[3]);
				init_vertices.add(new Vector3f(x,y,z));
			}
			else if (currLine.startsWith("vn "))
			{
				tokens = currLine.split(" ");
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				float z = Float.parseFloat(tokens[3]);
				init_normals.add(new Vector3f(x,y,z));
			}
			else if (currLine.startsWith("vt "))
			{
				tokens = currLine.split(" ");
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				init_texCoords.add(new Vector2f(x,y));
			}
			else if (currLine.startsWith("f "))
			{
				Face face = new Face();
				
				tokens = currLine.split(" ");
				int j = 0;
				for(int i = 1; i < 4; i++)
				{
					String[] toks = tokens[i].split("/");
					face.vertices[j] = new Vert(toks[0], toks[1], toks[2]);
					j++;
				}
				face.print();
				init_faces.add(face);
			}
		}
		
		/*
		 * 
		 * Ok so at this point in the code I now have the following data
		 * 
		 * A list of all the vertices
		 * A list of all the texcoords
		 * A list of all the normals
		 * 
		 * A list of faces
		 * 		Each face has 3 verts
		 * 			Each vert has three indices, one for the vertices, texcoords, and normals
		 * 
		 * 
		 * What I need to do:
		 * 
		 * 	Go through each face
		 * 		for each face, go through the verts
		 * 
		 * 			Add to the index buffer the NEW index of the unique triplet we get out
		 * 	
		 * 			Buffer in each of the output things the individual data from the thing referenced
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		//iterate over here and add it
		for(Face f : init_faces)
		{
			for(int j = 0; j < 3; j++)
			{
				Vert v = f.vertices[j];
				boolean flag = true;
				int index = -1;
				
				for(Vert uvert : unique_verts)
				{
					if(uvert.vertexIndex == v.vertexIndex && uvert.textureIndex == v.textureIndex && 
							uvert.normalIndex == v.normalIndex)
					{
						index = uvert.index;
						System.out.println("Index:" + v.index + " is " + index);
						flag = false;
						break;
					}
				}
				
				if(flag)
				{//add 
					//output_vertices.add(init_vertices.get(v.vertexIndex));
					//output_texCoords.add(init_texCoords.get(v.textureIndex));
					//output_normals.add(init_normals.get(v.normalIndex));
					
					
					unique_verts.add(v);
					index = v.index;
				}
				
				
				
				output_vertices.add(init_vertices.get(v.vertexIndex));
				output_texCoords.add(init_texCoords.get(v.textureIndex));
				output_normals.add(init_normals.get(v.normalIndex));
				
				unique_indices.add(index);
	
				
				
				
				
			}
			
		}
		System.out.println(init_vertices.size() + ", " + init_texCoords.size() + ", " + init_normals.size());
		System.out.println(unique_verts.size() + " vs " + init_faces.size());
		

		
		for(Vert v : unique_verts)
		{
			v.print();System.out.println();
			System.out.println("vert: " + init_vertices.get(v.vertexIndex));
			System.out.println("texc: " + init_texCoords.get(v.textureIndex));
			System.out.println("norm: " + init_normals.get(v.normalIndex));
			
			System.out.println();
		}
		
		
		
		this.elements = new int[unique_indices.size()];
		this.vertices = new float[3 * output_vertices.size()];
		this.texCoords = new float[2 * output_texCoords.size()];
		this.normals = new float[3 * output_normals.size()];
		
		System.out.println("//////////////////////////////////");
		System.out.println("///    INPUT DATA              ///");
		System.out.println("//////////////////////////////////");
		
		
		
		
		
		
		
		int counter = 0;
		for(int i = 0; i < unique_indices.size(); i++)
		{
			this.elements[counter++] = unique_indices.get(i);
		}
		
		counter = 0;
		for(Vector3f v : output_vertices)
		{
			this.vertices[counter++] = v.x;
			this.vertices[counter++] = v.y;
			this.vertices[counter++] = v.z;	
		}
		
		counter = 0;
		for(Vector2f v : output_texCoords)
		{
			System.out.println(v.x + ", " + v.y);
			this.texCoords[counter++] = v.x;
			this.texCoords[counter++] = 1.0f - v.y;	
		}
		
		counter = 0;
		for(Vector3f v : output_normals)
		{
			this.normals[counter++] = v.x;
			this.normals[counter++] = v.y;
			this.normals[counter++] = v.z;	
		}
		
		
	}
	
	
}
