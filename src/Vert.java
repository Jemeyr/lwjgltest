
public class Vert {
	public int vertexIndex;
	public int normalIndex;
	public int textureIndex;
	public int index;
	
	private static int indexCount = 0;
	
	public Vert(int v , int t, int n)
	{
		this.index = indexCount++;
		this.vertexIndex = v - 1;
		this.textureIndex = t - 1;
		this.normalIndex = n - 1;
	}
	
	public Vert(String v, String n, String t)
	{
		this(Integer.parseInt(v), Integer.parseInt(n), Integer.parseInt(t));
	}
	
	public void print()
	{
		System.out.print("(" + index + ": " + vertexIndex + "," + textureIndex + "," + normalIndex + ")" );
	}

}
