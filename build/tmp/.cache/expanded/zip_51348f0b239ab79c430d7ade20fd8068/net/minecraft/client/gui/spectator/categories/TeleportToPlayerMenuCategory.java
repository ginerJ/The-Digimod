package net.minecraft.client.gui.spectator.categories;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Comparator<PlayerInfo> PROFILE_ORDER = Comparator.comparing((p_253335_) -> {
      return p_253335_.getProfile().getId();
   });
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.teleport.prompt");
   private final List<SpectatorMenuItem> items;

   public TeleportToPlayerMenuCategory() {
      this(Minecraft.getInstance().getConnection().getListedOnlinePlayers());
   }

   public TeleportToPlayerMenuCategory(Collection<PlayerInfo> p_101861_) {
      this.items = p_101861_.stream().filter((p_253336_) -> {
         return p_253336_.getGameMode() != GameType.SPECTATOR;
      }).sorted(PROFILE_ORDER).map((p_253334_) -> {
         return (SpectatorMenuItem) new PlayerMenuItem(p_253334_.getProfile());
      }).toList();
   }

   public List<SpectatorMenuItem> getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(SpectatorMenu p_101868_) {
      p_101868_.selectCategory(this);
   }

   public Component getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(GuiGraphics p_281992_, float p_281684_, int p_281889_) {
      p_281992_.blit(SpectatorGui.SPECTATOR_LOCATION, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}