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
package com.mcmiddleearth.mcmechat;

import com.mcmiddleearth.mcmechat.placeholder.MCMEChatPlaceholder;
import com.mcmiddleearth.mcmechat.listener.AfkListener;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.luckperms.LuckPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class ChatPlugin extends JavaPlugin implements CommandExecutor{

    @Getter
    static JavaPlugin instance;
    
    @Getter
    static boolean luckPerms = false;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(getConfig().getBoolean("useLuckPerms")) {
            try {
                LuckPerms.getApi();
                luckPerms = true;
            } catch(IllegalStateException e) {
                Logger.getGlobal().info("LuckPerms not found, using permission attachments.");
            }
        }
        getServer().getPluginManager().registerEvents(new AfkListener(), this);
        getCommand("mcmechat").setExecutor(this);
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholderHook("mcmeChat", new MCMEChatPlaceholder());
        } else {
            Logger.getGlobal().warning("PlaceholderAPI not enabled");
        }
        instance = this;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("&cAccess denied.");
            return true;
        }
        if(args.length<1) {
            return false;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            this.reloadConfig();
            sender.sendMessage("Reloading config.");
            return true;
        }
        return false;
    }
    
    public static String getTabColorPermission() {
        return instance.getConfig().getString("playerTabList.tabColorPermission");
    }
    
    public static String getAfkColor() {
        return instance.getConfig().getString("playerTabList.afkColor");
    }
    
    public static String getConfigStringFromList(String key, String defaultText) {
        List<String> lines = instance.getConfig().getStringList(key);
        if(lines==null) {
            return defaultText;
        }
        String result = "";
        for(String line:lines) {
            if(!result.equals("")) {
                result = result + "\n";
            }
            result = result + line;
        }
        return result;
    }
 
    public static String getConfigString(String key, String defaultText) {
        String result = instance.getConfig().getString(key);
        if(result==null) {
            return defaultText;
        }
        return result;
    }

}
