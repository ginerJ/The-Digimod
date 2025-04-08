package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum Rotation implements StringRepresentable {
   NONE("none", OctahedralGroup.IDENTITY),
   CLOCKWISE_90("clockwise_90", OctahedralGroup.ROT_90_Y_NEG),
   CLOCKWISE_180("180", OctahedralGroup.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90("counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);

   public static final Codec<Rotation> CODEC = StringRepresentable.fromEnum(Rotation::values);
   private final String id;
   private final OctahedralGroup rotation;

   private Rotation(String p_221988_, OctahedralGroup p_221989_) {
      this.id = p_221988_;
      this.rotation = p_221989_;
   }

   public Rotation getRotated(Rotation p_55953_) {
      switch (p_55953_) {
         case CLOCKWISE_180:
            switch (this) {
               case NONE:
                  return CLOCKWISE_180;
               case CLOCKWISE_90:
                  return COUNTERCLOCKWISE_90;
               case CLOCKWISE_180:
                  return NONE;
               case COUNTERCLOCKWISE_90:
                  return CLOCKWISE_90;
            }
         case COUNTERCLOCKWISE_90:
            switch (this) {
               case NONE:
                  return COUNTERCLOCKWISE_90;
               case CLOCKWISE_90:
                  return NONE;
               case CLOCKWISE_180:
                  return CLOCKWISE_90;
               case COUNTERCLOCKWISE_90:
                  return CLOCKWISE_180;
            }
         case CLOCKWISE_90:
            switch (this) {
               case NONE:
                  return CLOCKWISE_90;
               case CLOCKWISE_90:
                  return CLOCKWISE_180;
               case CLOCKWISE_180:
                  return COUNTERCLOCKWISE_90;
               case COUNTERCLOCKWISE_90:
                  return NONE;
            }
         default:
            return this;
      }
   }

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Direction rotate(Direction p_55955_) {
      if (p_55955_.getAxis() == Direction.Axis.Y) {
         return p_55955_;
      } else {
         switch (this) {
            case CLOCKWISE_90:
               return p_55955_.getClockWise();
            case CLOCKWISE_180:
               return p_55955_.getOpposite();
            case COUNTERCLOCKWISE_90:
               return p_55955_.getCounterClockWise();
            default:
               return p_55955_;
         }
      }
   }

   public int rotate(int p_55950_, int p_55951_) {
      switch (this) {
         case CLOCKWISE_90:
            return (p_55950_ + p_55951_ / 4) % p_55951_;
         case CLOCKWISE_180:
            return (p_55950_ + p_55951_ / 2) % p_55951_;
         case COUNTERCLOCKWISE_90:
            return (p_55950_ + p_55951_ * 3 / 4) % p_55951_;
         default:
            return p_55950_;
      }
   }

   public static Rotation getRandom(RandomSource p_221991_) {
      return Util.getRandom(values(), p_221991_);
   }

   public static List<Rotation> getShuffled(RandomSource p_221993_) {
      return Util.shuffledCopy(values(), p_221993_);
   }

   public String getSerializedName() {
      return this.id;
   }
}