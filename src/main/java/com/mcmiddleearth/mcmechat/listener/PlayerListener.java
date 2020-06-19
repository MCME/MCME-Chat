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
import org.bukkit.configuration.ConfigurationSection;

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
        if(joinMsg==null){
            joinMsg = chatPlugin.getConfig().createSection("join_messages");
        }
        
        ConfigurationSection root = joinMsg.getConfigurationSection("group_root");
        if(root==null){ root = joinMsg.createSection("group_root"); }
        ConfigurationSection buildvalar = joinMsg.getConfigurationSection("group_buildvalar");
        if(buildvalar==null){ buildvalar = joinMsg.createSection("group_buildvalar"); }
        ConfigurationSection enforcevalar = joinMsg.getConfigurationSection("group_enforcevalar");
        if(enforcevalar==null){ enforcevalar = joinMsg.createSection("group_enforcevalar"); }
        ConfigurationSection valar = joinMsg.getConfigurationSection("group_valar");
        if(valar==null){ valar = joinMsg.createSection("group_valar"); }
        ConfigurationSection headdeveloper = joinMsg.getConfigurationSection("group_headdeveloper");
        if(headdeveloper==null){ headdeveloper = joinMsg.createSection("group_headdeveloper"); }
        ConfigurationSection headguide = joinMsg.getConfigurationSection("group_headguide");
        if(headguide==null){ headguide = joinMsg.createSection("group_headguide"); }
        ConfigurationSection designer = joinMsg.getConfigurationSection("group_designer");
        if(designer==null){ designer = joinMsg.createSection("group_designer"); }
        ConfigurationSection assistant = joinMsg.getConfigurationSection("group_assistant");
        if(assistant==null){ assistant = joinMsg.createSection("group_assistant"); }
        ConfigurationSection guide = joinMsg.getConfigurationSection("group_guide");
        if(guide==null){ guide = joinMsg.createSection("group_guide"); }
        ConfigurationSection artist = joinMsg.getConfigurationSection("group_artist");
        if(artist==null){ artist = joinMsg.createSection("group_artist"); }
        ConfigurationSection foreman = joinMsg.getConfigurationSection("group_foreman");
        if(foreman==null){ foreman = joinMsg.createSection("group_foreman"); }
        ConfigurationSection badgemod = joinMsg.getConfigurationSection("group_badgemoderator");
        if(badgemod==null){ badgemod = joinMsg.createSection("group_badgemoderator"); }
        ConfigurationSection DEFAULT = joinMsg.getConfigurationSection("group_default");
        if(DEFAULT==null){ DEFAULT = joinMsg.createSection("group_default"); }
        ConfigurationSection adventurer = joinMsg.getConfigurationSection("group_adventurer");
        if(adventurer==null){ adventurer = joinMsg.createSection("group_adventurer"); }
        ConfigurationSection commoner = joinMsg.getConfigurationSection("group_commoner");
        if(commoner==null){ commoner = joinMsg.createSection("group_commoner"); }
        ConfigurationSection oathbreaker = joinMsg.getConfigurationSection("group_oathbreaker");
        if(oathbreaker==null){ oathbreaker = joinMsg.createSection("group_oathbreaker"); }
        
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
