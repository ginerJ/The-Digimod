package net.minecraft.client.quickplay;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuickPlay {
   public static final Component ERROR_TITLE = Component.translatable("quickplay.error.title");
   private static final Component INVALID_IDENTIFIER = Component.translatable("quickplay.error.invalid_identifier");
   private static final Component REALM_CONNECT = Component.translatable("quickplay.error.realm_connect");
   private static final Component REALM_PERMISSION = Component.translatable("quickplay.error.realm_permission");
   private static final Component TO_TITLE = Component.translatable("gui.toTitle");
   private static final Component TO_WORLD_LIST = Component.translatable("gui.toWorld");
   private static final Component TO_REALMS_LIST = Component.translatable("gui.toRealms");

   public static void connect(Minecraft p_279319_, GameConfig.QuickPlayData p_279291_, ReloadInstance p_279328_, RealmsClient p_279322_) {
      String s = p_279291_.singleplayer();
      String s1 = p_279291_.multiplayer();
      String s2 = p_279291_.realms();
      p_279328_.done().thenRunAsync(() -> {
         if (!Util.isBlank(s)) {
            joinSingleplayerWorld(p_279319_, s);
         } else if (!Util.isBlank(s1)) {
            joinMultiplayerWorld(p_279319_, s1);
         } else if (!Util.isBlank(s2)) {
            joinRealmsWorld(p_279319_, p_279322_, s2);
         }

      }, p_279319_);
   }

   private static void joinSingleplayerWorld(Minecraft p_279420_, String p_279459_) {
      if (!p_279420_.getLevelSource().levelExists(p_279459_)) {
         Screen screen = new SelectWorldScreen(new TitleScreen());
         p_279420_.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
      } else {
         p_279420_.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
         p_279420_.createWorldOpenFlows().loadLevel(new TitleScreen(), p_279459_);
      }
   }

   private static void joinMultiplayerWorld(Minecraft p_279276_, String p_279128_) {
      ServerList serverlist = new ServerList(p_279276_);
      serverlist.load();
      ServerData serverdata = serverlist.get(p_279128_);
      if (serverdata == null) {
         serverdata = new ServerData(I18n.get("selectServer.defaultName"), p_279128_, false);
         serverlist.add(serverdata, true);
         serverlist.save();
      }

      ServerAddress serveraddress = ServerAddress.parseString(p_279128_);
      ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), p_279276_, serveraddress, serverdata, true);
   }

   private static void joinRealmsWorld(Minecraft p_279320_, RealmsClient p_279468_, String p_279371_) {
      long i;
      RealmsServerList realmsserverlist;
      try {
         i = Long.parseLong(p_279371_);
         realmsserverlist = p_279468_.listWorlds();
      } catch (NumberFormatException numberformatexception) {
         Screen screen1 = new RealmsMainScreen(new TitleScreen());
         p_279320_.setScreen(new DisconnectedScreen(screen1, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
         return;
      } catch (RealmsServiceException realmsserviceexception) {
         Screen screen = new TitleScreen();
         p_279320_.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
         return;
      }

      RealmsServer realmsserver = realmsserverlist.servers.stream().filter((p_279424_) -> {
         return p_279424_.id == i;
      }).findFirst().orElse((RealmsServer)null);
      if (realmsserver == null) {
         Screen screen2 = new RealmsMainScreen(new TitleScreen());
         p_279320_.setScreen(new DisconnectedScreen(screen2, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
      } else {
         TitleScreen titlescreen = new TitleScreen();
         GetServerDetailsTask getserverdetailstask = new GetServerDetailsTask(new RealmsMainScreen(titlescreen), titlescreen, realmsserver, new ReentrantLock());
         p_279320_.setScreen(new RealmsLongRunningMcoTaskScreen(titlescreen, getserverdetailstask));
      }
   }
}