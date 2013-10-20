package com.bnorm.opengl.tutorial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;

/**
 * Tutorial from http://www.opengl-tutorial.org/beginners-tutorials/tutorial-2-the-first-triangle/
 *
 * @author Brian Norman
 */
public class Tutorial02_TheFirstTriangle {

   public static void main(String[] args) {
      Tutorial02_TheFirstTriangle tutorial02TheFirstTriangle = new Tutorial02_TheFirstTriangle();
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

      int vsId = this.loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial02_vert.glsl", GL_VERTEX_SHADER);
      int fsId = this.loadShader("src/main/resources/com/bnorm/opengl/tutorial/tutorial02_frag.glsl", GL_FRAGMENT_SHADER);

      int pId = glCreateProgram();
      glAttachShader(pId, vsId);
      glAttachShader(pId, fsId);
      glLinkProgram(pId);
      glValidateProgram(pId);

      glDeleteShader(vsId);
      glDeleteShader(fsId);

      glClearColor(0.0f, 0.0f, 0.4f, 0.0f);

      while (!Display.isCloseRequested()) {

         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
         glUseProgram(pId);

         glEnableVertexAttribArray(0);
         glBindBuffer(GL_ARRAY_BUFFER, vboId);
         glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

         glDrawArrays(GL_TRIANGLES, 0, 3);
         glDisableVertexAttribArray(0);

         Display.update();
      }

      Display.destroy();
   }

   private int loadShader(String filename, int type) {
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