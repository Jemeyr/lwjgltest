import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Model {
	public float[] vertices;
	public int[] elements;
	public float[] normals;
	public float[] texCoords;
	
	
	public Model(String fileName)
	{
		List<Float> res = new ArrayList<Float>();
		List<Integer> resInt = new ArrayList<Integer>();
		
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


		String currLine = fileScanner.nextLine();
		
		
		
		while(!currLine.contains("v "))
		{
			currLine = fileScanner.nextLine();
		}
		
		while(!currLine.contains("vn"))
		{
			tokens = currLine.split(" ");
			for(int i = 1; i < 4; i++)
			{
				res.add(Float.parseFloat(tokens[i]));
			}
			
			currLine = fileScanner.nextLine();
		}
		
		while(!currLine.contains("f "))
		{
			currLine = fileScanner.nextLine();
		}
		
		
		while(!currLine.equals(""))
		{
			tokens = currLine.split(" ");
			for(int i = 1; i < 4; i++)
			{
				String start = tokens[i].split("/")[0];
				
				resInt.add(Integer.parseInt(start) - 1);
			}
			
			if(!fileScanner.hasNext())
			{
				break;
			}
			currLine = fileScanner.nextLine();
		}
		
		this.elements = new int[resInt.size()];
		for(int i =0; i < resInt.size(); i++)
		{
			elements[i] = resInt.get(i);
		}
		
		
		this.vertices = new float[res.size()];
		for(int i =0; i < res.size(); i++)
		{
			vertices[i] = res.get(i);
		}
		
		
		
		
	}
	
	
}
