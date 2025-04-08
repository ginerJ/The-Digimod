package com.mojang.blaze3d.vertex;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class SheetedDecalTextureGenerator extends DefaultedVertexConsumer {
   private final VertexConsumer delegate;
   private final Matrix4f cameraInversePose;
   private final Matrix3f normalInversePose;
   private final float textureScale;
   private float x;
   private float y;
   private float z;
   private int overlayU;
   private int overlayV;
   private int lightCoords;
   private float nx;
   private float ny;
   private float nz;

   public SheetedDecalTextureGenerator(VertexConsumer p_260211_, Matrix4f p_259178_, Matrix3f p_259698_, float p_259312_) {
      this.delegate = p_260211_;
      this.cameraInversePose = (new Matrix4f(p_259178_)).invert();
      this.normalInversePose = (new Matrix3f(p_259698_)).invert();
      this.textureScale = p_259312_;
      this.resetState();
   }

   private void resetState() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.overlayU = 0;
      this.overlayV = 10;
      this.lightCoords = 15728880;
      this.nx = 0.0F;
      this.ny = 1.0F;
      this.nz = 0.0F;
   }

   public void endVertex() {
      Vector3f vector3f = this.normalInversePose.transform(new Vector3f(this.nx, this.ny, this.nz));
      Direction direction = Direction.getNearestStable(vector3f.x(), vector3f.y(), vector3f.z());
      Vector4f vector4f = this.cameraInversePose.transform(new Vector4f(this.x, this.y, this.z, 1.0F));
      vector4f.rotateY((float)Math.PI);
      vector4f.rotateX((-(float)Math.PI / 2F));
      vector4f.rotate(direction.getRotation());
      float f = -vector4f.x() * this.textureScale;
      float f1 = -vector4f.y() * this.textureScale;
      this.delegate.vertex((double)this.x, (double)this.y, (double)this.z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(f, f1).overlayCoords(this.overlayU, this.overlayV).uv2(this.lightCoords).normal(this.nx, this.ny, this.nz).endVertex();
      this.resetState();
   }

   public VertexConsumer vertex(double p_85885_, double p_85886_, double p_85887_) {
      this.x = (float)p_85885_;
      this.y = (float)p_85886_;
      this.z = (float)p_85887_;
      return this;
   }

   public VertexConsumer color(int p_85895_, int p_85896_, int p_85897_, int p_85898_) {
      return this;
   }

   public VertexConsumer uv(float p_85889_, float p_85890_) {
      return this;
   }

   public VertexConsumer overlayCoords(int p_85892_, int p_85893_) {
      this.overlayU = p_85892_;
      this.overlayV = p_85893_;
      return this;
   }

   public VertexConsumer uv2(int p_85904_, int p_85905_) {
      this.lightCoords = p_85904_ | p_85905_ << 16;
      return this;
   }

   public VertexConsumer normal(float p_85900_, float p_85901_, float p_85902_) {
      this.nx = p_85900_;
      this.ny = p_85901_;
      this.nz = p_85902_;
      return this;
   }
}
