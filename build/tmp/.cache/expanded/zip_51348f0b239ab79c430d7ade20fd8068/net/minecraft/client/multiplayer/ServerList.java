package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ProcessorMailbox<Runnable> IO_MAILBOX = ProcessorMailbox.create(Util.backgroundExecutor(), "server-list-io");
   private static final int MAX_HIDDEN_SERVERS = 16;
   private final Minecraft minecraft;
   private final List<ServerData> serverList = Lists.newArrayList();
   private final List<ServerData> hiddenServerList = Lists.newArrayList();

   public ServerList(Minecraft p_105430_) {
      this.minecraft = p_105430_;
   }

   public void load() {
      try {
         this.serverList.clear();
         this.hiddenServerList.clear();
         CompoundTag compoundtag = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
         if (compoundtag == null) {
            return;
         }

         ListTag listtag = compoundtag.getList("servers", 10);

         for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag1 = listtag.getCompound(i);
            ServerData serverdata = ServerData.read(compoundtag1);
            if (compoundtag1.getBoolean("hidden")) {
               this.hiddenServerList.add(serverdata);
            } else {
               this.serverList.add(serverdata);
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Couldn't load server list", (Throwable)exception);
      }

   }

   public void save() {
      try {
         ListTag listtag = new ListTag();

         for(ServerData serverdata : this.serverList) {
            CompoundTag compoundtag = serverdata.write();
            compoundtag.putBoolean("hidden", false);
            listtag.add(compoundtag);
         }

         for(ServerData serverdata1 : this.hiddenServerList) {
            CompoundTag compoundtag2 = serverdata1.write();
            compoundtag2.putBoolean("hidden", true);
            listtag.add(compoundtag2);
         }

         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("servers", listtag);
         File file2 = File.createTempFile("servers", ".dat", this.minecraft.gameDirectory);
         NbtIo.write(compoundtag1, file2);
         File file3 = new File(this.minecraft.gameDirectory, "servers.dat_old");
         File file1 = new File(this.minecraft.gameDirectory, "servers.dat");
         Util.safeReplaceFile(file1, file2, file3);
      } catch (Exception exception) {
         LOGGER.error("Couldn't save server list", (Throwable)exception);
      }

   }

   public ServerData get(int p_105433_) {
      return this.serverList.get(p_105433_);
   }

   @Nullable
   public ServerData get(String p_233846_) {
      for(ServerData serverdata : this.serverList) {
         if (serverdata.ip.equals(p_233846_)) {
            return serverdata;
         }
      }

      for(ServerData serverdata1 : this.hiddenServerList) {
         if (serverdata1.ip.equals(p_233846_)) {
            return serverdata1;
         }
      }

      return null;
   }

   @Nullable
   public ServerData unhide(String p_233848_) {
      for(int i = 0; i < this.hiddenServerList.size(); ++i) {
         ServerData serverdata = this.hiddenServerList.get(i);
         if (serverdata.ip.equals(p_233848_)) {
            this.hiddenServerList.remove(i);
            this.serverList.add(serverdata);
            return serverdata;
         }
      }

      return null;
   }

   public void remove(ServerData p_105441_) {
      if (!this.serverList.remove(p_105441_)) {
         this.hiddenServerList.remove(p_105441_);
      }

   }

   public void add(ServerData p_233843_, boolean p_233844_) {
      if (p_233844_) {
         this.hiddenServerList.add(0, p_233843_);

         while(this.hiddenServerList.size() > 16) {
            this.hiddenServerList.remove(this.hiddenServerList.size() - 1);
         }
      } else {
         this.serverList.add(p_233843_);
      }

   }

   public int size() {
      return this.serverList.size();
   }

   public void swap(int p_105435_, int p_105436_) {
      ServerData serverdata = this.get(p_105435_);
      this.serverList.set(p_105435_, this.get(p_105436_));
      this.serverList.set(p_105436_, serverdata);
      this.save();
   }

   public void replace(int p_105438_, ServerData p_105439_) {
      this.serverList.set(p_105438_, p_105439_);
   }

   private static boolean set(ServerData p_233840_, List<ServerData> p_233841_) {
      for(int i = 0; i < p_233841_.size(); ++i) {
         ServerData serverdata = p_233841_.get(i);
         if (serverdata.name.equals(p_233840_.name) && serverdata.ip.equals(p_233840_.ip)) {
            p_233841_.set(i, p_233840_);
            return true;
         }
      }

      return false;
   }

   public static void saveSingleServer(ServerData p_105447_) {
      IO_MAILBOX.tell(() -> {
         ServerList serverlist = new ServerList(Minecraft.getInstance());
         serverlist.load();
         if (!set(p_105447_, serverlist.serverList)) {
            set(p_105447_, serverlist.hiddenServerList);
         }

         serverlist.save();
      });
   }
}