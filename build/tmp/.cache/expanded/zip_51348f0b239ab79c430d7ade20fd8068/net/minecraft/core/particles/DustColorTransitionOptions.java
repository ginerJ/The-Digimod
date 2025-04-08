package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
   public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create((p_253369_) -> {
      return p_253369_.group(ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter((p_253368_) -> {
         return p_253368_.color;
      }), ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter((p_253367_) -> {
         return p_253367_.toColor;
      }), Codec.FLOAT.fieldOf("scale").forGetter((p_175765_) -> {
         return p_175765_.scale;
      })).apply(p_253369_, DustColorTransitionOptions::new);
   });
   public static final ParticleOptions.Deserializer<DustColorTransitionOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>() {
      public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> p_175777_, StringReader p_175778_) throws CommandSyntaxException {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_175778_);
         p_175778_.expect(' ');
         float f = p_175778_.readFloat();
         Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(p_175778_);
         return new DustColorTransitionOptions(vector3f, vector3f1, f);
      }

      public DustColorTransitionOptions fromNetwork(ParticleType<DustColorTransitionOptions> p_175780_, FriendlyByteBuf p_175781_) {
         Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_175781_);
         float f = p_175781_.readFloat();
         Vector3f vector3f1 = DustParticleOptionsBase.readVector3f(p_175781_);
         return new DustColorTransitionOptions(vector3f, vector3f1, f);
      }
   };
   private final Vector3f toColor;

   public DustColorTransitionOptions(Vector3f p_254199_, Vector3f p_254529_, float p_254178_) {
      super(p_254199_, p_254178_);
      this.toColor = p_254529_;
   }

   public Vector3f getFromColor() {
      return this.color;
   }

   public Vector3f getToColor() {
      return this.toColor;
   }

   public void writeToNetwork(FriendlyByteBuf p_175767_) {
      super.writeToNetwork(p_175767_);
      p_175767_.writeFloat(this.toColor.x());
      p_175767_.writeFloat(this.toColor.y());
      p_175767_.writeFloat(this.toColor.z());
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale, this.toColor.x(), this.toColor.y(), this.toColor.z());
   }

   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }
}