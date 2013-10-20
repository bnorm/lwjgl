package com.bnorm.lwjgl.examples;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class DisplayExample {

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

      while (!Display.isCloseRequested()) {

         // render OpenGL here

         Display.update();
      }

      Display.destroy();
   }

   public static void main(String[] argv) {
      DisplayExample displayExample = new DisplayExample();
      displayExample.start();
   }
}