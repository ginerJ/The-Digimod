package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SymlinkWarningScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ObjectSelectionList<WorldSelectionList.Entry> {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private static final ResourceLocation FORGE_EXPERIMENTAL_WARNING_ICON = new ResourceLocation("forge","textures/gui/experimental_warning.png");
   static final Component FROM_NEWER_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
   static final Component FROM_NEWER_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
   static final Component SNAPSHOT_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
   static final Component SNAPSHOT_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
   static final Component WORLD_LOCKED_TOOLTIP = Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
   static final Component WORLD_REQUIRES_CONVERSION = Component.translatable("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED);
   private final SelectWorldScreen screen;
   private CompletableFuture<List<LevelSummary>> pendingLevels;
   @Nullable
   private List<LevelSummary> currentlyDisplayedLevels;
   private String filter;
   private final WorldSelectionList.LoadingHeader loadingHeader;

   public WorldSelectionList(SelectWorldScreen p_239540_, Minecraft p_239541_, int p_239542_, int p_239543_, int p_239544_, int p_239545_, int p_239546_, String p_239547_, @Nullable WorldSelectionList p_239548_) {
      super(p_239541_, p_239542_, p_239543_, p_239544_, p_239545_, p_239546_);
      this.screen = p_239540_;
      this.loadingHeader = new WorldSelectionList.LoadingHeader(p_239541_);
      this.filter = p_239547_;
      if (p_239548_ != null) {
         this.pendingLevels = p_239548_.pendingLevels;
      } else {
         this.pendingLevels = this.loadLevels();
      }

      this.handleNewLevels(this.pollLevelsIgnoreErrors());
   }

   protected void clearEntries() {
      this.children().forEach(WorldSelectionList.Entry::close);
      super.clearEntries();
   }

   @Nullable
   private List<LevelSummary> pollLevelsIgnoreErrors() {
      try {
         return this.pendingLevels.getNow((List<LevelSummary>)null);
      } catch (CancellationException | CompletionException completionexception) {
         return null;
      }
   }

   void reloadWorldList() {
      this.pendingLevels = this.loadLevels();
   }

   public boolean keyPressed(int p_289017_, int p_288966_, int p_289020_) {
      if (CommonInputs.selected(p_289017_)) {
         Optional<WorldSelectionList.WorldListEntry> optional = this.getSelectedOpt();
         if (optional.isPresent()) {
            optional.get().joinWorld();
            return true;
         }
      }

      return super.keyPressed(p_289017_, p_288966_, p_289020_);
   }

   public void render(GuiGraphics p_283323_, int p_282039_, int p_283339_, float p_281472_) {
      List<LevelSummary> list = this.pollLevelsIgnoreErrors();
      if (list != this.currentlyDisplayedLevels) {
         this.handleNewLevels(list);
      }

      super.render(p_283323_, p_282039_, p_283339_, p_281472_);
   }

   private void handleNewLevels(@Nullable List<LevelSummary> p_239665_) {
      if (p_239665_ == null) {
         this.fillLoadingLevels();
      } else {
         this.fillLevels(this.filter, p_239665_);
      }

      this.currentlyDisplayedLevels = p_239665_;
   }

   public void updateFilter(String p_239901_) {
      if (this.currentlyDisplayedLevels != null && !p_239901_.equals(this.filter)) {
         this.fillLevels(p_239901_, this.currentlyDisplayedLevels);
      }

      this.filter = p_239901_;
   }

   private CompletableFuture<List<LevelSummary>> loadLevels() {
      LevelStorageSource.LevelCandidates levelstoragesource$levelcandidates;
      try {
         levelstoragesource$levelcandidates = this.minecraft.getLevelSource().findLevelCandidates();
      } catch (LevelStorageException levelstorageexception) {
         LOGGER.error("Couldn't load level list", (Throwable)levelstorageexception);
         this.handleLevelLoadFailure(levelstorageexception.getMessageComponent());
         return CompletableFuture.completedFuture(List.of());
      }

      if (levelstoragesource$levelcandidates.isEmpty()) {
         CreateWorldScreen.openFresh(this.minecraft, (Screen)null);
         return CompletableFuture.completedFuture(List.of());
      } else {
         return this.minecraft.getLevelSource().loadLevelSummaries(levelstoragesource$levelcandidates).exceptionally((p_233202_) -> {
            this.minecraft.delayCrash(CrashReport.forThrowable(p_233202_, "Couldn't load level list"));
            return List.of();
         });
      }
   }

   private void fillLevels(String p_233199_, List<LevelSummary> p_233200_) {
      this.clearEntries();
      p_233199_ = p_233199_.toLowerCase(Locale.ROOT);

      for(LevelSummary levelsummary : p_233200_) {
         if (this.filterAccepts(p_233199_, levelsummary)) {
            this.addEntry(new WorldSelectionList.WorldListEntry(this, levelsummary));
         }
      }

      this.notifyListUpdated();
   }

   private boolean filterAccepts(String p_233196_, LevelSummary p_233197_) {
      return p_233197_.getLevelName().toLowerCase(Locale.ROOT).contains(p_233196_) || p_233197_.getLevelId().toLowerCase(Locale.ROOT).contains(p_233196_);
   }

   private void fillLoadingLevels() {
      this.clearEntries();
      this.addEntry(this.loadingHeader);
      this.notifyListUpdated();
   }

   private void notifyListUpdated() {
      this.screen.triggerImmediateNarration(true);
   }

   private void handleLevelLoadFailure(Component p_233212_) {
      this.minecraft.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), p_233212_));
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry p_233190_) {
      super.setSelected(p_233190_);
      this.screen.updateButtonStatus(p_233190_ != null && p_233190_.isSelectable(), p_233190_ != null);
   }

   public Optional<WorldSelectionList.WorldListEntry> getSelectedOpt() {
      WorldSelectionList.Entry worldselectionlist$entry = this.getSelected();
      if (worldselectionlist$entry instanceof WorldSelectionList.WorldListEntry worldselectionlist$worldlistentry) {
         return Optional.of(worldselectionlist$worldlistentry);
      } else {
         return Optional.empty();
      }
   }

   public SelectWorldScreen getScreen() {
      return this.screen;
   }

   public void updateNarration(NarrationElementOutput p_233188_) {
      if (this.children().contains(this.loadingHeader)) {
         this.loadingHeader.updateNarration(p_233188_);
      } else {
         super.updateNarration(p_233188_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ObjectSelectionList.Entry<WorldSelectionList.Entry> implements AutoCloseable {
      public abstract boolean isSelectable();

      public void close() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LoadingHeader extends WorldSelectionList.Entry {
      private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
      private final Minecraft minecraft;

      public LoadingHeader(Minecraft p_233222_) {
         this.minecraft = p_233222_;
      }

      public void render(GuiGraphics p_282319_, int p_283207_, int p_281352_, int p_283332_, int p_282400_, int p_282912_, int p_282760_, int p_281344_, boolean p_283655_, float p_283696_) {
         int i = (this.minecraft.screen.width - this.minecraft.font.width(LOADING_LABEL)) / 2;
         int j = p_281352_ + (p_282912_ - 9) / 2;
         p_282319_.drawString(this.minecraft.font, LOADING_LABEL, i, j, 16777215, false);
         String s = LoadingDotsText.get(Util.getMillis());
         int k = (this.minecraft.screen.width - this.minecraft.font.width(s)) / 2;
         int l = j + 9;
         p_282319_.drawString(this.minecraft.font, s, k, l, 8421504, false);
      }

      public Component getNarration() {
         return LOADING_LABEL;
      }

      public boolean isSelectable() {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public final class WorldListEntry extends WorldSelectionList.Entry implements AutoCloseable {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private static final int ICON_OVERLAY_X_JOIN = 0;
      private static final int ICON_OVERLAY_X_JOIN_WITH_NOTIFY = 32;
      private static final int ICON_OVERLAY_X_WARNING = 64;
      private static final int ICON_OVERLAY_X_ERROR = 96;
      private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
      private static final int ICON_OVERLAY_Y_SELECTED = 32;
      private final Minecraft minecraft;
      private final SelectWorldScreen screen;
      private final LevelSummary summary;
      private final FaviconTexture icon;
      @Nullable
      private Path iconFile;
      private long lastClickTime;

      public WorldListEntry(WorldSelectionList p_101702_, LevelSummary p_101703_) {
         this.minecraft = p_101702_.minecraft;
         this.screen = p_101702_.getScreen();
         this.summary = p_101703_;
         this.icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), p_101703_.getLevelId());
         this.iconFile = p_101703_.getIcon();
         this.validateIconFile();
         this.loadIcon();
      }

      private void validateIconFile() {
         if (this.iconFile != null) {
            try {
               BasicFileAttributes basicfileattributes = Files.readAttributes(this.iconFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
               if (basicfileattributes.isSymbolicLink()) {
                  List<ForbiddenSymlinkInfo> list = new ArrayList<>();
                  this.minecraft.getLevelSource().getWorldDirValidator().validateSymlink(this.iconFile, list);
                  if (!list.isEmpty()) {
                     WorldSelectionList.LOGGER.warn(ContentValidationException.getMessage(this.iconFile, list));
                     this.iconFile = null;
                  } else {
                     basicfileattributes = Files.readAttributes(this.iconFile, BasicFileAttributes.class);
                  }
               }

               if (!basicfileattributes.isRegularFile()) {
                  this.iconFile = null;
               }
            } catch (NoSuchFileException nosuchfileexception) {
               this.iconFile = null;
            } catch (IOException ioexception) {
               WorldSelectionList.LOGGER.error("could not validate symlink", (Throwable)ioexception);
               this.iconFile = null;
            }

         }
      }

      public Component getNarration() {
         Component component = Component.translatable("narrator.select.world_info", this.summary.getLevelName(), new Date(this.summary.getLastPlayed()), this.summary.getInfo());
         Component component1;
         if (this.summary.isLocked()) {
            component1 = CommonComponents.joinForNarration(component, WorldSelectionList.WORLD_LOCKED_TOOLTIP);
         } else {
            component1 = component;
         }

         return Component.translatable("narrator.select", component1);
      }

      public void render(GuiGraphics p_281612_, int p_281353_, int p_283181_, int p_282820_, int p_282420_, int p_281855_, int p_283204_, int p_283025_, boolean p_283396_, float p_282938_) {
         String s = this.summary.getLevelName();
         String s1 = this.summary.getLevelId();
         long i = this.summary.getLastPlayed();
         if (i != -1L) {
            s1 = s1 + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(i)) + ")";
         }

         if (StringUtils.isEmpty(s)) {
            s = I18n.get("selectWorld.world") + " " + (p_281353_ + 1);
         }

         Component component = this.summary.getInfo();
         p_281612_.drawString(this.minecraft.font, s, p_282820_ + 32 + 3, p_283181_ + 1, 16777215, false);
         p_281612_.drawString(this.minecraft.font, s1, p_282820_ + 32 + 3, p_283181_ + 9 + 3, 8421504, false);
         p_281612_.drawString(this.minecraft.font, component, p_282820_ + 32 + 3, p_283181_ + 9 + 9 + 3, 8421504, false);
         RenderSystem.enableBlend();
         p_281612_.blit(this.icon.textureLocation(), p_282820_, p_283181_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         renderExperimentalWarning(p_281612_, p_283204_, p_283025_, p_283181_, p_282820_);
         if (this.minecraft.options.touchscreen().get() || p_283396_) {
            p_281612_.fill(p_282820_, p_283181_, p_282820_ + 32, p_283181_ + 32, -1601138544);
            int j = p_283204_ - p_282820_;
            boolean flag = j < 32;
            int k = flag ? 32 : 0;
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 96.0F, (float)k, 32, 32, 256, 256);
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 32.0F, (float)k, 32, 32, 256, 256);
               return;
            }

            if (this.summary.isLocked()) {
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 96.0F, (float)k, 32, 32, 256, 256);
               if (flag) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
               }
            } else if (this.summary.requiresManualConversion()) {
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 96.0F, (float)k, 32, 32, 256, 256);
               if (flag) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175));
               }
            } else if (this.summary.markVersionInList()) {
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 32.0F, (float)k, 32, 32, 256, 256);
               if (this.summary.askToOpenWorld()) {
                  p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 96.0F, (float)k, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setTooltipForNextRenderPass(ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 64.0F, (float)k, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setTooltipForNextRenderPass(ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                  }
               }
            } else {
               p_281612_.blit(WorldSelectionList.ICON_OVERLAY_LOCATION, p_282820_, p_283181_, 0.0F, (float)k, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_101706_, double p_101707_, int p_101708_) {
         if (this.summary.isDisabled()) {
            return true;
         } else {
            WorldSelectionList.this.setSelected((WorldSelectionList.Entry)this);
            if (p_101706_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
               this.joinWorld();
               return true;
            } else if (Util.getMillis() - this.lastClickTime < 250L) {
               this.joinWorld();
               return true;
            } else {
               this.lastClickTime = Util.getMillis();
               return true;
            }
         }
      }

      public void joinWorld() {
         if (!this.summary.isDisabled()) {
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
               this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
            } else {
               LevelSummary.BackupStatus levelsummary$backupstatus = this.summary.backupStatus();
               if (levelsummary$backupstatus.shouldBackup()) {
                  String s = "selectWorld.backupQuestion." + levelsummary$backupstatus.getTranslationKey();
                  String s1 = "selectWorld.backupWarning." + levelsummary$backupstatus.getTranslationKey();
                  MutableComponent mutablecomponent = Component.translatable(s);
                  if (levelsummary$backupstatus.isSevere()) {
                     mutablecomponent.withStyle(ChatFormatting.BOLD, ChatFormatting.RED);
                  }

                  Component component = Component.translatable(s1, this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
                  this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (p_289912_, p_289913_) -> {
                     if (p_289912_) {
                        String s2 = this.summary.getLevelId();

                        try (LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().validateAndCreateAccess(s2)) {
                           EditWorldScreen.makeBackupAndShowToast(levelstoragesource$levelstorageaccess);
                        } catch (IOException ioexception) {
                           SystemToast.onWorldAccessFailure(this.minecraft, s2);
                           WorldSelectionList.LOGGER.error("Failed to backup level {}", s2, ioexception);
                        } catch (ContentValidationException contentvalidationexception) {
                           WorldSelectionList.LOGGER.warn("{}", (Object)contentvalidationexception.getMessage());
                           this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
                        }
                     }

                     this.loadWorld();
                  }, mutablecomponent, component, false));
               } else if (this.summary.askToOpenWorld()) {
                  this.minecraft.setScreen(new ConfirmScreen((p_101741_) -> {
                     if (p_101741_) {
                        try {
                           this.loadWorld();
                        } catch (Exception exception) {
                           WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                           this.minecraft.setScreen(new AlertScreen(() -> {
                              this.minecraft.setScreen(this.screen);
                           }, Component.translatable("selectWorld.futureworld.error.title"), Component.translatable("selectWorld.futureworld.error.text")));
                        }
                     } else {
                        this.minecraft.setScreen(this.screen);
                     }

                  }, Component.translatable("selectWorld.versionQuestion"), Component.translatable("selectWorld.versionWarning", this.summary.getWorldVersionName()), Component.translatable("selectWorld.versionJoinButton"), CommonComponents.GUI_CANCEL));
               } else {
                  this.loadWorld();
               }

            }
         }
      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((p_170322_) -> {
            if (p_170322_) {
               this.minecraft.setScreen(new ProgressScreen(true));
               this.doDeleteWorld();
            }

            if (this.minecraft.screen instanceof ProgressScreen) // Neo - fix MC-251068
            this.minecraft.setScreen(this.screen);
         }, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
      }

      public void doDeleteWorld() {
         LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
         String s = this.summary.getLevelId();

         try (LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = levelstoragesource.createAccess(s)) {
            levelstoragesource$levelstorageaccess.deleteLevel();
         } catch (IOException ioexception) {
            SystemToast.onWorldDeleteFailure(this.minecraft, s);
            WorldSelectionList.LOGGER.error("Failed to delete world {}", s, ioexception);
         }

         WorldSelectionList.this.reloadWorldList();
      }

      public void editWorld() {
         if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
            this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
         } else {
            this.queueLoadScreen();
            String s = this.summary.getLevelId();

            try {
               LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().validateAndCreateAccess(s);
               this.minecraft.setScreen(new EditWorldScreen((p_233244_) -> {
                  try {
                     levelstoragesource$levelstorageaccess.close();
                  } catch (IOException ioexception1) {
                     WorldSelectionList.LOGGER.error("Failed to unlock level {}", s, ioexception1);
                  }

                  if (p_233244_) {
                     WorldSelectionList.this.reloadWorldList();
                  }

                  this.minecraft.setScreen(this.screen);
               }, levelstoragesource$levelstorageaccess));
            } catch (IOException ioexception) {
               SystemToast.onWorldAccessFailure(this.minecraft, s);
               WorldSelectionList.LOGGER.error("Failed to access level {}", s, ioexception);
               WorldSelectionList.this.reloadWorldList();
            } catch (ContentValidationException contentvalidationexception) {
               WorldSelectionList.LOGGER.warn("{}", (Object)contentvalidationexception.getMessage());
               this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
            }

         }
      }

      public void recreateWorld() {
         if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
            this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
         } else {
            this.queueLoadScreen();

            try (LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().validateAndCreateAccess(this.summary.getLevelId())) {
               Pair<LevelSettings, WorldCreationContext> pair = this.minecraft.createWorldOpenFlows().recreateWorldData(levelstoragesource$levelstorageaccess);
               LevelSettings levelsettings = pair.getFirst();
               WorldCreationContext worldcreationcontext = pair.getSecond();
               Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
               if (worldcreationcontext.options().isOldCustomizedWorld()) {
                  this.minecraft.setScreen(new ConfirmScreen((p_275882_) -> {
                     this.minecraft.setScreen((Screen)(p_275882_ ? CreateWorldScreen.createFromExisting(this.minecraft, this.screen, levelsettings, worldcreationcontext, path) : this.screen));
                  }, Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
               } else {
                  this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.minecraft, this.screen, levelsettings, worldcreationcontext, path));
               }
            } catch (ContentValidationException contentvalidationexception) {
               WorldSelectionList.LOGGER.warn("{}", (Object)contentvalidationexception.getMessage());
               this.minecraft.setScreen(new SymlinkWarningScreen(this.screen));
            } catch (Exception exception) {
               WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception);
               this.minecraft.setScreen(new AlertScreen(() -> {
                  this.minecraft.setScreen(this.screen);
               }, Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
            }

         }
      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.queueLoadScreen();
            this.minecraft.createWorldOpenFlows().loadLevel(this.screen, this.summary.getLevelId());
         }

      }

      private void queueLoadScreen() {
         this.minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
      }

      private void loadIcon() {
         boolean flag = this.iconFile != null && Files.isRegularFile(this.iconFile);
         if (flag) {
            try (InputStream inputstream = Files.newInputStream(this.iconFile)) {
               this.icon.upload(NativeImage.read(inputstream));
            } catch (Throwable throwable) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), throwable);
               this.iconFile = null;
            }
         } else {
            this.icon.clear();
         }

      }

      public void close() {
         this.icon.close();
      }

      public String getLevelName() {
         return this.summary.getLevelName();
      }

      public boolean isSelectable() {
         return !this.summary.isDisabled();
      }

      // FORGE: Patch in experimental warning icon for worlds in the world selection screen
      private void renderExperimentalWarning(GuiGraphics guiGraphics, int mouseX, int mouseY, int top, int left) {
         if (this.summary.isLifecycleExperimental()) {
            int leftStart = left + WorldSelectionList.this.getRowWidth();
            guiGraphics.blit(WorldSelectionList.FORGE_EXPERIMENTAL_WARNING_ICON, leftStart - 36, top, 0.0F, 0.0F, 32, 32, 32, 32);
            if (WorldSelectionList.this.getEntryAtPosition(mouseX, mouseY) == this && mouseX > leftStart - 36 && mouseX < leftStart) {
               var font = Minecraft.getInstance().font;
               List<net.minecraft.util.FormattedCharSequence> tooltip = font.split(Component.translatable("forge.experimentalsettings.tooltip"), 200);
               guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
         }
      }
   }
}
