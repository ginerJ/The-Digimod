package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.fixes.References;

public enum DataFixTypes {
   LEVEL(References.LEVEL),
   PLAYER(References.PLAYER),
   CHUNK(References.CHUNK),
   HOTBAR(References.HOTBAR),
   OPTIONS(References.OPTIONS),
   STRUCTURE(References.STRUCTURE),
   STATS(References.STATS),
   SAVED_DATA(References.SAVED_DATA),
   ADVANCEMENTS(References.ADVANCEMENTS),
   POI_CHUNK(References.POI_CHUNK),
   WORLD_GEN_SETTINGS(References.WORLD_GEN_SETTINGS),
   ENTITY_CHUNK(References.ENTITY_CHUNK);

   public static final Set<DSL.TypeReference> TYPES_FOR_LEVEL_LIST;
   private final DSL.TypeReference type;

   private DataFixTypes(DSL.TypeReference p_14503_) {
      this.type = p_14503_;
   }

   private static int currentVersion() {
      return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
   }

   public <T> Dynamic<T> update(DataFixer p_265388_, Dynamic<T> p_265179_, int p_265372_, int p_265168_) {
      return p_265388_.update(this.type, p_265179_, p_265372_, p_265168_);
   }

   public <T> Dynamic<T> updateToCurrentVersion(DataFixer p_265085_, Dynamic<T> p_265237_, int p_265099_) {
      return this.update(p_265085_, p_265237_, p_265099_, currentVersion());
   }

   public CompoundTag update(DataFixer p_265128_, CompoundTag p_265422_, int p_265549_, int p_265304_) {
      return (CompoundTag)this.update(p_265128_, new Dynamic<>(NbtOps.INSTANCE, p_265422_), p_265549_, p_265304_).getValue();
   }

   public CompoundTag updateToCurrentVersion(DataFixer p_265583_, CompoundTag p_265401_, int p_265111_) {
      return this.update(p_265583_, p_265401_, p_265111_, currentVersion());
   }

   static {
      TYPES_FOR_LEVEL_LIST = Set.of(LEVEL.type);
   }
}