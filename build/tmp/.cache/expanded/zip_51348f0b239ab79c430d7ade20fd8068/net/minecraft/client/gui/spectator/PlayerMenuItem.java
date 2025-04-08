package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuItem implements SpectatorMenuItem {
   private final GameProfile profile;
   private final ResourceLocation location;
   private final Component name;

   public PlayerMenuItem(GameProfile p_101756_) {
      this.profile = p_101756_;
      Minecraft minecraft = Minecraft.getInstance();
      this.location = minecraft.getSkinManager().getInsecureSkinLocation(p_101756_);
      this.name = Component.literal(p_101756_.getName());
   }

   public void selectItem(SpectatorMenu p_101762_) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
   }

   public Component getName() {
      return this.name;
   }

   public void renderIcon(GuiGraphics p_282282_, float p_282686_, int p_282849_) {
      p_282282_.setColor(1.0F, 1.0F, 1.0F, (float)p_282849_ / 255.0F);
      PlayerFaceRenderer.draw(p_282282_, this.location, 2, 2, 12);
      p_282282_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean isEnabled() {
      return true;
   }
}