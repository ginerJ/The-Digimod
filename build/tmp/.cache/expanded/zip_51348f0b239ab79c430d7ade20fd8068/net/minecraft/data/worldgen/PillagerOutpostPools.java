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

public class PillagerOutpostPools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("pillager_outpost/base_plates");

   public static void bootstrap(BootstapContext<StructureTemplatePool> p_256545_) {
      HolderGetter<StructureProcessorList> holdergetter = p_256545_.lookup(Registries.PROCESSOR_LIST);
      Holder<StructureProcessorList> holder = holdergetter.getOrThrow(ProcessorLists.OUTPOST_ROT);
      HolderGetter<StructureTemplatePool> holdergetter1 = p_256545_.lookup(Registries.TEMPLATE_POOL);
      Holder<StructureTemplatePool> holder1 = holdergetter1.getOrThrow(Pools.EMPTY);
      p_256545_.register(START, new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/base_plate"), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(p_256545_, "pillager_outpost/towers", new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.list(ImmutableList.of(StructurePoolElement.legacy("pillager_outpost/watchtower"), StructurePoolElement.legacy("pillager_outpost/watchtower_overgrown", holder))), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(p_256545_, "pillager_outpost/feature_plates", new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_plate"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
      Pools.register(p_256545_, "pillager_outpost/features", new StructureTemplatePool(holder1, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage_with_allays"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_logs"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_targets"), 1), Pair.of(StructurePoolElement.empty(), 6)), StructureTemplatePool.Projection.RIGID));
   }
}