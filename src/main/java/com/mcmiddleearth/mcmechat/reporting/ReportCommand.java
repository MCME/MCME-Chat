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
package com.mcmiddleearth.mcmechat.reporting;

import com.mcmiddleearth.mcmechat.helper.*;
import com.mcmiddleearth.mcmechat.ChatPlugin;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.core.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ReportCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && !sender.hasPermission("mcmechat.reporter")) {
            ChatPlugin.getMessageUtil().sendNoPermissionError(sender);
            return true;
        }
        if(args.length==0) {
            ChatPlugin.getMessageUtil().sendNotEnoughArgumentsError(sender);
            return true;
        }
        String message = "";
        for(String word: args) {
            message = message+" "+word;
        }
        final Player reporter = (Player) sender;
        final String header = reporter.getName()+" filed a report:\n";
        final String finalMessage = message;
        Bukkit.getOnlinePlayers().stream()
              .filter(player -> player.hasPermission(ChatPlugin.getConfigString("report.receiverPermission", 
                                                                                "venturechat.moderationchannel")))
              .forEach(player -> player.sendMessage(""+ChatColor.GOLD+ChatColor.ITALIC+ChatColor.BOLD
                                               +header
                                               +ChatColor.GOLD+ChatColor.ITALIC+finalMessage));
        sendDiscord(header+finalMessage);
        ChatPlugin.getMessageUtil().sendInfoMessage(reporter, "Your report has been sent to the moderation team.");
        return true;
    }
    
    private void sendDiscord(String message) {
        String discordChannel = ChatPlugin.getConfigString("report.discordChannel", "reports");
        if ((discordChannel != null) && (!discordChannel.equals("")))
        {
          DiscordSRV discordPlugin = DiscordSRV.getPlugin();
          if (discordPlugin != null)
          {
            TextChannel channel = discordPlugin.getDestinationTextChannelForGameChannelName(discordChannel);
            if (channel != null) {
              DiscordUtil.sendMessage(channel, message, 0, false);
            } else {
              Logger.getLogger("ChatPlugin").warning("Chat Plugin: Discord channel not found.");
            }
          }
          else
          {
            Logger.getLogger("ChatPlugin").warning("ChatPlugin: DiscordSRV plugin not found.");
          }
        }
    }
    
    

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return HelperData.getHelpTopics(args[0]);
            default:
                return null;
        }
    }
    
}
