package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   @Nullable
   private final Advancement parent;
   @Nullable
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map<String, Criterion> criteria;
   private final String[][] requirements;
   private final Set<Advancement> children = Sets.newLinkedHashSet();
   private final Component chatComponent;
   private final boolean sendsTelemetryEvent;

   public Advancement(ResourceLocation p_286878_, @Nullable Advancement p_286496_, @Nullable DisplayInfo p_286499_, AdvancementRewards p_286389_, Map<String, Criterion> p_286635_, String[][] p_286882_, boolean p_286478_) {
      this.id = p_286878_;
      this.display = p_286499_;
      this.criteria = ImmutableMap.copyOf(p_286635_);
      this.parent = p_286496_;
      this.rewards = p_286389_;
      this.requirements = p_286882_;
      this.sendsTelemetryEvent = p_286478_;
      if (p_286496_ != null) {
         p_286496_.addChild(this);
      }

      if (p_286499_ == null) {
         this.chatComponent = Component.literal(p_286878_.toString());
      } else {
         Component component = p_286499_.getTitle();
         ChatFormatting chatformatting = p_286499_.getFrame().getChatColor();
         Component component1 = ComponentUtils.mergeStyles(component.copy(), Style.EMPTY.withColor(chatformatting)).append("\n").append(p_286499_.getDescription());
         Component component2 = component.copy().withStyle((p_138316_) -> {
            return p_138316_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component1));
         });
         this.chatComponent = ComponentUtils.wrapInSquareBrackets(component2).withStyle(chatformatting);
      }

   }

   public Advancement.Builder deconstruct() {
      return new Advancement.Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements, this.sendsTelemetryEvent);
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   public Advancement getRoot() {
      return getRoot(this);
   }

   public static Advancement getRoot(Advancement p_265545_) {
      Advancement advancement = p_265545_;

      while(true) {
         Advancement advancement1 = advancement.getParent();
         if (advancement1 == null) {
            return advancement;
         }

         advancement = advancement1;
      }
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public boolean sendsTelemetryEvent() {
      return this.sendsTelemetryEvent;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + ", sendsTelemetryEvent=" + this.sendsTelemetryEvent + "}";
   }

   public Iterable<Advancement> getChildren() {
      return this.children;
   }

   public Map<String, Criterion> getCriteria() {
      return this.criteria;
   }

   public int getMaxCriteraRequired() {
      return this.requirements.length;
   }

   public void addChild(Advancement p_138318_) {
      this.children.add(p_138318_);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean equals(Object p_138324_) {
      if (this == p_138324_) {
         return true;
      } else if (!(p_138324_ instanceof Advancement)) {
         return false;
      } else {
         Advancement advancement = (Advancement)p_138324_;
         return this.id.equals(advancement.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String[][] getRequirements() {
      return this.requirements;
   }

   public Component getChatComponent() {
      return this.chatComponent;
   }

   public static class Builder implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder {
      @Nullable
      private ResourceLocation parentId;
      @Nullable
      private Advancement parent;
      @Nullable
      private DisplayInfo display;
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
      @Nullable
      private String[][] requirements;
      private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;
      private final boolean sendsTelemetryEvent;

      Builder(@Nullable ResourceLocation p_286422_, @Nullable DisplayInfo p_286485_, AdvancementRewards p_286364_, Map<String, Criterion> p_286544_, String[][] p_286283_, boolean p_286626_) {
         this.parentId = p_286422_;
         this.display = p_286485_;
         this.rewards = p_286364_;
         this.criteria = p_286544_;
         this.requirements = p_286283_;
         this.sendsTelemetryEvent = p_286626_;
      }

      private Builder(boolean p_286780_) {
         this.sendsTelemetryEvent = p_286780_;
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder(true);
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder(false);
      }

      public Advancement.Builder parent(Advancement p_138399_) {
         this.parent = p_138399_;
         return this;
      }

      public Advancement.Builder parent(ResourceLocation p_138397_) {
         this.parentId = p_138397_;
         return this;
      }

      public Advancement.Builder display(ItemStack p_138363_, Component p_138364_, Component p_138365_, @Nullable ResourceLocation p_138366_, FrameType p_138367_, boolean p_138368_, boolean p_138369_, boolean p_138370_) {
         return this.display(new DisplayInfo(p_138363_, p_138364_, p_138365_, p_138366_, p_138367_, p_138368_, p_138369_, p_138370_));
      }

      public Advancement.Builder display(ItemLike p_138372_, Component p_138373_, Component p_138374_, @Nullable ResourceLocation p_138375_, FrameType p_138376_, boolean p_138377_, boolean p_138378_, boolean p_138379_) {
         return this.display(new DisplayInfo(new ItemStack(p_138372_.asItem()), p_138373_, p_138374_, p_138375_, p_138376_, p_138377_, p_138378_, p_138379_));
      }

      public Advancement.Builder display(DisplayInfo p_138359_) {
         this.display = p_138359_;
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder p_138355_) {
         return this.rewards(p_138355_.build());
      }

      public Advancement.Builder rewards(AdvancementRewards p_138357_) {
         this.rewards = p_138357_;
         return this;
      }

      public Advancement.Builder addCriterion(String p_138387_, CriterionTriggerInstance p_138388_) {
         return this.addCriterion(p_138387_, new Criterion(p_138388_));
      }

      public Advancement.Builder addCriterion(String p_138384_, Criterion p_138385_) {
         if (this.criteria.containsKey(p_138384_)) {
            throw new IllegalArgumentException("Duplicate criterion " + p_138384_);
         } else {
            this.criteria.put(p_138384_, p_138385_);
            return this;
         }
      }

      public Advancement.Builder requirements(RequirementsStrategy p_138361_) {
         this.requirementsStrategy = p_138361_;
         return this;
      }

      public Advancement.Builder requirements(String[][] p_143952_) {
         this.requirements = p_143952_;
         return this;
      }

      public boolean canBuild(Function<ResourceLocation, Advancement> p_138393_) {
         if (this.parentId == null) {
            return true;
         } else {
            if (this.parent == null) {
               this.parent = p_138393_.apply(this.parentId);
            }

            return this.parent != null;
         }
      }

      public Advancement build(ResourceLocation p_138404_) {
         if (!this.canBuild((p_138407_) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(p_138404_, this.parent, this.display, this.rewards, this.criteria, this.requirements, this.sendsTelemetryEvent);
         }
      }

      public Advancement save(Consumer<Advancement> p_138390_, String p_138391_) {
         Advancement advancement = this.build(new ResourceLocation(p_138391_));
         p_138390_.accept(advancement);
         return advancement;
      }

      public JsonObject serializeToJson() {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         JsonObject jsonobject = new JsonObject();
         if (this.parent != null) {
            jsonobject.addProperty("parent", this.parent.getId().toString());
         } else if (this.parentId != null) {
            jsonobject.addProperty("parent", this.parentId.toString());
         }

         if (this.display != null) {
            jsonobject.add("display", this.display.serializeToJson());
         }

         jsonobject.add("rewards", this.rewards.serializeToJson());
         JsonObject jsonobject1 = new JsonObject();

         for(Map.Entry<String, Criterion> entry : this.criteria.entrySet()) {
            jsonobject1.add(entry.getKey(), entry.getValue().serializeToJson());
         }

         jsonobject.add("criteria", jsonobject1);
         JsonArray jsonarray1 = new JsonArray();

         for(String[] astring : this.requirements) {
            JsonArray jsonarray = new JsonArray();

            for(String s : astring) {
               jsonarray.add(s);
            }

            jsonarray1.add(jsonarray);
         }

         jsonobject.add("requirements", jsonarray1);
         jsonobject.addProperty("sends_telemetry_event", this.sendsTelemetryEvent);
         return jsonobject;
      }

      public void serializeToNetwork(FriendlyByteBuf p_138395_) {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         p_138395_.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
         p_138395_.writeNullable(this.display, (p_214831_, p_214832_) -> {
            p_214832_.serializeToNetwork(p_214831_);
         });
         Criterion.serializeToNetwork(this.criteria, p_138395_);
         p_138395_.writeVarInt(this.requirements.length);

         for(String[] astring : this.requirements) {
            p_138395_.writeVarInt(astring.length);

            for(String s : astring) {
               p_138395_.writeUtf(s);
            }
         }

         p_138395_.writeBoolean(this.sendsTelemetryEvent);
      }

      public String toString() {
         return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + ", sends_telemetry_event=" + this.sendsTelemetryEvent + "}";
      }

      /** @deprecated Forge: use {@linkplain #fromJson(JsonObject, DeserializationContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext) overload with context}. */
      @Deprecated
      public static Advancement.Builder fromJson(JsonObject p_138381_, DeserializationContext p_138382_) {
         return fromJson(p_138381_, p_138382_, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
      }

      public static Advancement.Builder fromJson(JsonObject p_138381_, DeserializationContext p_138382_, net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
         if ((p_138381_ = net.minecraftforge.common.crafting.ConditionalAdvancement.processConditional(p_138381_, context)) == null) return null;
         ResourceLocation resourcelocation = p_138381_.has("parent") ? new ResourceLocation(GsonHelper.getAsString(p_138381_, "parent")) : null;
         DisplayInfo displayinfo = p_138381_.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(p_138381_, "display")) : null;
         AdvancementRewards advancementrewards = p_138381_.has("rewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(p_138381_, "rewards")) : AdvancementRewards.EMPTY;
         Map<String, Criterion> map = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(p_138381_, "criteria"), p_138382_);
         if (map.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_138381_, "requirements", new JsonArray());
            String[][] astring = new String[jsonarray.size()][];

            for(int i = 0; i < jsonarray.size(); ++i) {
               JsonArray jsonarray1 = GsonHelper.convertToJsonArray(jsonarray.get(i), "requirements[" + i + "]");
               astring[i] = new String[jsonarray1.size()];

               for(int j = 0; j < jsonarray1.size(); ++j) {
                  astring[i][j] = GsonHelper.convertToString(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
               }
            }

            if (astring.length == 0) {
               astring = new String[map.size()][];
               int k = 0;

               for(String s2 : map.keySet()) {
                  astring[k++] = new String[]{s2};
               }
            }

            for(String[] astring1 : astring) {
               if (astring1.length == 0 && map.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               for(String s : astring1) {
                  if (!map.containsKey(s)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + s + "'");
                  }
               }
            }

            for(String s1 : map.keySet()) {
               boolean flag1 = false;

               for(String[] astring2 : astring) {
                  if (ArrayUtils.contains(astring2, s1)) {
                     flag1 = true;
                     break;
                  }
               }

               if (!flag1) {
                  throw new JsonSyntaxException("Criterion '" + s1 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
               }
            }

            boolean flag = GsonHelper.getAsBoolean(p_138381_, "sends_telemetry_event", false);
            return new Advancement.Builder(resourcelocation, displayinfo, advancementrewards, map, astring, flag);
         }
      }

      public static Advancement.Builder fromNetwork(FriendlyByteBuf p_138402_) {
         ResourceLocation resourcelocation = p_138402_.readNullable(FriendlyByteBuf::readResourceLocation);
         DisplayInfo displayinfo = p_138402_.readNullable(DisplayInfo::fromNetwork);
         Map<String, Criterion> map = Criterion.criteriaFromNetwork(p_138402_);
         String[][] astring = new String[p_138402_.readVarInt()][];

         for(int i = 0; i < astring.length; ++i) {
            astring[i] = new String[p_138402_.readVarInt()];

            for(int j = 0; j < astring[i].length; ++j) {
               astring[i][j] = p_138402_.readUtf();
            }
         }

         boolean flag = p_138402_.readBoolean();
         return new Advancement.Builder(resourcelocation, displayinfo, AdvancementRewards.EMPTY, map, astring, flag);
      }

      public Map<String, Criterion> getCriteria() {
         return this.criteria;
      }
   }
}
