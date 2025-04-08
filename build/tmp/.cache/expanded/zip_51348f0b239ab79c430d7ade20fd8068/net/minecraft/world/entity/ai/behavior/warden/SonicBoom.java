package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;

public class SonicBoom extends Behavior<Warden> {
   private static final int DISTANCE_XZ = 15;
   private static final int DISTANCE_Y = 20;
   private static final double KNOCKBACK_VERTICAL = 0.5D;
   private static final double KNOCKBACK_HORIZONTAL = 2.5D;
   public static final int COOLDOWN = 40;
   private static final int TICKS_BEFORE_PLAYING_SOUND = Mth.ceil(34.0D);
   private static final int DURATION = Mth.ceil(60.0F);

   public SonicBoom() {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED, MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED), DURATION);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_217692_, Warden p_217693_) {
      return p_217693_.closerThan(p_217693_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D, 20.0D);
   }

   protected boolean canStillUse(ServerLevel p_217695_, Warden p_217696_, long p_217697_) {
      return true;
   }

   protected void start(ServerLevel p_217713_, Warden p_217714_, long p_217715_) {
      p_217714_.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)DURATION);
      p_217714_.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, (long)TICKS_BEFORE_PLAYING_SOUND);
      p_217713_.broadcastEntityEvent(p_217714_, (byte)62);
      p_217714_.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
   }

   protected void tick(ServerLevel p_217724_, Warden p_217725_, long p_217726_) {
      p_217725_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((p_289393_) -> {
         p_217725_.getLookControl().setLookAt(p_289393_.position());
      });
      if (!p_217725_.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) && !p_217725_.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
         p_217725_.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long)(DURATION - TICKS_BEFORE_PLAYING_SOUND));
         p_217725_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(p_217725_::canTargetEntity).filter((p_217707_) -> {
            return p_217725_.closerThan(p_217707_, 15.0D, 20.0D);
         }).ifPresent((p_217704_) -> {
            Vec3 vec3 = p_217725_.position().add(0.0D, (double)1.6F, 0.0D);
            Vec3 vec31 = p_217704_.getEyePosition().subtract(vec3);
            Vec3 vec32 = vec31.normalize();

            for(int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
               Vec3 vec33 = vec3.add(vec32.scale((double)i));
               p_217724_.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            p_217725_.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
            p_217704_.hurt(p_217724_.damageSources().sonicBoom(p_217725_), 10.0F);
            double d1 = 0.5D * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5D * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            p_217704_.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
         });
      }
   }

   protected void stop(ServerLevel p_217732_, Warden p_217733_, long p_217734_) {
      setCooldown(p_217733_, 40);
   }

   public static void setCooldown(LivingEntity p_217699_, int p_217700_) {
      p_217699_.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, (long)p_217700_);
   }
}