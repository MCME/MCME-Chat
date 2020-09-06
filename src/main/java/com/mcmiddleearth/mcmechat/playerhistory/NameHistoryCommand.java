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
                    e.printStackTrace();
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
