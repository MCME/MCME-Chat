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
package com.mcmiddleearth.mcmechat.helper;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class HelperCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && !sender.hasPermission("mcmechat.helper.info")) {
            ChatPlugin.getMessageUtil().sendNoPermissionError(sender);
            return true;
        }
        if(args.length==0) {
            ChatPlugin.getMessageUtil().sendInfoMessage(sender, "Help topics:");
            for(String topic: HelperData.getHelpTopics("")) {
                if(sender instanceof Player) {
                    FancyMessage msg = new FancyMessage(MessageType.INFO_INDENTED,ChatPlugin.getMessageUtil())
                                           .addSimple("- ")
                                           .addClickable(ChatPlugin.getMessageUtil().STRESSED + topic,
                                                         "/helper "+topic)
                                           .setRunDirect()
                                           .send((Player)sender);
                } else {
                    ChatPlugin.getMessageUtil().sendIndentedInfoMessage(sender, "- "
                            + ChatPlugin.getMessageUtil().STRESSED + topic);
                }
            }
            return true;
        } else {
            if(args[0].equalsIgnoreCase("devinfo") && sender.isOp()) {
                if(HelperData.copyLog()) {
                    ChatPlugin.getMessageUtil().sendInfoMessage(sender, "Done");
                } else {
                    ChatPlugin.getMessageUtil().sendInfoMessage(sender, "Failed");
                }
                return true;
            }
            List<String> topic = HelperData.getHelp(args[0]);
            if(topic == null) {
                ChatPlugin.getMessageUtil().sendErrorMessage(sender, "No help found for: "+args[0]);
            } else {
                ChatPlugin.getMessageUtil().sendInfoMessage(sender, "Help for " 
                        +ChatPlugin.getMessageUtil().STRESSED + args[0]
                        +ChatPlugin.getMessageUtil().INFO + ":");
                for(String line: topic) {
                    line = line.replace("&", ""+ChatColor.COLOR_CHAR);
                    ChatPlugin.getMessageUtil().sendNoPrefixInfoMessage(sender, 
                        ChatPlugin.getMessageUtil().HIGHLIGHT + line);
                }
            }
            return true;
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
