import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

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
        float vertices[] = {};
        
        int elements[] = {};
        
        float tcoords[] = {};
        
        float normals[] = {};
        
        String textureFileName = "assets/textures/slint.png";
        String modelFileName = "assets/models/spear.obj";
        
        
        Model monkey = new Model(modelFileName);
        vertices = monkey.vertices;
        tcoords = monkey.texCoords;
        normals = monkey.normals;
        

        elements = monkey.elements;
        
        
        //shader stuff
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
        
        //end shader stuff
        
        
        int vao = glGenVertexArrays();
        
        glBindVertexArray(vao);
        
        
        
        //texture stuff
        
        //Let's load a texture here.
        InputStream is = null;
        ByteBuffer buf = null;
        
        try{
        	is = new FileInputStream(textureFileName);
        	PNGDecoder pd = new PNGDecoder(is);
        	
        	buf = ByteBuffer.allocateDirect(4*pd.getWidth()*pd.getHeight());
        	pd.decode(buf, pd.getWidth()*4, Format.RGBA);
        	buf.flip();

        	
        }catch (Exception e)
        {
        	System.out.println("error " + e);
        }
        
        
        int texO = glGenTextures();
        
        glActiveTexture(0);
        glBindTexture(GL_TEXTURE_2D, texO);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, 1024, 1024, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        
        
        
        //done that stuff 
        FloatBuffer vbuff = genFloatBuffer(vertices);
        FloatBuffer nbuff = genFloatBuffer(normals);
        FloatBuffer tcbuff = genFloatBuffer(tcoords);
        
        IntBuffer ebuff = BufferUtils.createIntBuffer(elements.length);
        ebuff.put(elements);
        ebuff.rewind();
        
        int vertBO = glGenBuffers();
        int normalBO = glGenBuffers();
        int tCoordBO = glGenBuffers();
        int elemBO = glGenBuffers();
        
        glBindBuffer(GL_ARRAY_BUFFER, vertBO);
        glBufferData(GL_ARRAY_BUFFER, vbuff , GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, tCoordBO);
        glBufferData(GL_ARRAY_BUFFER, tcbuff, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, normalBO);
        glBufferData(GL_ARRAY_BUFFER, nbuff , GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elemBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ebuff, GL_STATIC_DRAW);
        
        
        
        int positionAttrib = glGetAttribLocation( shaderProgram, "position");
        int normalAttrib = glGetAttribLocation( shaderProgram, "normal");
        int tCoordAttrib = glGetAttribLocation( shaderProgram, "texCoord");
        
        
        
        
        glBindBuffer(GL_ARRAY_BUFFER, vertBO);
        glVertexAttribPointer( positionAttrib, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(positionAttrib);
        
        
        glBindBuffer(GL_ARRAY_BUFFER, tCoordBO);
        glVertexAttribPointer( tCoordAttrib, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(tCoordAttrib);
        
        

        

        glBindBuffer(GL_ARRAY_BUFFER, normalBO);
        glVertexAttribPointer( normalAttrib, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(normalAttrib);
        
        boolean quit = false;

        
        
        int A_viewMat = glGetUniformLocation(shaderProgram, "viewMatrix");
        int A_projMat = glGetUniformLocation(shaderProgram, "projMatrix");
        

        int A_cam = glGetUniformLocation(shaderProgram, "cam");

        int A_img = glGetUniformLocation( shaderProgram, "tex");
        
        
        //camera yz
        float y = 1f;
        
        
        
        float rot  = 0f;
        float rad = 3f;
        
        
        //generate view and projection matrix here;
        Matrix4f proj = buildProjectionMatrix(90.0f, 640f/480f, 0.01f, 100.0f);
        Matrix4f view = buildViewMatrix(new Vector3f(rad * (float)Math.cos(rot), y,rad * (float)Math.sin(rot)), new Vector3f(0.0f, 0.0f, 0.0f));
        
        
        glUniformMatrix4(A_viewMat, true, genFloatBuffer(view));
        glUniformMatrix4(A_projMat, true, genFloatBuffer(proj));
        
        //0
        glUniform1i( A_img, 0);

        //undo the element array buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        while (!quit) {         
            // Clear the screen.
        	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	
        	//gluniform sets go here for live update	
            glUniformMatrix4(A_viewMat, true, genFloatBuffer(buildViewMatrix(new Vector3f(rad * (float)Math.cos(rot), y,rad * (float)Math.sin(rot)), new Vector3f(0.0f, 0.0f, 0.0f))));

            
            
            glUniform3f(A_cam, rad * (float)Math.sin(rot/16), rad,rad * (float)Math.cos(rot/16));
            
        	// Draw code
        	//glDrawArrays(GL_TRIANGLES, 0, vertices.length);
        	glDrawElements(GL_TRIANGLES, ebuff);
            
        	// Update
            Display.update();
            
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
            	rot += 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
            	rot -= 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
            	y -= 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
            	y += 0.01f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
    		{
            	rad -= 0.01f;
    		}
        
            if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
    		{
            	rad += 0.01f;
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