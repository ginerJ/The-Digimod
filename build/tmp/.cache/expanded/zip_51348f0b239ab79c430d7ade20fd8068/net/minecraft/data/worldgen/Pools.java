package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
   public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

   public static ResourceKey<StructureTemplatePool> createKey(String p_256439_) {
      return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_256439_));
   }

   public static void register(BootstapContext<StructureTemplatePool> p_255823_, String p_255837_, StructureTemplatePool p_256161_) {
      p_255823_.register(createKey(p_255837_), p_256161_);
   }

   public static void bootstrap(BootstapContext<StructureTemplatePool> p_255897_) {
      HolderGetter<StructureTemplatePool> holdergetter = p_255897_.lookup(Registries.TEMPLATE_POOL);
      Holder<StructureTemplatePool> holder = holdergetter.getOrThrow(EMPTY);
      p_255897_.register(EMPTY, new StructureTemplatePool(holder, ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
      BastionPieces.bootstrap(p_255897_);
      PillagerOutpostPools.bootstrap(p_255897_);
      VillagePools.bootstrap(p_255897_);
      AncientCityStructurePieces.bootstrap(p_255897_);
      TrailRuinsStructurePools.bootstrap(p_255897_);
   }
}