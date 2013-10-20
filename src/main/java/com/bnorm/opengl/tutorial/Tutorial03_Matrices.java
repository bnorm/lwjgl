package com.bnorm.opengl.tutorial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Tutorial from http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
 *
 * @author Brian Norman
 */
public class Tutorial03_Matrices {

   int fps;
   long lastFPS;

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

   public static void main(String[] args) {
      Tutorial03_Matrices tutorial02TheFirstTriangle = new Tutorial03_Matrices();
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

      // init OpenGL here
      //      glMatrixMode(GL_PROJECTION);
      //      glLoadIdentity();
      //      glOrtho(0, 800, 0, 600, 1, -1);
      //      glMatrixMode(GL_MODELVIEW);

      // render OpenGL here
      int vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // An array of 3 vectors which represents 3 vertices
      final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
      buffer.put(new float[] {
              -1.0f, -1.0f, 0.0f,
              1.0f, -1.0f, 0.0f,
              0.0f, 1.0f, 0.0f
      });
      buffer.flip();

      int vboId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

      int vsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial03_vert.glsl", GL_VERTEX_SHADER);
      int fsId = loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial03_frag.glsl", GL_FRAGMENT_SHADER);

      int pId = glCreateProgram();
      glAttachShader(pId, vsId);
      glAttachShader(pId, fsId);
      glLinkProgram(pId);
      glValidateProgram(pId);

      glDeleteShader(vsId);
      glDeleteShader(fsId);

      glClearColor(0.0f, 0.0f, 0.4f, 0.0f);



      // Projection matrix : 45° Field of View, 4:3 ratio, display range : 0.1 unit <-> 100 units
      Matrix4f projection = perspective((float) Math.PI / 4.0f, 4.0f / 3.0f, 0.1f, 100.0f);

      // Camera matrix
      Vector3f eyes = new Vector3f();
      eyes.x = 4;
      eyes.y = 3;
      eyes.z = 3;
      Vector3f center = new Vector3f();
      Vector3f up = new Vector3f();
      up.y = 1;
      Matrix4f view = lookAt(eyes, center, up);

      // Model matrix : an identity matrix (model will be at the origin)
      Matrix4f model = new Matrix4f();

      // Our ModelViewProjection : multiplication of our 3 matrices
      Matrix4f MVP = new Matrix4f();
      Matrix4f.mul(projection, view, MVP);
      Matrix4f.mul(MVP, model, MVP);

      FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
      MVP.store(mvpBuffer);
      mvpBuffer.flip();

      int matrixId = glGetUniformLocation(pId, "MVP");

      while (!Display.isCloseRequested()) {
         updateFPS();

         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         glUseProgram(pId);

         glUniformMatrix4(matrixId, false, mvpBuffer);

         glEnableVertexAttribArray(0);
         glBindBuffer(GL_ARRAY_BUFFER, vboId);
         glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

         glDrawArrays(GL_TRIANGLES, 0, 3);
         glDisableVertexAttribArray(0);

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

   private static Matrix4f perspective(float fov, float aspect, float zNear, float zFar) {
      float sine;
      float cotangent;
      float deltaZ;

      deltaZ = zFar - zNear;
      sine = (float) Math.sin(fov);

      if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
         return new Matrix4f();
      }

      cotangent = (float) Math.cos(fov) / sine;

      Matrix4f perspective = new Matrix4f();

      perspective.m00 = cotangent / aspect;
      perspective.m11 = cotangent;
      perspective.m22 = -(zFar + zNear) / deltaZ;
      perspective.m23 = -1;
      perspective.m32 = -2 * zNear * zFar / deltaZ;
      perspective.m33 = 0;

      return perspective;
   }

   private static Matrix4f lookAt(Vector3f eyes, Vector3f center, Vector3f up) {
      Vector3f forward = Vector3f.sub(center, eyes, null);
      Vector3f side = Vector3f.cross(forward, up, null);
      Vector3f cameraUp = Vector3f.cross(side, forward, null);

      forward.normalise();
      side.normalise();
      cameraUp.normalise();

      Matrix4f lookAt = new Matrix4f();
      lookAt.m00 = side.x;
      lookAt.m10 = side.y;
      lookAt.m20 = side.z;

      lookAt.m01 = cameraUp.x;
      lookAt.m11 = cameraUp.y;
      lookAt.m21 = cameraUp.z;

      lookAt.m02 = -forward.x;
      lookAt.m12 = -forward.y;
      lookAt.m22 = -forward.z;

      eyes = new Vector3f(eyes);
      eyes.negate();
      lookAt.translate(eyes);

      return lookAt;
   }
}