package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class Frustum {
   public static final int OFFSET_STEP = 4;
   private final FrustumIntersection intersection = new FrustumIntersection();
   private final Matrix4f matrix = new Matrix4f();
   private Vector4f viewVector;
   private double camX;
   private double camY;
   private double camZ;

   public Frustum(Matrix4f p_254207_, Matrix4f p_254535_) {
      this.calculateFrustum(p_254207_, p_254535_);
   }

   public Frustum(Frustum p_194440_) {
      this.intersection.set(p_194440_.matrix);
      this.matrix.set((Matrix4fc)p_194440_.matrix);
      this.camX = p_194440_.camX;
      this.camY = p_194440_.camY;
      this.camZ = p_194440_.camZ;
      this.viewVector = p_194440_.viewVector;
   }

   public Frustum offsetToFullyIncludeCameraCube(int p_194442_) {
      double d0 = Math.floor(this.camX / (double)p_194442_) * (double)p_194442_;
      double d1 = Math.floor(this.camY / (double)p_194442_) * (double)p_194442_;
      double d2 = Math.floor(this.camZ / (double)p_194442_) * (double)p_194442_;
      double d3 = Math.ceil(this.camX / (double)p_194442_) * (double)p_194442_;
      double d4 = Math.ceil(this.camY / (double)p_194442_) * (double)p_194442_;

      for(double d5 = Math.ceil(this.camZ / (double)p_194442_) * (double)p_194442_; this.intersection.intersectAab((float)(d0 - this.camX), (float)(d1 - this.camY), (float)(d2 - this.camZ), (float)(d3 - this.camX), (float)(d4 - this.camY), (float)(d5 - this.camZ)) != -2; this.camZ -= (double)(this.viewVector.z() * 4.0F)) {
         this.camX -= (double)(this.viewVector.x() * 4.0F);
         this.camY -= (double)(this.viewVector.y() * 4.0F);
      }

      return this;
   }

   public void prepare(double p_113003_, double p_113004_, double p_113005_) {
      this.camX = p_113003_;
      this.camY = p_113004_;
      this.camZ = p_113005_;
   }

   private void calculateFrustum(Matrix4f p_253909_, Matrix4f p_254521_) {
      p_254521_.mul(p_253909_, this.matrix);
      this.intersection.set(this.matrix);
      this.viewVector = this.matrix.transformTranspose(new Vector4f(0.0F, 0.0F, 1.0F, 0.0F));
   }

   public boolean isVisible(AABB p_113030_) {
      // FORGE: exit early for infinite bounds, these would otherwise fail in the intersection test at certain camera angles (GH-9321)
      if (p_113030_.equals(net.minecraftforge.common.extensions.IForgeBlockEntity.INFINITE_EXTENT_AABB)) return true;
      return this.cubeInFrustum(p_113030_.minX, p_113030_.minY, p_113030_.minZ, p_113030_.maxX, p_113030_.maxY, p_113030_.maxZ);
   }

   private boolean cubeInFrustum(double p_113007_, double p_113008_, double p_113009_, double p_113010_, double p_113011_, double p_113012_) {
      float f = (float)(p_113007_ - this.camX);
      float f1 = (float)(p_113008_ - this.camY);
      float f2 = (float)(p_113009_ - this.camZ);
      float f3 = (float)(p_113010_ - this.camX);
      float f4 = (float)(p_113011_ - this.camY);
      float f5 = (float)(p_113012_ - this.camZ);
      return this.intersection.testAab(f, f1, f2, f3, f4, f5);
   }
}
