package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
   private Button renameButton;
   private final BooleanConsumer callback;
   private EditBox nameEdit;
   private final LevelStorageSource.LevelStorageAccess levelAccess;

   public EditWorldScreen(BooleanConsumer p_101252_, LevelStorageSource.LevelStorageAccess p_101253_) {
      super(Component.translatable("selectWorld.edit.title"));
      this.callback = p_101252_;
      this.levelAccess = p_101253_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.renameButton = Button.builder(Component.translatable("selectWorld.edit.save"), (p_101280_) -> {
         this.onRename();
      }).bounds(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20).build();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, Component.translatable("selectWorld.enterName"));
      LevelSummary levelsummary = this.levelAccess.getSummary();
      String s = levelsummary == null ? "" : levelsummary.getLevelName();
      this.nameEdit.setValue(s);
      this.nameEdit.setResponder((p_280914_) -> {
         this.renameButton.active = !p_280914_.trim().isEmpty();
      });
      this.addWidget(this.nameEdit);
      Button button = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.resetIcon"), (p_280916_) -> {
         this.levelAccess.getIconFile().ifPresent((p_182594_) -> {
            FileUtils.deleteQuietly(p_182594_.toFile());
         });
         p_280916_.active = false;
      }).bounds(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.openFolder"), (p_101294_) -> {
         Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile());
      }).bounds(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backup"), (p_101292_) -> {
         boolean flag = makeBackupAndShowToast(this.levelAccess);
         this.callback.accept(!flag);
      }).bounds(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backupFolder"), (p_280915_) -> {
         LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();
         Path path = levelstoragesource.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Util.getPlatform().openFile(path.toFile());
      }).bounds(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.optimize"), (p_280913_) -> {
         this.minecraft.setScreen(new BackupConfirmScreen(this, (p_280911_, p_280912_) -> {
            if (p_280911_) {
               makeBackupAndShowToast(this.levelAccess);
            }

            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, p_280912_));
         }, Component.translatable("optimizeWorld.confirm.title"), Component.translatable("optimizeWorld.confirm.description"), true));
      }).bounds(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20).build());
      this.addRenderableWidget(this.renameButton);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_101273_) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20).build());
      button.active = this.levelAccess.getIconFile().filter((p_182587_) -> {
         return Files.isRegularFile(p_182587_);
      }).isPresent();
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft p_101269_, int p_101270_, int p_101271_) {
      String s = this.nameEdit.getValue();
      this.init(p_101269_, p_101270_, p_101271_);
      this.nameEdit.setValue(s);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   private void onRename() {
      try {
         this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
         this.callback.accept(true);
      } catch (IOException ioexception) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), ioexception);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
         this.callback.accept(true);
      }

   }

   public static void makeBackupAndShowToast(LevelStorageSource p_101261_, String p_101262_) {
      boolean flag = false;

      try (LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = p_101261_.validateAndCreateAccess(p_101262_)) {
         flag = true;
         makeBackupAndShowToast(levelstoragesource$levelstorageaccess);
      } catch (IOException ioexception) {
         if (!flag) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), p_101262_);
         }

         LOGGER.warn("Failed to create backup of level {}", p_101262_, ioexception);
      } catch (ContentValidationException contentvalidationexception) {
         LOGGER.warn("{}", (Object)contentvalidationexception.getMessage());
         SystemToast.onWorldAccessFailure(Minecraft.getInstance(), p_101262_);
      }

   }

   public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess p_101259_) {
      long i = 0L;
      IOException ioexception = null;

      try {
         i = p_101259_.makeWorldBackup();
      } catch (IOException ioexception1) {
         ioexception = ioexception1;
      }

      if (ioexception != null) {
         Component component2 = Component.translatable("selectWorld.edit.backupFailed");
         Component component3 = Component.literal(ioexception.getMessage());
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, component2, component3));
         return false;
      } else {
         Component component = Component.translatable("selectWorld.edit.backupCreated", p_101259_.getLevelId());
         Component component1 = Component.translatable("selectWorld.edit.backupSize", Mth.ceil((double)i / 1048576.0D));
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, component, component1));
         return true;
      }
   }

   public void render(GuiGraphics p_281742_, int p_101265_, int p_101266_, float p_101267_) {
      this.renderBackground(p_281742_);
      p_281742_.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      p_281742_.drawString(this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
      this.nameEdit.render(p_281742_, p_101265_, p_101266_, p_101267_);
      super.render(p_281742_, p_101265_, p_101266_, p_101267_);
   }
}