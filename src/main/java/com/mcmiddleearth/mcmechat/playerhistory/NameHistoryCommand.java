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

import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import me.gilan.mcmetesting.MCMEtesting;
import me.gilan.mcmetesting.utils.NameHistoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
/*
*
*@author Gilan
*
*/
public class NameHistoryCommand implements CommandExecutor {

    int page;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length!=0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String name = args[0];
                try {
                    page = Integer.parseInt(args[1]);
                } catch (ArrayIndexOutOfBoundsException e){
                    page = 1;
                }

                if (!args[0].matches("^[a-zA-Z0-9_]+$")) {
                    sender.sendMessage(ChatColor.DARK_RED + "Please only type in characters that are allowed in a minecraft username.");
                    return true;
                }
              
                List<FancyMessage> fancyList = NameHistoryUtil.getFancyList(name);
                if(fancyList.size()<1){
                    sender.sendMessage(ChatColor.DARK_RED + "That player does not exist! Make sure you have entered the name correctly or check to make sure they have not changed it.");
                    return true;
                }

                FancyMessage header = new FancyMessage(MessageType.INFO, MCMEtesting.getMessageUtil())
                        .addSimple(ChatColor.DARK_AQUA + "Name History of Player: " + name);

                try {
                    MCMEtesting.getMessageUtil().sendFancyListMessage((Player) sender, header,
                            fancyList,
                            "/namehistory", page);
                }catch (NullPointerException e) {
                    Logger.getLogger(ChatPlugin.getInstance().getClass().getSimpleName()).log(Level.WARNING,null, e);
                    sender.sendMessage(ChatColor.DARK_RED + "Please include player name and page number: /namehistory <name> <page>");
                }
            }
        }
        else{
            sender.sendMessage(ChatColor.DARK_RED + "Please include player name and page number: /namehistory <name> <page>");
        }

        return true;
    }
}
