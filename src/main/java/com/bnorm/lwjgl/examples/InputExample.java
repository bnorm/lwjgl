package com.bnorm.lwjgl.examples;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class InputExample {

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

         pollInput();
         Display.update();
      }

      Display.destroy();
   }

   public void pollInput() {
      if (Mouse.isButtonDown(0)) {
         int x = Mouse.getX();
         int y = Mouse.getY();

         System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
      }

      if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
         System.out.println("SPACE KEY IS DOWN");
      }

      while (Keyboard.next()) {
         String state = Keyboard.getEventKeyState() ? "Pressed" : "Released";
         if (Keyboard.getEventKey() == Keyboard.KEY_A) {
            System.out.println("A Key " + state);
         }
         if (Keyboard.getEventKey() == Keyboard.KEY_S) {
            System.out.println("S Key " + state);
         }
         if (Keyboard.getEventKey() == Keyboard.KEY_D) {
            System.out.println("D Key " + state);
         }
      }
   }

   public static void main(String[] argv) {
      InputExample inputExample = new InputExample();
      inputExample.start();
   }
}