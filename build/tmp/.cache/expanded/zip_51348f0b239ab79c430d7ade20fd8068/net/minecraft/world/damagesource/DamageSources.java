package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class DamageSources {
   private final Registry<DamageType> damageTypes;
   private final DamageSource inFire;
   private final DamageSource lightningBolt;
   private final DamageSource onFire;
   private final DamageSource lava;
   private final DamageSource hotFloor;
   private final DamageSource inWall;
   private final DamageSource cramming;
   private final DamageSource drown;
   private final DamageSource starve;
   private final DamageSource cactus;
   private final DamageSource fall;
   private final DamageSource flyIntoWall;
   private final DamageSource fellOutOfWorld;
   private final DamageSource generic;
   private final DamageSource magic;
   private final DamageSource wither;
   private final DamageSource dragonBreath;
   private final DamageSource dryOut;
   private final DamageSource sweetBerryBush;
   private final DamageSource freeze;
   private final DamageSource stalagmite;
   private final DamageSource outsideBorder;
   private final DamageSource genericKill;

   public DamageSources(RegistryAccess p_270740_) {
      this.damageTypes = p_270740_.registryOrThrow(Registries.DAMAGE_TYPE);
      this.inFire = this.source(DamageTypes.IN_FIRE);
      this.lightningBolt = this.source(DamageTypes.LIGHTNING_BOLT);
      this.onFire = this.source(DamageTypes.ON_FIRE);
      this.lava = this.source(DamageTypes.LAVA);
      this.hotFloor = this.source(DamageTypes.HOT_FLOOR);
      this.inWall = this.source(DamageTypes.IN_WALL);
      this.cramming = this.source(DamageTypes.CRAMMING);
      this.drown = this.source(DamageTypes.DROWN);
      this.starve = this.source(DamageTypes.STARVE);
      this.cactus = this.source(DamageTypes.CACTUS);
      this.fall = this.source(DamageTypes.FALL);
      this.flyIntoWall = this.source(DamageTypes.FLY_INTO_WALL);
      this.fellOutOfWorld = this.source(DamageTypes.FELL_OUT_OF_WORLD);
      this.generic = this.source(DamageTypes.GENERIC);
      this.magic = this.source(DamageTypes.MAGIC);
      this.wither = this.source(DamageTypes.WITHER);
      this.dragonBreath = this.source(DamageTypes.DRAGON_BREATH);
      this.dryOut = this.source(DamageTypes.DRY_OUT);
      this.sweetBerryBush = this.source(DamageTypes.SWEET_BERRY_BUSH);
      this.freeze = this.source(DamageTypes.FREEZE);
      this.stalagmite = this.source(DamageTypes.STALAGMITE);
      this.outsideBorder = this.source(DamageTypes.OUTSIDE_BORDER);
      this.genericKill = this.source(DamageTypes.GENERIC_KILL);
   }

   private DamageSource source(ResourceKey<DamageType> p_270957_) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(p_270957_));
   }

   private DamageSource source(ResourceKey<DamageType> p_270142_, @Nullable Entity p_270696_) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(p_270142_), p_270696_);
   }

   private DamageSource source(ResourceKey<DamageType> p_270076_, @Nullable Entity p_270656_, @Nullable Entity p_270242_) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(p_270076_), p_270656_, p_270242_);
   }

   public DamageSource inFire() {
      return this.inFire;
   }

   public DamageSource lightningBolt() {
      return this.lightningBolt;
   }

   public DamageSource onFire() {
      return this.onFire;
   }

   public DamageSource lava() {
      return this.lava;
   }

   public DamageSource hotFloor() {
      return this.hotFloor;
   }

   public DamageSource inWall() {
      return this.inWall;
   }

   public DamageSource cramming() {
      return this.cramming;
   }

   public DamageSource drown() {
      return this.drown;
   }

   public DamageSource starve() {
      return this.starve;
   }

   public DamageSource cactus() {
      return this.cactus;
   }

   public DamageSource fall() {
      return this.fall;
   }

   public DamageSource flyIntoWall() {
      return this.flyIntoWall;
   }

   public DamageSource fellOutOfWorld() {
      return this.fellOutOfWorld;
   }

   public DamageSource generic() {
      return this.generic;
   }

   public DamageSource magic() {
      return this.magic;
   }

   public DamageSource wither() {
      return this.wither;
   }

   public DamageSource dragonBreath() {
      return this.dragonBreath;
   }

   public DamageSource dryOut() {
      return this.dryOut;
   }

   public DamageSource sweetBerryBush() {
      return this.sweetBerryBush;
   }

   public DamageSource freeze() {
      return this.freeze;
   }

   public DamageSource stalagmite() {
      return this.stalagmite;
   }

   public DamageSource fallingBlock(Entity p_270643_) {
      return this.source(DamageTypes.FALLING_BLOCK, p_270643_);
   }

   public DamageSource anvil(Entity p_270112_) {
      return this.source(DamageTypes.FALLING_ANVIL, p_270112_);
   }

   public DamageSource fallingStalactite(Entity p_270720_) {
      return this.source(DamageTypes.FALLING_STALACTITE, p_270720_);
   }

   public DamageSource sting(LivingEntity p_270689_) {
      return this.source(DamageTypes.STING, p_270689_);
   }

   public DamageSource mobAttack(LivingEntity p_270357_) {
      return this.source(DamageTypes.MOB_ATTACK, p_270357_);
   }

   public DamageSource noAggroMobAttack(LivingEntity p_270502_) {
      return this.source(DamageTypes.MOB_ATTACK_NO_AGGRO, p_270502_);
   }

   public DamageSource playerAttack(Player p_270723_) {
      return this.source(DamageTypes.PLAYER_ATTACK, p_270723_);
   }

   public DamageSource arrow(AbstractArrow p_270570_, @Nullable Entity p_270857_) {
      return this.source(DamageTypes.ARROW, p_270570_, p_270857_);
   }

   public DamageSource trident(Entity p_270146_, @Nullable Entity p_270358_) {
      return this.source(DamageTypes.TRIDENT, p_270146_, p_270358_);
   }

   public DamageSource mobProjectile(Entity p_270210_, @Nullable LivingEntity p_270757_) {
      return this.source(DamageTypes.MOB_PROJECTILE, p_270210_, p_270757_);
   }

   public DamageSource fireworks(FireworkRocketEntity p_270571_, @Nullable Entity p_270768_) {
      return this.source(DamageTypes.FIREWORKS, p_270571_, p_270768_);
   }

   public DamageSource fireball(Fireball p_270147_, @Nullable Entity p_270824_) {
      return p_270824_ == null ? this.source(DamageTypes.UNATTRIBUTED_FIREBALL, p_270147_) : this.source(DamageTypes.FIREBALL, p_270147_, p_270824_);
   }

   public DamageSource witherSkull(WitherSkull p_270367_, Entity p_270887_) {
      return this.source(DamageTypes.WITHER_SKULL, p_270367_, p_270887_);
   }

   public DamageSource thrown(Entity p_270388_, @Nullable Entity p_270485_) {
      return this.source(DamageTypes.THROWN, p_270388_, p_270485_);
   }

   public DamageSource indirectMagic(Entity p_270560_, @Nullable Entity p_270646_) {
      return this.source(DamageTypes.INDIRECT_MAGIC, p_270560_, p_270646_);
   }

   public DamageSource thorns(Entity p_270917_) {
      return this.source(DamageTypes.THORNS, p_270917_);
   }

   public DamageSource explosion(@Nullable Explosion p_270369_) {
      return p_270369_ != null ? this.explosion(p_270369_.getDirectSourceEntity(), p_270369_.getIndirectSourceEntity()) : this.explosion((Entity)null, (Entity)null);
   }

   public DamageSource explosion(@Nullable Entity p_271016_, @Nullable Entity p_270814_) {
      return this.source(p_270814_ != null && p_271016_ != null ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION, p_271016_, p_270814_);
   }

   public DamageSource sonicBoom(Entity p_270401_) {
      return this.source(DamageTypes.SONIC_BOOM, p_270401_);
   }

   public DamageSource badRespawnPointExplosion(Vec3 p_270175_) {
      return new DamageSource(this.damageTypes.getHolderOrThrow(DamageTypes.BAD_RESPAWN_POINT), p_270175_);
   }

   public DamageSource outOfBorder() {
      return this.outsideBorder;
   }

   public DamageSource genericKill() {
      return this.genericKill;
   }
}