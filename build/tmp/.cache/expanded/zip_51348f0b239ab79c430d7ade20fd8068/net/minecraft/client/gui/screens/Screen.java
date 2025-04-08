package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
   private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   protected final Component title;
   private final List<GuiEventListener> children = Lists.newArrayList();
   private final List<NarratableEntry> narratables = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   private boolean initialized;
   public int width;
   public int height;
   public final List<Renderable> renderables = Lists.newArrayList();
   protected Font font;
   @Nullable
   private URI clickedLink;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
   private static final long NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
   private long narrationSuppressTime = Long.MIN_VALUE;
   private long nextNarrationTime = Long.MAX_VALUE;
   @Nullable
   private NarratableEntry lastNarratable;
   @Nullable
   private Screen.DeferredTooltipRendering deferredTooltipRendering;
   protected final Executor screenExecutor = (p_289626_) -> {
      this.minecraft.execute(() -> {
         if (this.minecraft.screen == this) {
            p_289626_.run();
         }

      });
   };

   protected Screen(Component p_96550_) {
      this.title = p_96550_;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getNarrationMessage() {
      return this.getTitle();
   }

   public final void renderWithTooltip(GuiGraphics p_282345_, int p_283456_, int p_283586_, float p_282339_) {
      this.render(p_282345_, p_283456_, p_283586_, p_282339_);
      if (this.deferredTooltipRendering != null) {
         p_282345_.renderTooltip(this.font, this.deferredTooltipRendering.tooltip(), this.deferredTooltipRendering.positioner(), p_283456_, p_283586_);
         this.deferredTooltipRendering = null;
      }

   }

   public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
      for(Renderable renderable : this.renderables) {
         renderable.render(p_281549_, p_281550_, p_282878_, p_282465_);
      }

   }

   public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
      if (p_96552_ == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (super.keyPressed(p_96552_, p_96553_, p_96554_)) {
         return true;
      } else {
         Object object;
         switch (p_96552_) {
            case 258:
               object = this.createTabEvent();
               break;
            case 259:
            case 260:
            case 261:
            default:
               object = null;
               break;
            case 262:
               object = this.createArrowEvent(ScreenDirection.RIGHT);
               break;
            case 263:
               object = this.createArrowEvent(ScreenDirection.LEFT);
               break;
            case 264:
               object = this.createArrowEvent(ScreenDirection.DOWN);
               break;
            case 265:
               object = this.createArrowEvent(ScreenDirection.UP);
         }

         FocusNavigationEvent focusnavigationevent = (FocusNavigationEvent)object;
         if (focusnavigationevent != null) {
            ComponentPath componentpath = super.nextFocusPath(focusnavigationevent);
            if (componentpath == null && focusnavigationevent instanceof FocusNavigationEvent.TabNavigation) {
               this.clearFocus();
               componentpath = super.nextFocusPath(focusnavigationevent);
            }

            if (componentpath != null) {
               this.changeFocus(componentpath);
            }
         }

         return false;
      }
   }

   private FocusNavigationEvent.TabNavigation createTabEvent() {
      boolean flag = !hasShiftDown();
      return new FocusNavigationEvent.TabNavigation(flag);
   }

   private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection p_265049_) {
      return new FocusNavigationEvent.ArrowNavigation(p_265049_);
   }

   protected void setInitialFocus(GuiEventListener p_265756_) {
      ComponentPath componentpath = ComponentPath.path(this, p_265756_.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
      if (componentpath != null) {
         this.changeFocus(componentpath);
      }

   }

   private void clearFocus() {
      ComponentPath componentpath = this.getCurrentFocusPath();
      if (componentpath != null) {
         componentpath.applyFocus(false);
      }

   }

   @VisibleForTesting
   protected void changeFocus(ComponentPath p_265308_) {
      this.clearFocus();
      p_265308_.applyFocus(true);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.popGuiLayer();
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T p_169406_) {
      this.renderables.add(p_169406_);
      return this.addWidget(p_169406_);
   }

   protected <T extends Renderable> T addRenderableOnly(T p_254514_) {
      this.renderables.add(p_254514_);
      return p_254514_;
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T p_96625_) {
      this.children.add(p_96625_);
      this.narratables.add(p_96625_);
      return p_96625_;
   }

   protected void removeWidget(GuiEventListener p_169412_) {
      if (p_169412_ instanceof Renderable) {
         this.renderables.remove((Renderable)p_169412_);
      }

      if (p_169412_ instanceof NarratableEntry) {
         this.narratables.remove((NarratableEntry)p_169412_);
      }

      this.children.remove(p_169412_);
   }

   protected void clearWidgets() {
      this.renderables.clear();
      this.children.clear();
      this.narratables.clear();
   }

   public static List<Component> getTooltipFromItem(Minecraft p_281881_, ItemStack p_282833_) {
      return p_282833_.getTooltipLines(p_281881_.player, p_281881_.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   protected void insertText(String p_96587_, boolean p_96588_) {
   }

   public boolean handleComponentClicked(@Nullable Style p_96592_) {
      if (p_96592_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_96592_.getClickEvent();
         if (hasShiftDown()) {
            if (p_96592_.getInsertion() != null) {
               this.insertText(p_96592_.getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.options.chatLinks().get()) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.options.chatLinksPrompt().get()) {
                     this.clickedLink = uri;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, clickevent.getValue(), false));
                  } else {
                     this.openLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(SharedConstants.filterText(clickevent.getValue()), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               String s1 = SharedConstants.filterText(clickevent.getValue());
               if (s1.startsWith("/")) {
                  if (!this.minecraft.player.connection.sendUnsignedCommand(s1.substring(1))) {
                     LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", (Object)s1);
                  }
               } else {
                  LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", (Object)s1);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardHandler.setClipboard(clickevent.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", (Object)clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public final void init(Minecraft p_96607_, int p_96608_, int p_96609_) {
      this.minecraft = p_96607_;
      this.font = p_96607_.font;
      this.width = p_96608_;
      this.height = p_96609_;
      if (!this.initialized) {
         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Pre(this, this.children, this::addEventWidget, this::removeWidget))) {
         this.init();
         }
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Post(this, this.children, this::addEventWidget, this::removeWidget));
      } else {
         this.repositionElements();
      }

      this.initialized = true;
      this.triggerImmediateNarration(false);
      this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
   }

   protected void rebuildWidgets() {
      this.clearWidgets();
      this.clearFocus();
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Pre(this, this.children, this::addEventWidget, this::removeWidget))) {
      this.init();
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Init.Post(this, this.children, this::addEventWidget, this::removeWidget));
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void added() {
   }

   public void renderBackground(GuiGraphics p_283688_) {
      if (this.minecraft.level != null) {
         p_283688_.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, p_283688_));
      } else {
         this.renderDirtBackground(p_283688_);
      }

   }

   public void renderDirtBackground(GuiGraphics p_282281_) {
      p_282281_.setColor(0.25F, 0.25F, 0.25F, 1.0F);
      int i = 32;
      p_282281_.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
      p_282281_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, p_282281_));
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean p_96623_) {
      if (p_96623_) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI p_96590_) {
      Util.getPlatform().openUri(p_96590_);
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isCut(int p_96629_) {
      return p_96629_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int p_96631_) {
      return p_96631_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int p_96633_) {
      return p_96633_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int p_96635_) {
      return p_96635_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   protected void repositionElements() {
      this.rebuildWidgets();
   }

   public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
      this.width = p_96576_;
      this.height = p_96577_;
      this.repositionElements();
   }

   public static void wrapScreenError(Runnable p_96580_, String p_96581_, String p_96582_) {
      try {
         p_96580_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, p_96581_);
         CrashReportCategory crashreportcategory = crashreport.addCategory("Affected screen");
         crashreportcategory.setDetail("Screen name", () -> {
            return p_96582_;
         });
         throw new ReportedException(crashreport);
      }
   }

   protected boolean isValidCharacterForName(String p_96584_, char p_96585_, int p_96586_) {
      int i = p_96584_.indexOf(58);
      int j = p_96584_.indexOf(47);
      if (p_96585_ == ':') {
         return (j == -1 || p_96586_ <= j) && i == -1;
      } else if (p_96585_ == '/') {
         return p_96586_ > i;
      } else {
         return p_96585_ == '_' || p_96585_ == '-' || p_96585_ >= 'a' && p_96585_ <= 'z' || p_96585_ >= '0' && p_96585_ <= '9' || p_96585_ == '.';
      }
   }

   public boolean isMouseOver(double p_96595_, double p_96596_) {
      return true;
   }

   public void onFilesDrop(List<Path> p_96591_) {
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   private void scheduleNarration(long p_169381_, boolean p_169382_) {
      this.nextNarrationTime = Util.getMillis() + p_169381_;
      if (p_169382_) {
         this.narrationSuppressTime = Long.MIN_VALUE;
      }

   }

   private void suppressNarration(long p_169379_) {
      this.narrationSuppressTime = Util.getMillis() + p_169379_;
   }

   public void afterMouseMove() {
      this.scheduleNarration(750L, false);
   }

   public void afterMouseAction() {
      this.scheduleNarration(200L, true);
   }

   public void afterKeyboardAction() {
      this.scheduleNarration(200L, true);
   }

   private boolean shouldRunNarration() {
      return this.minecraft.getNarrator().isActive();
   }

   public void handleDelayedNarration() {
      if (this.shouldRunNarration()) {
         long i = Util.getMillis();
         if (i > this.nextNarrationTime && i > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = Long.MAX_VALUE;
         }
      }

   }

   public void triggerImmediateNarration(boolean p_169408_) {
      if (this.shouldRunNarration()) {
         this.runNarration(p_169408_);
      }

   }

   private void runNarration(boolean p_169410_) {
      this.narrationState.update(this::updateNarrationState);
      String s = this.narrationState.collectNarrationText(!p_169410_);
      if (!s.isEmpty()) {
         this.minecraft.getNarrator().sayNow(s);
      }

   }

   protected boolean shouldNarrateNavigation() {
      return true;
   }

   protected void updateNarrationState(NarrationElementOutput p_169396_) {
      p_169396_.add(NarratedElementType.TITLE, this.getNarrationMessage());
      if (this.shouldNarrateNavigation()) {
         p_169396_.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

      this.updateNarratedWidget(p_169396_);
   }

   protected void updateNarratedWidget(NarrationElementOutput p_169403_) {
      List<NarratableEntry> list = this.narratables.stream().filter(NarratableEntry::isActive).collect(Collectors.toList());
      Collections.sort(list, Comparator.comparingInt(TabOrderedElement::getTabOrderGroup));
      Screen.NarratableSearchResult screen$narratablesearchresult = findNarratableWidget(list, this.lastNarratable);
      if (screen$narratablesearchresult != null) {
         if (screen$narratablesearchresult.priority.isTerminal()) {
            this.lastNarratable = screen$narratablesearchresult.entry;
         }

         if (list.size() > 1) {
            p_169403_.add(NarratedElementType.POSITION, Component.translatable("narrator.position.screen", screen$narratablesearchresult.index + 1, list.size()));
            if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               p_169403_.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
            }
         }

         screen$narratablesearchresult.entry.updateNarration(p_169403_.nest());
      }

   }

   @Nullable
   public static Screen.NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> p_169401_, @Nullable NarratableEntry p_169402_) {
      Screen.NarratableSearchResult screen$narratablesearchresult = null;
      Screen.NarratableSearchResult screen$narratablesearchresult1 = null;
      int i = 0;

      for(int j = p_169401_.size(); i < j; ++i) {
         NarratableEntry narratableentry = p_169401_.get(i);
         NarratableEntry.NarrationPriority narratableentry$narrationpriority = narratableentry.narrationPriority();
         if (narratableentry$narrationpriority.isTerminal()) {
            if (narratableentry != p_169402_) {
               return new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
            }

            screen$narratablesearchresult1 = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
         } else if (narratableentry$narrationpriority.compareTo(screen$narratablesearchresult != null ? screen$narratablesearchresult.priority : NarratableEntry.NarrationPriority.NONE) > 0) {
            screen$narratablesearchresult = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
         }
      }

      return screen$narratablesearchresult != null ? screen$narratablesearchresult : screen$narratablesearchresult1;
   }

   public void narrationEnabled() {
      this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> p_259937_) {
      this.setTooltipForNextRenderPass(p_259937_, DefaultTooltipPositioner.INSTANCE, true);
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> p_262939_, ClientTooltipPositioner p_263078_, boolean p_263107_) {
      if (this.deferredTooltipRendering == null || p_263107_) {
         this.deferredTooltipRendering = new Screen.DeferredTooltipRendering(p_262939_, p_263078_);
      }

   }

   protected void setTooltipForNextRenderPass(Component p_259986_) {
      this.setTooltipForNextRenderPass(Tooltip.splitTooltip(this.minecraft, p_259986_));
   }

   public void setTooltipForNextRenderPass(Tooltip p_262992_, ClientTooltipPositioner p_262980_, boolean p_262988_) {
      this.setTooltipForNextRenderPass(p_262992_.toCharSequence(this.minecraft), p_262980_, p_262988_);
   }

   protected static void hideWidgets(AbstractWidget... p_202377_) {
      for(AbstractWidget abstractwidget : p_202377_) {
         abstractwidget.visible = false;
      }

   }

   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(0, 0, this.width, this.height);
   }

   @Nullable
   public Music getBackgroundMusic() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   static record DeferredTooltipRendering(List<FormattedCharSequence> tooltip, ClientTooltipPositioner positioner) {
   }

   @OnlyIn(Dist.CLIENT)
   public static class NarratableSearchResult {
      public final NarratableEntry entry;
      public final int index;
      public final NarratableEntry.NarrationPriority priority;

      public NarratableSearchResult(NarratableEntry p_169424_, int p_169425_, NarratableEntry.NarrationPriority p_169426_) {
         this.entry = p_169424_;
         this.index = p_169425_;
         this.priority = p_169426_;
      }
   }

   private void addEventWidget(GuiEventListener b) {
      if (b instanceof Renderable r)
         this.renderables.add(r);
      if (b instanceof NarratableEntry ne)
         this.narratables.add(ne);
      children.add(b);
   }
}
