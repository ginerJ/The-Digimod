package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
   MobEffect getSuspiciousEffect();

   int getEffectDuration();

   static List<SuspiciousEffectHolder> getAllEffectHolders() {
      return BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @Nullable
   static SuspiciousEffectHolder tryGet(ItemLike p_259322_) {
      Item item1 = p_259322_.asItem();
      if (item1 instanceof BlockItem blockitem) {
         Block block = blockitem.getBlock();
         if (block instanceof SuspiciousEffectHolder suspiciouseffectholder1) {
            return suspiciouseffectholder1;
         }
      }

      Item $$2 = p_259322_.asItem();
      if ($$2 instanceof SuspiciousEffectHolder suspiciouseffectholder) {
         return suspiciouseffectholder;
      } else {
         return null;
      }
   }
}