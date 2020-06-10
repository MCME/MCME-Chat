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
package com.mcmiddleearth.mcmechat.playerhistory;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.mcmechat.util.LuckPermsUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerHistoryData {

    private static final Map<UUID,HistoryData> playerHistory = new HashMap<>();
    
    private static final File PLAYER_HISTORY_FILE = new File(ChatPlugin.getInstance().getDataFolder(),"playerdata.yml");
    
    private static final String BADGE_GROUP = "group.badge_history";
    
    public static HistoryData getPlayerHistory(OfflinePlayer p) {
        HistoryData data = playerHistory.get(p.getUniqueId());
        if(data==null) {
            data = new HistoryData();
            playerHistory.put(p.getUniqueId(),data);
            addBadge(p);
        }
        return data;
    }
    
    public static void remove(OfflinePlayer p) {
        playerHistory.remove(p.getUniqueId());
        removeBadge(p);
    }
    public static boolean hasPlayerHistory(OfflinePlayer p) {
        return playerHistory.get(p.getUniqueId())!=null;
    }
    
    public static void saveToFile() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            for(UUID uuid: playerHistory.keySet()) {
                config.set(uuid.toString(), playerHistory.get(uuid).getHistory());
            }
            config.save(PLAYER_HISTORY_FILE);
        } catch (IOException ex) {
            Logger.getLogger(PlayerHistoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadFromFile() {
        try {
            playerHistory.clear();
            if(PLAYER_HISTORY_FILE.exists()) {
                for(Player player: Bukkit.getOnlinePlayers()) {
                    removeBadge(player);
                }
                YamlConfiguration config = new YamlConfiguration();
                config.load(PLAYER_HISTORY_FILE);
                for(String uuid: config.getKeys(false)) {
                    List<String> lines = config.getStringList(uuid);
                    playerHistory.put(UUID.fromString(uuid), new HistoryData(lines));
                    addBadge(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                }
            }
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(PlayerHistoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void removeBadge(OfflinePlayer p) {
        LuckPerms api = LuckPermsUtil.getApi();
        User user = api.getUserManager().getUser(p.getUniqueId());
        if(user != null) {
            user.getData(DataType.NORMAL).remove(InheritanceNode.builder(BADGE_GROUP).build());
            //api.getUserManager().saveUser(user);
        }
    }
    
    public static void addBadge(OfflinePlayer p) {
        LuckPerms api = LuckPermsUtil.getApi();
        User user = api.getUserManager().getUser(p.getUniqueId());
        if(user != null) {
            user.getData(DataType.NORMAL).add(InheritanceNode.builder(BADGE_GROUP).build());
            //api.getUserManager().saveUser(user);
        }
    }

}
