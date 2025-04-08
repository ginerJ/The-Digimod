package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String STRUCTURE_DIRECTORY_NAME = "structures";
   private static final String TEST_STRUCTURES_DIR = "gameteststructures";
   private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
   private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
   private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
   private final DataFixer fixerUpper;
   private ResourceManager resourceManager;
   private final Path generatedDir;
   private final List<StructureTemplateManager.Source> sources;
   private final HolderGetter<Block> blockLookup;
   private static final FileToIdConverter LISTER = new FileToIdConverter("structures", ".nbt");

   public StructureTemplateManager(ResourceManager p_249872_, LevelStorageSource.LevelStorageAccess p_249864_, DataFixer p_249868_, HolderGetter<Block> p_256126_) {
      this.resourceManager = p_249872_;
      this.fixerUpper = p_249868_;
      this.generatedDir = p_249864_.getLevelPath(LevelResource.GENERATED_DIR).normalize();
      this.blockLookup = p_256126_;
      ImmutableList.Builder<StructureTemplateManager.Source> builder = ImmutableList.builder();
      builder.add(new StructureTemplateManager.Source(this::loadFromGenerated, this::listGenerated));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         builder.add(new StructureTemplateManager.Source(this::loadFromTestStructures, this::listTestStructures));
      }

      builder.add(new StructureTemplateManager.Source(this::loadFromResource, this::listResources));
      this.sources = builder.build();
   }

   public StructureTemplate getOrCreate(ResourceLocation p_230360_) {
      Optional<StructureTemplate> optional = this.get(p_230360_);
      if (optional.isPresent()) {
         return optional.get();
      } else {
         StructureTemplate structuretemplate = new StructureTemplate();
         this.structureRepository.put(p_230360_, Optional.of(structuretemplate));
         return structuretemplate;
      }
   }

   public Optional<StructureTemplate> get(ResourceLocation p_230408_) {
      return this.structureRepository.computeIfAbsent(p_230408_, this::tryLoad);
   }

   public Stream<ResourceLocation> listTemplates() {
      return this.sources.stream().flatMap((p_230376_) -> {
         return p_230376_.lister().get();
      }).distinct();
   }

   private Optional<StructureTemplate> tryLoad(ResourceLocation p_230426_) {
      for(StructureTemplateManager.Source structuretemplatemanager$source : this.sources) {
         try {
            Optional<StructureTemplate> optional = structuretemplatemanager$source.loader().apply(p_230426_);
            if (optional.isPresent()) {
               return optional;
            }
         } catch (Exception exception) {
         }
      }

      return Optional.empty();
   }

   public void onResourceManagerReload(ResourceManager p_230371_) {
      this.resourceManager = p_230371_;
      this.structureRepository.clear();
   }

   private Optional<StructureTemplate> loadFromResource(ResourceLocation p_230428_) {
      ResourceLocation resourcelocation = LISTER.idToFile(p_230428_);
      return this.load(() -> {
         return this.resourceManager.open(resourcelocation);
      }, (p_230366_) -> {
         LOGGER.error("Couldn't load structure {}", p_230428_, p_230366_);
      });
   }

   private Stream<ResourceLocation> listResources() {
      return LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(LISTER::fileToId);
   }

   private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation p_230430_) {
      return this.loadFromSnbt(p_230430_, Paths.get("gameteststructures"));
   }

   private Stream<ResourceLocation> listTestStructures() {
      return this.listFolderContents(Paths.get("gameteststructures"), "minecraft", ".snbt");
   }

   private Optional<StructureTemplate> loadFromGenerated(ResourceLocation p_230432_) {
      if (!Files.isDirectory(this.generatedDir)) {
         return Optional.empty();
      } else {
         Path path = createAndValidatePathToStructure(this.generatedDir, p_230432_, ".nbt");
         return this.load(() -> {
            return new FileInputStream(path.toFile());
         }, (p_230400_) -> {
            LOGGER.error("Couldn't load structure from {}", path, p_230400_);
         });
      }
   }

   private Stream<ResourceLocation> listGenerated() {
      if (!Files.isDirectory(this.generatedDir)) {
         return Stream.empty();
      } else {
         try {
            return Files.list(this.generatedDir).filter((p_230419_) -> {
               return Files.isDirectory(p_230419_);
            }).flatMap((p_230410_) -> {
               return this.listGeneratedInNamespace(p_230410_);
            });
         } catch (IOException ioexception) {
            return Stream.empty();
         }
      }
   }

   private Stream<ResourceLocation> listGeneratedInNamespace(Path p_230389_) {
      Path path = p_230389_.resolve("structures");
      return this.listFolderContents(path, p_230389_.getFileName().toString(), ".nbt");
   }

   private Stream<ResourceLocation> listFolderContents(Path p_230395_, String p_230396_, String p_230397_) {
      if (!Files.isDirectory(p_230395_)) {
         return Stream.empty();
      } else {
         int i = p_230397_.length();
         Function<String, String> function = (p_230358_) -> {
            return p_230358_.substring(0, p_230358_.length() - i);
         };

         try {
            return Files.walk(p_230395_).filter((p_230381_) -> {
               return p_230381_.toString().endsWith(p_230397_);
            }).mapMulti((p_230386_, p_230387_) -> {
               try {
                  p_230387_.accept(new ResourceLocation(p_230396_, function.apply(this.relativize(p_230395_, p_230386_))));
               } catch (ResourceLocationException resourcelocationexception) {
                  LOGGER.error("Invalid location while listing pack contents", (Throwable)resourcelocationexception);
               }

            });
         } catch (IOException ioexception) {
            LOGGER.error("Failed to list folder contents", (Throwable)ioexception);
            return Stream.empty();
         }
      }
   }

   private String relativize(Path p_230402_, Path p_230403_) {
      return p_230402_.relativize(p_230403_).toString().replace(File.separator, "/");
   }

   private Optional<StructureTemplate> loadFromSnbt(ResourceLocation p_230368_, Path p_230369_) {
      if (!Files.isDirectory(p_230369_)) {
         return Optional.empty();
      } else {
         Path path = FileUtil.createPathToResource(p_230369_, p_230368_.getPath(), ".snbt");

         try (BufferedReader bufferedreader = Files.newBufferedReader(path)) {
            String s = IOUtils.toString((Reader)bufferedreader);
            return Optional.of(this.readStructure(NbtUtils.snbtToStructure(s)));
         } catch (NoSuchFileException nosuchfileexception) {
            return Optional.empty();
         } catch (CommandSyntaxException | IOException ioexception) {
            LOGGER.error("Couldn't load structure from {}", path, ioexception);
            return Optional.empty();
         }
      }
   }

   private Optional<StructureTemplate> load(StructureTemplateManager.InputStreamOpener p_230373_, Consumer<Throwable> p_230374_) {
      try (InputStream inputstream = p_230373_.open()) {
         return Optional.of(this.readStructure(inputstream));
      } catch (FileNotFoundException filenotfoundexception) {
         return Optional.empty();
      } catch (Throwable throwable) {
         p_230374_.accept(throwable);
         return Optional.empty();
      }
   }

   private StructureTemplate readStructure(InputStream p_230378_) throws IOException {
      CompoundTag compoundtag = NbtIo.readCompressed(p_230378_);
      return this.readStructure(compoundtag);
   }

   public StructureTemplate readStructure(CompoundTag p_230405_) {
      StructureTemplate structuretemplate = new StructureTemplate();
      int i = NbtUtils.getDataVersion(p_230405_, 500);
      structuretemplate.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, p_230405_, i));
      return structuretemplate;
   }

   public boolean save(ResourceLocation p_230417_) {
      Optional<StructureTemplate> optional = this.structureRepository.get(p_230417_);
      if (!optional.isPresent()) {
         return false;
      } else {
         StructureTemplate structuretemplate = optional.get();
         Path path = createAndValidatePathToStructure(this.generatedDir, p_230417_, ".nbt");
         Path path1 = path.getParent();
         if (path1 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
            } catch (IOException ioexception) {
               LOGGER.error("Failed to create parent directory: {}", (Object)path1);
               return false;
            }

            CompoundTag compoundtag = structuretemplate.save(new CompoundTag());

            try {
               try (OutputStream outputstream = new FileOutputStream(path.toFile())) {
                  NbtIo.writeCompressed(compoundtag, outputstream);
               }

               return true;
            } catch (Throwable throwable1) {
               return false;
            }
         }
      }
   }

   public Path getPathToGeneratedStructure(ResourceLocation p_230362_, String p_230363_) {
      return createPathToStructure(this.generatedDir, p_230362_, p_230363_);
   }

   public static Path createPathToStructure(Path p_230391_, ResourceLocation p_230392_, String p_230393_) {
      try {
         Path path = p_230391_.resolve(p_230392_.getNamespace());
         Path path1 = path.resolve("structures");
         return FileUtil.createPathToResource(path1, p_230392_.getPath(), p_230393_);
      } catch (InvalidPathException invalidpathexception) {
         throw new ResourceLocationException("Invalid resource path: " + p_230392_, invalidpathexception);
      }
   }

   private static Path createAndValidatePathToStructure(Path p_230412_, ResourceLocation p_230413_, String p_230414_) {
      if (p_230413_.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + p_230413_);
      } else {
         Path path = createPathToStructure(p_230412_, p_230413_, p_230414_);
         if (path.startsWith(p_230412_) && FileUtil.isPathNormalized(path) && FileUtil.isPathPortable(path)) {
            return path;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + path);
         }
      }
   }

   public void remove(ResourceLocation p_230422_) {
      this.structureRepository.remove(p_230422_);
   }

   @FunctionalInterface
   interface InputStreamOpener {
      InputStream open() throws IOException;
   }

   static record Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
   }
}