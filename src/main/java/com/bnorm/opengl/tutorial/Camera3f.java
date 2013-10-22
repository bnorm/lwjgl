package com.bnorm.opengl.tutorial;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 */
public class Camera3f {

   private static final Vector3f DEFAULT_POSITION = new Vector3f();
   private static final float DEFAULT_HORIZONTAL = (float) Math.PI;
   private static final float DEFAULT_VERTICAL = 0.0f;
   private static final float DEFAULT_FOV = (float) Math.PI / 4.0f;
   private static final float DEFAULT_ASPECT = 4.0f / 3.0f;
   private static final float DEFAULT_Z_NEAR = 0.1f;
   private static final float DEFAULT_Z_FAR = 100.0f;

   private Vector3f position;
   private float horizontal;
   private float vertical;
   private float fov;
   private float aspect;
   private float zNear;
   private float zFar;

   public Camera3f() {
      this(DEFAULT_POSITION, DEFAULT_HORIZONTAL, DEFAULT_VERTICAL, DEFAULT_FOV, DEFAULT_ASPECT, DEFAULT_Z_NEAR,
           DEFAULT_Z_FAR);
   }


   public Camera3f(Vector3f position) {
      this(position, DEFAULT_HORIZONTAL, DEFAULT_VERTICAL, DEFAULT_FOV, DEFAULT_ASPECT, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
   }

   public Camera3f(Vector3f position, float horizontal) {
      this(position, horizontal, DEFAULT_VERTICAL, DEFAULT_FOV, DEFAULT_ASPECT, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
   }

   public Camera3f(Vector3f position, float horizontal, float vertical) {
      this(position, horizontal, vertical, DEFAULT_FOV, DEFAULT_ASPECT, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
   }


   public Camera3f(float fov) {
      this(DEFAULT_POSITION, DEFAULT_HORIZONTAL, DEFAULT_VERTICAL, fov, DEFAULT_ASPECT, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
   }

   public Camera3f(float fov, float aspect) {
      this(DEFAULT_POSITION, DEFAULT_HORIZONTAL, DEFAULT_VERTICAL, fov, aspect, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
   }

   public Camera3f(float fov, float aspect, float zNear, float zFar) {
      this(DEFAULT_POSITION, DEFAULT_HORIZONTAL, DEFAULT_VERTICAL, fov, aspect, zNear, zFar);
   }


   public Camera3f(Vector3f position, float horizontal, float vertical, float fov, float aspect, float zNear,
                   float zFar) {
      this.position = position;
      this.horizontal = horizontal;
      this.vertical = vertical;
      this.fov = fov;
      this.aspect = aspect;
      this.zNear = zNear;
      this.zFar = zFar;
   }


   public Vector3f getPosition() {
      return position.negate(null);
   }

   public void setPosition(Vector3f position) {
      this.position = position;
   }

   public float getHorizontal() {
      return horizontal;
   }

   public void setHorizontal(float horizontal) {
      this.horizontal = horizontal;
   }

   public float getVertical() {
      return vertical;
   }

   public void setVertical(float vertical) {
      this.vertical = vertical;
   }

   public float getFov() {
      return fov;
   }

   public void setFov(float fov) {
      this.fov = fov;
   }

   public float getAspect() {
      return aspect;
   }

   public void setAspect(float aspect) {
      this.aspect = aspect;
   }

   public float getZNear() {
      return zNear;
   }

   public void setZNear(float zNear) {
      this.zNear = zNear;
   }

   public float getZFar() {
      return zFar;
   }

   public void setZFar(float zFar) {
      this.zFar = zFar;
   }


   public void rotateRight(float angle) {
      horizontal += angle;
   }

   public void rotateLeft(float angle) {
      horizontal -= angle;
   }

   public void rotateUp(float angle) {
      vertical += angle;
   }

   public void rotateDown(float angle) {
      vertical -= angle;
   }


   public void moveX(float distance) {
      position.setX(position.getX() + distance);
   }

   public void moveY(float distance) {
      position.setY(position.getY() + distance);
   }

   public void moveZ(float distance) {
      position.setZ(position.getZ() + distance);
   }


   public void moveRight(float distance) {
      Vector3f right = new Vector3f((float) -Math.cos(horizontal), 0.0f, (float) Math.sin(horizontal));
      right.scale(distance);
      Vector3f.add(position, right, position);
   }

   public void moveForward(float distance) {
      float cosV = (float) Math.cos(vertical);
      Vector3f forward = new Vector3f(cosV * (float) Math.sin(horizontal), (float) Math.sin(vertical),
                                      cosV * (float) Math.cos(horizontal));
      forward.scale(distance);
      Vector3f.add(position, forward, position);
   }

   public void moveUp(float distance) {
      position.setY(position.getY() + distance);
   }


   public Matrix4f getView() {
      Matrix4f view = new Matrix4f();

      float sinH = (float) Math.sin(horizontal);
      float cosH = (float) Math.cos(horizontal);
      float sinV = (float) Math.sin(vertical);
      float cosV = (float) Math.cos(vertical);

      Vector3f forward = new Vector3f(cosV * sinH, sinV, cosV * cosH);
      Vector3f right = new Vector3f(-cosH, 0.0f, sinH);
      Vector3f up = Vector3f.cross(right, forward, null);

      view.m00 = right.x;
      view.m10 = right.y;
      view.m20 = right.z;
      view.m01 = up.x;
      view.m11 = up.y;
      view.m21 = up.z;
      view.m02 = -forward.x;
      view.m12 = -forward.y;
      view.m22 = -forward.z;

      Vector3f temp = new Vector3f(position);
      temp.negate();
      view.translate(temp);

      return view;
   }

   public Matrix4f getPerspective() {
      Matrix4f perspective = new Matrix4f();

      float sine;
      float cotangent;
      float deltaZ;

      deltaZ = zFar - zNear;
      sine = (float) Math.sin(fov);

      if (deltaZ != 0 && sine != 0 && aspect != 0) {
         cotangent = (float) Math.cos(fov) / sine;

         perspective.m00 = cotangent / aspect;
         perspective.m11 = cotangent;
         perspective.m22 = -(zFar + zNear) / deltaZ;
         perspective.m23 = -1;
         perspective.m32 = -2 * zNear * zFar / deltaZ;
         perspective.m33 = 0;
      }

      return perspective;
   }
}
