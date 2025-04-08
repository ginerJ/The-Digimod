package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public interface MemoryCondition<F extends K1, Value> {
   MemoryModuleType<Value> memory();

   MemoryStatus condition();

   @Nullable
   MemoryAccessor<F, Value> createAccessor(Brain<?> p_259936_, Optional<Value> p_259724_);

   public static record Absent<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<Const.Mu<Unit>, Value> {
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_ABSENT;
      }

      public MemoryAccessor<Const.Mu<Unit>, Value> createAccessor(Brain<?> p_259727_, Optional<Value> p_260359_) {
         return p_260359_.isPresent() ? null : new MemoryAccessor<>(p_259727_, this.memory, Const.create(Unit.INSTANCE));
      }

      public MemoryModuleType<Value> memory() {
         return this.memory;
      }
   }

   public static record Present<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<IdF.Mu, Value> {
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_PRESENT;
      }

      public MemoryAccessor<IdF.Mu, Value> createAccessor(Brain<?> p_259253_, Optional<Value> p_260268_) {
         return p_260268_.isEmpty() ? null : new MemoryAccessor<>(p_259253_, this.memory, IdF.create(p_260268_.get()));
      }

      public MemoryModuleType<Value> memory() {
         return this.memory;
      }
   }

   public static record Registered<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<OptionalBox.Mu, Value> {
      public MemoryStatus condition() {
         return MemoryStatus.REGISTERED;
      }

      public MemoryAccessor<OptionalBox.Mu, Value> createAccessor(Brain<?> p_260149_, Optional<Value> p_259303_) {
         return new MemoryAccessor<>(p_260149_, this.memory, OptionalBox.create(p_259303_));
      }

      public MemoryModuleType<Value> memory() {
         return this.memory;
      }
   }
}