


public class Face {
	Vert[] vertices;
	
	
	public Face()
	{
		vertices = new Vert[3];
	}
	
	public void printIndices()
	{
		System.out.print("{");
		for(int i = 0; i < vertices.length; i++)
		{
			if(vertices[i] != null)
				System.out.print(vertices[i].index);
			System.out.print(",");
		}
		System.out.println("}");
	}
	
	public void print()
	{
		System.out.print("{");
		for(int i = 0; i < vertices.length; i++)
		{
			if(vertices[i] != null)
				vertices[i].print();
			System.out.print(",");
		}
		System.out.println("}");
	}
}
