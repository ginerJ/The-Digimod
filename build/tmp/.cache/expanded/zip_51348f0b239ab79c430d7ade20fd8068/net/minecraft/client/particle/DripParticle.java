package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DripParticle extends TextureSheetParticle {
   private final Fluid type;
   protected boolean isGlowing;

   DripParticle(ClientLevel p_106051_, double p_106052_, double p_106053_, double p_106054_, Fluid p_106055_) {
      super(p_106051_, p_106052_, p_106053_, p_106054_);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = p_106055_;
   }

   protected Fluid getType() {
      return this.type;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_106065_) {
      return this.isGlowing ? 240 : super.getLightColor(p_106065_);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.preMoveUpdate();
      if (!this.removed) {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if (!this.removed) {
            this.xd *= (double)0.98F;
            this.yd *= (double)0.98F;
            this.zd *= (double)0.98F;
            if (this.type != Fluids.EMPTY) {
               BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
               FluidState fluidstate = this.level.getFluidState(blockpos);
               if (fluidstate.getType() == this.type && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
                  this.remove();
               }

            }
         }
      }
   }

   protected void preMoveUpdate() {
      if (this.lifetime-- <= 0) {
         this.remove();
      }

   }

   protected void postMoveUpdate() {
   }

   public static TextureSheetParticle createWaterHangParticle(SimpleParticleType p_272626_, ClientLevel p_273102_, double p_273456_, double p_272984_, double p_273398_, double p_272880_, double p_273725_, double p_273051_) {
      DripParticle dripparticle = new DripParticle.DripHangParticle(p_273102_, p_273456_, p_272984_, p_273398_, Fluids.WATER, ParticleTypes.FALLING_WATER);
      dripparticle.setColor(0.2F, 0.3F, 1.0F);
      return dripparticle;
   }

   public static TextureSheetParticle createWaterFallParticle(SimpleParticleType p_273627_, ClientLevel p_273486_, double p_273309_, double p_273125_, double p_272992_, double p_273177_, double p_273537_, double p_272846_) {
      DripParticle dripparticle = new DripParticle.FallAndLandParticle(p_273486_, p_273309_, p_273125_, p_272992_, Fluids.WATER, ParticleTypes.SPLASH);
      dripparticle.setColor(0.2F, 0.3F, 1.0F);
      return dripparticle;
   }

   public static TextureSheetParticle createLavaHangParticle(SimpleParticleType p_273228_, ClientLevel p_273622_, double p_273666_, double p_273570_, double p_273214_, double p_273664_, double p_273595_, double p_272690_) {
      return new DripParticle.CoolingDripHangParticle(p_273622_, p_273666_, p_273570_, p_273214_, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
   }

   public static TextureSheetParticle createLavaFallParticle(SimpleParticleType p_273238_, ClientLevel p_273752_, double p_272651_, double p_273625_, double p_273136_, double p_273204_, double p_272797_, double p_273362_) {
      DripParticle dripparticle = new DripParticle.FallAndLandParticle(p_273752_, p_272651_, p_273625_, p_273136_, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
      dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
      return dripparticle;
   }

   public static TextureSheetParticle createLavaLandParticle(SimpleParticleType p_273607_, ClientLevel p_272692_, double p_273544_, double p_272768_, double p_272726_, double p_273719_, double p_272833_, double p_272949_) {
      DripParticle dripparticle = new DripParticle.DripLandParticle(p_272692_, p_273544_, p_272768_, p_272726_, Fluids.LAVA);
      dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
      return dripparticle;
   }

   public static TextureSheetParticle createHoneyHangParticle(SimpleParticleType p_273557_, ClientLevel p_273367_, double p_272749_, double p_272697_, double p_272849_, double p_273144_, double p_273170_, double p_272932_) {
      DripParticle.DripHangParticle dripparticle$driphangparticle = new DripParticle.DripHangParticle(p_273367_, p_272749_, p_272697_, p_272849_, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
      dripparticle$driphangparticle.gravity *= 0.01F;
      dripparticle$driphangparticle.lifetime = 100;
      dripparticle$driphangparticle.setColor(0.622F, 0.508F, 0.082F);
      return dripparticle$driphangparticle;
   }

   public static TextureSheetParticle createHoneyFallParticle(SimpleParticleType p_273140_, ClientLevel p_273042_, double p_272969_, double p_273737_, double p_273454_, double p_273211_, double p_273723_, double p_273474_) {
      DripParticle dripparticle = new DripParticle.HoneyFallAndLandParticle(p_273042_, p_272969_, p_273737_, p_273454_, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
      dripparticle.gravity = 0.01F;
      dripparticle.setColor(0.582F, 0.448F, 0.082F);
      return dripparticle;
   }

   public static TextureSheetParticle createHoneyLandParticle(SimpleParticleType p_273477_, ClientLevel p_273770_, double p_272822_, double p_273147_, double p_272597_, double p_273614_, double p_273085_, double p_273097_) {
      DripParticle dripparticle = new DripParticle.DripLandParticle(p_273770_, p_272822_, p_273147_, p_272597_, Fluids.EMPTY);
      dripparticle.lifetime = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
      dripparticle.setColor(0.522F, 0.408F, 0.082F);
      return dripparticle;
   }

   public static TextureSheetParticle createDripstoneWaterHangParticle(SimpleParticleType p_273781_, ClientLevel p_272876_, double p_273499_, double p_273028_, double p_273663_, double p_273004_, double p_272801_, double p_272665_) {
      DripParticle dripparticle = new DripParticle.DripHangParticle(p_272876_, p_273499_, p_273028_, p_273663_, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
      dripparticle.setColor(0.2F, 0.3F, 1.0F);
      return dripparticle;
   }

   public static TextureSheetParticle createDripstoneWaterFallParticle(SimpleParticleType p_272684_, ClientLevel p_273226_, double p_273142_, double p_273070_, double p_273153_, double p_273735_, double p_273317_, double p_273234_) {
      DripParticle dripparticle = new DripParticle.DripstoneFallAndLandParticle(p_273226_, p_273142_, p_273070_, p_273153_, Fluids.WATER, ParticleTypes.SPLASH);
      dripparticle.setColor(0.2F, 0.3F, 1.0F);
      return dripparticle;
   }

   public static TextureSheetParticle createDripstoneLavaHangParticle(SimpleParticleType p_273453_, ClientLevel p_273616_, double p_272691_, double p_272725_, double p_273259_, double p_273634_, double p_273065_, double p_273428_) {
      return new DripParticle.CoolingDripHangParticle(p_273616_, p_272691_, p_272725_, p_273259_, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
   }

   public static TextureSheetParticle createDripstoneLavaFallParticle(SimpleParticleType p_272890_, ClientLevel p_273172_, double p_272954_, double p_272803_, double p_273427_, double p_273081_, double p_273047_, double p_272960_) {
      DripParticle dripparticle = new DripParticle.DripstoneFallAndLandParticle(p_273172_, p_272954_, p_272803_, p_273427_, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
      dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
      return dripparticle;
   }

   public static TextureSheetParticle createNectarFallParticle(SimpleParticleType p_273349_, ClientLevel p_272672_, double p_272820_, double p_273386_, double p_272886_, double p_272935_, double p_273715_, double p_273202_) {
      DripParticle dripparticle = new DripParticle.FallingParticle(p_272672_, p_272820_, p_273386_, p_272886_, Fluids.EMPTY);
      dripparticle.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      dripparticle.gravity = 0.007F;
      dripparticle.setColor(0.92F, 0.782F, 0.72F);
      return dripparticle;
   }

   public static TextureSheetParticle createSporeBlossomFallParticle(SimpleParticleType p_273654_, ClientLevel p_272678_, double p_272637_, double p_273253_, double p_273293_, double p_273363_, double p_273132_, double p_273215_) {
      int i = (int)(64.0F / Mth.randomBetween(p_272678_.getRandom(), 0.1F, 0.9F));
      DripParticle dripparticle = new DripParticle.FallingParticle(p_272678_, p_272637_, p_273253_, p_273293_, Fluids.EMPTY, i);
      dripparticle.gravity = 0.005F;
      dripparticle.setColor(0.32F, 0.5F, 0.22F);
      return dripparticle;
   }

   public static TextureSheetParticle createObsidianTearHangParticle(SimpleParticleType p_273120_, ClientLevel p_272664_, double p_272879_, double p_272592_, double p_272967_, double p_272834_, double p_273440_, double p_272888_) {
      DripParticle.DripHangParticle dripparticle$driphangparticle = new DripParticle.DripHangParticle(p_272664_, p_272879_, p_272592_, p_272967_, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
      dripparticle$driphangparticle.isGlowing = true;
      dripparticle$driphangparticle.gravity *= 0.01F;
      dripparticle$driphangparticle.lifetime = 100;
      dripparticle$driphangparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
      return dripparticle$driphangparticle;
   }

   public static TextureSheetParticle createObsidianTearFallParticle(SimpleParticleType p_272859_, ClientLevel p_273478_, double p_273621_, double p_273279_, double p_273227_, double p_273061_, double p_273257_, double p_273164_) {
      DripParticle dripparticle = new DripParticle.FallAndLandParticle(p_273478_, p_273621_, p_273279_, p_273227_, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
      dripparticle.isGlowing = true;
      dripparticle.gravity = 0.01F;
      dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
      return dripparticle;
   }

   public static TextureSheetParticle createObsidianTearLandParticle(SimpleParticleType p_272836_, ClientLevel p_273162_, double p_273543_, double p_273247_, double p_272921_, double p_273397_, double p_273472_, double p_273488_) {
      DripParticle dripparticle = new DripParticle.DripLandParticle(p_273162_, p_273543_, p_273247_, p_272921_, Fluids.EMPTY);
      dripparticle.isGlowing = true;
      dripparticle.lifetime = (int)(28.0D / (Math.random() * 0.8D + 0.2D));
      dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
      return dripparticle;
   }

   @OnlyIn(Dist.CLIENT)
   static class CoolingDripHangParticle extends DripParticle.DripHangParticle {
      CoolingDripHangParticle(ClientLevel p_106068_, double p_106069_, double p_106070_, double p_106071_, Fluid p_106072_, ParticleOptions p_106073_) {
         super(p_106068_, p_106069_, p_106070_, p_106071_, p_106072_, p_106073_);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DripHangParticle extends DripParticle {
      private final ParticleOptions fallingParticle;

      DripHangParticle(ClientLevel p_106085_, double p_106086_, double p_106087_, double p_106088_, Fluid p_106089_, ParticleOptions p_106090_) {
         super(p_106085_, p_106086_, p_106087_, p_106088_, p_106089_);
         this.fallingParticle = p_106090_;
         this.gravity *= 0.02F;
         this.lifetime = 40;
      }

      protected void preMoveUpdate() {
         if (this.lifetime-- <= 0) {
            this.remove();
            this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }

      }

      protected void postMoveUpdate() {
         this.xd *= 0.02D;
         this.yd *= 0.02D;
         this.zd *= 0.02D;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DripLandParticle extends DripParticle {
      DripLandParticle(ClientLevel p_106102_, double p_106103_, double p_106104_, double p_106105_, Fluid p_106106_) {
         super(p_106102_, p_106103_, p_106104_, p_106105_, p_106106_);
         this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DripstoneFallAndLandParticle extends DripParticle.FallAndLandParticle {
      DripstoneFallAndLandParticle(ClientLevel p_171930_, double p_171931_, double p_171932_, double p_171933_, Fluid p_171934_, ParticleOptions p_171935_) {
         super(p_171930_, p_171931_, p_171932_, p_171933_, p_171934_, p_171935_);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            SoundEvent soundevent = this.getType() == Fluids.LAVA ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
            float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundSource.BLOCKS, f, 1.0F, false);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallAndLandParticle extends DripParticle.FallingParticle {
      protected final ParticleOptions landParticle;

      FallAndLandParticle(ClientLevel p_106116_, double p_106117_, double p_106118_, double p_106119_, Fluid p_106120_, ParticleOptions p_106121_) {
         super(p_106116_, p_106117_, p_106118_, p_106119_, p_106120_);
         this.landParticle = p_106121_;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingParticle extends DripParticle {
      FallingParticle(ClientLevel p_106132_, double p_106133_, double p_106134_, double p_106135_, Fluid p_106136_) {
         this(p_106132_, p_106133_, p_106134_, p_106135_, p_106136_, (int)(64.0D / (Math.random() * 0.8D + 0.2D)));
      }

      FallingParticle(ClientLevel p_172022_, double p_172023_, double p_172024_, double p_172025_, Fluid p_172026_, int p_172027_) {
         super(p_172022_, p_172023_, p_172024_, p_172025_, p_172026_);
         this.lifetime = p_172027_;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class HoneyFallAndLandParticle extends DripParticle.FallAndLandParticle {
      HoneyFallAndLandParticle(ClientLevel p_106146_, double p_106147_, double p_106148_, double p_106149_, Fluid p_106150_, ParticleOptions p_106151_) {
         super(p_106146_, p_106147_, p_106148_, p_106149_, p_106150_, p_106151_);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, f, 1.0F, false);
         }

      }
   }
}