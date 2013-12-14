package com.bnorm.opengl.tutorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

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
public class Tutorial07_ModelLoading {

   int fps;
   long lastFPS;
   long lastFrame;

   private boolean captureMouse = false;

   public long getTime() {
      return (Sys.getTime() * 1000) / Sys.getTimerResolution();
   }

   public void updateFPS() {
      if (getTime() - lastFPS > 1000) {
         Display.setTitle("FPS: " + fps + " :: SPF: " + (1000.0 / fps) + "ms");
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
      System.setProperty("org.lwjgl.librarypath", new File("build/natives").getAbsolutePath());
      Tutorial07_ModelLoading tutorial07ModelLoading = new Tutorial07_ModelLoading();
      tutorial07ModelLoading.start();
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
      Model model = Model.loadModel("src/main/resources/com/bnorm/opengl/tutorial/apple.obj");
      Texture tex;
      try {
         tex = TextureLoader.getTexture("PNG", ResourceLoader
                 .getResourceAsStream("src/main/resources/com/bnorm/opengl/tutorial/apple.png"));
         tex.bind();
      } catch (IOException e) {
         e.printStackTrace();
         return;
      }

      // Loaded model buffer
      List<Vector3f> vertexes = model.getVectors();
      final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(3 * vertexes.size());
      for (Vector3f v : vertexes) {
         vertexBuffer.put(v.getX());
         vertexBuffer.put(v.getY());
         vertexBuffer.put(v.getZ());
      }
      vertexBuffer.flip();

      int vertexBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
      glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);


      // Loaded texture buffer
      List<Vector2f> texture = model.getTextures();
      final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(2 * texture.size());
      for (Vector2f v : texture) {
         textureBuffer.put(v.getX());
         textureBuffer.put(v.getY());
      }
      textureBuffer.flip();

      int textureBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, textureBufferId);
      glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);


      int vsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial07_vert.glsl", GL_VERTEX_SHADER);
      int fsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial07_frag.glsl", GL_FRAGMENT_SHADER);

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

         if (Display.isActive()) {
            if (captureMouse) {
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
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
               captureMouse = false;
            } else if (Mouse.isButtonDown(0)) {
               captureMouse = true;
               Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
            }
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

         glDrawArrays(GL_TRIANGLES, 0, vertexes.size() * 3);

         glDisableVertexAttribArray(0);
         glEnableVertexAttribArray(1);

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