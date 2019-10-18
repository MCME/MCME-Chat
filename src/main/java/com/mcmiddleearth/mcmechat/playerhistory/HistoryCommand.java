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
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class HistoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(cs instanceof Player && !((Player) cs).hasPermission("mcmechat.history")) {
            ChatPlugin.getMessageUtil().sendNoPermissionError(cs);
            return true;
        }
        if(args.length<2) {
            sendHelpMessage(cs);
            return true;
        }
        OfflinePlayer player = Bukkit.getPlayer(args[0]);
        if(player.getLastPlayed()==0) {
            ChatPlugin.getMessageUtil().sendErrorMessage(cs, "Unkown player.");
            return true;
        }
        String subcommand = args[1];
        if(subcommand.equalsIgnoreCase("show")) {
            if(!PlayerHistoryData.hasPlayerHistory(player)) {
                ChatPlugin.getMessageUtil().sendInfoMessage(cs, "No History found for "+player.getName());
                return true;
            }
            HistoryData data = PlayerHistoryData.getPlayerHistory(player);
            ChatPlugin.getMessageUtil().sendInfoMessage(cs,"MCME History of "+player.getName());
            for(int i=0; i<data.getHistory().length;i++) {
                String line= data.getHistory()[i];
                if(cs instanceof Player) {
                    FancyMessage message = new FancyMessage(MessageType.INFO,ChatPlugin.getMessageUtil())
                                            .addClickable("["+i+"] "+line, "/history "+player.getName()+" edit "+i+" "+line)
                                            .send((Player)cs);
                } else {
                    ChatPlugin.getMessageUtil().sendInfoMessage(cs, "["+i+"] "+line);
                }
            }
            if(cs instanceof Player) {
                ChatPlugin.getMessageUtil().sendInfoMessage(cs, ChatColor.GREEN+"Click at a line to edit.");
            }
            return true;
        }
        if(subcommand.equalsIgnoreCase("add")) {
            String entry = concatArgs(args,2);
            HistoryData data = PlayerHistoryData.getPlayerHistory(player);
            data.add(entry);
            PlayerHistoryData.saveToFile();
            ChatPlugin.getMessageUtil().sendInfoMessage(cs, "History entry added.");
            return true;
        }
        
        if(args.length<3 || !NumericUtil.isInt(args[2])) {
            ChatPlugin.getMessageUtil().sendErrorMessage(cs, "Syntax error");
            return true;
        }
        int index = NumericUtil.getInt(args[2]);
        String entry = concatArgs(args,3);
        if(subcommand.equalsIgnoreCase("insert")) {
            HistoryData data = PlayerHistoryData.getPlayerHistory(player);
            data.insert(index, entry);
            PlayerHistoryData.saveToFile();
            ChatPlugin.getMessageUtil().sendInfoMessage(cs, "History entry inserted.");
            return true;
        }
        if(subcommand.equalsIgnoreCase("delete")) {
            HistoryData data = PlayerHistoryData.getPlayerHistory(player);
            data.remove(index);
            if(data.isEmpty()) {
                PlayerHistoryData.remove(player);
            }
            PlayerHistoryData.saveToFile();
            ChatPlugin.getMessageUtil().sendInfoMessage(cs, "History entry deleted.");
            return true;
        }
        if(subcommand.equalsIgnoreCase("edit")) {
            if(!PlayerHistoryData.hasPlayerHistory(player)) {
                ChatPlugin.getMessageUtil().sendInfoMessage(cs, "No History found for "+player.getName());
                return true;
            }
            HistoryData data = PlayerHistoryData.getPlayerHistory(player);
            data.set(index, entry);
            PlayerHistoryData.saveToFile();
            ChatPlugin.getMessageUtil().sendInfoMessage(cs, "History entry edited.");
            return true;
        }
        return false;
    }
    
    private String concatArgs(String[] args, int start) {
        String result = "";
        for(int i=start; i<args.length; i++) {
            result = result + args[i]+" ";
        }
        return result.trim();
    }
    
    private void sendHelpMessage(CommandSender cs) {
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "Usage:");
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "/history <player> show");
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "/history <player> add <history entry>");
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "/history <player> insert <#index> <history entry>");
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "/history <player> remove <#index> <history entry>");
        ChatPlugin.getMessageUtil().sendInfoMessage(cs, "/history <player> edit <#index>");
    }
}
