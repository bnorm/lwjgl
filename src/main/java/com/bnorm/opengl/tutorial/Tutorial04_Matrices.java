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
public class Tutorial04_Matrices {

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
      Tutorial04_Matrices tutorial02TheFirstTriangle = new Tutorial04_Matrices();
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

      // Enable depth test
      glEnable(GL_DEPTH_TEST);
      // Accept fragment if it closer to the camera than the former one
      glDepthFunc(GL_LESS);

      // render OpenGL here
      int vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // An array of 3 vectors which represents 3 vertices
      final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(12 * 3 * 3);
      vertexBuffer.put(new float[] {
              -1.0f,-1.0f,-1.0f, // triangle 1 : begin
              -1.0f,-1.0f, 1.0f,
              -1.0f, 1.0f, 1.0f, // triangle 1 : end
              1.0f, 1.0f,-1.0f, // triangle 2 : begin
              -1.0f,-1.0f,-1.0f,
              -1.0f, 1.0f,-1.0f, // triangle 2 : end
              1.0f,-1.0f, 1.0f,
              -1.0f,-1.0f,-1.0f,
              1.0f,-1.0f,-1.0f,
              1.0f, 1.0f,-1.0f,
              1.0f,-1.0f,-1.0f,
              -1.0f,-1.0f,-1.0f,
              -1.0f,-1.0f,-1.0f,
              -1.0f, 1.0f, 1.0f,
              -1.0f, 1.0f,-1.0f,
              1.0f,-1.0f, 1.0f,
              -1.0f,-1.0f, 1.0f,
              -1.0f,-1.0f,-1.0f,
              -1.0f, 1.0f, 1.0f,
              -1.0f,-1.0f, 1.0f,
              1.0f,-1.0f, 1.0f,
              1.0f, 1.0f, 1.0f,
              1.0f,-1.0f,-1.0f,
              1.0f, 1.0f,-1.0f,
              1.0f,-1.0f,-1.0f,
              1.0f, 1.0f, 1.0f,
              1.0f,-1.0f, 1.0f,
              1.0f, 1.0f, 1.0f,
              1.0f, 1.0f,-1.0f,
              -1.0f, 1.0f,-1.0f,
              1.0f, 1.0f, 1.0f,
              -1.0f, 1.0f,-1.0f,
              -1.0f, 1.0f, 1.0f,
              1.0f, 1.0f, 1.0f,
              -1.0f, 1.0f, 1.0f,
              1.0f,-1.0f, 1.0f
      });
      vertexBuffer.flip();

      int vertexBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
      glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

      // One color for each vertex. They were generated randomly.
      final FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(12 * 3 * 3);
      colorBuffer.put(new float[] {
              0.583f,  0.771f,  0.014f,
              0.609f,  0.115f,  0.436f,
              0.327f,  0.483f,  0.844f,
              0.822f,  0.569f,  0.201f,
              0.435f,  0.602f,  0.223f,
              0.310f,  0.747f,  0.185f,
              0.597f,  0.770f,  0.761f,
              0.559f,  0.436f,  0.730f,
              0.359f,  0.583f,  0.152f,
              0.483f,  0.596f,  0.789f,
              0.559f,  0.861f,  0.639f,
              0.195f,  0.548f,  0.859f,
              0.014f,  0.184f,  0.576f,
              0.771f,  0.328f,  0.970f,
              0.406f,  0.615f,  0.116f,
              0.676f,  0.977f,  0.133f,
              0.971f,  0.572f,  0.833f,
              0.140f,  0.616f,  0.489f,
              0.997f,  0.513f,  0.064f,
              0.945f,  0.719f,  0.592f,
              0.543f,  0.021f,  0.978f,
              0.279f,  0.317f,  0.505f,
              0.167f,  0.620f,  0.077f,
              0.347f,  0.857f,  0.137f,
              0.055f,  0.953f,  0.042f,
              0.714f,  0.505f,  0.345f,
              0.783f,  0.290f,  0.734f,
              0.722f,  0.645f,  0.174f,
              0.302f,  0.455f,  0.848f,
              0.225f,  0.587f,  0.040f,
              0.517f,  0.713f,  0.338f,
              0.053f,  0.959f,  0.120f,
              0.393f,  0.621f,  0.362f,
              0.673f,  0.211f,  0.457f,
              0.820f,  0.883f,  0.371f,
              0.982f,  0.099f,  0.879f
      });
      colorBuffer.flip();

      int colorBufferId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, colorBufferId);
      glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);

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



      // Projection matrix : 45° Field of View, 4:3 ratio, display range : 0.1 unit <-> 100 units
      Matrix4f projection = perspective((float) Math.PI / 4.0f, 4.0f / 3.0f, 0.1f, 100.0f);

      // Camera matrix
      Vector3f eyes = new Vector3f();
      eyes.x = -4;
      eyes.y = -3;
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

         // 2nd attribute buffer : colors
         glEnableVertexAttribArray(0);
         glEnableVertexAttribArray(1);

         glBindBuffer(GL_ARRAY_BUFFER, colorBufferId);
         glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

         glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
         glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

         glDrawArrays(GL_TRIANGLES, 0, 12*3);

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