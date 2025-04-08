package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class PoseStack implements net.minecraftforge.client.extensions.IForgePoseStack {
   private final Deque<PoseStack.Pose> poseStack = Util.make(Queues.newArrayDeque(), (p_85848_) -> {
      Matrix4f matrix4f = new Matrix4f();
      Matrix3f matrix3f = new Matrix3f();
      p_85848_.add(new PoseStack.Pose(matrix4f, matrix3f));
   });

   public void translate(double p_85838_, double p_85839_, double p_85840_) {
      this.translate((float)p_85838_, (float)p_85839_, (float)p_85840_);
   }

   public void translate(float p_254202_, float p_253782_, float p_254238_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.translate(p_254202_, p_253782_, p_254238_);
   }

   public void scale(float p_85842_, float p_85843_, float p_85844_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.scale(p_85842_, p_85843_, p_85844_);
      if (p_85842_ == p_85843_ && p_85843_ == p_85844_) {
         if (p_85842_ > 0.0F) {
            return;
         }

         posestack$pose.normal.scale(-1.0F);
      }

      float f = 1.0F / p_85842_;
      float f1 = 1.0F / p_85843_;
      float f2 = 1.0F / p_85844_;
      float f3 = Mth.fastInvCubeRoot(f * f1 * f2);
      posestack$pose.normal.scale(f3 * f, f3 * f1, f3 * f2);
   }

   public void mulPose(Quaternionf p_254385_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.rotate(p_254385_);
      posestack$pose.normal.rotate(p_254385_);
   }

   public void rotateAround(Quaternionf p_272904_, float p_273581_, float p_272655_, float p_273275_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.rotateAround(p_272904_, p_273581_, p_272655_, p_273275_);
      posestack$pose.normal.rotate(p_272904_);
   }

   public void pushPose() {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      this.poseStack.addLast(new PoseStack.Pose(new Matrix4f(posestack$pose.pose), new Matrix3f(posestack$pose.normal)));
   }

   public void popPose() {
      this.poseStack.removeLast();
   }

   public PoseStack.Pose last() {
      return this.poseStack.getLast();
   }

   public boolean clear() {
      return this.poseStack.size() == 1;
   }

   public void setIdentity() {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.identity();
      posestack$pose.normal.identity();
   }

   public void mulPoseMatrix(Matrix4f p_254128_) {
      (this.poseStack.getLast()).pose.mul(p_254128_);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Pose {
      final Matrix4f pose;
      final Matrix3f normal;

      Pose(Matrix4f p_254509_, Matrix3f p_254348_) {
         this.pose = p_254509_;
         this.normal = p_254348_;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }
   }
}
