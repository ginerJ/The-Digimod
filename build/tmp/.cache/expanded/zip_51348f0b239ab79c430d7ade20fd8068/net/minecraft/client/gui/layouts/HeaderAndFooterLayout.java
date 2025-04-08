package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeaderAndFooterLayout implements Layout {
   private static final int DEFAULT_HEADER_AND_FOOTER_HEIGHT = 36;
   private static final int DEFAULT_CONTENT_MARGIN_TOP = 30;
   private final FrameLayout headerFrame = new FrameLayout();
   private final FrameLayout footerFrame = new FrameLayout();
   private final FrameLayout contentsFrame = new FrameLayout();
   private final Screen screen;
   private int headerHeight;
   private int footerHeight;

   public HeaderAndFooterLayout(Screen p_270234_) {
      this(p_270234_, 36);
   }

   public HeaderAndFooterLayout(Screen p_270404_, int p_270984_) {
      this(p_270404_, p_270984_, p_270984_);
   }

   public HeaderAndFooterLayout(Screen p_270083_, int p_270134_, int p_270996_) {
      this.screen = p_270083_;
      this.headerHeight = p_270134_;
      this.footerHeight = p_270996_;
      this.headerFrame.defaultChildLayoutSetting().align(0.5F, 0.5F);
      this.footerFrame.defaultChildLayoutSetting().align(0.5F, 0.5F);
      this.contentsFrame.defaultChildLayoutSetting().align(0.5F, 0.0F).paddingTop(30);
   }

   public void setX(int p_270309_) {
   }

   public void setY(int p_270318_) {
   }

   public int getX() {
      return 0;
   }

   public int getY() {
      return 0;
   }

   public int getWidth() {
      return this.screen.width;
   }

   public int getHeight() {
      return this.screen.height;
   }

   public int getFooterHeight() {
      return this.footerHeight;
   }

   public void setFooterHeight(int p_270260_) {
      this.footerHeight = p_270260_;
   }

   public void setHeaderHeight(int p_270135_) {
      this.headerHeight = p_270135_;
   }

   public int getHeaderHeight() {
      return this.headerHeight;
   }

   public void visitChildren(Consumer<LayoutElement> p_270213_) {
      this.headerFrame.visitChildren(p_270213_);
      this.contentsFrame.visitChildren(p_270213_);
      this.footerFrame.visitChildren(p_270213_);
   }

   public void arrangeElements() {
      int i = this.getHeaderHeight();
      int j = this.getFooterHeight();
      this.headerFrame.setMinWidth(this.screen.width);
      this.headerFrame.setMinHeight(i);
      this.headerFrame.setPosition(0, 0);
      this.headerFrame.arrangeElements();
      this.footerFrame.setMinWidth(this.screen.width);
      this.footerFrame.setMinHeight(j);
      this.footerFrame.arrangeElements();
      this.footerFrame.setY(this.screen.height - j);
      this.contentsFrame.setMinWidth(this.screen.width);
      this.contentsFrame.setMinHeight(this.screen.height - i - j);
      this.contentsFrame.setPosition(0, i);
      this.contentsFrame.arrangeElements();
   }

   public <T extends LayoutElement> T addToHeader(T p_270636_) {
      return this.headerFrame.addChild(p_270636_);
   }

   public <T extends LayoutElement> T addToHeader(T p_270870_, LayoutSettings p_270558_) {
      return this.headerFrame.addChild(p_270870_, p_270558_);
   }

   public <T extends LayoutElement> T addToFooter(T p_270951_) {
      return this.footerFrame.addChild(p_270951_);
   }

   public <T extends LayoutElement> T addToFooter(T p_270362_, LayoutSettings p_270492_) {
      return this.footerFrame.addChild(p_270362_, p_270492_);
   }

   public <T extends LayoutElement> T addToContents(T p_270895_) {
      return this.contentsFrame.addChild(p_270895_);
   }

   public <T extends LayoutElement> T addToContents(T p_270611_, LayoutSettings p_270075_) {
      return this.contentsFrame.addChild(p_270611_, p_270075_);
   }

   public LayoutSettings newHeaderLayoutSettings() {
      return this.headerFrame.newChildLayoutSettings();
   }

   public LayoutSettings newContentLayoutSettings() {
      return this.contentsFrame.newChildLayoutSettings();
   }

   public LayoutSettings newFooterLayoutSettings() {
      return this.footerFrame.newChildLayoutSettings();
   }
}