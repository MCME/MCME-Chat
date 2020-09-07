 /* Copyright (C) 2018 MCME
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
 * @author Gilan
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
        
        ConfigurationSection joinMsg = ChatPlugin.getInstance().getConfig().getConfigurationSection("join_messages");
        if(joinMsg==null){
            joinMsg = chatPlugin.getConfig().createSection("join_messages");
        }

        for(String key : joinMsg.getKeys(false)){
            if(p.hasPermission("group." + key)){
                for(String line : joinMsg.getStringList(key)){
                   p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
        }
        
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MineverseChatPlayer mcp = MineverseChatAPI
                                  .getMineverseChatPlayer(event.getPlayer().getUniqueId());
        mcp.clearListening();
    }
}
