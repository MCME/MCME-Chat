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

import com.mcmiddleearth.mcmechat.console.DiscordPublishHandler;
import com.mcmiddleearth.mcmechat.helper.HelperCommand;
import com.mcmiddleearth.mcmechat.helper.HelperData;
import com.mcmiddleearth.mcmechat.listener.AfkListener;
import com.mcmiddleearth.mcmechat.listener.PlayerListener;
import com.mcmiddleearth.mcmechat.placeholder.MCMEChatPlaceholder;
import com.mcmiddleearth.mcmechat.playerhistory.HistoryCommand;
import com.mcmiddleearth.mcmechat.playerhistory.PlayerHistoryData;
import com.mcmiddleearth.mcmechat.reporting.ReportCommand;
import com.mcmiddleearth.mcmechat.util.LuckPermsUtil;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class ChatPlugin extends JavaPlugin implements CommandExecutor{

    private static JavaPlugin instance;
    
    private static boolean luckPerms = false;
    
    private static TextChannel discordConsoleChannel = null;
    
    private static MessageUtil messageUtil = new MessageUtil();
    
    private static DiscordPublishHandler consolePublisher;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(getConfig().getBoolean("useLuckPerms")) {
            try {
                LuckPermsUtil.getApi();
                luckPerms = true;
            } catch(IllegalStateException e) {
                Logger.getGlobal().info("LuckPerms not found, using permission attachments.");
            }
        }
        setDiscordConsoleChannel();
        messageUtil.setPluginName("MCME-Chat");
        instance = this;
        PlayerHistoryData.loadFromFile();
        getServer().getPluginManager().registerEvents(new AfkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("mcmechat").setExecutor(this);
        getCommand("history").setExecutor(new HistoryCommand());
        getCommand("report").setExecutor(new ReportCommand());
        HelperData.init();
        getCommand("helper").setExecutor(new HelperCommand());
        getCommand("namehistory").setExecutor(new NameHistoryCommand());
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new MCMEChatPlaceholder().register();
        /*if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholderHook("mcmeChat", new MCMEChatPlaceholder());*/
        } else {
            Logger.getGlobal().warning("PlaceholderAPI not enabled");
        }
        //Bukkit.getServer().getMessenger()
        //        .registerIncomingPluginChannel(this, Server.BROADCAST_CHANNEL_ADMINISTRATIVE, new ConsoleListener());
        consolePublisher = new DiscordPublishHandler();
    }
    
    @Override
    public void onDisable() {
        consolePublisher.remove();
    }
    
    public static JavaPlugin getInstance() {
        return instance;
    }
    public static boolean isLuckPerms() {
        return luckPerms;
    }
    public static TextChannel getDiscordConsoleChannel() {
        return discordConsoleChannel;
    }
    public static MessageUtil getMessageUtil() {
        return messageUtil;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && !((Player) sender).hasPermission("mcmechat.reload")) {
            ChatPlugin.getMessageUtil().sendNoPermissionError(sender);
            return true;
        }
        if(args.length<1) {
            return false;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            this.reloadConfig();
            PlayerHistoryData.loadFromFile();
            HelperData.init();
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

    public static List<String> getConfigStringList(String key) {
        return instance.getConfig().getStringList(key);
    }
    
    private void setDiscordConsoleChannel() {
        new BukkitRunnable() {
            int counter = 30;
            @Override
            public void run() {
                String channelName = getConfig().getString("discordConsoleChannel");
                if(channelName!=null && !channelName.equals("")) {
                  DiscordSRV discordPlugin = DiscordSRV.getPlugin();
                  if (discordPlugin != null)
                  {
                    TextChannel channel = discordPlugin.getDestinationTextChannelForGameChannelName(channelName);
                    if (channel != null) {
                        discordConsoleChannel = channel;
                        cancel();
                    }
                  } else {
                    Logger.getLogger(ChatPlugin.class.getName()).warning("DiscordSRV plugin not found. Console forwarding to Discord disabled!");
                  }
                }
                counter--;
                if(counter == 0) {
                    Logger.getLogger(ChatPlugin.class.getName()).warning("Channel "+channelName+" not found. Console forwarding to Discord disabled!");
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static boolean isLuckPerms() {
        return luckPerms;
    }

    public static TextChannel getDiscordConsoleChannel() {
        return discordConsoleChannel;
    }

    public static MessageUtil getMessageUtil() {
        return messageUtil;
    }



}
