package net.minecraft.data.worldgen;

import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class VillagePools {
   public static void bootstrap(BootstapContext<StructureTemplatePool> p_256339_) {
      PlainVillagePools.bootstrap(p_256339_);
      SnowyVillagePools.bootstrap(p_256339_);
      SavannaVillagePools.bootstrap(p_256339_);
      DesertVillagePools.bootstrap(p_256339_);
      TaigaVillagePools.bootstrap(p_256339_);
   }
}