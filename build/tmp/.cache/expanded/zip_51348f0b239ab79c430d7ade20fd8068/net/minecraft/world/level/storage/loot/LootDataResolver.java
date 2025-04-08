package net.minecraft.world.level.storage.loot;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface LootDataResolver {
   @Nullable
   <T> T getElement(LootDataId<T> p_279309_);

   @Nullable
   default <T> T getElement(LootDataType<T> p_279423_, ResourceLocation p_279277_) {
      return this.getElement(new LootDataId<>(p_279423_, p_279277_));
   }

   default <T> Optional<T> getElementOptional(LootDataId<T> p_279486_) {
      return Optional.ofNullable(this.getElement(p_279486_));
   }

   default <T> Optional<T> getElementOptional(LootDataType<T> p_279350_, ResourceLocation p_279323_) {
      return this.getElementOptional(new LootDataId<>(p_279350_, p_279323_));
   }

   default LootTable getLootTable(ResourceLocation p_279456_) {
      return this.getElementOptional(LootDataType.TABLE, p_279456_).orElse(LootTable.EMPTY);
   }
}