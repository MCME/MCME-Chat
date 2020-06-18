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
import com.mcmiddleearth.mcmechat.playerhistory.PlayerHistoryData;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(PlayerHistoryData.hasPlayerHistory(e.getPlayer())) {
            PlayerHistoryData.addBadge(e.getPlayer());
        } else {
            PlayerHistoryData.removeBadge(e.getPlayer());
        }
        if(ChatPlugin.getInstance().getConfig().getBoolean("autoLink",false)) {
            Bukkit.dispatchCommand(e.getPlayer(), "link");
        }
        
        ChatPlugin chatPlugin = new ChatPlugin();

        ConfigurationSection joinMsg = chatPlugin.getConfig().getConfigurationSection("join_messages");

        if(e.getPlayer().hasPermission("group.root")){
            String msg = joinMsg.getString("join_messages.group_root");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.buildvalar")){
            String msg = joinMsg.getString("join_messages.group_buildvalar");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.enforcevalar")){
            String msg = joinMsg.getString("join_messages.group_enforcevalar");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.valar")){
            String msg = joinMsg.getString("join_messages.group_valar");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.headdeveloper")){
            String msg = joinMsg.getString("join_messages.group_headdeveloper");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.headguide")){
            String msg = joinMsg.getString("join_messages.group_headguide");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.designer")){
            String msg = joinMsg.getString("join_messages.group_designer");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.assistant")){
            String msg = joinMsg.getString("join_messages.group_assistant");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.guide")){
            String msg = joinMsg.getString("join_messages.group_guide");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.artist")){
            String msg = joinMsg.getString("join_messages.group_artist");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.foreman")){
            String msg = joinMsg.getString("join_messages.group_foreman");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.badge_moderator")){
            String msg = joinMsg.getString("join_messages.group_badgemoderator");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.default")){
            String msg = joinMsg.getString("join_messages.group_default");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.adventurer")){
            String msg = joinMsg.getString("join_messages.group_adventurer");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.commoner")){
            String msg = joinMsg.getString("join_messages.group_commoner");
            e.getPlayer().sendMessage(msg);
        }else if(e.getPlayer().hasPermission("group.oathbreaker")){
            String msg = joinMsg.getString("join_messages.group_oathbreaker");
            e.getPlayer().sendMessage(msg);
        }
        
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MineverseChatPlayer mcp = MineverseChatAPI
                                  .getMineverseChatPlayer(event.getPlayer().getUniqueId());
        mcp.clearListening();
    }
}
