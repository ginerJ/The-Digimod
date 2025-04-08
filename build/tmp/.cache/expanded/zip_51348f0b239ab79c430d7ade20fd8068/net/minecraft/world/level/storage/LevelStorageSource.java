package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import net.minecraft.world.level.validation.PathAllowList;
import org.slf4j.Logger;

public class LevelStorageSource {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
   private static final String TAG_DATA = "Data";
   private static final PathAllowList NO_SYMLINKS_ALLOWED = new PathAllowList(List.of());
   public static final String ALLOWED_SYMLINKS_CONFIG_NAME = "allowed_symlinks.txt";
   private final Path baseDir;
   private final Path backupDir;
   final DataFixer fixerUpper;
   private final DirectoryValidator worldDirValidator;

   public LevelStorageSource(Path p_289985_, Path p_289978_, DirectoryValidator p_289922_, DataFixer p_289940_) {
      this.fixerUpper = p_289940_;

      try {
         FileUtil.createDirectoriesSafe(p_289985_);
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }

      this.baseDir = p_289985_;
      this.backupDir = p_289978_;
      this.worldDirValidator = p_289922_;
   }

   public static DirectoryValidator parseValidator(Path p_289968_) {
      if (Files.exists(p_289968_)) {
         try (BufferedReader bufferedreader = Files.newBufferedReader(p_289968_)) {
            return new DirectoryValidator(PathAllowList.readPlain(bufferedreader));
         } catch (Exception exception) {
            LOGGER.error("Failed to parse {}, disallowing all symbolic links", "allowed_symlinks.txt", exception);
         }
      }

      return new DirectoryValidator(NO_SYMLINKS_ALLOWED);
   }

   public static LevelStorageSource createDefault(Path p_78243_) {
      DirectoryValidator directoryvalidator = parseValidator(p_78243_.resolve("allowed_symlinks.txt"));
      return new LevelStorageSource(p_78243_, p_78243_.resolve("../backups"), directoryvalidator, DataFixers.getDataFixer());
   }

   private static <T> DataResult<WorldGenSettings> readWorldGenSettings(Dynamic<T> p_251661_, DataFixer p_251712_, int p_250368_) {
      Dynamic<T> dynamic = p_251661_.get("WorldGenSettings").orElseEmptyMap();

      for(String s : OLD_SETTINGS_KEYS) {
         Optional<Dynamic<T>> optional = p_251661_.get(s).result();
         if (optional.isPresent()) {
            dynamic = dynamic.set(s, optional.get());
         }
      }

      Dynamic<T> dynamic1 = DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion(p_251712_, dynamic, p_250368_);
      return WorldGenSettings.CODEC.parse(dynamic1);
   }

   private static WorldDataConfiguration readDataConfig(Dynamic<?> p_250884_) {
      return WorldDataConfiguration.CODEC.parse(p_250884_).resultOrPartial(LOGGER::error).orElse(WorldDataConfiguration.DEFAULT);
   }

   public String getName() {
      return "Anvil";
   }

   public LevelStorageSource.LevelCandidates findLevelCandidates() throws LevelStorageException {
      if (!Files.isDirectory(this.baseDir)) {
         throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
      } else {
         try (Stream<Path> stream = Files.list(this.baseDir)) {
            List<LevelStorageSource.LevelDirectory> list = stream.filter((p_230839_) -> {
               return Files.isDirectory(p_230839_);
            }).map(LevelStorageSource.LevelDirectory::new).filter((p_230835_) -> {
               return Files.isRegularFile(p_230835_.dataFile()) || Files.isRegularFile(p_230835_.oldDataFile());
            }).toList();
            return new LevelStorageSource.LevelCandidates(list);
         } catch (IOException ioexception) {
            throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
         }
      }
   }

   public CompletableFuture<List<LevelSummary>> loadLevelSummaries(LevelStorageSource.LevelCandidates p_230814_) {
      List<CompletableFuture<LevelSummary>> list = new ArrayList<>(p_230814_.levels.size());

      for(LevelStorageSource.LevelDirectory levelstoragesource$leveldirectory : p_230814_.levels) {
         list.add(CompletableFuture.supplyAsync(() -> {
            boolean flag;
            try {
               flag = DirectoryLock.isLocked(levelstoragesource$leveldirectory.path());
            } catch (Exception exception) {
               LOGGER.warn("Failed to read {} lock", levelstoragesource$leveldirectory.path(), exception);
               return null;
            }

            try {
               LevelSummary levelsummary = this.readLevelData(levelstoragesource$leveldirectory, this.levelSummaryReader(levelstoragesource$leveldirectory, flag));
               return levelsummary != null ? levelsummary : null;
            } catch (OutOfMemoryError outofmemoryerror) {
               MemoryReserve.release();
               System.gc();
               LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", (Object)levelstoragesource$leveldirectory.directoryName());
               throw outofmemoryerror;
            } catch (StackOverflowError stackoverflowerror) {
               LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.", (Object)levelstoragesource$leveldirectory.directoryName());
               Util.safeReplaceOrMoveFile(levelstoragesource$leveldirectory.dataFile(), levelstoragesource$leveldirectory.oldDataFile(), levelstoragesource$leveldirectory.corruptedDataFile(LocalDateTime.now()), true);
               throw stackoverflowerror;
            }
         }, Util.backgroundExecutor()));
      }

      return Util.sequenceFailFastAndCancel(list).thenApply((p_230832_) -> {
         return p_230832_.stream().filter(Objects::nonNull).sorted().toList();
      });
   }

   private int getStorageVersion() {
      return 19133;
   }

   @Nullable
   <T> T readLevelData(LevelStorageSource.LevelDirectory p_230818_, BiFunction<Path, DataFixer, T> p_230819_) {
      if (!Files.exists(p_230818_.path())) {
         return (T)null;
      } else {
         Path path = p_230818_.dataFile();
         if (Files.exists(path)) {
            T t = p_230819_.apply(path, this.fixerUpper);
            if (t != null) {
               return t;
            }
         }

         path = p_230818_.oldDataFile();
         return (T)(Files.exists(path) ? p_230819_.apply(path, this.fixerUpper) : null);
      }
   }

   @Nullable
   private static WorldDataConfiguration getDataConfiguration(Path p_230829_, DataFixer p_230830_) {
      try {
         Tag tag = readLightweightData(p_230829_);
         if (tag instanceof CompoundTag compoundtag) {
            CompoundTag compoundtag1 = compoundtag.getCompound("Data");
            int i = NbtUtils.getDataVersion(compoundtag1, -1);
            Dynamic<?> dynamic = DataFixTypes.LEVEL.updateToCurrentVersion(p_230830_, new Dynamic<>(NbtOps.INSTANCE, compoundtag1), i);
            return readDataConfig(dynamic);
         }
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_230829_, exception);
      }

      return null;
   }

   static BiFunction<Path, DataFixer, Pair<WorldData, WorldDimensions.Complete>> getLevelData(DynamicOps<Tag> p_250592_, WorldDataConfiguration p_249054_, Registry<LevelStem> p_249363_, Lifecycle p_251214_) {
      return (p_265020_, p_265021_) -> {
         CompoundTag compoundtag;
         try {
            compoundtag = NbtIo.readCompressed(p_265020_.toFile());
         } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
         }

         CompoundTag compoundtag1 = compoundtag.getCompound("Data");
         CompoundTag compoundtag2 = compoundtag1.contains("Player", 10) ? compoundtag1.getCompound("Player") : null;
         compoundtag1.remove("Player");
         int i = NbtUtils.getDataVersion(compoundtag1, -1);
         Dynamic<?> dynamic = DataFixTypes.LEVEL.updateToCurrentVersion(p_265021_, new Dynamic<>(p_250592_, compoundtag1), i);
         WorldGenSettings worldgensettings = readWorldGenSettings(dynamic, p_265021_, i).getOrThrow(false, Util.prefix("WorldGenSettings: ", LOGGER::error));
         LevelVersion levelversion = LevelVersion.parse(dynamic);
         LevelSettings levelsettings = LevelSettings.parse(dynamic, p_249054_);
         WorldDimensions.Complete worlddimensions$complete = worldgensettings.dimensions().bake(p_249363_);
         Lifecycle lifecycle = worlddimensions$complete.lifecycle().add(p_251214_);
         PrimaryLevelData primaryleveldata = PrimaryLevelData.parse(dynamic, p_265021_, i, compoundtag2, levelsettings, levelversion, worlddimensions$complete.specialWorldProperty(), worldgensettings.options(), lifecycle);
         return Pair.of(primaryleveldata, worlddimensions$complete);
      };
   }

   BiFunction<Path, DataFixer, LevelSummary> levelSummaryReader(LevelStorageSource.LevelDirectory p_230821_, boolean p_230822_) {
      return (p_289916_, p_289917_) -> {
         try {
            if (Files.isSymbolicLink(p_289916_)) {
               List<ForbiddenSymlinkInfo> list = new ArrayList<>();
               this.worldDirValidator.validateSymlink(p_289916_, list);
               if (!list.isEmpty()) {
                  LOGGER.warn(ContentValidationException.getMessage(p_289916_, list));
                  return new LevelSummary.SymlinkLevelSummary(p_230821_.directoryName(), p_230821_.iconFile());
               }
            }

            Tag tag = readLightweightData(p_289916_);
            if (tag instanceof CompoundTag compoundtag) {
               CompoundTag compoundtag1 = compoundtag.getCompound("Data");
               int i = NbtUtils.getDataVersion(compoundtag1, -1);
               Dynamic<?> dynamic = DataFixTypes.LEVEL.updateToCurrentVersion(p_289917_, new Dynamic<>(NbtOps.INSTANCE, compoundtag1), i);
               LevelVersion levelversion = LevelVersion.parse(dynamic);
               int j = levelversion.levelDataVersion();
               if (j == 19132 || j == 19133) {
                  boolean flag = j != this.getStorageVersion();
                  Path path = p_230821_.iconFile();
                  WorldDataConfiguration worlddataconfiguration = readDataConfig(dynamic);
                  LevelSettings levelsettings = LevelSettings.parse(dynamic, worlddataconfiguration);
                  FeatureFlagSet featureflagset = parseFeatureFlagsFromSummary(dynamic);
                  boolean flag1 = FeatureFlags.isExperimental(featureflagset);
                  return new LevelSummary(levelsettings, levelversion, p_230821_.directoryName(), flag, p_230822_, flag1, path);
               }
            } else {
               LOGGER.warn("Invalid root tag in {}", (Object)p_289916_);
            }

            return null;
         } catch (Exception exception) {
            LOGGER.error("Exception reading {}", p_289916_, exception);
            return null;
         }
      };
   }

   private static FeatureFlagSet parseFeatureFlagsFromSummary(Dynamic<?> p_249466_) {
      Set<ResourceLocation> set = p_249466_.get("enabled_features").asStream().flatMap((p_248492_) -> {
         return p_248492_.asString().result().map(ResourceLocation::tryParse).stream();
      }).collect(Collectors.toSet());
      return FeatureFlags.REGISTRY.fromNames(set, (p_248503_) -> {
      });
   }

   @Nullable
   private static Tag readLightweightData(Path p_230837_) throws IOException {
      SkipFields skipfields = new SkipFields(new FieldSelector("Data", CompoundTag.TYPE, "Player"), new FieldSelector("Data", CompoundTag.TYPE, "WorldGenSettings"));
      NbtIo.parseCompressed(p_230837_.toFile(), skipfields);
      return skipfields.getResult();
   }

   public boolean isNewLevelIdAcceptable(String p_78241_) {
      try {
         Path path = this.getLevelPath(p_78241_);
         Files.createDirectory(path);
         Files.deleteIfExists(path);
         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   public boolean levelExists(String p_78256_) {
      return Files.isDirectory(this.getLevelPath(p_78256_));
   }

   private Path getLevelPath(String p_289974_) {
      return this.baseDir.resolve(p_289974_);
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String p_289980_) throws IOException, ContentValidationException {
      Path path = this.getLevelPath(p_289980_);
      List<ForbiddenSymlinkInfo> list = this.worldDirValidator.validateSave(path, true);
      if (!list.isEmpty()) {
         throw new ContentValidationException(path, list);
      } else {
         return new LevelStorageSource.LevelStorageAccess(p_289980_, path);
      }
   }

   public LevelStorageSource.LevelStorageAccess createAccess(String p_78261_) throws IOException {
      Path path = this.getLevelPath(p_78261_);
      return new LevelStorageSource.LevelStorageAccess(p_78261_, path);
   }

   public DirectoryValidator getWorldDirValidator() {
      return this.worldDirValidator;
   }

   public static record LevelCandidates(List<LevelStorageSource.LevelDirectory> levels) implements Iterable<LevelStorageSource.LevelDirectory> {
      public boolean isEmpty() {
         return this.levels.isEmpty();
      }

      public Iterator<LevelStorageSource.LevelDirectory> iterator() {
         return this.levels.iterator();
      }
   }

   public static record LevelDirectory(Path path) {
      public String directoryName() {
         return this.path.getFileName().toString();
      }

      public Path dataFile() {
         return this.resourcePath(LevelResource.LEVEL_DATA_FILE);
      }

      public Path oldDataFile() {
         return this.resourcePath(LevelResource.OLD_LEVEL_DATA_FILE);
      }

      public Path corruptedDataFile(LocalDateTime p_230857_) {
         return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_corrupted_" + p_230857_.format(LevelStorageSource.FORMATTER));
      }

      public Path iconFile() {
         return this.resourcePath(LevelResource.ICON_FILE);
      }

      public Path lockFile() {
         return this.resourcePath(LevelResource.LOCK_FILE);
      }

      public Path resourcePath(LevelResource p_230855_) {
         return this.path.resolve(p_230855_.getId());
      }
   }

   public class LevelStorageAccess implements AutoCloseable {
      final DirectoryLock lock;
      final LevelStorageSource.LevelDirectory levelDirectory;
      private final String levelId;
      private final Map<LevelResource, Path> resources = Maps.newHashMap();

      LevelStorageAccess(String p_289967_, Path p_289988_) throws IOException {
         this.levelId = p_289967_;
         this.levelDirectory = new LevelStorageSource.LevelDirectory(p_289988_);
         this.lock = DirectoryLock.create(p_289988_);
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(LevelResource p_78284_) {
         return this.resources.computeIfAbsent(p_78284_, this.levelDirectory::resourcePath);
      }

      public Path getDimensionPath(ResourceKey<Level> p_197395_) {
         return DimensionType.getStorageFolder(p_197395_, this.levelDirectory.path());
      }

      private void checkLock() {
         if (!this.lock.isValid()) {
            throw new IllegalStateException("Lock is no longer valid");
         }
      }

      public PlayerDataStorage createPlayerStorage() {
         this.checkLock();
         return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
      }

      @Nullable
      public LevelSummary getSummary() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.this.levelSummaryReader(this.levelDirectory, false));
      }

      @Nullable
      public Pair<WorldData, WorldDimensions.Complete> getDataTag(DynamicOps<Tag> p_248747_, WorldDataConfiguration p_251873_, Registry<LevelStem> p_249187_, Lifecycle p_249736_) {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource.getLevelData(p_248747_, p_251873_, p_249187_, p_249736_));
      }

      public void readAdditionalLevelSaveData() {
         checkLock();
         LevelStorageSource.this.readLevelData(this.levelDirectory, (path, dataFixer) -> {
            try {
               CompoundTag compoundTag = NbtIo.readCompressed(path.toFile());
               net.minecraftforge.common.ForgeHooks.readAdditionalLevelSaveData(compoundTag, this.levelDirectory);
            } catch (Exception e) {
                LOGGER.error("Exception reading {}", path, e);
            }
            return ""; // Return non-null to prevent level.dat-old inject
         });
      }

      @Nullable
      public WorldDataConfiguration getDataConfiguration() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelDirectory, LevelStorageSource::getDataConfiguration);
      }

      public void saveDataTag(RegistryAccess p_78288_, WorldData p_78289_) {
         this.saveDataTag(p_78288_, p_78289_, (CompoundTag)null);
      }

      public void saveDataTag(RegistryAccess p_78291_, WorldData p_78292_, @Nullable CompoundTag p_78293_) {
         File file1 = this.levelDirectory.path().toFile();
         CompoundTag compoundtag = p_78292_.createTag(p_78291_, p_78293_);
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("Data", compoundtag);

         net.minecraftforge.common.ForgeHooks.writeAdditionalLevelSaveData(p_78292_, compoundtag1);

         try {
            File file2 = File.createTempFile("level", ".dat", file1);
            NbtIo.writeCompressed(compoundtag1, file2);
            File file3 = this.levelDirectory.oldDataFile().toFile();
            File file4 = this.levelDirectory.dataFile().toFile();
            Util.safeReplaceFile(file4, file2, file3);
         } catch (Exception exception) {
            LevelStorageSource.LOGGER.error("Failed to save level {}", file1, exception);
         }

      }

      public Optional<Path> getIconFile() {
         return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelDirectory.iconFile());
      }

      public Path getWorldDir() {
         return baseDir;
      }

      public void deleteLevel() throws IOException {
         this.checkLock();
         final Path path = this.levelDirectory.lockFile();
         LevelStorageSource.LOGGER.info("Deleting level {}", (Object)this.levelId);

         for(int i = 1; i <= 5; ++i) {
            LevelStorageSource.LOGGER.info("Attempt {}...", (int)i);

            try {
               Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
                  public FileVisitResult visitFile(Path p_78323_, BasicFileAttributes p_78324_) throws IOException {
                     if (!p_78323_.equals(path)) {
                        LevelStorageSource.LOGGER.debug("Deleting {}", (Object)p_78323_);
                        Files.delete(p_78323_);
                     }

                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path p_78320_, @Nullable IOException p_78321_) throws IOException {
                     if (p_78321_ != null) {
                        throw p_78321_;
                     } else {
                        if (p_78320_.equals(LevelStorageAccess.this.levelDirectory.path())) {
                           LevelStorageAccess.this.lock.close();
                           Files.deleteIfExists(path);
                        }

                        Files.delete(p_78320_);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
               break;
            } catch (IOException ioexception) {
               if (i >= 5) {
                  throw ioexception;
               }

               LevelStorageSource.LOGGER.warn("Failed to delete {}", this.levelDirectory.path(), ioexception);

               try {
                  Thread.sleep(500L);
               } catch (InterruptedException interruptedexception) {
               }
            }
         }

      }

      public void renameLevel(String p_78298_) throws IOException {
         this.checkLock();
         Path path = this.levelDirectory.dataFile();
         if (Files.exists(path)) {
            CompoundTag compoundtag = NbtIo.readCompressed(path.toFile());
            CompoundTag compoundtag1 = compoundtag.getCompound("Data");
            compoundtag1.putString("LevelName", p_78298_);
            NbtIo.writeCompressed(compoundtag, path.toFile());
         }

      }

      public long makeWorldBackup() throws IOException {
         this.checkLock();
         String s = LocalDateTime.now().format(LevelStorageSource.FORMATTER) + "_" + this.levelId;
         Path path = LevelStorageSource.this.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Path path1 = path.resolve(FileUtil.findAvailableName(path, s, ".zip"));

         try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path1)))) {
            final Path path2 = Paths.get(this.levelId);
            Files.walkFileTree(this.levelDirectory.path(), new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(Path p_78339_, BasicFileAttributes p_78340_) throws IOException {
                  if (p_78339_.endsWith("session.lock")) {
                     return FileVisitResult.CONTINUE;
                  } else {
                     String s1 = path2.resolve(LevelStorageAccess.this.levelDirectory.path().relativize(p_78339_)).toString().replace('\\', '/');
                     ZipEntry zipentry = new ZipEntry(s1);
                     zipoutputstream.putNextEntry(zipentry);
                     com.google.common.io.Files.asByteSource(p_78339_.toFile()).copyTo(zipoutputstream);
                     zipoutputstream.closeEntry();
                     return FileVisitResult.CONTINUE;
                  }
               }
            });
         }

         return Files.size(path1);
      }

      public void close() throws IOException {
         this.lock.close();
      }
   }
}
