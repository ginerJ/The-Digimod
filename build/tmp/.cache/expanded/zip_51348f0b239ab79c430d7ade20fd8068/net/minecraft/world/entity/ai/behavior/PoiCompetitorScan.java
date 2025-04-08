package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class PoiCompetitorScan {
   public static BehaviorControl<Villager> create() {
      return BehaviorBuilder.create((p_258576_) -> {
         return p_258576_.group(p_258576_.present(MemoryModuleType.JOB_SITE), p_258576_.present(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(p_258576_, (p_258590_, p_258591_) -> {
            return (p_258580_, p_258581_, p_258582_) -> {
               GlobalPos globalpos = p_258576_.get(p_258590_);
               p_258580_.getPoiManager().getType(globalpos.pos()).ifPresent((p_258588_) -> {
                  p_258576_.<List<LivingEntity>>get(p_258591_).stream().filter((p_258593_) -> {
                     return p_258593_ instanceof Villager && p_258593_ != p_258581_;
                  }).map((p_258583_) -> {
                     return (Villager)p_258583_;
                  }).filter(LivingEntity::isAlive).filter((p_258596_) -> {
                     return competesForSameJobsite(globalpos, p_258588_, p_258596_);
                  }).reduce(p_258581_, PoiCompetitorScan::selectWinner);
               });
               return true;
            };
         });
      });
   }

   private static Villager selectWinner(Villager p_23725_, Villager p_23726_) {
      Villager villager;
      Villager villager1;
      if (p_23725_.getVillagerXp() > p_23726_.getVillagerXp()) {
         villager = p_23725_;
         villager1 = p_23726_;
      } else {
         villager = p_23726_;
         villager1 = p_23725_;
      }

      villager1.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
      return villager;
   }

   private static boolean competesForSameJobsite(GlobalPos p_217330_, Holder<PoiType> p_217331_, Villager p_217332_) {
      Optional<GlobalPos> optional = p_217332_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      return optional.isPresent() && p_217330_.equals(optional.get()) && hasMatchingProfession(p_217331_, p_217332_.getVillagerData().getProfession());
   }

   private static boolean hasMatchingProfession(Holder<PoiType> p_217334_, VillagerProfession p_217335_) {
      return p_217335_.heldJobSite().test(p_217334_);
   }
}