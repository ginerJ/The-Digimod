package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class VibrationSignalParticle extends TextureSheetParticle {
   private final PositionSource target;
   private float rot;
   private float rotO;
   private float pitch;
   private float pitchO;

   VibrationSignalParticle(ClientLevel p_234105_, double p_234106_, double p_234107_, double p_234108_, PositionSource p_234109_, int p_234110_) {
      super(p_234105_, p_234106_, p_234107_, p_234108_, 0.0D, 0.0D, 0.0D);
      this.quadSize = 0.3F;
      this.target = p_234109_;
      this.lifetime = p_234110_;
      Optional<Vec3> optional = p_234109_.getPosition(p_234105_);
      if (optional.isPresent()) {
         Vec3 vec3 = optional.get();
         double d0 = p_234106_ - vec3.x();
         double d1 = p_234107_ - vec3.y();
         double d2 = p_234108_ - vec3.z();
         this.rotO = this.rot = (float)Mth.atan2(d0, d2);
         this.pitchO = this.pitch = (float)Mth.atan2(d1, Math.sqrt(d0 * d0 + d2 * d2));
      }

   }

   public void render(VertexConsumer p_172475_, Camera p_172476_, float p_172477_) {
      float f = Mth.sin(((float)this.age + p_172477_ - ((float)Math.PI * 2F)) * 0.05F) * 2.0F;
      float f1 = Mth.lerp(p_172477_, this.rotO, this.rot);
      float f2 = Mth.lerp(p_172477_, this.pitchO, this.pitch) + ((float)Math.PI / 2F);
      this.renderSignal(p_172475_, p_172476_, p_172477_, (p_253355_) -> {
         p_253355_.rotateY(f1).rotateX(-f2).rotateY(f);
      });
      this.renderSignal(p_172475_, p_172476_, p_172477_, (p_253351_) -> {
         p_253351_.rotateY(-(float)Math.PI + f1).rotateX(f2).rotateY(f);
      });
   }

   private void renderSignal(VertexConsumer p_172479_, Camera p_172480_, float p_172481_, Consumer<Quaternionf> p_172482_) {
      Vec3 vec3 = p_172480_.getPosition();
      float f = (float)(Mth.lerp((double)p_172481_, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp((double)p_172481_, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp((double)p_172481_, this.zo, this.z) - vec3.z());
      Vector3f vector3f = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
      Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
      p_172482_.accept(quaternionf);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f3 = this.getQuadSize(p_172481_);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f1 = avector3f[i];
         vector3f1.rotate(quaternionf);
         vector3f1.mul(f3);
         vector3f1.add(f, f1, f2);
      }

      float f6 = this.getU0();
      float f7 = this.getU1();
      float f4 = this.getV0();
      float f5 = this.getV1();
      int j = this.getLightColor(p_172481_);
      p_172479_.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_172479_.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
   }

   public int getLightColor(float p_172469_) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         Optional<Vec3> optional = this.target.getPosition(this.level);
         if (optional.isEmpty()) {
            this.remove();
         } else {
            int i = this.lifetime - this.age;
            double d0 = 1.0D / (double)i;
            Vec3 vec3 = optional.get();
            this.x = Mth.lerp(d0, this.x, vec3.x());
            this.y = Mth.lerp(d0, this.y, vec3.y());
            this.z = Mth.lerp(d0, this.z, vec3.z());
            this.setPos(this.x, this.y, this.z); // FORGE: Update the particle's bounding box
            double d1 = this.x - vec3.x();
            double d2 = this.y - vec3.y();
            double d3 = this.z - vec3.z();
            this.rotO = this.rot;
            this.rot = (float)Mth.atan2(d1, d3);
            this.pitchO = this.pitch;
            this.pitch = (float)Mth.atan2(d2, Math.sqrt(d1 * d1 + d3 * d3));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<VibrationParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_172490_) {
         this.sprite = p_172490_;
      }

      public Particle createParticle(VibrationParticleOption p_172501_, ClientLevel p_172502_, double p_172503_, double p_172504_, double p_172505_, double p_172506_, double p_172507_, double p_172508_) {
         VibrationSignalParticle vibrationsignalparticle = new VibrationSignalParticle(p_172502_, p_172503_, p_172504_, p_172505_, p_172501_.getDestination(), p_172501_.getArrivalInTicks());
         vibrationsignalparticle.pickSprite(this.sprite);
         vibrationsignalparticle.setAlpha(1.0F);
         return vibrationsignalparticle;
      }
   }
}
