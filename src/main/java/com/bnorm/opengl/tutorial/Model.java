package com.bnorm.opengl.tutorial;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 */
public class Model {

   private List<Vector3f> vectors = new LinkedList<>();

   private List<Vector2f> textures = new LinkedList<>();

   public List<Vector3f> getVectors() {
      return vectors;
   }

   public List<Vector2f> getTextures() {
      return textures;
   }

   public static Model loadModel(String location) {
      Model model = new Model();

      List<Vector3f> vectors = new LinkedList<>();
      List<Vector2f> textures = new LinkedList<>();
      List<Vector3f> normals = new LinkedList<>();

      try (Scanner file = new Scanner(new File(location))) {
         while (file.hasNextLine()) {
            Scanner line = new Scanner(file.nextLine());

            switch (line.next()) {
               case "v":
                  vectors.add(new Vector3f(line.nextFloat(), line.nextFloat(), line.nextFloat()));
                  break;
               case "vt":
                  textures.add(new Vector2f(line.nextFloat(), 1.0f - line.nextFloat()));
                  break;
               case "vn":
                  normals.add(new Vector3f(line.nextFloat(), line.nextFloat(), line.nextFloat()));
                  break;
               case "f":
                  while (line.hasNext()) {
                     String[] split = line.next().split("/");
                     model.vectors.add(vectors.get(Integer.valueOf(split[0]) - 1));
                     model.textures.add(textures.get(Integer.valueOf(split[1]) - 1));
                  }
                  break;
               default:
                  break;
            }
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      return model;
   }
}
