import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class TheQuadExampleInterleaved {

   // Entry point for the application
   public static void main(String[] args) {
      new TheQuadExampleInterleaved();
   }

   // Setup variables
   private final String WINDOW_TITLE = "The Quad: Interleaved";
   private final int WIDTH = 320;
   private final int HEIGHT = 240;
   // Quad variables
   private int vaoId = 0;
   private int vboId = 0;
   private int vboiId = 0;
   private int indicesCount = 0;
   // Shader variables
   private int vsId = 0;
   private int fsId = 0;
   private int pId = 0;

   public TheQuadExampleInterleaved() {
      // Initialize OpenGL (Display)
      this.setupOpenGL();

      this.setupQuad();
      this.setupShaders();

      while (!Display.isCloseRequested()) {
         // Do a single loop (logic/render)
         this.loopCycle();

         // Force a maximum FPS of about 60
         Display.sync(60);
         // Let the CPU synchronize with the GPU if GPU is tagging behind
         Display.update();
      }

      // Destroy OpenGL (Display)
      this.destroyOpenGL();
   }

   public void setupOpenGL() {
      // Setup an OpenGL context with API version 3.2
      try {
         PixelFormat pixelFormat = new PixelFormat();
         ContextAttribs contextAtrributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

         Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
         Display.setTitle(WINDOW_TITLE);
         Display.create(pixelFormat, contextAtrributes);

         GL11.glViewport(0, 0, WIDTH, HEIGHT);
      } catch (LWJGLException e) {
         e.printStackTrace();
         System.exit(-1);
      }

      // Setup an XNA like background color
      GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);

      // Map the internal OpenGL coordinate system to the entire screen
      GL11.glViewport(0, 0, WIDTH, HEIGHT);
   }

   public void setupQuad() {
      // We'll define our quad using 4 vertices of the custom 'Vertex' class
      Vertex v0 = new Vertex();
      v0.setXYZ(-0.5f, 0.5f, 0f);
      v0.setRGB(1, 0, 0);
      Vertex v1 = new Vertex();
      v1.setXYZ(-0.5f, -0.5f, 0f);
      v1.setRGB(0, 1, 0);
      Vertex v2 = new Vertex();
      v2.setXYZ(0.5f, -0.5f, 0f);
      v2.setRGB(0, 0, 1);
      Vertex v3 = new Vertex();
      v3.setXYZ(0.5f, 0.5f, 0f);
      v3.setRGB(1, 1, 1);

      Vertex[] vertices = new Vertex[] {v0, v1, v2, v3};
      // Put each 'Vertex' in one FloatBuffer
      FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * Vertex.elementCount);
      for (int i = 0; i < vertices.length; i++) {
         verticesBuffer.put(vertices[i].getXYZW());
         verticesBuffer.put(vertices[i].getRGBA());
      }
      verticesBuffer.flip();

      // OpenGL expects to draw vertices in counter clockwise order by default
      byte[] indices = {0, 1, 2, 2, 3, 0};
      indicesCount = indices.length;
      ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
      indicesBuffer.put(indices);
      indicesBuffer.flip();

      // Create a new Vertex Array Object in memory and select it (bind)
      vaoId = GL30.glGenVertexArrays();
      GL30.glBindVertexArray(vaoId);

      // Create a new Vertex Buffer Object in memory and select it (bind)
      vboId = GL15.glGenBuffers();
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
      GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
      // Put the positions in attribute list 0
      GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, 0);
      // Put the colors in attribute list 1
      GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, Vertex.elementBytes * 4);
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

      // Deselect (bind to 0) the VAO
      GL30.glBindVertexArray(0);

      // Create a new VBO for the indices and select it (bind) - INDICES
      vboiId = GL15.glGenBuffers();
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
      GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
   }

   private void setupShaders() {
      int errorCheckValue = GL11.glGetError();

      // Load the vertex shader
      vsId = this.loadShader("src/main/resources/vertex.glsl", GL20.GL_VERTEX_SHADER);
      // Load the fragment shader
      fsId = this.loadShader("src/main/resources/fragment.glsl", GL20.GL_FRAGMENT_SHADER);

      // Create a new shader program that links both shaders
      pId = GL20.glCreateProgram();
      GL20.glAttachShader(pId, vsId);
      GL20.glAttachShader(pId, fsId);

      // Position information will be attribute 0
      GL20.glBindAttribLocation(pId, 0, "in_Position");
      // Color information will be attribute 1
      GL20.glBindAttribLocation(pId, 1, "in_Color");

      GL20.glLinkProgram(pId);
      GL20.glValidateProgram(pId);

      errorCheckValue = GL11.glGetError();
      if (errorCheckValue != GL11.GL_NO_ERROR) {
         System.out.println("ERROR - Could not create the shaders:" + GLU.gluErrorString(errorCheckValue));
         System.exit(-1);
      }
   }

   public void loopCycle() {
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

      GL20.glUseProgram(pId);

      // Bind to the VAO that has all the information about the vertices
      GL30.glBindVertexArray(vaoId);
      GL20.glEnableVertexAttribArray(0);
      GL20.glEnableVertexAttribArray(1);

      // Bind to the index VBO that has all the information about the order of the vertices
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

      // Draw the vertices
      GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

      // Put everything back to default (deselect)
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
      GL20.glDisableVertexAttribArray(0);
      GL20.glDisableVertexAttribArray(1);
      GL30.glBindVertexArray(0);
      GL20.glUseProgram(0);
   }

   public void destroyOpenGL() {
      // Delete the shaders
      GL20.glUseProgram(0);
      GL20.glDetachShader(pId, vsId);
      GL20.glDetachShader(pId, fsId);

      GL20.glDeleteShader(vsId);
      GL20.glDeleteShader(fsId);
      GL20.glDeleteProgram(pId);

      // Select the VAO
      GL30.glBindVertexArray(vaoId);

      // Disable the VBO index from the VAO attributes list
      GL20.glDisableVertexAttribArray(0);
      GL20.glDisableVertexAttribArray(1);

      // Delete the vertex VBO
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
      GL15.glDeleteBuffers(vboId);

      // Delete the index VBO
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
      GL15.glDeleteBuffers(vboiId);

      // Delete the VAO
      GL30.glBindVertexArray(0);
      GL30.glDeleteVertexArrays(vaoId);

      Display.destroy();
   }

   public int loadShader(String filename, int type) {
      StringBuilder shaderSource = new StringBuilder();
      int shaderID = 0;

      try {
         BufferedReader reader = new BufferedReader(new FileReader(filename));
         String line;
         while ((line = reader.readLine()) != null) {
            shaderSource.append(line).append("\n");
         }
         reader.close();
      } catch (IOException e) {
         System.err.println("Could not read file.");
         e.printStackTrace();
         System.exit(-1);
      }

      shaderID = GL20.glCreateShader(type);
      GL20.glShaderSource(shaderID, shaderSource);
      GL20.glCompileShader(shaderID);

      if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
         System.err.println("Could not compile shader.");
         System.exit(-1);
      }

      return shaderID;
   }
}