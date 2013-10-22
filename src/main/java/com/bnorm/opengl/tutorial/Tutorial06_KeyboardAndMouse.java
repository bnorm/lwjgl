package com.bnorm.opengl.tutorial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Tutorial from http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
 *
 * @author Brian Norman
 */
public class Tutorial06_KeyboardAndMouse {

   int fps;
   long lastFPS;
   long lastFrame;

   public long getTime() {
      return (Sys.getTime() * 1000) / Sys.getTimerResolution();
   }

   public void updateFPS() {
      if (getTime() - lastFPS > 1000) {
         Display.setTitle("FPS: " + fps);
         fps = 0;
         lastFPS += 1000;
      }
      fps++;
   }

   public int getDelta() {
      long time = getTime();
      int delta = (int) (time - lastFrame);
      lastFrame = time;

      return delta;
   }

   public static void main(String[] args) {
      Tutorial06_KeyboardAndMouse tutorial02TheFirstTriangle = new Tutorial06_KeyboardAndMouse();
      tutorial02TheFirstTriangle.start();
   }

   public void start() {
      try {
         Display.setDisplayMode(new DisplayMode(800, 600));
         Display.create();
      } catch (LWJGLException e) {
         System.err.println("Error creating LWJGL::Display");
         e.printStackTrace(System.err);
         System.exit(0);
      }

      lastFPS = getTime(); // call before loop to initialise fps timer

      // Camera
      Camera3f camera = new Camera3f(new Vector3f(2, 2, 3));
      Matrix4f mvp = new Matrix4f();
      FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);

      // Enable depth test
      glEnable(GL_DEPTH_TEST);
      // Accept fragment if it closer to the camera than the former one
      glDepthFunc(GL_LESS);

      // render OpenGL here
      int vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // An array of 3 vectors which represents 3 vertices
      final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(12 * 3 * 3);
      vertexBuffer
              .put(new float[] {-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
                                -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                                -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                                -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                                -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                                1.0f});
      vertexBuffer.flip();

      int vertexBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
      glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      // One color for each vertex. They were generated randomly.
      final FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(12 * 3 * 3);
      colorBuffer
              .put(new float[] {0.583f, 0.771f, 0.014f, 0.609f, 0.115f, 0.436f, 0.327f, 0.483f, 0.844f, 0.822f, 0.569f,
                                0.201f, 0.435f, 0.602f, 0.223f, 0.310f, 0.747f, 0.185f, 0.597f, 0.770f, 0.761f, 0.559f,
                                0.436f, 0.730f, 0.359f, 0.583f, 0.152f, 0.483f, 0.596f, 0.789f, 0.559f, 0.861f, 0.639f,
                                0.195f, 0.548f, 0.859f, 0.014f, 0.184f, 0.576f, 0.771f, 0.328f, 0.970f, 0.406f, 0.615f,
                                0.116f, 0.676f, 0.977f, 0.133f, 0.971f, 0.572f, 0.833f, 0.140f, 0.616f, 0.489f, 0.997f,
                                0.513f, 0.064f, 0.945f, 0.719f, 0.592f, 0.543f, 0.021f, 0.978f, 0.279f, 0.317f, 0.505f,
                                0.167f, 0.620f, 0.077f, 0.347f, 0.857f, 0.137f, 0.055f, 0.953f, 0.042f, 0.714f, 0.505f,
                                0.345f, 0.783f, 0.290f, 0.734f, 0.722f, 0.645f, 0.174f, 0.302f, 0.455f, 0.848f, 0.225f,
                                0.587f, 0.040f, 0.517f, 0.713f, 0.338f, 0.053f, 0.959f, 0.120f, 0.393f, 0.621f, 0.362f,
                                0.673f, 0.211f, 0.457f, 0.820f, 0.883f, 0.371f, 0.982f, 0.099f, 0.879f});
      colorBuffer.flip();

      int colorBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, colorBufferId);
      glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      int vsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial04_vert.glsl", GL_VERTEX_SHADER);
      int fsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial04_frag.glsl", GL_FRAGMENT_SHADER);

      int pId = glCreateProgram();
      glAttachShader(pId, vsId);
      glAttachShader(pId, fsId);
      glLinkProgram(pId);
      glValidateProgram(pId);

      glDeleteShader(vsId);
      glDeleteShader(fsId);

      glClearColor(0.0f, 0.0f, 0.4f, 0.0f);

      int matrixId = glGetUniformLocation(pId, "MVP");
      Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
      while (!Display.isCloseRequested()) {
         updateFPS();
         int delta = getDelta();

         // Calculate view

         int x = Mouse.getX();
         int y = Mouse.getY();
         camera.rotateRight(0.005f * (Display.getWidth() / 2 - x));
         camera.rotateUp(-0.005f * (Display.getHeight() / 2 - y));
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);

         if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            camera.moveForward(0.005f * delta);
         }
         if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            camera.moveForward(-0.005f * delta);
         }
         if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            camera.moveRight(0.005f * delta);
         }
         if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            camera.moveRight(-0.005f * delta);
         }
         if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            camera.moveUp(0.005f * delta);
         }
         if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            camera.moveUp(-0.005f * delta);
         }

         Matrix4f projection = camera.getPerspective();
         Matrix4f view = camera.getView();
         //      Matrix4f model = new Matrix4f();
         Matrix4f.mul(projection, view, mvp);
         //      Matrix4f.mul(MVP, model, MVP);
         mvp.store(mvpBuffer);
         mvpBuffer.flip();


         // Render

         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         glUseProgram(pId);

         glUniformMatrix4(matrixId, false, mvpBuffer);

         // 2nd attribute buffer : colors
         glEnableVertexAttribArray(0);
         glEnableVertexAttribArray(1);

         glDrawArrays(GL_TRIANGLES, 0, 12 * 3);

         glDisableVertexAttribArray(0);
         glDisableVertexAttribArray(1);

         Display.update();
      }

      Display.destroy();
   }

   private static int loadShader(String filename, int type) {
      StringBuilder shaderSource = new StringBuilder();
      int shaderId;

      try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
         String line;
         while ((line = reader.readLine()) != null) {
            shaderSource.append(line).append("\n");
         }
         reader.close();
      } catch (IOException e) {
         System.err.println("Could not read shader file.");
         e.printStackTrace(System.err);
         System.exit(-1);
      }

      shaderId = glCreateShader(type);
      glShaderSource(shaderId, shaderSource);
      glCompileShader(shaderId);

      if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
         System.err.println("Could not compile shader.");
         System.exit(-1);
      }

      return shaderId;
   }
}