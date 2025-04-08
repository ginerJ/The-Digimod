package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
   public static void spawnParticlesOnBlockFaces(Level p_216314_, BlockPos p_216315_, ParticleOptions p_216316_, IntProvider p_216317_) {
      for(Direction direction : Direction.values()) {
         spawnParticlesOnBlockFace(p_216314_, p_216315_, p_216316_, p_216317_, direction, () -> {
            return getRandomSpeedRanges(p_216314_.random);
         }, 0.55D);
      }

   }

   public static void spawnParticlesOnBlockFace(Level p_216319_, BlockPos p_216320_, ParticleOptions p_216321_, IntProvider p_216322_, Direction p_216323_, Supplier<Vec3> p_216324_, double p_216325_) {
      int i = p_216322_.sample(p_216319_.random);

      for(int j = 0; j < i; ++j) {
         spawnParticleOnFace(p_216319_, p_216320_, p_216323_, p_216321_, p_216324_.get(), p_216325_);
      }

   }

   private static Vec3 getRandomSpeedRanges(RandomSource p_216303_) {
      return new Vec3(Mth.nextDouble(p_216303_, -0.5D, 0.5D), Mth.nextDouble(p_216303_, -0.5D, 0.5D), Mth.nextDouble(p_216303_, -0.5D, 0.5D));
   }

   public static void spawnParticlesAlongAxis(Direction.Axis p_144968_, Level p_144969_, BlockPos p_144970_, double p_144971_, ParticleOptions p_144972_, UniformInt p_144973_) {
      Vec3 vec3 = Vec3.atCenterOf(p_144970_);
      boolean flag = p_144968_ == Direction.Axis.X;
      boolean flag1 = p_144968_ == Direction.Axis.Y;
      boolean flag2 = p_144968_ == Direction.Axis.Z;
      int i = p_144973_.sample(p_144969_.random);

      for(int j = 0; j < i; ++j) {
         double d0 = vec3.x + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag ? 0.5D : p_144971_);
         double d1 = vec3.y + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag1 ? 0.5D : p_144971_);
         double d2 = vec3.z + Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) * (flag2 ? 0.5D : p_144971_);
         double d3 = flag ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         double d4 = flag1 ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         double d5 = flag2 ? Mth.nextDouble(p_144969_.random, -1.0D, 1.0D) : 0.0D;
         p_144969_.addParticle(p_144972_, d0, d1, d2, d3, d4, d5);
      }

   }

   public static void spawnParticleOnFace(Level p_216307_, BlockPos p_216308_, Direction p_216309_, ParticleOptions p_216310_, Vec3 p_216311_, double p_216312_) {
      Vec3 vec3 = Vec3.atCenterOf(p_216308_);
      int i = p_216309_.getStepX();
      int j = p_216309_.getStepY();
      int k = p_216309_.getStepZ();
      double d0 = vec3.x + (i == 0 ? Mth.nextDouble(p_216307_.random, -0.5D, 0.5D) : (double)i * p_216312_);
      double d1 = vec3.y + (j == 0 ? Mth.nextDouble(p_216307_.random, -0.5D, 0.5D) : (double)j * p_216312_);
      double d2 = vec3.z + (k == 0 ? Mth.nextDouble(p_216307_.random, -0.5D, 0.5D) : (double)k * p_216312_);
      double d3 = i == 0 ? p_216311_.x() : 0.0D;
      double d4 = j == 0 ? p_216311_.y() : 0.0D;
      double d5 = k == 0 ? p_216311_.z() : 0.0D;
      p_216307_.addParticle(p_216310_, d0, d1, d2, d3, d4, d5);
   }

   public static void spawnParticleBelow(Level p_273159_, BlockPos p_273452_, RandomSource p_273538_, ParticleOptions p_273419_) {
      double d0 = (double)p_273452_.getX() + p_273538_.nextDouble();
      double d1 = (double)p_273452_.getY() - 0.05D;
      double d2 = (double)p_273452_.getZ() + p_273538_.nextDouble();
      p_273159_.addParticle(p_273419_, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }
}