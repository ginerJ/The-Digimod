package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class AppendLoot implements RuleBlockEntityModifier {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<AppendLoot> CODEC = RecordCodecBuilder.create((p_277957_) -> {
      return p_277957_.group(ResourceLocation.CODEC.fieldOf("loot_table").forGetter((p_277581_) -> {
         return p_277581_.lootTable;
      })).apply(p_277957_, AppendLoot::new);
   });
   private final ResourceLocation lootTable;

   public AppendLoot(ResourceLocation p_277694_) {
      this.lootTable = p_277694_;
   }

   public CompoundTag apply(RandomSource p_277994_, @Nullable CompoundTag p_277854_) {
      CompoundTag compoundtag = p_277854_ == null ? new CompoundTag() : p_277854_.copy();
      ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.lootTable).resultOrPartial(LOGGER::error).ifPresent((p_277353_) -> {
         compoundtag.put("LootTable", p_277353_);
      });
      compoundtag.putLong("LootTableSeed", p_277994_.nextLong());
      return compoundtag;
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_LOOT;
   }
}