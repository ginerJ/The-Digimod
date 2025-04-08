package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditGameRulesScreen extends Screen {
   private final Consumer<Optional<GameRules>> exitCallback;
   private EditGameRulesScreen.RuleList rules;
   private final Set<EditGameRulesScreen.RuleEntry> invalidEntries = Sets.newHashSet();
   private Button doneButton;
   @Nullable
   private List<FormattedCharSequence> tooltip;
   private final GameRules gameRules;

   public EditGameRulesScreen(GameRules p_101051_, Consumer<Optional<GameRules>> p_101052_) {
      super(Component.translatable("editGamerule.title"));
      this.gameRules = p_101051_;
      this.exitCallback = p_101052_;
   }

   protected void init() {
      this.rules = new EditGameRulesScreen.RuleList(this.gameRules);
      this.addWidget(this.rules);
      GridLayout.RowHelper gridlayout$rowhelper = (new GridLayout()).columnSpacing(10).createRowHelper(2);
      this.doneButton = gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_DONE, (p_101059_) -> {
         this.exitCallback.accept(Optional.of(this.gameRules));
      }).build());
      gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_101073_) -> {
         this.exitCallback.accept(Optional.empty());
      }).build());
      gridlayout$rowhelper.getGrid().visitWidgets((p_267855_) -> {
         AbstractWidget abstractwidget = this.addRenderableWidget(p_267855_);
      });
      gridlayout$rowhelper.getGrid().setPosition(this.width / 2 - 155, this.height - 28);
      gridlayout$rowhelper.getGrid().arrangeElements();
   }

   public void onClose() {
      this.exitCallback.accept(Optional.empty());
   }

   public void render(GuiGraphics p_282252_, int p_281351_, int p_282537_, float p_281589_) {
      this.tooltip = null;
      this.rules.render(p_282252_, p_281351_, p_282537_, p_281589_);
      p_282252_.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_282252_, p_281351_, p_282537_, p_281589_);
   }

   private void updateDoneButton() {
      this.doneButton.active = this.invalidEntries.isEmpty();
   }

   void markInvalid(EditGameRulesScreen.RuleEntry p_101061_) {
      this.invalidEntries.add(p_101061_);
      this.updateDoneButton();
   }

   void clearInvalid(EditGameRulesScreen.RuleEntry p_101075_) {
      this.invalidEntries.remove(p_101075_);
      this.updateDoneButton();
   }

   @OnlyIn(Dist.CLIENT)
   public class BooleanRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final CycleButton<Boolean> checkbox;

      public BooleanRuleEntry(Component p_101101_, List<FormattedCharSequence> p_101102_, String p_101103_, GameRules.BooleanValue p_101104_) {
         super(p_101102_, p_101101_);
         this.checkbox = CycleButton.onOffBuilder(p_101104_.get()).displayOnlyValue().withCustomNarration((p_170219_) -> {
            return p_170219_.createDefaultNarrationMessage().append("\n").append(p_101103_);
         }).create(10, 5, 44, 20, p_101101_, (p_170215_, p_170216_) -> {
            p_101104_.set(p_170216_, (MinecraftServer)null);
         });
         this.children.add(this.checkbox);
      }

      public void render(GuiGraphics p_281587_, int p_281471_, int p_281257_, int p_282541_, int p_282993_, int p_283543_, int p_281322_, int p_282930_, boolean p_283227_, float p_283364_) {
         this.renderLabel(p_281587_, p_281257_, p_282541_);
         this.checkbox.setX(p_282541_ + p_282993_ - 45);
         this.checkbox.setY(p_281257_);
         this.checkbox.render(p_281587_, p_281322_, p_282930_, p_283364_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryRuleEntry extends EditGameRulesScreen.RuleEntry {
      final Component label;

      public CategoryRuleEntry(Component p_101141_) {
         super((List<FormattedCharSequence>)null);
         this.label = p_101141_;
      }

      public void render(GuiGraphics p_283335_, int p_283214_, int p_283476_, int p_281365_, int p_281817_, int p_283006_, int p_282893_, int p_282500_, boolean p_283421_, float p_282445_) {
         p_283335_.drawCenteredString(EditGameRulesScreen.this.minecraft.font, this.label, p_281365_ + p_281817_ / 2, p_283476_ + 5, 16777215);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput p_170225_) {
               p_170225_.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
            }
         });
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   interface EntryFactory<T extends GameRules.Value<T>> {
      EditGameRulesScreen.RuleEntry create(Component p_101155_, List<FormattedCharSequence> p_101156_, String p_101157_, T p_101158_);
   }

   @OnlyIn(Dist.CLIENT)
   public abstract class GameRuleEntry extends EditGameRulesScreen.RuleEntry {
      private final List<FormattedCharSequence> label;
      protected final List<AbstractWidget> children = Lists.newArrayList();

      public GameRuleEntry(@Nullable List<FormattedCharSequence> p_101164_, Component p_101165_) {
         super(p_101164_);
         this.label = EditGameRulesScreen.this.minecraft.font.split(p_101165_, 175);
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      protected void renderLabel(GuiGraphics p_282711_, int p_281539_, int p_281414_) {
         if (this.label.size() == 1) {
            p_282711_.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(0), p_281414_, p_281539_ + 5, 16777215, false);
         } else if (this.label.size() >= 2) {
            p_282711_.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(0), p_281414_, p_281539_, 16777215, false);
            p_282711_.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(1), p_281414_, p_281539_ + 10, 16777215, false);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public class IntegerRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final EditBox input;

      public IntegerRuleEntry(Component p_101175_, List<FormattedCharSequence> p_101176_, String p_101177_, GameRules.IntegerValue p_101178_) {
         super(p_101176_, p_101175_);
         this.input = new EditBox(EditGameRulesScreen.this.minecraft.font, 10, 5, 42, 20, p_101175_.copy().append("\n").append(p_101177_).append("\n"));
         this.input.setValue(Integer.toString(p_101178_.get()));
         this.input.setResponder((p_101181_) -> {
            if (p_101178_.tryDeserialize(p_101181_)) {
               this.input.setTextColor(14737632);
               EditGameRulesScreen.this.clearInvalid(this);
            } else {
               this.input.setTextColor(16711680);
               EditGameRulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.input);
      }

      public void render(GuiGraphics p_281756_, int p_281882_, int p_281876_, int p_283136_, int p_283044_, int p_282526_, int p_282433_, int p_281816_, boolean p_282227_, float p_281751_) {
         this.renderLabel(p_281756_, p_281876_, p_283136_);
         this.input.setX(p_283136_ + p_283044_ - 44);
         this.input.setY(p_281876_);
         this.input.render(p_281756_, p_282433_, p_281816_, p_281751_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class RuleEntry extends ContainerObjectSelectionList.Entry<EditGameRulesScreen.RuleEntry> {
      @Nullable
      final List<FormattedCharSequence> tooltip;

      public RuleEntry(@Nullable List<FormattedCharSequence> p_194062_) {
         this.tooltip = p_194062_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class RuleList extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {
      public RuleList(final GameRules p_101203_) {
         super(EditGameRulesScreen.this.minecraft, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
         final Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.RuleEntry>> map = Maps.newHashMap();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public void visitBoolean(GameRules.Key<GameRules.BooleanValue> p_101238_, GameRules.Type<GameRules.BooleanValue> p_101239_) {
               this.addEntry(p_101238_, (p_101228_, p_101229_, p_101230_, p_101231_) -> {
                  return EditGameRulesScreen.this.new BooleanRuleEntry(p_101228_, p_101229_, p_101230_, p_101231_);
               });
            }

            public void visitInteger(GameRules.Key<GameRules.IntegerValue> p_101241_, GameRules.Type<GameRules.IntegerValue> p_101242_) {
               this.addEntry(p_101241_, (p_101233_, p_101234_, p_101235_, p_101236_) -> {
                  return EditGameRulesScreen.this.new IntegerRuleEntry(p_101233_, p_101234_, p_101235_, p_101236_);
               });
            }

            private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> p_101225_, EditGameRulesScreen.EntryFactory<T> p_101226_) {
               Component component = Component.translatable(p_101225_.getDescriptionId());
               Component component1 = Component.literal(p_101225_.getId()).withStyle(ChatFormatting.YELLOW);
               T t = p_101203_.getRule(p_101225_);
               String s = t.serialize();
               Component component2 = Component.translatable("editGamerule.default", Component.literal(s)).withStyle(ChatFormatting.GRAY);
               String s1 = p_101225_.getDescriptionId() + ".description";
               List<FormattedCharSequence> list;
               String s2;
               if (I18n.exists(s1)) {
                  ImmutableList.Builder<FormattedCharSequence> builder = ImmutableList.<FormattedCharSequence>builder().add(component1.getVisualOrderText());
                  Component component3 = Component.translatable(s1);
                  EditGameRulesScreen.this.font.split(component3, 150).forEach(builder::add);
                  list = builder.add(component2.getVisualOrderText()).build();
                  s2 = component3.getString() + "\n" + component2.getString();
               } else {
                  list = ImmutableList.of(component1.getVisualOrderText(), component2.getVisualOrderText());
                  s2 = component2.getString();
               }

               map.computeIfAbsent(p_101225_.getCategory(), (p_101223_) -> {
                  return Maps.newHashMap();
               }).put(p_101225_, p_101226_.create(component, list, s2, t));
            }
         });
         map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((p_101210_) -> {
            this.addEntry(EditGameRulesScreen.this.new CategoryRuleEntry(Component.translatable(p_101210_.getKey().getDescriptionId()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
            p_101210_.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getId))).forEach((p_170229_) -> {
               this.addEntry(p_170229_.getValue());
            });
         });
      }

      public void render(GuiGraphics p_283604_, int p_281425_, int p_282248_, float p_281463_) {
         super.render(p_283604_, p_281425_, p_282248_, p_281463_);
         EditGameRulesScreen.RuleEntry editgamerulesscreen$ruleentry = this.getHovered();
         if (editgamerulesscreen$ruleentry != null && editgamerulesscreen$ruleentry.tooltip != null) {
            EditGameRulesScreen.this.setTooltipForNextRenderPass(editgamerulesscreen$ruleentry.tooltip);
         }

      }
   }
}