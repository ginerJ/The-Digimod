package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MobCategory implements StringRepresentable, net.minecraftforge.common.IExtensibleEnum {
   MONSTER("monster", 70, false, false, 128),
   CREATURE("creature", 10, true, true, 128),
   AMBIENT("ambient", 15, true, false, 128),
   AXOLOTLS("axolotls", 5, true, false, 128),
   UNDERGROUND_WATER_CREATURE("underground_water_creature", 5, true, false, 128),
   WATER_CREATURE("water_creature", 5, true, false, 128),
   WATER_AMBIENT("water_ambient", 20, true, false, 64),
   MISC("misc", -1, true, true, 128);

   public static final Codec<MobCategory> CODEC = net.minecraftforge.common.IExtensibleEnum.createCodecForExtensibleEnum(MobCategory::values, MobCategory::byName);
   private static final java.util.Map<String, MobCategory> BY_NAME = java.util.Arrays.stream(values()).collect(java.util.stream.Collectors.toMap(MobCategory::getName, mobCategory -> mobCategory));
   private final int max;
   private final boolean isFriendly;
   private final boolean isPersistent;
   private final String name;
   private final int noDespawnDistance = 32;
   private final int despawnDistance;

   private MobCategory(String p_21597_, int p_21598_, boolean p_21599_, boolean p_21600_, int p_21601_) {
      this.name = p_21597_;
      this.max = p_21598_;
      this.isFriendly = p_21599_;
      this.isPersistent = p_21600_;
      this.despawnDistance = p_21601_;
   }

   public String getName() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public int getMaxInstancesPerChunk() {
      return this.max;
   }

   public boolean isFriendly() {
      return this.isFriendly;
   }

   public boolean isPersistent() {
      return this.isPersistent;
   }

   public static MobCategory create(String name, String id, int maxNumberOfCreatureIn, boolean isPeacefulCreatureIn, boolean isAnimalIn, int despawnDistance) {
      throw new IllegalStateException("Enum not extended");
   }

   @Override
   @Deprecated
   public void init() {
      BY_NAME.put(this.getName(), this);
   }

   // Forge: Access enum members by name
   public static MobCategory byName(String name) {
      return BY_NAME.get(name);
   }

   public int getDespawnDistance() {
      return this.despawnDistance;
   }

   public int getNoDespawnDistance() {
      return 32;
   }
}
