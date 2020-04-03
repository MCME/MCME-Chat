/*
 * Copyright (C) 2018 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.mcmechat.listener;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.mcmechat.util.LuckPermsUtil;
//import static com.mcmiddleearth.mcmechat.listener._invalid_AfkListener.showAttachments;
import com.mcmiddleearth.mcmechat.util.TabUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

/**
 *
 * @author Eriol_Eandur
 */
public class AfkListener implements Listener{
    

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void afkStatusChange(AfkStatusChangeEvent event) {
        if(event.getValue()) {
            setAfk(event.getAffected().getBase());
        } else {
            removeAfk(event.getAffected().getBase());
        }
    }
    
    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if(ChatPlugin.isLuckPerms()) {
            removeAfkLuckPerms(event.getPlayer());
        } else {
            attachments.remove(event.getPlayer().getUniqueId());
        }
    }
     
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        if(ChatPlugin.isLuckPerms()) {
            removeAfkLuckPerms(event.getPlayer());
        }
    }
     
    private static void setAfk(Player player) {
        if(ChatPlugin.isLuckPerms()) {
            setAfkLuckPerms(player);
        } else {
            setAfkAttachment(player);
        }
    }
    
    private static void removeAfk(Player player) {
        if(ChatPlugin.isLuckPerms()) {
            removeAfkLuckPerms(player);
        } else {
            removeAfkAttachment(player);
        }
    }
    
    private static void setAfkLuckPerms(Player player) {
        LuckPerms lpApi = LuckPermsUtil.getApi();
        Node afkNode = PermissionNode.builder(ChatPlugin.getTabColorPermission()
                                       +TabUtil.getTabColor(ChatPlugin.getAfkColor()))
                            //.setValue(true)
                            .build();
       
        User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if(user == null) {
            return;
        }
        DataMutateResult result = user.getData(DataType.NORMAL).add(afkNode);
        for(ChatColor color : ChatColor.values()) {
            if(!color.name().equals(ChatPlugin.getAfkColor())) {
                Node tabColorNode = PermissionNode.builder(ChatPlugin.getTabColorPermission()
                                       +TabUtil.getTabColor(color.name())).negated(true).build();
                user.getData(DataType.NORMAL).add(tabColorNode);
            }
        }
        lpApi.getUserManager().saveUser(user); 
        user.getCachedData().invalidate();
    }
  
    public static void removeAfkLuckPerms(Player player) {
        LuckPerms lpApi = LuckPermsUtil.getApi();
        User user = lpApi.getUserManager().getUser(player.getUniqueId());
        if(user == null) {
            return;
        }
        for(ChatColor color : ChatColor.values()) {
            Node tabColorNode = PermissionNode.builder(ChatPlugin.getTabColorPermission()
                                   +TabUtil.getTabColor(color.name())).build();
            user.getData(DataType.NORMAL).remove(tabColorNode);
        }
        lpApi.getUserManager().saveUser(user);        
        user.getCachedData().invalidate();
    }
 
    private static Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    private static void setAfkAttachment(Player player) {
        //showAttachments(player);
        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        if(attachment==null) {
            attachment = player.addAttachment(ChatPlugin.getInstance());
            attachments.put(player.getUniqueId(), attachment);
        }
        attachment.setPermission(ChatPlugin.getTabColorPermission()
                                       +TabUtil.getTabColor(ChatPlugin.getAfkColor()), true);
        for(ChatColor color : ChatColor.values()) {
            if(!color.name().equals(ChatPlugin.getAfkColor())) {
                attachment.setPermission(ChatPlugin.getTabColorPermission()+TabUtil.getTabColor(color.name()), false);
            }
            //player.recalculatePermissions();
        }
    }
  
    public static void removeAfkAttachment(Player player) {
        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        if(attachment!=null) {
            player.removeAttachment(attachment);
            attachments.remove(player.getUniqueId());
            attachment = player.addAttachment(ChatPlugin.getInstance());
            for(ChatColor color : ChatColor.values()) {
                attachment.unsetPermission(ChatPlugin.getTabColorPermission()+TabUtil.getTabColor(color.name()));
            }
            attachments.put(player.getUniqueId(), attachment);
            player.recalculatePermissions();
            //showAttachments(player);
        }
        /*PermissionAttachment attachment = getAfkAttachment(player);
        if(attachment!=null) {
            player.removeAttachment(attachment);
            player.recalculatePermissions();
        }*/
    }
    
    /*private static PermissionAttachment getAfkAttachment(Player player) {
        for(PermissionAttachmentInfo info: player.getEffectivePermissions()) {
            PermissionAttachment attachment = info.getAttachment();
            if(attachment!=null && attachment.getPlugin().equals(ChatPlugin.getInstance())) {
                return attachment;
            }
        }
        return null;
    }*/
    
}
