package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.slf4j.Logger;

public final class ItemStack extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> implements net.minecraftforge.common.extensions.IForgeItemStack {
   public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create((p_258963_) -> {
      return p_258963_.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem), Codec.INT.fieldOf("Count").forGetter(ItemStack::getCount), CompoundTag.CODEC.optionalFieldOf("tag").forGetter((p_281115_) -> {
         return Optional.ofNullable(p_281115_.getTag());
      })).apply(p_258963_, ItemStack::new);
   });
   @org.jetbrains.annotations.Nullable
   private final net.minecraft.core.Holder.Reference<Item> delegate;
   private CompoundTag capNBT;

   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Void)null);
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), (p_41704_) -> {
      p_41704_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   public static final String TAG_ENCH = "Enchantments";
   public static final String TAG_DISPLAY = "display";
   public static final String TAG_DISPLAY_NAME = "Name";
   public static final String TAG_LORE = "Lore";
   public static final String TAG_DAMAGE = "Damage";
   public static final String TAG_COLOR = "color";
   private static final String TAG_UNBREAKABLE = "Unbreakable";
   private static final String TAG_REPAIR_COST = "RepairCost";
   private static final String TAG_CAN_DESTROY_BLOCK_LIST = "CanDestroy";
   private static final String TAG_CAN_PLACE_ON_BLOCK_LIST = "CanPlaceOn";
   private static final String TAG_HIDE_FLAGS = "HideFlags";
   private static final Component DISABLED_ITEM_TOOLTIP = Component.translatable("item.disabled").withStyle(ChatFormatting.RED);
   private static final int DONT_HIDE_TOOLTIP = 0;
   private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
   private int count;
   private int popTime;
   /** @deprecated */
   @Deprecated
   @Nullable
   private final Item item;
   @Nullable
   private CompoundTag tag;
   @Nullable
   private Entity entityRepresentation;
   @Nullable
   private AdventureModeCheck adventureBreakCheck;
   @Nullable
   private AdventureModeCheck adventurePlaceCheck;

   public Optional<TooltipComponent> getTooltipImage() {
      return this.getItem().getTooltipImage(this);
   }

   public ItemStack(ItemLike p_41599_) {
      this(p_41599_, 1);
   }

   public ItemStack(Holder<Item> p_204116_) {
      this(p_204116_.value(), 1);
   }

   private ItemStack(ItemLike p_41604_, int p_41605_, Optional<CompoundTag> p_41606_) {
      this(p_41604_, p_41605_);
      p_41606_.ifPresent(this::setTag);
   }

   public ItemStack(Holder<Item> p_220155_, int p_220156_) {
      this(p_220155_.value(), p_220156_);
   }

   public ItemStack(ItemLike p_41601_, int p_41602_) { this(p_41601_, p_41602_, (CompoundTag) null); }
   public ItemStack(ItemLike p_41604_, int p_41605_, @Nullable CompoundTag p_41606_) {
      super(ItemStack.class, true);
      this.capNBT = p_41606_;
      this.item = p_41604_.asItem();
      this.delegate = net.minecraftforge.registries.ForgeRegistries.ITEMS.getDelegateOrThrow(p_41604_.asItem());
      this.count = p_41605_;
      this.forgeInit();
      if (this.item.isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

   }

   private ItemStack(@Nullable Void p_282703_) {
      super(ItemStack.class, true);
      this.item = null;
      this.delegate = null;
   }

   private ItemStack(CompoundTag p_41608_) {
      super(ItemStack.class, true);
      this.capNBT = p_41608_.contains("ForgeCaps") ? p_41608_.getCompound("ForgeCaps") : null;
      Item rawItem =
      this.item = BuiltInRegistries.ITEM.get(new ResourceLocation(p_41608_.getString("id")));
      this.delegate = net.minecraftforge.registries.ForgeRegistries.ITEMS.getDelegateOrThrow(rawItem);
      this.count = p_41608_.getByte("Count");
      if (p_41608_.contains("tag", 10)) {
         this.tag = p_41608_.getCompound("tag");
         this.getItem().verifyTagAfterLoad(this.tag);
      }
      this.forgeInit();

      if (this.getItem().isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

   }

   public static ItemStack of(CompoundTag p_41713_) {
      try {
         return new ItemStack(p_41713_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.debug("Tried to load invalid item: {}", p_41713_, runtimeexception);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      return this == EMPTY || this.count <= 0 || this.delegate.get() == Items.AIR;
   }

   public boolean isItemEnabled(FeatureFlagSet p_250869_) {
      return this.isEmpty() || this.getItem().isEnabled(p_250869_);
   }

   public ItemStack split(int p_41621_) {
      int i = Math.min(p_41621_, this.getCount());
      ItemStack itemstack = this.copyWithCount(i);
      this.shrink(i);
      return itemstack;
   }

   public ItemStack copyAndClear() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack itemstack = this.copy();
         this.setCount(0);
         return itemstack;
      }
   }

   public Item getItem() {
      return this.isEmpty() ? Items.AIR : this.delegate.get();
   }

   public Holder<Item> getItemHolder() {
      return this.getItem().builtInRegistryHolder();
   }

   public boolean is(TagKey<Item> p_204118_) {
      return this.getItem().builtInRegistryHolder().is(p_204118_);
   }

   public boolean is(Item p_150931_) {
      return this.getItem() == p_150931_;
   }

   public boolean is(Predicate<Holder<Item>> p_220168_) {
      return p_220168_.test(this.getItem().builtInRegistryHolder());
   }

   public boolean is(Holder<Item> p_220166_) {
      return this.getItem().builtInRegistryHolder() == p_220166_;
   }

   public Stream<TagKey<Item>> getTags() {
      return this.getItem().builtInRegistryHolder().tags();
   }

   public InteractionResult useOn(UseOnContext p_41662_) {
      if (!p_41662_.getLevel().isClientSide) return net.minecraftforge.common.ForgeHooks.onPlaceItemIntoWorld(p_41662_);
      return onItemUse(p_41662_, (c) -> getItem().useOn(p_41662_));
   }

   public InteractionResult onItemUseFirst(UseOnContext p_41662_) {
      return onItemUse(p_41662_, (c) -> getItem().onItemUseFirst(this, p_41662_));
   }

   private InteractionResult onItemUse(UseOnContext p_41662_, java.util.function.Function<UseOnContext, InteractionResult> callback) {
      Player player = p_41662_.getPlayer();
      BlockPos blockpos = p_41662_.getClickedPos();
      BlockInWorld blockinworld = new BlockInWorld(p_41662_.getLevel(), blockpos, false);
      if (player != null && !player.getAbilities().mayBuild && !this.hasAdventureModePlaceTagForBlock(p_41662_.getLevel().registryAccess().registryOrThrow(Registries.BLOCK), blockinworld)) {
         return InteractionResult.PASS;
      } else {
         Item item = this.getItem();
         InteractionResult interactionresult = callback.apply(p_41662_);
         if (player != null && interactionresult.shouldAwardStats()) {
            player.awardStat(Stats.ITEM_USED.get(item));
         }

         return interactionresult;
      }
   }

   public float getDestroySpeed(BlockState p_41692_) {
      return this.getItem().getDestroySpeed(this, p_41692_);
   }

   public InteractionResultHolder<ItemStack> use(Level p_41683_, Player p_41684_, InteractionHand p_41685_) {
      return this.getItem().use(p_41683_, p_41684_, p_41685_);
   }

   public ItemStack finishUsingItem(Level p_41672_, LivingEntity p_41673_) {
      return this.getItem().finishUsingItem(this, p_41672_, p_41673_);
   }

   public CompoundTag save(CompoundTag p_41740_) {
      ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.getItem());
      p_41740_.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      p_41740_.putByte("Count", (byte)this.count);
      if (this.tag != null) {
         p_41740_.put("tag", this.tag.copy());
      }

      CompoundTag cnbt = this.serializeCaps();
      if (cnbt != null && !cnbt.isEmpty()) {
         p_41740_.put("ForgeCaps", cnbt);
      }
      return p_41740_;
   }

   public int getMaxStackSize() {
      return this.getItem().getMaxStackSize(this);
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
   }

   public boolean isDamageableItem() {
      if (!this.isEmpty() && this.getItem().isDamageable(this)) {
         CompoundTag compoundtag = this.getTag();
         return compoundtag == null || !compoundtag.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageableItem() && getItem().isDamaged(this);
   }

   public int getDamageValue() {
      return this.getItem().getDamage(this);
   }

   public void setDamageValue(int p_41722_) {
      this.getItem().setDamage(this, p_41722_);
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage(this);
   }

   public boolean hurt(int p_220158_, RandomSource p_220159_, @Nullable ServerPlayer p_220160_) {
      if (!this.isDamageableItem()) {
         return false;
      } else {
         if (p_220158_ > 0) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int j = 0;

            for(int k = 0; i > 0 && k < p_220158_; ++k) {
               if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, i, p_220159_)) {
                  ++j;
               }
            }

            p_220158_ -= j;
            if (p_220158_ <= 0) {
               return false;
            }
         }

         if (p_220160_ != null && p_220158_ != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(p_220160_, this, this.getDamageValue() + p_220158_);
         }

         int l = this.getDamageValue() + p_220158_;
         this.setDamageValue(l);
         return l >= this.getMaxDamage();
      }
   }

   public <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_) {
      if (!p_41624_.level().isClientSide && (!(p_41624_ instanceof Player) || !((Player)p_41624_).getAbilities().instabuild)) {
         if (this.isDamageableItem()) {
            p_41623_ = this.getItem().damageItem(this, p_41623_, p_41624_, p_41625_);
            if (this.hurt(p_41623_, p_41624_.getRandom(), p_41624_ instanceof ServerPlayer ? (ServerPlayer)p_41624_ : null)) {
               p_41625_.accept(p_41624_);
               Item item = this.getItem();
               this.shrink(1);
               if (p_41624_ instanceof Player) {
                  ((Player)p_41624_).awardStat(Stats.ITEM_BROKEN.get(item));
               }

               this.setDamageValue(0);
            }

         }
      }
   }

   public boolean isBarVisible() {
      return this.getItem().isBarVisible(this);
   }

   public int getBarWidth() {
      return this.getItem().getBarWidth(this);
   }

   public int getBarColor() {
      return this.getItem().getBarColor(this);
   }

   public boolean overrideStackedOnOther(Slot p_150927_, ClickAction p_150928_, Player p_150929_) {
      return this.getItem().overrideStackedOnOther(this, p_150927_, p_150928_, p_150929_);
   }

   public boolean overrideOtherStackedOnMe(ItemStack p_150933_, Slot p_150934_, ClickAction p_150935_, Player p_150936_, SlotAccess p_150937_) {
      return this.getItem().overrideOtherStackedOnMe(this, p_150933_, p_150934_, p_150935_, p_150936_, p_150937_);
   }

   public void hurtEnemy(LivingEntity p_41641_, Player p_41642_) {
      Item item = this.getItem();
      if (item.hurtEnemy(this, p_41641_, p_41642_)) {
         p_41642_.awardStat(Stats.ITEM_USED.get(item));
      }

   }

   public void mineBlock(Level p_41687_, BlockState p_41688_, BlockPos p_41689_, Player p_41690_) {
      Item item = this.getItem();
      if (item.mineBlock(this, p_41687_, p_41688_, p_41689_, p_41690_)) {
         p_41690_.awardStat(Stats.ITEM_USED.get(item));
      }

   }

   public boolean isCorrectToolForDrops(BlockState p_41736_) {
      return this.getItem().isCorrectToolForDrops(this, p_41736_);
   }

   public InteractionResult interactLivingEntity(Player p_41648_, LivingEntity p_41649_, InteractionHand p_41650_) {
      return this.getItem().interactLivingEntity(this, p_41648_, p_41649_, p_41650_);
   }

   public ItemStack copy() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack itemstack = new ItemStack(this.getItem(), this.count, this.serializeCaps());
         itemstack.setPopTime(this.getPopTime());
         if (this.tag != null) {
            itemstack.tag = this.tag.copy();
         }

         return itemstack;
      }
   }

   public ItemStack copyWithCount(int p_256354_) {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack itemstack = this.copy();
         itemstack.setCount(p_256354_);
         return itemstack;
      }
   }

   public static boolean matches(ItemStack p_41729_, ItemStack p_41730_) {
      if (p_41729_ == p_41730_) {
         return true;
      } else {
         return p_41729_.getCount() != p_41730_.getCount() ? false : isSameItemSameTags(p_41729_, p_41730_);
      }
   }

   public static boolean isSameItem(ItemStack p_287761_, ItemStack p_287676_) {
      return p_287761_.is(p_287676_.getItem());
   }

   public static boolean isSameItemSameTags(ItemStack p_150943_, ItemStack p_150944_) {
      if (!p_150943_.is(p_150944_.getItem())) {
         return false;
      } else {
         return p_150943_.isEmpty() && p_150944_.isEmpty() ? true : Objects.equals(p_150943_.tag, p_150944_.tag) && p_150943_.areCapsCompatible(p_150944_);
      }
   }

   public String getDescriptionId() {
      return this.getItem().getDescriptionId(this);
   }

   public String toString() {
      return this.getCount() + " " + this.getItem();
   }

   public void inventoryTick(Level p_41667_, Entity p_41668_, int p_41669_, boolean p_41670_) {
      if (this.popTime > 0) {
         --this.popTime;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, p_41667_, p_41668_, p_41669_, p_41670_);
      }

   }

   public void onCraftedBy(Level p_41679_, Player p_41680_, int p_41681_) {
      p_41680_.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), p_41681_);
      this.getItem().onCraftedBy(this, p_41679_, p_41680_);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAnim getUseAnimation() {
      return this.getItem().getUseAnimation(this);
   }

   public void releaseUsing(Level p_41675_, LivingEntity p_41676_, int p_41677_) {
      this.getItem().releaseUsing(this, p_41675_, p_41676_, p_41677_);
   }

   public boolean useOnRelease() {
      return this.getItem().useOnRelease(this);
   }

   public boolean hasTag() {
      return !this.isEmpty() && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public CompoundTag getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new CompoundTag());
      }

      return this.tag;
   }

   public CompoundTag getOrCreateTagElement(String p_41699_) {
      if (this.tag != null && this.tag.contains(p_41699_, 10)) {
         return this.tag.getCompound(p_41699_);
      } else {
         CompoundTag compoundtag = new CompoundTag();
         this.addTagElement(p_41699_, compoundtag);
         return compoundtag;
      }
   }

   @Nullable
   public CompoundTag getTagElement(String p_41738_) {
      return this.tag != null && this.tag.contains(p_41738_, 10) ? this.tag.getCompound(p_41738_) : null;
   }

   public void removeTagKey(String p_41750_) {
      if (this.tag != null && this.tag.contains(p_41750_)) {
         this.tag.remove(p_41750_);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public ListTag getEnchantmentTags() {
      return this.tag != null ? this.tag.getList("Enchantments", 10) : new ListTag();
   }

   public void setTag(@Nullable CompoundTag p_41752_) {
      this.tag = p_41752_;
      if (this.getItem().isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

      if (p_41752_ != null) {
         this.getItem().verifyTagAfterLoad(p_41752_);
      }

   }

   public Component getHoverName() {
      CompoundTag compoundtag = this.getTagElement("display");
      if (compoundtag != null && compoundtag.contains("Name", 8)) {
         try {
            Component component = Component.Serializer.fromJson(compoundtag.getString("Name"));
            if (component != null) {
               return component;
            }

            compoundtag.remove("Name");
         } catch (Exception exception) {
            compoundtag.remove("Name");
         }
      }

      return this.getItem().getName(this);
   }

   public ItemStack setHoverName(@Nullable Component p_41715_) {
      CompoundTag compoundtag = this.getOrCreateTagElement("display");
      if (p_41715_ != null) {
         compoundtag.putString("Name", Component.Serializer.toJson(p_41715_));
      } else {
         compoundtag.remove("Name");
      }

      return this;
   }

   public void resetHoverName() {
      CompoundTag compoundtag = this.getTagElement("display");
      if (compoundtag != null) {
         compoundtag.remove("Name");
         if (compoundtag.isEmpty()) {
            this.removeTagKey("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasCustomHoverName() {
      CompoundTag compoundtag = this.getTagElement("display");
      return compoundtag != null && compoundtag.contains("Name", 8);
   }

   public List<Component> getTooltipLines(@Nullable Player p_41652_, TooltipFlag p_41653_) {
      List<Component> list = Lists.newArrayList();
      MutableComponent mutablecomponent = Component.empty().append(this.getHoverName()).withStyle(this.getRarity().getStyleModifier());
      if (this.hasCustomHoverName()) {
         mutablecomponent.withStyle(ChatFormatting.ITALIC);
      }

      list.add(mutablecomponent);
      if (!p_41653_.isAdvanced() && !this.hasCustomHoverName() && this.is(Items.FILLED_MAP)) {
         Integer integer = MapItem.getMapId(this);
         if (integer != null) {
            list.add(Component.literal("#" + integer).withStyle(ChatFormatting.GRAY));
         }
      }

      int j = this.getHideFlags();
      if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
         this.getItem().appendHoverText(this, p_41652_ == null ? null : p_41652_.level(), list, p_41653_);
      }

      if (this.hasTag()) {
         if (shouldShowInTooltip(j, ItemStack.TooltipPart.UPGRADES) && p_41652_ != null) {
            ArmorTrim.appendUpgradeHoverText(this, p_41652_.level().registryAccess(), list);
         }

         if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
            appendEnchantmentNames(list, this.getEnchantmentTags());
         }

         if (this.tag.contains("display", 10)) {
            CompoundTag compoundtag = this.tag.getCompound("display");
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.DYE) && compoundtag.contains("color", 99)) {
               if (p_41653_.isAdvanced()) {
                  list.add(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", compoundtag.getInt("color"))).withStyle(ChatFormatting.GRAY));
               } else {
                  list.add(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
               }
            }

            if (compoundtag.getTagType("Lore") == 9) {
               ListTag listtag = compoundtag.getList("Lore", 8);

               for(int i = 0; i < listtag.size(); ++i) {
                  String s = listtag.getString(i);

                  try {
                     MutableComponent mutablecomponent1 = Component.Serializer.fromJson(s);
                     if (mutablecomponent1 != null) {
                        list.add(ComponentUtils.mergeStyles(mutablecomponent1, LORE_STYLE));
                     }
                  } catch (Exception exception) {
                     compoundtag.remove("Lore");
                  }
               }
            }
         }
      }

      if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
         for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslot);
            if (!multimap.isEmpty()) {
               list.add(CommonComponents.EMPTY);
               list.add(Component.translatable("item.modifiers." + equipmentslot.getName()).withStyle(ChatFormatting.GRAY));

               for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  double d0 = attributemodifier.getAmount();
                  boolean flag = false;
                  if (p_41652_ != null) {
                     if (attributemodifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                        d0 += p_41652_.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                        d0 += (double)EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                        flag = true;
                     } else if (attributemodifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                        d0 += p_41652_.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                        flag = true;
                     }
                  }

                  double d1;
                  if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                     if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        d1 = d0 * 10.0D;
                     } else {
                        d1 = d0;
                     }
                  } else {
                     d1 = d0 * 100.0D;
                  }

                  if (flag) {
                     list.add(CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                  } else if (d0 > 0.0D) {
                     list.add(Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                  } else if (d0 < 0.0D) {
                     d1 *= -1.0D;
                     list.add(Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
                  }
               }
            }
         }
      }

      if (this.hasTag()) {
         if (shouldShowInTooltip(j, ItemStack.TooltipPart.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
            list.add(Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE));
         }

         if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
            ListTag listtag1 = this.tag.getList("CanDestroy", 8);
            if (!listtag1.isEmpty()) {
               list.add(CommonComponents.EMPTY);
               list.add(Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY));

               for(int k = 0; k < listtag1.size(); ++k) {
                  list.addAll(expandBlockState(listtag1.getString(k)));
               }
            }
         }

         if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
            ListTag listtag2 = this.tag.getList("CanPlaceOn", 8);
            if (!listtag2.isEmpty()) {
               list.add(CommonComponents.EMPTY);
               list.add(Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY));

               for(int l = 0; l < listtag2.size(); ++l) {
                  list.addAll(expandBlockState(listtag2.getString(l)));
               }
            }
         }
      }

      if (p_41653_.isAdvanced()) {
         if (this.isDamaged()) {
            list.add(Component.translatable("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
         }

         list.add(Component.literal(BuiltInRegistries.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
         if (this.hasTag()) {
            list.add(Component.translatable("item.nbt_tags", this.tag.getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      if (p_41652_ != null && !this.getItem().isEnabled(p_41652_.level().enabledFeatures())) {
         list.add(DISABLED_ITEM_TOOLTIP);
      }

      net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this, p_41652_, list, p_41653_);
      return list;
   }

   private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
      return (p_41627_ & p_41628_.getMask()) == 0;
   }

   private int getHideFlags() {
      return this.hasTag() && this.tag.contains("HideFlags", 99) ? this.tag.getInt("HideFlags") : this.getItem().getDefaultTooltipHideFlags(this);
   }

   public void hideTooltipPart(ItemStack.TooltipPart p_41655_) {
      CompoundTag compoundtag = this.getOrCreateTag();
      compoundtag.putInt("HideFlags", compoundtag.getInt("HideFlags") | p_41655_.getMask());
   }

   public static void appendEnchantmentNames(List<Component> p_41710_, ListTag p_41711_) {
      for(int i = 0; i < p_41711_.size(); ++i) {
         CompoundTag compoundtag = p_41711_.getCompound(i);
         BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId(compoundtag)).ifPresent((p_41708_) -> {
            p_41710_.add(p_41708_.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag)));
         });
      }

   }

   private static Collection<Component> expandBlockState(String p_41762_) {
      try {
         return BlockStateParser.parseForTesting(BuiltInRegistries.BLOCK.asLookup(), p_41762_, true).map((p_220162_) -> {
            return Lists.newArrayList(p_220162_.blockState().getBlock().getName().withStyle(ChatFormatting.DARK_GRAY));
         }, (p_220164_) -> {
            return p_220164_.tag().stream().map((p_220172_) -> {
               return p_220172_.value().getName().withStyle(ChatFormatting.DARK_GRAY);
            }).collect(Collectors.toList());
         });
      } catch (CommandSyntaxException commandsyntaxexception) {
         return Lists.newArrayList(Component.literal("missingno").withStyle(ChatFormatting.DARK_GRAY));
      }
   }

   public boolean hasFoil() {
      return this.getItem().isFoil(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void enchant(Enchantment p_41664_, int p_41665_) {
      this.getOrCreateTag();
      if (!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new ListTag());
      }

      ListTag listtag = this.tag.getList("Enchantments", 10);
      listtag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(p_41664_), (byte)p_41665_));
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.contains("Enchantments", 9)) {
         return !this.tag.getList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void addTagElement(String p_41701_, Tag p_41702_) {
      this.getOrCreateTag().put(p_41701_, p_41702_);
   }

   public boolean isFramed() {
      return this.entityRepresentation instanceof ItemFrame;
   }

   public void setEntityRepresentation(@Nullable Entity p_41637_) {
      this.entityRepresentation = p_41637_;
   }

   @Nullable
   public ItemFrame getFrame() {
      return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
   }

   @Nullable
   public Entity getEntityRepresentation() {
      return !this.isEmpty() ? this.entityRepresentation : null;
   }

   public int getBaseRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int p_41743_) {
      this.getOrCreateTag().putInt("RepairCost", p_41743_);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot p_41639_) {
      Multimap<Attribute, AttributeModifier> multimap;
      if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         ListTag listtag = this.tag.getList("AttributeModifiers", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            if (!compoundtag.contains("Slot", 8) || compoundtag.getString("Slot").equals(p_41639_.getName())) {
               Optional<Attribute> optional = BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundtag.getString("AttributeName")));
               if (optional.isPresent()) {
                  AttributeModifier attributemodifier = AttributeModifier.load(compoundtag);
                  if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L && attributemodifier.getId().getMostSignificantBits() != 0L) {
                     multimap.put(optional.get(), attributemodifier);
                  }
               }
            }
         }
      } else {
         multimap = this.getItem().getAttributeModifiers(p_41639_, this);
      }

      multimap = net.minecraftforge.common.ForgeHooks.getAttributeModifiers(this, p_41639_, multimap);
      return multimap;
   }

   public void addAttributeModifier(Attribute p_41644_, AttributeModifier p_41645_, @Nullable EquipmentSlot p_41646_) {
      this.getOrCreateTag();
      if (!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new ListTag());
      }

      ListTag listtag = this.tag.getList("AttributeModifiers", 10);
      CompoundTag compoundtag = p_41645_.save();
      compoundtag.putString("AttributeName", BuiltInRegistries.ATTRIBUTE.getKey(p_41644_).toString());
      if (p_41646_ != null) {
         compoundtag.putString("Slot", p_41646_.getName());
      }

      listtag.add(compoundtag);
   }

   public Component getDisplayName() {
      MutableComponent mutablecomponent = Component.empty().append(this.getHoverName());
      if (this.hasCustomHoverName()) {
         mutablecomponent.withStyle(ChatFormatting.ITALIC);
      }

      MutableComponent mutablecomponent1 = ComponentUtils.wrapInSquareBrackets(mutablecomponent);
      if (!this.isEmpty()) {
         mutablecomponent1.withStyle(this.getRarity().getStyleModifier()).withStyle((p_220170_) -> {
            return p_220170_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this)));
         });
      }

      return mutablecomponent1;
   }

   public boolean hasAdventureModePlaceTagForBlock(Registry<Block> p_204122_, BlockInWorld p_204123_) {
      if (this.adventurePlaceCheck == null) {
         this.adventurePlaceCheck = new AdventureModeCheck("CanPlaceOn");
      }

      return this.adventurePlaceCheck.test(this, p_204122_, p_204123_);
   }

   public boolean hasAdventureModeBreakTagForBlock(Registry<Block> p_204129_, BlockInWorld p_204130_) {
      if (this.adventureBreakCheck == null) {
         this.adventureBreakCheck = new AdventureModeCheck("CanDestroy");
      }

      return this.adventureBreakCheck.test(this, p_204129_, p_204130_);
   }

   public int getPopTime() {
      return this.popTime;
   }

   public void setPopTime(int p_41755_) {
      this.popTime = p_41755_;
   }

   public int getCount() {
      return this.isEmpty() ? 0 : this.count;
   }

   public void setCount(int p_41765_) {
      this.count = p_41765_;
   }

   public void grow(int p_41770_) {
      this.setCount(this.getCount() + p_41770_);
   }

   public void shrink(int p_41775_) {
      this.grow(-p_41775_);
   }

   public void onUseTick(Level p_41732_, LivingEntity p_41733_, int p_41734_) {
      this.getItem().onUseTick(p_41732_, p_41733_, this, p_41734_);
   }

   /** @deprecated Forge: Use {@linkplain net.minecraftforge.common.extensions.IForgeItemStack#onDestroyed(ItemEntity, net.minecraft.world.damagesource.DamageSource) damage source sensitive version} */
   @Deprecated
   public void onDestroyed(ItemEntity p_150925_) {
      this.getItem().onDestroyed(p_150925_);
   }

   public boolean isEdible() {
      return this.getItem().isEdible();
   }

   // FORGE START
   public void deserializeNBT(CompoundTag nbt) {
      final ItemStack itemStack = ItemStack.of(nbt);
      this.setTag(itemStack.getTag());
      if (itemStack.capNBT != null) deserializeCaps(itemStack.capNBT);
   }

   /**
    * Set up forge's ItemStack additions.
    */
   private void forgeInit() {
      if (this.delegate != null) {
         this.gatherCapabilities(() -> item.initCapabilities(this, this.capNBT));
         if (this.capNBT != null) deserializeCaps(this.capNBT);
      }
   }

   public SoundEvent getDrinkingSound() {
      return this.getItem().getDrinkingSound();
   }

   public SoundEvent getEatingSound() {
      return this.getItem().getEatingSound();
   }

   public static enum TooltipPart {
      ENCHANTMENTS,
      MODIFIERS,
      UNBREAKABLE,
      CAN_DESTROY,
      CAN_PLACE,
      ADDITIONAL,
      DYE,
      UPGRADES;

      private final int mask = 1 << this.ordinal();

      public int getMask() {
         return this.mask;
      }
   }
}
