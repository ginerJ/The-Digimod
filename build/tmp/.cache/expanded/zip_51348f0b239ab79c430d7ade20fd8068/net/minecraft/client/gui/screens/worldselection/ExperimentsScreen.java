package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperimentsScreen extends Screen {
   private static final int MAIN_CONTENT_WIDTH = 310;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final PackRepository packRepository;
   private final Consumer<PackRepository> output;
   private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap<>();

   protected ExperimentsScreen(Screen p_270165_, PackRepository p_270308_, Consumer<PackRepository> p_270392_) {
      super(Component.translatable("experiments_screen.title"));
      this.parent = p_270165_;
      this.packRepository = p_270308_;
      this.output = p_270392_;

      for(Pack pack : p_270308_.getAvailablePacks()) {
         if (pack.getPackSource() == PackSource.FEATURE) {
            this.packs.put(pack, p_270308_.getSelectedPacks().contains(pack));
         }
      }

   }

   protected void init() {
      this.layout.addToHeader(new StringWidget(Component.translatable("selectWorld.experiments"), this.font));
      GridLayout.RowHelper gridlayout$rowhelper = this.layout.addToContents(new GridLayout()).createRowHelper(1);
      gridlayout$rowhelper.addChild((new MultiLineTextWidget(Component.translatable("selectWorld.experiments.info").withStyle(ChatFormatting.RED), this.font)).setMaxWidth(310), gridlayout$rowhelper.newCellSettings().paddingBottom(15));
      SwitchGrid.Builder switchgrid$builder = SwitchGrid.builder(310).withInfoUnderneath(2, true).withRowSpacing(4);
      this.packs.forEach((p_270880_, p_270874_) -> {
         switchgrid$builder.addSwitch(getHumanReadableTitle(p_270880_), () -> {
            return this.packs.getBoolean(p_270880_);
         }, (p_270491_) -> {
            this.packs.put(p_270880_, p_270491_.booleanValue());
         }).withInfo(p_270880_.getDescription());
      });
      switchgrid$builder.build(gridlayout$rowhelper::addChild);
      GridLayout.RowHelper gridlayout$rowhelper1 = this.layout.addToFooter((new GridLayout()).columnSpacing(10)).createRowHelper(2);
      gridlayout$rowhelper1.addChild(Button.builder(CommonComponents.GUI_DONE, (p_270336_) -> {
         this.onDone();
      }).build());
      gridlayout$rowhelper1.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_274702_) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((p_270313_) -> {
         AbstractWidget abstractwidget = this.addRenderableWidget(p_270313_);
      });
      this.repositionElements();
   }

   private static Component getHumanReadableTitle(Pack p_270861_) {
      String s = "dataPack." + p_270861_.getId() + ".name";
      return (Component)(I18n.exists(s) ? Component.translatable(s) : p_270861_.getTitle());
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private void onDone() {
      List<Pack> list = new ArrayList<>(this.packRepository.getSelectedPacks());
      List<Pack> list1 = new ArrayList<>();
      this.packs.forEach((p_270540_, p_270780_) -> {
         list.remove(p_270540_);
         if (p_270780_) {
            list1.add(p_270540_);
         }

      });
      list.addAll(Lists.reverse(list1));
      this.packRepository.setSelected(list.stream().map(Pack::getId).toList());
      this.output.accept(this.packRepository);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void render(GuiGraphics p_283515_, int p_283170_, int p_283248_, float p_283106_) {
      this.renderBackground(p_283515_);
      p_283515_.setColor(0.125F, 0.125F, 0.125F, 1.0F);
      int i = 32;
      p_283515_.blit(BACKGROUND_LOCATION, 0, this.layout.getHeaderHeight(), 0.0F, 0.0F, this.width, this.height - this.layout.getHeaderHeight() - this.layout.getFooterHeight(), 32, 32);
      p_283515_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      super.render(p_283515_, p_283170_, p_283248_, p_283106_);
   }
}