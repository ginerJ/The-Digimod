package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class StructureCheck {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_STRUCTURE = -1;
   private final ChunkScanAccess storageAccess;
   private final RegistryAccess registryAccess;
   private final Registry<Biome> biomes;
   private final Registry<Structure> structureConfigs;
   private final StructureTemplateManager structureTemplateManager;
   private final ResourceKey<Level> dimension;
   private final ChunkGenerator chunkGenerator;
   private final RandomState randomState;
   private final LevelHeightAccessor heightAccessor;
   private final BiomeSource biomeSource;
   private final long seed;
   private final DataFixer fixerUpper;
   private final Long2ObjectMap<Object2IntMap<Structure>> loadedChunks = new Long2ObjectOpenHashMap<>();
   private final Map<Structure, Long2BooleanMap> featureChecks = new HashMap<>();

   public StructureCheck(ChunkScanAccess p_226712_, RegistryAccess p_226713_, StructureTemplateManager p_226714_, ResourceKey<Level> p_226715_, ChunkGenerator p_226716_, RandomState p_226717_, LevelHeightAccessor p_226718_, BiomeSource p_226719_, long p_226720_, DataFixer p_226721_) {
      this.storageAccess = p_226712_;
      this.registryAccess = p_226713_;
      this.structureTemplateManager = p_226714_;
      this.dimension = p_226715_;
      this.chunkGenerator = p_226716_;
      this.randomState = p_226717_;
      this.heightAccessor = p_226718_;
      this.biomeSource = p_226719_;
      this.seed = p_226720_;
      this.fixerUpper = p_226721_;
      this.biomes = p_226713_.registryOrThrow(Registries.BIOME);
      this.structureConfigs = p_226713_.registryOrThrow(Registries.STRUCTURE);
   }

   public StructureCheckResult checkStart(ChunkPos p_226730_, Structure p_226731_, boolean p_226732_) {
      long i = p_226730_.toLong();
      Object2IntMap<Structure> object2intmap = this.loadedChunks.get(i);
      if (object2intmap != null) {
         return this.checkStructureInfo(object2intmap, p_226731_, p_226732_);
      } else {
         StructureCheckResult structurecheckresult = this.tryLoadFromStorage(p_226730_, p_226731_, p_226732_, i);
         if (structurecheckresult != null) {
            return structurecheckresult;
         } else {
            boolean flag = this.featureChecks.computeIfAbsent(p_226731_, (p_226739_) -> {
               return new Long2BooleanOpenHashMap();
            }).computeIfAbsent(i, (p_226728_) -> {
               return this.canCreateStructure(p_226730_, p_226731_);
            });
            return !flag ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
         }
      }
   }

   private boolean canCreateStructure(ChunkPos p_226756_, Structure p_226757_) {
      return p_226757_.findValidGenerationPoint(new Structure.GenerationContext(this.registryAccess, this.chunkGenerator, this.biomeSource, this.randomState, this.structureTemplateManager, this.seed, p_226756_, this.heightAccessor, p_226757_.biomes()::contains)).isPresent();
   }

   @Nullable
   private StructureCheckResult tryLoadFromStorage(ChunkPos p_226734_, Structure p_226735_, boolean p_226736_, long p_226737_) {
      CollectFields collectfields = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector("Level", "Structures", CompoundTag.TYPE, "Starts"), new FieldSelector("structures", CompoundTag.TYPE, "starts"));

      try {
         this.storageAccess.scanChunk(p_226734_, collectfields).join();
      } catch (Exception exception1) {
         LOGGER.warn("Failed to read chunk {}", p_226734_, exception1);
         return StructureCheckResult.CHUNK_LOAD_NEEDED;
      }

      Tag tag = collectfields.getResult();
      if (!(tag instanceof CompoundTag compoundtag)) {
         return null;
      } else {
         int i = ChunkStorage.getVersion(compoundtag);
         if (i <= 1493) {
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
         } else {
            ChunkStorage.injectDatafixingContext(compoundtag, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());

            CompoundTag compoundtag1;
            try {
               compoundtag1 = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, compoundtag, i);
            } catch (Exception exception) {
               LOGGER.warn("Failed to partially datafix chunk {}", p_226734_, exception);
               return StructureCheckResult.CHUNK_LOAD_NEEDED;
            }

            Object2IntMap<Structure> object2intmap = this.loadStructures(compoundtag1);
            if (object2intmap == null) {
               return null;
            } else {
               this.storeFullResults(p_226737_, object2intmap);
               return this.checkStructureInfo(object2intmap, p_226735_, p_226736_);
            }
         }
      }
   }

   @Nullable
   private Object2IntMap<Structure> loadStructures(CompoundTag p_197312_) {
      if (!p_197312_.contains("structures", 10)) {
         return null;
      } else {
         CompoundTag compoundtag = p_197312_.getCompound("structures");
         if (!compoundtag.contains("starts", 10)) {
            return null;
         } else {
            CompoundTag compoundtag1 = compoundtag.getCompound("starts");
            if (compoundtag1.isEmpty()) {
               return Object2IntMaps.emptyMap();
            } else {
               Object2IntMap<Structure> object2intmap = new Object2IntOpenHashMap<>();
               Registry<Structure> registry = this.registryAccess.registryOrThrow(Registries.STRUCTURE);

               for(String s : compoundtag1.getAllKeys()) {
                  ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
                  if (resourcelocation != null) {
                     Structure structure = registry.get(resourcelocation);
                     if (structure != null) {
                        CompoundTag compoundtag2 = compoundtag1.getCompound(s);
                        if (!compoundtag2.isEmpty()) {
                           String s1 = compoundtag2.getString("id");
                           if (!"INVALID".equals(s1)) {
                              int i = compoundtag2.getInt("references");
                              object2intmap.put(structure, i);
                           }
                        }
                     }
                  }
               }

               return object2intmap;
            }
         }
      }
   }

   private static Object2IntMap<Structure> deduplicateEmptyMap(Object2IntMap<Structure> p_197299_) {
      return p_197299_.isEmpty() ? Object2IntMaps.emptyMap() : p_197299_;
   }

   private StructureCheckResult checkStructureInfo(Object2IntMap<Structure> p_226752_, Structure p_226753_, boolean p_226754_) {
      int i = p_226752_.getOrDefault(p_226753_, -1);
      return i == -1 || p_226754_ && i != 0 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.START_PRESENT;
   }

   public void onStructureLoad(ChunkPos p_197283_, Map<Structure, StructureStart> p_197284_) {
      long i = p_197283_.toLong();
      Object2IntMap<Structure> object2intmap = new Object2IntOpenHashMap<>();
      p_197284_.forEach((p_226749_, p_226750_) -> {
         if (p_226750_.isValid()) {
            object2intmap.put(p_226749_, p_226750_.getReferences());
         }

      });
      this.storeFullResults(i, object2intmap);
   }

   private void storeFullResults(long p_197264_, Object2IntMap<Structure> p_197265_) {
      this.loadedChunks.put(p_197264_, deduplicateEmptyMap(p_197265_));
      this.featureChecks.values().forEach((p_209956_) -> {
         p_209956_.remove(p_197264_);
      });
   }

   public void incrementReference(ChunkPos p_226723_, Structure p_226724_) {
      this.loadedChunks.compute(p_226723_.toLong(), (p_226745_, p_226746_) -> {
         if (p_226746_ == null || p_226746_.isEmpty()) {
            p_226746_ = new Object2IntOpenHashMap<>();
         }

         p_226746_.computeInt(p_226724_, (p_226741_, p_226742_) -> {
            return p_226742_ == null ? 1 : p_226742_ + 1;
         });
         return p_226746_;
      });
   }
}