package com.mojang.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Transformation implements net.minecraftforge.common.extensions.IForgeTransformation {
   private final Matrix4f matrix;
   public static final Codec<Transformation> CODEC = RecordCodecBuilder.create((p_269604_) -> {
      return p_269604_.group(ExtraCodecs.VECTOR3F.fieldOf("translation").forGetter((p_269599_) -> {
         return p_269599_.translation;
      }), ExtraCodecs.QUATERNIONF.fieldOf("left_rotation").forGetter((p_269600_) -> {
         return p_269600_.leftRotation;
      }), ExtraCodecs.VECTOR3F.fieldOf("scale").forGetter((p_269603_) -> {
         return p_269603_.scale;
      }), ExtraCodecs.QUATERNIONF.fieldOf("right_rotation").forGetter((p_269598_) -> {
         return p_269598_.rightRotation;
      })).apply(p_269604_, Transformation::new);
   });
   public static final Codec<Transformation> EXTENDED_CODEC = Codec.either(CODEC, ExtraCodecs.MATRIX4F.xmap(Transformation::new, Transformation::getMatrix)).xmap((p_269605_) -> {
      return p_269605_.map((p_269601_) -> {
         return p_269601_;
      }, (p_269602_) -> {
         return p_269602_;
      });
   }, Either::left);
   private boolean decomposed;
   @Nullable
   private Vector3f translation;
   @Nullable
   private Quaternionf leftRotation;
   @Nullable
   private Vector3f scale;
   @Nullable
   private Quaternionf rightRotation;
   private static final Transformation IDENTITY = Util.make(() -> {
      Transformation transformation = new Transformation(new Matrix4f());
      transformation.translation = new Vector3f();
      transformation.leftRotation = new Quaternionf();
      transformation.scale = new Vector3f(1.0F, 1.0F, 1.0F);
      transformation.rightRotation = new Quaternionf();
      transformation.decomposed = true;
      return transformation;
   });

   public Transformation(@Nullable Matrix4f p_253689_) {
      if (p_253689_ == null) {
         this.matrix = new Matrix4f();
      } else {
         this.matrix = p_253689_;
      }

   }

   public Transformation(@Nullable Vector3f p_253831_, @Nullable Quaternionf p_253846_, @Nullable Vector3f p_254502_, @Nullable Quaternionf p_253912_) {
      this.matrix = compose(p_253831_, p_253846_, p_254502_, p_253912_);
      this.translation = p_253831_ != null ? p_253831_ : new Vector3f();
      this.leftRotation = p_253846_ != null ? p_253846_ : new Quaternionf();
      this.scale = p_254502_ != null ? p_254502_ : new Vector3f(1.0F, 1.0F, 1.0F);
      this.rightRotation = p_253912_ != null ? p_253912_ : new Quaternionf();
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(Transformation p_121097_) {
      Matrix4f matrix4f = this.getMatrix();
      matrix4f.mul(p_121097_.getMatrix());
      return new Transformation(matrix4f);
   }

   @Nullable
   public Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f matrix4f = this.getMatrix().invert();
         return matrix4f.isFinite() ? new Transformation(matrix4f) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         float f = 1.0F / this.matrix.m33();
         Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose((new Matrix3f(this.matrix)).scale(f));
         this.translation = this.matrix.getTranslation(new Vector3f()).mul(f);
         this.leftRotation = new Quaternionf(triple.getLeft());
         this.scale = new Vector3f(triple.getMiddle());
         this.rightRotation = new Quaternionf(triple.getRight());
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(@Nullable Vector3f p_254465_, @Nullable Quaternionf p_254416_, @Nullable Vector3f p_254499_, @Nullable Quaternionf p_254334_) {
      Matrix4f matrix4f = new Matrix4f();
      if (p_254465_ != null) {
         matrix4f.translation(p_254465_);
      }

      if (p_254416_ != null) {
         matrix4f.rotate(p_254416_);
      }

      if (p_254499_ != null) {
         matrix4f.scale(p_254499_);
      }

      if (p_254334_ != null) {
         matrix4f.rotate(p_254334_);
      }

      return matrix4f;
   }

   public Matrix4f getMatrix() {
      return new Matrix4f(this.matrix);
   }

   public Vector3f getTranslation() {
      this.ensureDecomposed();
      return new Vector3f((Vector3fc)this.translation);
   }

   public Quaternionf getLeftRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.leftRotation);
   }

   public Vector3f getScale() {
      this.ensureDecomposed();
      return new Vector3f((Vector3fc)this.scale);
   }

   public Quaternionf getRightRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.rightRotation);
   }

   public boolean equals(Object p_121108_) {
      if (this == p_121108_) {
         return true;
      } else if (p_121108_ != null && this.getClass() == p_121108_.getClass()) {
         Transformation transformation = (Transformation)p_121108_;
         return Objects.equals(this.matrix, transformation.matrix);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.matrix);
   }

    private Matrix3f normalTransform = null;
    public Matrix3f getNormalMatrix() {
        checkNormalTransform();
        return normalTransform;
    }
    private void checkNormalTransform() {
        if (normalTransform == null) {
            normalTransform = new Matrix3f(matrix);
            normalTransform.invert();
            normalTransform.transpose();
        }
    }

   public Transformation slerp(Transformation p_175938_, float p_175939_) {
      Vector3f vector3f = this.getTranslation();
      Quaternionf quaternionf = this.getLeftRotation();
      Vector3f vector3f1 = this.getScale();
      Quaternionf quaternionf1 = this.getRightRotation();
      vector3f.lerp(p_175938_.getTranslation(), p_175939_);
      quaternionf.slerp(p_175938_.getLeftRotation(), p_175939_);
      vector3f1.lerp(p_175938_.getScale(), p_175939_);
      quaternionf1.slerp(p_175938_.getRightRotation(), p_175939_);
      return new Transformation(vector3f, quaternionf, vector3f1, quaternionf1);
   }
}
