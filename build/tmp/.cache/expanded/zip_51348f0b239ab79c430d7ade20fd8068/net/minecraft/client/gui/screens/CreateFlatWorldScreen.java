package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   protected final CreateWorldScreen parent;
   private final Consumer<FlatLevelGeneratorSettings> applySettings;
   FlatLevelGeneratorSettings generator;
   private Component columnType;
   private Component columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen p_95822_, Consumer<FlatLevelGeneratorSettings> p_95823_, FlatLevelGeneratorSettings p_95824_) {
      super(Component.translatable("createWorld.customize.flat.title"));
      this.parent = p_95822_;
      this.applySettings = p_95823_;
      this.generator = p_95824_;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.generator;
   }

   public void setConfig(FlatLevelGeneratorSettings p_95826_) {
      this.generator = p_95826_;
   }

   protected void init() {
      this.columnType = Component.translatable("createWorld.customize.flat.tile");
      this.columnHeight = Component.translatable("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.addWidget(this.list);
      this.deleteLayerButton = this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.flat.removeLayer"), (p_95845_) -> {
         if (this.hasValidSelection()) {
            List<FlatLayerInfo> list = this.generator.getLayersInfo();
            int i = this.list.children().indexOf(this.list.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.list.setSelected(list.isEmpty() ? null : this.list.children().get(Math.min(i, list.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
         }
      }).bounds(this.width / 2 - 155, this.height - 52, 150, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.presets"), (p_280790_) -> {
         this.minecraft.setScreen(new PresetFlatWorldScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }).bounds(this.width / 2 + 5, this.height - 52, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280791_) -> {
         this.applySettings.accept(this.generator);
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_280792_) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(GuiGraphics p_282393_, int p_95829_, int p_95830_, float p_95831_) {
      this.renderBackground(p_282393_);
      this.list.render(p_282393_, p_95829_, p_95830_, p_95831_);
      p_282393_.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      p_282393_.drawString(this.font, this.columnType, i, 32, 16777215);
      p_282393_.drawString(this.font, this.columnHeight, i + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
      super.render(p_282393_, p_95829_, p_95830_, p_95831_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ObjectSelectionList<CreateFlatWorldScreen.DetailsList.Entry> {
      static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");

      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.Entry p_95855_) {
         super.setSelected(p_95855_);
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void resetRows() {
         int i = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int j = 0; j < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++j) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

         List<CreateFlatWorldScreen.DetailsList.Entry> list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected(list.get(i));
         }

      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ObjectSelectionList.Entry<CreateFlatWorldScreen.DetailsList.Entry> {
         public void render(GuiGraphics p_281319_, int p_281943_, int p_283629_, int p_283315_, int p_282974_, int p_281870_, int p_283341_, int p_281639_, boolean p_282715_, float p_281937_) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - p_281943_ - 1);
            BlockState blockstate = flatlayerinfo.getBlockState();
            ItemStack itemstack = this.getDisplayItem(blockstate);
            this.blitSlot(p_281319_, p_283315_, p_283629_, itemstack);
            p_281319_.drawString(CreateFlatWorldScreen.this.font, itemstack.getHoverName(), p_283315_ + 18 + 5, p_283629_ + 3, 16777215, false);
            Component component;
            if (p_281943_ == 0) {
               component = Component.translatable("createWorld.customize.flat.layer.top", flatlayerinfo.getHeight());
            } else if (p_281943_ == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               component = Component.translatable("createWorld.customize.flat.layer.bottom", flatlayerinfo.getHeight());
            } else {
               component = Component.translatable("createWorld.customize.flat.layer", flatlayerinfo.getHeight());
            }

            p_281319_.drawString(CreateFlatWorldScreen.this.font, component, p_283315_ + 2 + 213 - CreateFlatWorldScreen.this.font.width(component), p_283629_ + 3, 16777215, false);
         }

         private ItemStack getDisplayItem(BlockState p_169294_) {
            Item item = p_169294_.getBlock().asItem();
            if (item == Items.AIR) {
               if (p_169294_.is(Blocks.WATER)) {
                  item = Items.WATER_BUCKET;
               } else if (p_169294_.is(Blocks.LAVA)) {
                  item = Items.LAVA_BUCKET;
               }
            }

            return new ItemStack(item);
         }

         public Component getNarration() {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - DetailsList.this.children().indexOf(this) - 1);
            ItemStack itemstack = this.getDisplayItem(flatlayerinfo.getBlockState());
            return (Component)(!itemstack.isEmpty() ? Component.translatable("narrator.select", itemstack.getHoverName()) : CommonComponents.EMPTY);
         }

         public boolean mouseClicked(double p_95868_, double p_95869_, int p_95870_) {
            if (p_95870_ == 0) {
               DetailsList.this.setSelected(this);
               return true;
            } else {
               return false;
            }
         }

         private void blitSlot(GuiGraphics p_281733_, int p_282373_, int p_282844_, ItemStack p_281263_) {
            this.blitSlotBg(p_281733_, p_282373_ + 1, p_282844_ + 1);
            if (!p_281263_.isEmpty()) {
               p_281733_.renderFakeItem(p_281263_, p_282373_ + 2, p_282844_ + 2);
            }

         }

         private void blitSlotBg(GuiGraphics p_282271_, int p_281324_, int p_283171_) {
            p_282271_.blit(CreateFlatWorldScreen.DetailsList.STATS_ICON_LOCATION, p_281324_, p_283171_, 0, 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}