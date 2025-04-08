package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class AncientCityStructurePieces {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("ancient_city/city_center");

   public static void bootstrap(BootstapContext<StructureTemplatePool> p_255893_) {
      HolderGetter<StructureProcessorList> holdergetter = p_255893_.lookup(Registries.PROCESSOR_LIST);
      Holder<StructureProcessorList> holder = holdergetter.getOrThrow(ProcessorLists.ANCIENT_CITY_START_DEGRADATION);
      HolderGetter<StructureTemplatePool> holdergetter1 = p_255893_.lookup(Registries.TEMPLATE_POOL);
      Holder<StructureTemplatePool> holder1 = holdergetter1.getOrThrow(Pools.EMPTY);
      p_255893_.register(START, new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_1", holder), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_2", holder), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_3", holder), 1)), StructureTemplatePool.Projection.RIGID));
      AncientCityStructurePools.bootstrap(p_255893_);
   }
}