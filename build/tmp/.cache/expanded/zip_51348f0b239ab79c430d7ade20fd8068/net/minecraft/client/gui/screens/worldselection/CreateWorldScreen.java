package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private static final int GROUP_BOTTOM = 1;
   private static final int TAB_COLUMN_WIDTH = 210;
   private static final int FOOTER_HEIGHT = 36;
   private static final int TEXT_INDENT = 1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TEMP_WORLD_PREFIX = "mcworld-";
   static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
   static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
   static final Component EXPERIMENTS_LABEL = Component.translatable("selectWorld.experiments");
   static final Component ALLOW_CHEATS_INFO = Component.translatable("selectWorld.allowCommands.info");
   private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
   private static final int HORIZONTAL_BUTTON_SPACING = 10;
   private static final int VERTICAL_BUTTON_SPACING = 8;
   public static final ResourceLocation HEADER_SEPERATOR = new ResourceLocation("textures/gui/header_separator.png");
   public static final ResourceLocation FOOTER_SEPERATOR = new ResourceLocation("textures/gui/footer_separator.png");
   public static final ResourceLocation LIGHT_DIRT_BACKGROUND = new ResourceLocation("textures/gui/light_dirt_background.png");
   final WorldCreationUiState uiState;
   private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
      this.removeWidget(p_267853_);
   });
   private boolean recreated;
   @Nullable
   private final Screen lastScreen;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private PackRepository tempDataPackRepository;
   @Nullable
   private GridLayout bottomButtons;
   @Nullable
   private TabNavigationBar tabNavigationBar;

   public static void openFresh(Minecraft p_232897_, @Nullable Screen p_232898_) {
      queueLoadScreen(p_232897_, PREPARING_WORLD_DATA);
      PackRepository packrepository = new PackRepository(new ServerPacksSource());
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.event.AddPackFindersEvent(net.minecraft.server.packs.PackType.SERVER_DATA, packrepository::addPackFinder));
      WorldLoader.InitConfig worldloader$initconfig = createDefaultLoadConfig(packrepository, WorldDataConfiguration.DEFAULT);
      CompletableFuture<WorldCreationContext> completablefuture = WorldLoader.load(worldloader$initconfig, (p_247792_) -> {
         return new WorldLoader.DataLoadOutput<>(new CreateWorldScreen.DataPackReloadCookie(new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), WorldPresets.createNormalWorldDimensions(p_247792_.datapackWorldgen())), p_247792_.dataConfiguration()), p_247792_.datapackDimensions());
      }, (p_247798_, p_247799_, p_247800_, p_247801_) -> {
         p_247798_.close();
         return new WorldCreationContext(p_247801_.worldGenSettings(), p_247800_, p_247799_, p_247801_.dataConfiguration());
      }, Util.backgroundExecutor(), p_232897_);
      p_232897_.managedBlock(completablefuture::isDone);
      p_232897_.setScreen(new CreateWorldScreen(p_232897_, p_232898_, completablefuture.join(), Optional.of(WorldPresets.NORMAL), OptionalLong.empty()));
   }

   public static CreateWorldScreen createFromExisting(Minecraft p_276017_, @Nullable Screen p_276029_, LevelSettings p_276055_, WorldCreationContext p_276028_, @Nullable Path p_276040_) {
      CreateWorldScreen createworldscreen = new CreateWorldScreen(p_276017_, p_276029_, p_276028_, WorldPresets.fromSettings(p_276028_.selectedDimensions().dimensions()), OptionalLong.of(p_276028_.options().seed()));
      createworldscreen.recreated = true;
      createworldscreen.uiState.setName(p_276055_.levelName());
      createworldscreen.uiState.setAllowCheats(p_276055_.allowCommands());
      createworldscreen.uiState.setDifficulty(p_276055_.difficulty());
      createworldscreen.uiState.getGameRules().assignFrom(p_276055_.gameRules(), (MinecraftServer)null);
      if (p_276055_.hardcore()) {
         createworldscreen.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
      } else if (p_276055_.gameType().isSurvival()) {
         createworldscreen.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
      } else if (p_276055_.gameType().isCreative()) {
         createworldscreen.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
      }

      createworldscreen.tempDataPackDir = p_276040_;
      return createworldscreen;
   }

   private CreateWorldScreen(Minecraft p_276053_, @Nullable Screen p_276049_, WorldCreationContext p_276047_, Optional<ResourceKey<WorldPreset>> p_276013_, OptionalLong p_276031_) {
      super(Component.translatable("selectWorld.create"));
      this.lastScreen = p_276049_;
      this.uiState = new WorldCreationUiState(p_276053_.getLevelSource().getBaseDir(), p_276047_, p_276013_, p_276031_);
   }

   public WorldCreationUiState getUiState() {
      return this.uiState;
   }

   public void tick() {
      this.tabManager.tickCurrent();
   }

   protected void init() {
      this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new CreateWorldScreen.GameTab(), new CreateWorldScreen.WorldTab(), new CreateWorldScreen.MoreTab()).build();
      this.addRenderableWidget(this.tabNavigationBar);
      this.bottomButtons = (new GridLayout()).columnSpacing(10);
      GridLayout.RowHelper gridlayout$rowhelper = this.bottomButtons.createRowHelper(2);
      gridlayout$rowhelper.addChild(Button.builder(Component.translatable("selectWorld.create"), (p_232938_) -> {
         this.onCreate();
      }).build());
      gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_232903_) -> {
         this.popScreen();
      }).build());
      this.bottomButtons.visitWidgets((p_267851_) -> {
         p_267851_.setTabOrderGroup(1);
         this.addRenderableWidget(p_267851_);
      });
      this.tabNavigationBar.selectTab(0, false);
      this.uiState.onChanged();
      this.repositionElements();
   }

   public void repositionElements() {
      if (this.tabNavigationBar != null && this.bottomButtons != null) {
         this.tabNavigationBar.setWidth(this.width);
         this.tabNavigationBar.arrangeElements();
         this.bottomButtons.arrangeElements();
         FrameLayout.centerInRectangle(this.bottomButtons, 0, this.height - 36, this.width, 36);
         int i = this.tabNavigationBar.getRectangle().bottom();
         ScreenRectangle screenrectangle = new ScreenRectangle(0, i, this.width, this.bottomButtons.getY() - i);
         this.tabManager.setTabArea(screenrectangle);
      }
   }

   private static void queueLoadScreen(Minecraft p_232900_, Component p_232901_) {
      p_232900_.forceSetScreen(new GenericDirtMessageScreen(p_232901_));
   }

   private void onCreate() {
      WorldCreationContext worldcreationcontext = this.uiState.getSettings();
      WorldDimensions.Complete worlddimensions$complete = worldcreationcontext.selectedDimensions().bake(worldcreationcontext.datapackDimensions());
      LayeredRegistryAccess<RegistryLayer> layeredregistryaccess = worldcreationcontext.worldgenRegistries().replaceFrom(RegistryLayer.DIMENSIONS, worlddimensions$complete.dimensionsRegistryAccess());
      Lifecycle lifecycle = FeatureFlags.isExperimental(worldcreationcontext.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
      Lifecycle lifecycle1 = layeredregistryaccess.compositeAccess().allRegistriesLifecycle();
      Lifecycle lifecycle2 = lifecycle1.add(lifecycle);
      boolean flag = !this.recreated && lifecycle1 == Lifecycle.stable();
      WorldOpenFlows.confirmWorldCreation(this.minecraft, this, lifecycle2, () -> {
         this.createNewWorld(worlddimensions$complete.specialWorldProperty(), layeredregistryaccess, lifecycle2);
      }, flag);
   }

   private void createNewWorld(PrimaryLevelData.SpecialWorldProperty p_250577_, LayeredRegistryAccess<RegistryLayer> p_249152_, Lifecycle p_249994_) {
      queueLoadScreen(this.minecraft, PREPARING_WORLD_DATA);
      Optional<LevelStorageSource.LevelStorageAccess> optional = this.createNewWorldDirectory();
      if (!optional.isEmpty()) {
         this.removeTempDataPackDir();
         boolean flag = p_250577_ == PrimaryLevelData.SpecialWorldProperty.DEBUG;
         WorldCreationContext worldcreationcontext = this.uiState.getSettings();
         LevelSettings levelsettings = this.createLevelSettings(flag);
         WorldData worlddata = new PrimaryLevelData(levelsettings, worldcreationcontext.options(), p_250577_, p_249994_);
         if(worlddata.worldGenSettingsLifecycle() != Lifecycle.stable()) {
            // Neo: set experimental settings confirmation flag so user is not shown warning on next open
            ((PrimaryLevelData)worlddata).withConfirmedWarning(true);
         }
         this.minecraft.createWorldOpenFlows().createLevelFromExistingSettings(optional.get(), worldcreationcontext.dataPackResources(), p_249152_, worlddata);
      }
   }

   private LevelSettings createLevelSettings(boolean p_205448_) {
      String s = this.uiState.getName().trim();
      if (p_205448_) {
         GameRules gamerules = new GameRules();
         gamerules.getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
         return new LevelSettings(s, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, WorldDataConfiguration.DEFAULT);
      } else {
         return new LevelSettings(s, this.uiState.getGameMode().gameType, this.uiState.isHardcore(), this.uiState.getDifficulty(), this.uiState.isAllowCheats(), this.uiState.getGameRules(), this.uiState.getSettings().dataConfiguration());
      }
   }

   public boolean keyPressed(int p_100875_, int p_100876_, int p_100877_) {
      if (this.tabNavigationBar.keyPressed(p_100875_)) {
         return true;
      } else if (super.keyPressed(p_100875_, p_100876_, p_100877_)) {
         return true;
      } else if (p_100875_ != 257 && p_100875_ != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   public void onClose() {
      this.popScreen();
   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.removeTempDataPackDir();
   }

   public void render(GuiGraphics p_282137_, int p_283640_, int p_281243_, float p_282743_) {
      this.renderBackground(p_282137_);
      p_282137_.blit(FOOTER_SEPERATOR, 0, Mth.roundToward(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
      super.render(p_282137_, p_283640_, p_281243_, p_282743_);
   }

   public void renderDirtBackground(GuiGraphics p_281950_) {
      int i = 32;
      p_281950_.blit(LIGHT_DIRT_BACKGROUND, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T p_100948_) {
      return super.addWidget(p_100948_);
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T p_170199_) {
      return super.addRenderableWidget(p_170199_);
   }

   @Nullable
   private Path getTempDataPackDir() {
      if (this.tempDataPackDir == null) {
         try {
            this.tempDataPackDir = Files.createTempDirectory("mcworld-");
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to create temporary dir", (Throwable)ioexception);
            SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
            this.popScreen();
         }
      }

      return this.tempDataPackDir;
   }

   void openExperimentsScreen(WorldDataConfiguration p_270214_) {
      Pair<Path, PackRepository> pair = this.getDataPackSelectionSettings(p_270214_);
      if (pair != null) {
         this.minecraft.setScreen(new ExperimentsScreen(this, pair.getSecond(), (p_269636_) -> {
            this.tryApplyNewDataPacks(p_269636_, false, this::openExperimentsScreen);
         }));
      }

   }

   void openDataPackSelectionScreen(WorldDataConfiguration p_268186_) {
      Pair<Path, PackRepository> pair = this.getDataPackSelectionSettings(p_268186_);
      if (pair != null) {
         this.minecraft.setScreen(new PackSelectionScreen(pair.getSecond(), (p_269637_) -> {
            this.tryApplyNewDataPacks(p_269637_, true, this::openDataPackSelectionScreen);
         }, pair.getFirst(), Component.translatable("dataPack.title")));
      }

   }

   private void tryApplyNewDataPacks(PackRepository p_270299_, boolean p_270896_, Consumer<WorldDataConfiguration> p_270760_) {
      List<String> list = ImmutableList.copyOf(p_270299_.getSelectedIds());
      List<String> list1 = p_270299_.getAvailableIds().stream().filter((p_232927_) -> {
         return !list.contains(p_232927_);
      }).collect(ImmutableList.toImmutableList());
      WorldDataConfiguration worlddataconfiguration = new WorldDataConfiguration(new DataPackConfig(list, list1), this.uiState.getSettings().dataConfiguration().enabledFeatures());
      if (this.uiState.tryUpdateDataConfiguration(worlddataconfiguration)) {
         this.minecraft.setScreen(this);
      } else {
         FeatureFlagSet featureflagset = p_270299_.getRequestedFeatureFlags();
         if (FeatureFlags.isExperimental(featureflagset) && p_270896_) {
            this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen(p_270299_.getSelectedPacks(), (p_269635_) -> {
               if (p_269635_) {
                  this.applyNewPackConfig(p_270299_, worlddataconfiguration, p_270760_);
               } else {
                  p_270760_.accept(this.uiState.getSettings().dataConfiguration());
               }

            }));
         } else {
            this.applyNewPackConfig(p_270299_, worlddataconfiguration, p_270760_);
         }

      }
   }

   private void applyNewPackConfig(PackRepository p_270272_, WorldDataConfiguration p_270573_, Consumer<WorldDataConfiguration> p_270552_) {
      this.minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("dataPack.validation.working")));
      WorldLoader.InitConfig worldloader$initconfig = createDefaultLoadConfig(p_270272_, p_270573_);
      WorldLoader.<CreateWorldScreen.DataPackReloadCookie, WorldCreationContext>load(worldloader$initconfig, (p_247793_) -> {
         if (p_247793_.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).size() == 0) {
            throw new IllegalStateException("Needs at least one world preset to continue");
         } else if (p_247793_.datapackWorldgen().registryOrThrow(Registries.BIOME).size() == 0) {
            throw new IllegalStateException("Needs at least one biome continue");
         } else {
            WorldCreationContext worldcreationcontext = this.uiState.getSettings();
            DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, worldcreationcontext.worldgenLoadContext());
            DataResult<JsonElement> dataresult = WorldGenSettings.encode(dynamicops, worldcreationcontext.options(), worldcreationcontext.selectedDimensions()).setLifecycle(Lifecycle.stable());
            DynamicOps<JsonElement> dynamicops1 = RegistryOps.create(JsonOps.INSTANCE, p_247793_.datapackWorldgen());
            WorldGenSettings worldgensettings = dataresult.flatMap((p_232895_) -> {
               return WorldGenSettings.CODEC.parse(dynamicops1, p_232895_);
            }).getOrThrow(false, Util.prefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error));
            return new WorldLoader.DataLoadOutput<>(new CreateWorldScreen.DataPackReloadCookie(worldgensettings, p_247793_.dataConfiguration()), p_247793_.datapackDimensions());
         }
      }, (p_247788_, p_247789_, p_247790_, p_247791_) -> {
         p_247788_.close();
         return new WorldCreationContext(p_247791_.worldGenSettings(), p_247790_, p_247789_, p_247791_.dataConfiguration());
      }, Util.backgroundExecutor(), this.minecraft).thenAcceptAsync(this.uiState::setSettings, this.minecraft).handle((p_280900_, p_280901_) -> {
         if (p_280901_ != null) {
            LOGGER.warn("Failed to validate datapack", p_280901_);
            this.minecraft.setScreen(new ConfirmScreen((p_269627_) -> {
               if (p_269627_) {
                  p_270552_.accept(this.uiState.getSettings().dataConfiguration());
               } else {
                  p_270552_.accept(new WorldDataConfiguration(new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of()), FeatureFlags.DEFAULT_FLAGS)); // FORGE: Revert to *actual* vanilla data
               }

            }, Component.translatable("dataPack.validation.failed"), CommonComponents.EMPTY, Component.translatable("dataPack.validation.back"), Component.translatable("dataPack.validation.reset")));
         } else {
            this.minecraft.setScreen(this);
         }

         return null;
      });
   }

   private static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository p_251829_, WorldDataConfiguration p_251555_) {
      WorldLoader.PackConfig worldloader$packconfig = new WorldLoader.PackConfig(p_251829_, p_251555_, false, true);
      return new WorldLoader.InitConfig(worldloader$packconfig, Commands.CommandSelection.INTEGRATED, 2);
   }

   private void removeTempDataPackDir() {
      if (this.tempDataPackDir != null) {
         try (Stream<Path> stream = Files.walk(this.tempDataPackDir)) {
            stream.sorted(Comparator.reverseOrder()).forEach((p_232942_) -> {
               try {
                  Files.delete(p_232942_);
               } catch (IOException ioexception1) {
                  LOGGER.warn("Failed to remove temporary file {}", p_232942_, ioexception1);
               }

            });
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
         }

         this.tempDataPackDir = null;
      }

   }

   private static void copyBetweenDirs(Path p_100913_, Path p_100914_, Path p_100915_) {
      try {
         Util.copyBetweenDirs(p_100913_, p_100914_, p_100915_);
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to copy datapack file from {} to {}", p_100915_, p_100914_);
         throw new UncheckedIOException(ioexception);
      }
   }

   private Optional<LevelStorageSource.LevelStorageAccess> createNewWorldDirectory() {
      String s = this.uiState.getTargetFolder();

      try {
         LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess(s);
         if (this.tempDataPackDir == null) {
            return Optional.of(levelstoragesource$levelstorageaccess);
         }

         try (Stream<Path> stream = Files.walk(this.tempDataPackDir)) {
            Path path = levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR);
            FileUtil.createDirectoriesSafe(path);
            stream.filter((p_232921_) -> {
               return !p_232921_.equals(this.tempDataPackDir);
            }).forEach((p_232945_) -> {
               copyBetweenDirs(this.tempDataPackDir, path, p_232945_);
            });
            return Optional.of(levelstoragesource$levelstorageaccess);
         } catch (UncheckedIOException | IOException ioexception) {
            LOGGER.warn("Failed to copy datapacks to world {}", s, ioexception);
            levelstoragesource$levelstorageaccess.close();
         }
      } catch (UncheckedIOException | IOException ioexception1) {
         LOGGER.warn("Failed to create access for {}", s, ioexception1);
      }

      SystemToast.onPackCopyFailure(this.minecraft, s);
      this.popScreen();
      return Optional.empty();
   }

   @Nullable
   public static Path createTempDataPackDirFromExistingWorld(Path p_100907_, Minecraft p_100908_) {
      MutableObject<Path> mutableobject = new MutableObject<>();

      try (Stream<Path> stream = Files.walk(p_100907_)) {
         stream.filter((p_232924_) -> {
            return !p_232924_.equals(p_100907_);
         }).forEach((p_232933_) -> {
            Path path = mutableobject.getValue();
            if (path == null) {
               try {
                  path = Files.createTempDirectory("mcworld-");
               } catch (IOException ioexception1) {
                  LOGGER.warn("Failed to create temporary dir");
                  throw new UncheckedIOException(ioexception1);
               }

               mutableobject.setValue(path);
            }

            copyBetweenDirs(p_100907_, path, p_232933_);
         });
      } catch (UncheckedIOException | IOException ioexception) {
         LOGGER.warn("Failed to copy datapacks from world {}", p_100907_, ioexception);
         SystemToast.onPackCopyFailure(p_100908_, p_100907_.toString());
         return null;
      }

      return mutableobject.getValue();
   }

   @Nullable
   private Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration p_268328_) {
      Path path = this.getTempDataPackDir();
      if (path != null) {
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = ServerPacksSource.createPackRepository(path);
            net.minecraftforge.resource.ResourcePackLoader.loadResourcePacks(this.tempDataPackRepository, net.minecraftforge.server.ServerLifecycleHooks::buildPackFinder);
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(p_268328_.dataPacks().getEnabled());
         return Pair.of(path, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
   }

   @OnlyIn(Dist.CLIENT)
   class GameTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.game.title");
      private static final Component ALLOW_CHEATS = Component.translatable("selectWorld.allowCommands");
      private final EditBox nameEdit;

      GameTab() {
         super(TITLE);
         GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(1);
         LayoutSettings layoutsettings = gridlayout$rowhelper.newCellSettings();
         GridLayout.RowHelper gridlayout$rowhelper1 = (new GridLayout()).rowSpacing(4).createRowHelper(1);
         gridlayout$rowhelper1.addChild(new StringWidget(CreateWorldScreen.NAME_LABEL, CreateWorldScreen.this.minecraft.font), gridlayout$rowhelper1.newCellSettings().paddingLeft(1));
         this.nameEdit = gridlayout$rowhelper1.addChild(new EditBox(CreateWorldScreen.this.font, 0, 0, 208, 20, Component.translatable("selectWorld.enterName")), gridlayout$rowhelper1.newCellSettings().padding(1));
         this.nameEdit.setValue(CreateWorldScreen.this.uiState.getName());
         this.nameEdit.setResponder(CreateWorldScreen.this.uiState::setName);
         CreateWorldScreen.this.uiState.addListener((p_275871_) -> {
            this.nameEdit.setTooltip(Tooltip.create(Component.translatable("selectWorld.targetFolder", Component.literal(p_275871_.getTargetFolder()).withStyle(ChatFormatting.ITALIC))));
         });
         CreateWorldScreen.this.setInitialFocus(this.nameEdit);
         gridlayout$rowhelper.addChild(gridlayout$rowhelper1.getGrid(), gridlayout$rowhelper.newCellSettings().alignHorizontallyCenter());
         CycleButton<WorldCreationUiState.SelectedGameMode> cyclebutton = gridlayout$rowhelper.addChild(CycleButton.<WorldCreationUiState.SelectedGameMode>builder((p_268080_) -> {
            return p_268080_.displayName;
         }).withValues(WorldCreationUiState.SelectedGameMode.SURVIVAL, WorldCreationUiState.SelectedGameMode.HARDCORE, WorldCreationUiState.SelectedGameMode.CREATIVE).create(0, 0, 210, 20, CreateWorldScreen.GAME_MODEL_LABEL, (p_268266_, p_268208_) -> {
            CreateWorldScreen.this.uiState.setGameMode(p_268208_);
         }), layoutsettings);
         CreateWorldScreen.this.uiState.addListener((p_280907_) -> {
            cyclebutton.setValue(p_280907_.getGameMode());
            cyclebutton.active = !p_280907_.isDebug();
            cyclebutton.setTooltip(Tooltip.create(p_280907_.getGameMode().getInfo()));
         });
         CycleButton<Difficulty> cyclebutton1 = gridlayout$rowhelper.addChild(CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).create(0, 0, 210, 20, Component.translatable("options.difficulty"), (p_267962_, p_268338_) -> {
            CreateWorldScreen.this.uiState.setDifficulty(p_268338_);
         }), layoutsettings);
         CreateWorldScreen.this.uiState.addListener((p_280905_) -> {
            cyclebutton1.setValue(CreateWorldScreen.this.uiState.getDifficulty());
            cyclebutton1.active = !CreateWorldScreen.this.uiState.isHardcore();
            cyclebutton1.setTooltip(Tooltip.create(CreateWorldScreen.this.uiState.getDifficulty().getInfo()));
         });
         CycleButton<Boolean> cyclebutton2 = gridlayout$rowhelper.addChild(CycleButton.onOffBuilder().withTooltip((p_267952_) -> {
            return Tooltip.create(CreateWorldScreen.ALLOW_CHEATS_INFO);
         }).create(0, 0, 210, 20, ALLOW_CHEATS, (p_268200_, p_268324_) -> {
            CreateWorldScreen.this.uiState.setAllowCheats(p_268324_);
         }));
         CreateWorldScreen.this.uiState.addListener((p_280903_) -> {
            cyclebutton2.setValue(CreateWorldScreen.this.uiState.isAllowCheats());
            cyclebutton2.active = !CreateWorldScreen.this.uiState.isDebug() && !CreateWorldScreen.this.uiState.isHardcore();
         });
         if (!SharedConstants.getCurrentVersion().isStable()) {
            gridlayout$rowhelper.addChild(Button.builder(CreateWorldScreen.EXPERIMENTS_LABEL, (p_269641_) -> {
               CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration());
            }).width(210).build());
         }

      }

      public void tick() {
         this.nameEdit.tick();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class MoreTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.more.title");
      private static final Component GAME_RULES_LABEL = Component.translatable("selectWorld.gameRules");
      private static final Component DATA_PACKS_LABEL = Component.translatable("selectWorld.dataPacks");

      MoreTab() {
         super(TITLE);
         GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(1);
         gridlayout$rowhelper.addChild(Button.builder(GAME_RULES_LABEL, (p_268028_) -> {
            this.openGameRulesScreen();
         }).width(210).build());
         gridlayout$rowhelper.addChild(Button.builder(CreateWorldScreen.EXPERIMENTS_LABEL, (p_269642_) -> {
            CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration());
         }).width(210).build());
         gridlayout$rowhelper.addChild(Button.builder(DATA_PACKS_LABEL, (p_268345_) -> {
            CreateWorldScreen.this.openDataPackSelectionScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration());
         }).width(210).build());
      }

      private void openGameRulesScreen() {
         CreateWorldScreen.this.minecraft.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.uiState.getGameRules().copy(), (p_268107_) -> {
            CreateWorldScreen.this.minecraft.setScreen(CreateWorldScreen.this);
            p_268107_.ifPresent(CreateWorldScreen.this.uiState::setGameRules);
         }));
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldTab extends GridLayoutTab {
      private static final Component TITLE = Component.translatable("createWorld.tab.world.title");
      private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
      private static final Component GENERATE_STRUCTURES = Component.translatable("selectWorld.mapFeatures");
      private static final Component GENERATE_STRUCTURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
      private static final Component BONUS_CHEST = Component.translatable("selectWorld.bonusItems");
      private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
      static final Component SEED_EMPTY_HINT = Component.translatable("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
      private static final int WORLD_TAB_WIDTH = 310;
      private final EditBox seedEdit;
      private final Button customizeTypeButton;

      WorldTab() {
         super(TITLE);
         GridLayout.RowHelper gridlayout$rowhelper = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
         CycleButton<WorldCreationUiState.WorldTypeEntry> cyclebutton = gridlayout$rowhelper.addChild(CycleButton.builder(WorldCreationUiState.WorldTypeEntry::describePreset).withValues(this.createWorldTypeValueSupplier()).withCustomNarration(CreateWorldScreen.WorldTab::createTypeButtonNarration).create(0, 0, 150, 20, Component.translatable("selectWorld.mapType"), (p_268242_, p_267954_) -> {
            CreateWorldScreen.this.uiState.setWorldType(p_267954_);
         }));
         cyclebutton.setValue(CreateWorldScreen.this.uiState.getWorldType());
         CreateWorldScreen.this.uiState.addListener((p_280909_) -> {
            WorldCreationUiState.WorldTypeEntry worldcreationuistate$worldtypeentry = p_280909_.getWorldType();
            cyclebutton.setValue(worldcreationuistate$worldtypeentry);
            if (worldcreationuistate$worldtypeentry.isAmplified()) {
               cyclebutton.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
            } else {
               cyclebutton.setTooltip((Tooltip)null);
            }

            cyclebutton.active = CreateWorldScreen.this.uiState.getWorldType().preset() != null;
         });
         this.customizeTypeButton = gridlayout$rowhelper.addChild(Button.builder(Component.translatable("selectWorld.customizeType"), (p_268355_) -> {
            this.openPresetEditor();
         }).build());
         CreateWorldScreen.this.uiState.addListener((p_280910_) -> {
            this.customizeTypeButton.active = !p_280910_.isDebug() && p_280910_.getPresetEditor() != null;
         });
         GridLayout.RowHelper gridlayout$rowhelper1 = (new GridLayout()).rowSpacing(4).createRowHelper(1);
         gridlayout$rowhelper1.addChild((new StringWidget(SEED_LABEL, CreateWorldScreen.this.font)).alignLeft());
         this.seedEdit = gridlayout$rowhelper1.addChild(new EditBox(CreateWorldScreen.this.font, 0, 0, 308, 20, Component.translatable("selectWorld.enterSeed")) {
            protected MutableComponent createNarrationMessage() {
               return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(CreateWorldScreen.WorldTab.SEED_EMPTY_HINT);
            }
         }, gridlayout$rowhelper.newCellSettings().padding(1));
         this.seedEdit.setHint(SEED_EMPTY_HINT);
         this.seedEdit.setValue(CreateWorldScreen.this.uiState.getSeed());
         this.seedEdit.setResponder((p_268342_) -> {
            CreateWorldScreen.this.uiState.setSeed(this.seedEdit.getValue());
         });
         gridlayout$rowhelper.addChild(gridlayout$rowhelper1.getGrid(), 2);
         SwitchGrid.Builder switchgrid$builder = SwitchGrid.builder(310).withPaddingLeft(1);
         switchgrid$builder.addSwitch(GENERATE_STRUCTURES, CreateWorldScreen.this.uiState::isGenerateStructures, CreateWorldScreen.this.uiState::setGenerateStructures).withIsActiveCondition(() -> {
            return !CreateWorldScreen.this.uiState.isDebug();
         }).withInfo(GENERATE_STRUCTURES_INFO);
         switchgrid$builder.addSwitch(BONUS_CHEST, CreateWorldScreen.this.uiState::isBonusChest, CreateWorldScreen.this.uiState::setBonusChest).withIsActiveCondition(() -> {
            return !CreateWorldScreen.this.uiState.isHardcore() && !CreateWorldScreen.this.uiState.isDebug();
         });
         SwitchGrid switchgrid = switchgrid$builder.build((p_267961_) -> {
            gridlayout$rowhelper.addChild(p_267961_, 2);
         });
         CreateWorldScreen.this.uiState.addListener((p_268209_) -> {
            switchgrid.refreshStates();
         });
      }

      private void openPresetEditor() {
         PresetEditor preseteditor = CreateWorldScreen.this.uiState.getPresetEditor();
         if (preseteditor != null) {
            CreateWorldScreen.this.minecraft.setScreen(preseteditor.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.uiState.getSettings()));
         }

      }

      private CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry> createWorldTypeValueSupplier() {
         return new CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry>() {
            public List<WorldCreationUiState.WorldTypeEntry> getSelectedList() {
               return CycleButton.DEFAULT_ALT_LIST_SELECTOR.getAsBoolean() ? CreateWorldScreen.this.uiState.getAltPresetList() : CreateWorldScreen.this.uiState.getNormalPresetList();
            }

            public List<WorldCreationUiState.WorldTypeEntry> getDefaultList() {
               return CreateWorldScreen.this.uiState.getNormalPresetList();
            }
         };
      }

      private static MutableComponent createTypeButtonNarration(CycleButton<WorldCreationUiState.WorldTypeEntry> p_268292_) {
         return p_268292_.getValue().isAmplified() ? CommonComponents.joinForNarration(p_268292_.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT) : p_268292_.createDefaultNarrationMessage();
      }

      public void tick() {
         this.seedEdit.tick();
      }
   }
}
