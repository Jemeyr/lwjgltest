import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;


import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;


//FUuuuck
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import org.lwjgl.input.Keyboard;

public class Game{
	
	
	private final String VertexShaderSource = 	"assets/shaders/vertexShader.glslv";

	private final String FragShaderSource = 	"assets/shaders/fragmentShader.glslf";
	
	
	public int loadShader(String fileName, int shaderType) throws Exception
	{
		int shader = -1;
		
		Path filePath = Paths.get(fileName);
		byte[] file = Files.readAllBytes(filePath);
		
		String shaderSource = new String(file);
		
		shader = glCreateShader(shaderType);
		
        glShaderSource(shader, shaderSource );
        glCompileShader(shader);

        //error check
        IntBuffer infoLogLength = BufferUtils.createIntBuffer(1);
        glGetShader(shader, GL_INFO_LOG_LENGTH, infoLogLength);
        ByteBuffer infoLog = BufferUtils.createByteBuffer(infoLogLength.get(0));
        infoLogLength.clear();
        glGetShaderInfoLog(shader, infoLogLength, infoLog);
        byte[] infoLogBytes = new byte[infoLogLength.get(0)];
        infoLog.get(infoLogBytes, 0, infoLogLength.get(0));
        String res = new String(infoLogBytes);
        
        if(!res.equals(""))
        {
        	System.out.println("Error loading " + fileName + "\n");
        	System.out.println("Hey, this means you should upgrade your opengl driver and perhaps your graphics card\n");
        	System.out.println(res);
        	throw new Exception();
        }
        
		return shader;
	}
	
	
	
	
    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //clear to black
        glClearColor(0f, 0f, 0f, 1f);

        //some vertices
        float vertices[] = {0.0f,  0.5f,
			 				0.5f, 0.0f,
							-0.5f, 0.0f,
							0.0f,  -0.6f,
				 			0.5f, -0.1f,
		 					-0.5f, -0.1f
 					
        					};//new float[6];
        FloatBuffer fbuff = null;
        try{
	        fbuff = BufferUtils.createFloatBuffer(vertices.length);
	        
	        fbuff.put(vertices);
	        fbuff.rewind();
        }
        catch (Exception e)
        {
        	System.out.println(e);
        
        }
        
        
        
        int vao = glGenVertexArrays();
        
        int vbo = glGenBuffers();
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fbuff , GL_STATIC_DRAW);
        
        
        int fs = 0;
        int vs = 0;
        
        try{
        	vs = loadShader(VertexShaderSource, GL_VERTEX_SHADER);
        	fs = loadShader(FragShaderSource, GL_FRAGMENT_SHADER);
        } catch (Exception e)
        {
        	System.out.println("ERROR IN SHADER LOADING");
        	System.out.println(e);
        	
        	//end program
        	Display.destroy();
        	return;
        }
       
        
    
        
        
        int shaderProgram = glCreateProgram();
        
        glAttachShader(shaderProgram, vs);
        glAttachShader(shaderProgram, fs);
        
        glBindFragDataLocation( shaderProgram, 0, "outColor");
        
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);
        
        
        
        int positionAttrib = glGetAttribLocation( shaderProgram, "position");
        
        glVertexAttribPointer( positionAttrib, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(positionAttrib);
        
        boolean quit = false;

        
        int triangleGreen = glGetUniformLocation(shaderProgram, "triGreen");
        
        glUniform1f(triangleGreen, 1.0f);
        
        boolean desc = true;
        float greenVal = 1.0f;
        
        while (!quit) {         
            // Clear the screen.
        	glClear(GL_COLOR_BUFFER_BIT);
        	
        	if(desc)
        	{
        		if(greenVal > 0f)
        		{
        			greenVal -= 0.01f;
        		}
        		else
        		{
        			desc = false;
        		}
        	} 
        	else 
        	{
        		if (greenVal < 1)
	        	{
	        		greenVal += 0.01f;
	        	}
	        	else
	        	{
	        		desc = true;
	        	}
        	}
        	
        	glUniform1f(triangleGreen, greenVal);
        	
        	// Draw code
        	glDrawArrays(GL_TRIANGLES, 0, 6);
        	
        	// Update
            Display.update();

            if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                quit = true;
        }

        Display.destroy();
    }

    public static void main(String args[]) {
        Game application = new Game();
        application.start();
    }

}