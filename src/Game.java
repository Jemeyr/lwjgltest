import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;


//FUuuuck
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.input.Keyboard;

public class Game{
	
	
	private static final double M_PI = 3.141592;

	private final String VertexShaderSource = 	"assets/shaders/vertexShader.glslv";

	private final String FragShaderSource = 	"assets/shaders/fragmentShader.glslf";
	
	
	public int loadShader(String fileName, int shaderType) throws Exception
	{
		
		int shader = -1;
		
		//load the file into a string
		String fileString = "";
		String temp = "";

		BufferedReader in = new BufferedReader(new FileReader(fileName)); 

		while((temp = in.readLine()) != null)
		{
			fileString += temp + "\n";
		}
		in.close();
		
		//load the shader from its source
		String shaderSource = new String(fileString);
		
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
        
        if(res.contains("error"))
        {
        	System.out.println("Error loading " + fileName + "\n");
        	System.out.println("Hey, this means you should upgrade your opengl driver and perhaps your graphics card\n");
        	System.out.println(res);
        	throw new Exception();
        }
        
		return shader;
	}
	
	public FloatBuffer genFloatBuffer(float[] input)
	{
		
		FloatBuffer fbuff = null;
		try{
			fbuff = BufferUtils.createFloatBuffer(input.length);
			fbuff.put(input);
		
			fbuff.rewind();
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return fbuff;
	}
	
	public FloatBuffer genFloatBuffer(Matrix4f input)
	{
		
        FloatBuffer fbuff = null;
        try{
        	fbuff = BufferUtils.createFloatBuffer(16);
	        input.store(fbuff);
        	
	        fbuff.rewind();
        }
        catch (Exception e)
        {
        	System.out.println(e);
        	return null;
        }
        
        return fbuff;
		
	}
	

	
	Matrix4f buildProjectionMatrix(float fov, float ratio, float nearP, float farP) {
		 
	    float f = 1.0f / (float)Math.tan(fov * (M_PI / 360.0));
	 
	    Matrix4f projMatrix = new Matrix4f();
	    Matrix4f.setIdentity(projMatrix);
	 
	    projMatrix.m00 = f / ratio;
	    projMatrix.m11 = f;
	    projMatrix.m22 = (farP + nearP) / (nearP - farP);
	    projMatrix.m32 = (2.0f * farP * nearP) / (nearP - farP);
	    projMatrix.m23 = -1.0f;
	    projMatrix.m33 = 0.0f;
	    
	    projMatrix.transpose();
	    
	    return projMatrix;
	}
	
	Matrix4f buildViewMatrix(Vector3f camPos, Vector3f target)
	{
		Vector3f dir,up,right;

		dir 	= new Vector3f();
		up 		= new Vector3f(0.0f, 1.0f, 0.0f);
		right 	= new Vector3f();
		
		Vector3f.sub(target, camPos, dir);
		dir.normalise();
		
		Vector3f.cross(dir, up, right);
		right.normalise();

		
		//this seems unnecessary? maybe just has to do with normalisation?
		Vector3f.cross(right, dir, up);
		up.normalise();
		
		Matrix4f view = new Matrix4f();
		
		view.m00 = right.x;
		view.m10 = right.y;
		view.m20 = right.z;
		
		
		view.m01 = up.x;
		view.m11 = up.y;
		view.m21 = up.z;
		
		view.m02 = -dir.x;
		view.m12 = -dir.y;
		view.m22 = -dir.z;
		
		view.m33 = 1.0f;
		
		
		Matrix4f aux = new Matrix4f();
		
		aux.m30 = -camPos.x;
		aux.m31 = -camPos.y;
		aux.m32 = -camPos.z;
		
		Matrix4f result = new Matrix4f();
		Matrix4f.mul(view, aux, result);
		
		result.transpose();
		
		return result;
		
	}
	
	
    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        glEnable(GL_DEPTH_TEST);

        //clear to black
        glClearColor(0f, 0f, 0f, 1f);

        //some vertices
        float vertices[] = {

        		 1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f,  -1.0f,
                0.0f,  1.0f,  1.0f,
                
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f,  1.0f,
                0.0f,  1.0f,  -1.0f,
                
                
                
                		};
        
        float colors[] = {

        		0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 

        		0.0f, 1.0f, 0.0f,
        		0.0f, 1.0f, 0.0f,
        		0.0f, 1.0f, 0.0f,
                
        };
        
        FloatBuffer vbuff = genFloatBuffer(vertices);
        FloatBuffer cbuff = genFloatBuffer(colors);
        
        int vao = glGenVertexArrays();
        
        int vertBO = glGenBuffers();
        int colorBO = glGenBuffers();
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vertBO);
        glBufferData(GL_ARRAY_BUFFER, vbuff , GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, colorBO);
        glBufferData(GL_ARRAY_BUFFER, cbuff , GL_STATIC_DRAW);
        
        
        
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
       
        
    
        //shader program gets set up here
        
        int shaderProgram = glCreateProgram();
        
        glAttachShader(shaderProgram, vs);
        glAttachShader(shaderProgram, fs);
        
        glBindFragDataLocation( shaderProgram, 0, "outColor");
        
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);
        
        
        int positionAttrib = glGetAttribLocation( shaderProgram, "position");
        int colorAttrib = glGetAttribLocation( shaderProgram, "color");
        

        glBindBuffer(GL_ARRAY_BUFFER, vertBO);
        glVertexAttribPointer( positionAttrib, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(positionAttrib);
        

        glBindBuffer(GL_ARRAY_BUFFER, colorBO);
        glVertexAttribPointer( colorAttrib, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(colorAttrib);
        
        boolean quit = false;

        
        
        
        int A_viewMat = glGetUniformLocation(shaderProgram, "viewMatrix");
        int A_projMat = glGetUniformLocation(shaderProgram, "projMatrix");

        //camera yz
        float y = 1f;
        float z = 0f;
        
        //generate view and projection matrix here;
        Matrix4f proj = buildProjectionMatrix(90.0f, 640f/480f, 0.01f, 100.0f);
        Matrix4f view = buildViewMatrix(new Vector3f(3.0f, y, z), new Vector3f(0.0f, 0.0f, 0.0f));
        
        
        glUniformMatrix4(A_viewMat, true, genFloatBuffer(view));
        glUniformMatrix4(A_projMat, true, genFloatBuffer(proj));
        
        
        while (!quit) {         
            // Clear the screen.
        	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	
        //gluniform sets go here for live update	

            glUniformMatrix4(A_viewMat, true, genFloatBuffer(buildViewMatrix(new Vector3f(3.0f, y, z), new Vector3f(0.0f, 0.0f, 0.0f))));
        	
        	// Draw code
        	glDrawArrays(GL_TRIANGLES, 0, vertices.length);
        	
        	// Update
            Display.update();
            
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
            	z += 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
            	z -= 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
            	y += 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
            	y -= 0.01f;
            }
            
            
            
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