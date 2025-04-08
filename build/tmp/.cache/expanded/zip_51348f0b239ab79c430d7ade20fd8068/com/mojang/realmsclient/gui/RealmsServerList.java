package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServerList {
   private final Minecraft minecraft;
   private final Set<RealmsServer> removedServers = Sets.newHashSet();
   private List<RealmsServer> servers = Lists.newArrayList();

   public RealmsServerList(Minecraft p_239233_) {
      this.minecraft = p_239233_;
   }

   public List<RealmsServer> updateServersList(List<RealmsServer> p_239869_) {
      List<RealmsServer> list = new ArrayList<>(p_239869_);
      list.sort(new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
      boolean flag = list.removeAll(this.removedServers);
      if (!flag) {
         this.removedServers.clear();
      }

      this.servers = list;
      return List.copyOf(this.servers);
   }

   public synchronized List<RealmsServer> removeItem(RealmsServer p_240077_) {
      this.servers.remove(p_240077_);
      this.removedServers.add(p_240077_);
      return List.copyOf(this.servers);
   }
}