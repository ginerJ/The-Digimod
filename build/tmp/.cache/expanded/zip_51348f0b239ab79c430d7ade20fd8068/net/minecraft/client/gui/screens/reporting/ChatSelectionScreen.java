package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatSelectionScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.chatSelection.title");
   private static final Component CONTEXT_INFO = Component.translatable("gui.chatSelection.context").withStyle(ChatFormatting.GRAY);
   @Nullable
   private final Screen lastScreen;
   private final ReportingContext reportingContext;
   private Button confirmSelectedButton;
   private MultiLineLabel contextInfoLabel;
   @Nullable
   private ChatSelectionScreen.ChatSelectionList chatSelectionList;
   final ChatReportBuilder report;
   private final Consumer<ChatReportBuilder> onSelected;
   private ChatSelectionLogFiller chatLogFiller;

   public ChatSelectionScreen(@Nullable Screen p_239090_, ReportingContext p_239091_, ChatReportBuilder p_239092_, Consumer<ChatReportBuilder> p_239093_) {
      super(TITLE);
      this.lastScreen = p_239090_;
      this.reportingContext = p_239091_;
      this.report = p_239092_.copy();
      this.onSelected = p_239093_;
   }

   protected void init() {
      this.chatLogFiller = new ChatSelectionLogFiller(this.reportingContext, this::canReport);
      this.contextInfoLabel = MultiLineLabel.create(this.font, CONTEXT_INFO, this.width - 16);
      this.chatSelectionList = new ChatSelectionScreen.ChatSelectionList(this.minecraft, (this.contextInfoLabel.getLineCount() + 1) * 9);
      this.chatSelectionList.setRenderBackground(false);
      this.addWidget(this.chatSelectionList);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_239860_) -> {
         this.onClose();
      }).bounds(this.width / 2 - 155, this.height - 32, 150, 20).build());
      this.confirmSelectedButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_239591_) -> {
         this.onSelected.accept(this.report);
         this.onClose();
      }).bounds(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
      this.updateConfirmSelectedButton();
      this.extendLog();
      this.chatSelectionList.setScrollAmount((double)this.chatSelectionList.getMaxScroll());
   }

   private boolean canReport(LoggedChatMessage p_242240_) {
      return p_242240_.canReport(this.report.reportedProfileId());
   }

   private void extendLog() {
      int i = this.chatSelectionList.getMaxVisibleEntries();
      this.chatLogFiller.fillNextPage(i, this.chatSelectionList);
   }

   void onReachedScrollTop() {
      this.extendLog();
   }

   void updateConfirmSelectedButton() {
      this.confirmSelectedButton.active = !this.report.reportedMessages().isEmpty();
   }

   public void render(GuiGraphics p_282899_, int p_239287_, int p_239288_, float p_239289_) {
      this.renderBackground(p_282899_);
      this.chatSelectionList.render(p_282899_, p_239287_, p_239288_, p_239289_);
      p_282899_.drawCenteredString(this.font, this.title, this.width / 2, 16, 16777215);
      AbuseReportLimits abusereportlimits = this.reportingContext.sender().reportLimits();
      int i = this.report.reportedMessages().size();
      int j = abusereportlimits.maxReportedMessageCount();
      Component component = Component.translatable("gui.chatSelection.selected", i, j);
      p_282899_.drawCenteredString(this.font, component, this.width / 2, 16 + 9 * 3 / 2, 10526880);
      this.contextInfoLabel.renderCentered(p_282899_, this.width / 2, this.chatSelectionList.getFooterTop());
      super.render(p_282899_, p_239287_, p_239288_, p_239289_);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), CONTEXT_INFO);
   }

   @OnlyIn(Dist.CLIENT)
   public class ChatSelectionList extends ObjectSelectionList<ChatSelectionScreen.ChatSelectionList.Entry> implements ChatSelectionLogFiller.Output {
      @Nullable
      private ChatSelectionScreen.ChatSelectionList.Heading previousHeading;

      public ChatSelectionList(Minecraft p_239060_, int p_239061_) {
         super(p_239060_, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height, 40, ChatSelectionScreen.this.height - 40 - p_239061_, 16);
      }

      public void setScrollAmount(double p_239021_) {
         double d0 = this.getScrollAmount();
         super.setScrollAmount(p_239021_);
         if ((float)this.getMaxScroll() > 1.0E-5F && p_239021_ <= (double)1.0E-5F && !Mth.equal(p_239021_, d0)) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public void acceptMessage(int p_242846_, LoggedChatMessage.Player p_242909_) {
         boolean flag = p_242909_.canReport(ChatSelectionScreen.this.report.reportedProfileId());
         ChatTrustLevel chattrustlevel = p_242909_.trustLevel();
         GuiMessageTag guimessagetag = chattrustlevel.createTag(p_242909_.message());
         ChatSelectionScreen.ChatSelectionList.Entry chatselectionscreen$chatselectionlist$entry = new ChatSelectionScreen.ChatSelectionList.MessageEntry(p_242846_, p_242909_.toContentComponent(), p_242909_.toNarrationComponent(), guimessagetag, flag, true);
         this.addEntryToTop(chatselectionscreen$chatselectionlist$entry);
         this.updateHeading(p_242909_, flag);
      }

      private void updateHeading(LoggedChatMessage.Player p_242229_, boolean p_240019_) {
         ChatSelectionScreen.ChatSelectionList.Entry chatselectionscreen$chatselectionlist$entry = new ChatSelectionScreen.ChatSelectionList.MessageHeadingEntry(p_242229_.profile(), p_242229_.toHeadingComponent(), p_240019_);
         this.addEntryToTop(chatselectionscreen$chatselectionlist$entry);
         ChatSelectionScreen.ChatSelectionList.Heading chatselectionscreen$chatselectionlist$heading = new ChatSelectionScreen.ChatSelectionList.Heading(p_242229_.profileId(), chatselectionscreen$chatselectionlist$entry);
         if (this.previousHeading != null && this.previousHeading.canCombine(chatselectionscreen$chatselectionlist$heading)) {
            this.removeEntryFromTop(this.previousHeading.entry());
         }

         this.previousHeading = chatselectionscreen$chatselectionlist$heading;
      }

      public void acceptDivider(Component p_239876_) {
         this.addEntryToTop(new ChatSelectionScreen.ChatSelectionList.PaddingEntry());
         this.addEntryToTop(new ChatSelectionScreen.ChatSelectionList.DividerEntry(p_239876_));
         this.addEntryToTop(new ChatSelectionScreen.ChatSelectionList.PaddingEntry());
         this.previousHeading = null;
      }

      protected int getScrollbarPosition() {
         return (this.width + this.getRowWidth()) / 2;
      }

      public int getRowWidth() {
         return Math.min(350, this.width - 50);
      }

      public int getMaxVisibleEntries() {
         return Mth.positiveCeilDiv(this.y1 - this.y0, this.itemHeight);
      }

      protected void renderItem(GuiGraphics p_281532_, int p_239775_, int p_239776_, float p_239777_, int p_239778_, int p_239779_, int p_239780_, int p_239781_, int p_239782_) {
         ChatSelectionScreen.ChatSelectionList.Entry chatselectionscreen$chatselectionlist$entry = this.getEntry(p_239778_);
         if (this.shouldHighlightEntry(chatselectionscreen$chatselectionlist$entry)) {
            boolean flag = this.getSelected() == chatselectionscreen$chatselectionlist$entry;
            int i = this.isFocused() && flag ? -1 : -8355712;
            this.renderSelection(p_281532_, p_239780_, p_239781_, p_239782_, i, -16777216);
         }

         chatselectionscreen$chatselectionlist$entry.render(p_281532_, p_239778_, p_239780_, p_239779_, p_239781_, p_239782_, p_239775_, p_239776_, this.getHovered() == chatselectionscreen$chatselectionlist$entry, p_239777_);
      }

      private boolean shouldHighlightEntry(ChatSelectionScreen.ChatSelectionList.Entry p_240327_) {
         if (p_240327_.canSelect()) {
            boolean flag = this.getSelected() == p_240327_;
            boolean flag1 = this.getSelected() == null;
            boolean flag2 = this.getHovered() == p_240327_;
            return flag || flag1 && flag2 && p_240327_.canReport();
         } else {
            return false;
         }
      }

      @Nullable
      protected ChatSelectionScreen.ChatSelectionList.Entry nextEntry(ScreenDirection p_265203_) {
         return this.nextEntry(p_265203_, ChatSelectionScreen.ChatSelectionList.Entry::canSelect);
      }

      public void setSelected(@Nullable ChatSelectionScreen.ChatSelectionList.Entry p_265249_) {
         super.setSelected(p_265249_);
         ChatSelectionScreen.ChatSelectionList.Entry chatselectionscreen$chatselectionlist$entry = this.nextEntry(ScreenDirection.UP);
         if (chatselectionscreen$chatselectionlist$entry == null) {
            ChatSelectionScreen.this.onReachedScrollTop();
         }

      }

      public boolean keyPressed(int p_239322_, int p_239323_, int p_239324_) {
         ChatSelectionScreen.ChatSelectionList.Entry chatselectionscreen$chatselectionlist$entry = this.getSelected();
         return chatselectionscreen$chatselectionlist$entry != null && chatselectionscreen$chatselectionlist$entry.keyPressed(p_239322_, p_239323_, p_239324_) ? true : super.keyPressed(p_239322_, p_239323_, p_239324_);
      }

      public int getFooterTop() {
         return this.y1 + 9;
      }

      @OnlyIn(Dist.CLIENT)
      public class DividerEntry extends ChatSelectionScreen.ChatSelectionList.Entry {
         private static final int COLOR = -6250336;
         private final Component text;

         public DividerEntry(Component p_239672_) {
            this.text = p_239672_;
         }

         public void render(GuiGraphics p_283635_, int p_239815_, int p_239816_, int p_239817_, int p_239818_, int p_239819_, int p_239820_, int p_239821_, boolean p_239822_, float p_239823_) {
            int i = p_239816_ + p_239819_ / 2;
            int j = p_239817_ + p_239818_ - 8;
            int k = ChatSelectionScreen.this.font.width(this.text);
            int l = (p_239817_ + j - k) / 2;
            int i1 = i - 9 / 2;
            p_283635_.drawString(ChatSelectionScreen.this.font, this.text, l, i1, -6250336);
         }

         public Component getNarration() {
            return this.text;
         }
      }

      @OnlyIn(Dist.CLIENT)
      public abstract class Entry extends ObjectSelectionList.Entry<ChatSelectionScreen.ChatSelectionList.Entry> {
         public Component getNarration() {
            return CommonComponents.EMPTY;
         }

         public boolean isSelected() {
            return false;
         }

         public boolean canSelect() {
            return false;
         }

         public boolean canReport() {
            return this.canSelect();
         }
      }

      @OnlyIn(Dist.CLIENT)
      static record Heading(UUID sender, ChatSelectionScreen.ChatSelectionList.Entry entry) {
         public boolean canCombine(ChatSelectionScreen.ChatSelectionList.Heading p_239748_) {
            return p_239748_.sender.equals(this.sender);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public class MessageEntry extends ChatSelectionScreen.ChatSelectionList.Entry {
         private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
         private static final int CHECKMARK_WIDTH = 9;
         private static final int CHECKMARK_HEIGHT = 8;
         private static final int INDENT_AMOUNT = 11;
         private static final int TAG_MARGIN_LEFT = 4;
         private final int chatId;
         private final FormattedText text;
         private final Component narration;
         @Nullable
         private final List<FormattedCharSequence> hoverText;
         @Nullable
         private final GuiMessageTag.Icon tagIcon;
         @Nullable
         private final List<FormattedCharSequence> tagHoverText;
         private final boolean canReport;
         private final boolean playerMessage;

         public MessageEntry(int p_240650_, Component p_240525_, Component p_240539_, @Nullable GuiMessageTag p_240551_, boolean p_240596_, boolean p_240615_) {
            this.chatId = p_240650_;
            this.tagIcon = Optionull.map(p_240551_, GuiMessageTag::icon);
            this.tagHoverText = p_240551_ != null && p_240551_.text() != null ? ChatSelectionScreen.this.font.split(p_240551_.text(), ChatSelectionList.this.getRowWidth()) : null;
            this.canReport = p_240596_;
            this.playerMessage = p_240615_;
            FormattedText formattedtext = ChatSelectionScreen.this.font.substrByWidth(p_240525_, this.getMaximumTextWidth() - ChatSelectionScreen.this.font.width(CommonComponents.ELLIPSIS));
            if (p_240525_ != formattedtext) {
               this.text = FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS);
               this.hoverText = ChatSelectionScreen.this.font.split(p_240525_, ChatSelectionList.this.getRowWidth());
            } else {
               this.text = p_240525_;
               this.hoverText = null;
            }

            this.narration = p_240539_;
         }

         public void render(GuiGraphics p_281361_, int p_239596_, int p_239597_, int p_239598_, int p_239599_, int p_239600_, int p_239601_, int p_239602_, boolean p_239603_, float p_239604_) {
            if (this.isSelected() && this.canReport) {
               this.renderSelectedCheckmark(p_281361_, p_239597_, p_239598_, p_239600_);
            }

            int i = p_239598_ + this.getTextIndent();
            int j = p_239597_ + 1 + (p_239600_ - 9) / 2;
            p_281361_.drawString(ChatSelectionScreen.this.font, Language.getInstance().getVisualOrder(this.text), i, j, this.canReport ? -1 : -1593835521);
            if (this.hoverText != null && p_239603_) {
               ChatSelectionScreen.this.setTooltipForNextRenderPass(this.hoverText);
            }

            int k = ChatSelectionScreen.this.font.width(this.text);
            this.renderTag(p_281361_, i + k + 4, p_239597_, p_239600_, p_239601_, p_239602_);
         }

         private void renderTag(GuiGraphics p_281776_, int p_240566_, int p_240565_, int p_240581_, int p_240614_, int p_240612_) {
            if (this.tagIcon != null) {
               int i = p_240565_ + (p_240581_ - this.tagIcon.height) / 2;
               this.tagIcon.draw(p_281776_, p_240566_, i);
               if (this.tagHoverText != null && p_240614_ >= p_240566_ && p_240614_ <= p_240566_ + this.tagIcon.width && p_240612_ >= i && p_240612_ <= i + this.tagIcon.height) {
                  ChatSelectionScreen.this.setTooltipForNextRenderPass(this.tagHoverText);
               }
            }

         }

         private void renderSelectedCheckmark(GuiGraphics p_281342_, int p_281492_, int p_283046_, int p_283458_) {
            int i = p_281492_ + (p_283458_ - 8) / 2;
            RenderSystem.enableBlend();
            p_281342_.blit(CHECKMARK_TEXTURE, p_283046_, i, 0.0F, 0.0F, 9, 8, 9, 8);
            RenderSystem.disableBlend();
         }

         private int getMaximumTextWidth() {
            int i = this.tagIcon != null ? this.tagIcon.width + 4 : 0;
            return ChatSelectionList.this.getRowWidth() - this.getTextIndent() - 4 - i;
         }

         private int getTextIndent() {
            return this.playerMessage ? 11 : 0;
         }

         public Component getNarration() {
            return (Component)(this.isSelected() ? Component.translatable("narrator.select", this.narration) : this.narration);
         }

         public boolean mouseClicked(double p_239729_, double p_239730_, int p_239731_) {
            if (p_239731_ == 0) {
               ChatSelectionList.this.setSelected((ChatSelectionScreen.ChatSelectionList.Entry)null);
               return this.toggleReport();
            } else {
               return false;
            }
         }

         public boolean keyPressed(int p_239368_, int p_239369_, int p_239370_) {
            return CommonInputs.selected(p_239368_) ? this.toggleReport() : false;
         }

         public boolean isSelected() {
            return ChatSelectionScreen.this.report.isReported(this.chatId);
         }

         public boolean canSelect() {
            return true;
         }

         public boolean canReport() {
            return this.canReport;
         }

         private boolean toggleReport() {
            if (this.canReport) {
               ChatSelectionScreen.this.report.toggleReported(this.chatId);
               ChatSelectionScreen.this.updateConfirmSelectedButton();
               return true;
            } else {
               return false;
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public class MessageHeadingEntry extends ChatSelectionScreen.ChatSelectionList.Entry {
         private static final int FACE_SIZE = 12;
         private final Component heading;
         private final ResourceLocation skin;
         private final boolean canReport;

         public MessageHeadingEntry(GameProfile p_240080_, Component p_240081_, boolean p_240082_) {
            this.heading = p_240081_;
            this.canReport = p_240082_;
            this.skin = ChatSelectionList.this.minecraft.getSkinManager().getInsecureSkinLocation(p_240080_);
         }

         public void render(GuiGraphics p_281320_, int p_283177_, int p_282422_, int p_282017_, int p_282555_, int p_283255_, int p_283682_, int p_281582_, boolean p_282259_, float p_283561_) {
            int i = p_282017_ - 12 - 4;
            int j = p_282422_ + (p_283255_ - 12) / 2;
            PlayerFaceRenderer.draw(p_281320_, this.skin, i, j, 12);
            int k = p_282422_ + 1 + (p_283255_ - 9) / 2;
            p_281320_.drawString(ChatSelectionScreen.this.font, this.heading, p_282017_, k, this.canReport ? -1 : -1593835521);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public class PaddingEntry extends ChatSelectionScreen.ChatSelectionList.Entry {
         public void render(GuiGraphics p_282007_, int p_240110_, int p_240111_, int p_240112_, int p_240113_, int p_240114_, int p_240115_, int p_240116_, boolean p_240117_, float p_240118_) {
         }
      }
   }
}