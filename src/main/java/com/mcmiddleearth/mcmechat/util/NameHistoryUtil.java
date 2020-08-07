package me.gilan.mcmetesting.utils;

import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import com.mcmiddleearth.pluginutil.NumericUtil;
import me.gilan.mcmetesting.MCMEtesting;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NameHistoryUtil implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                //Name (Args[0])
                String name = args[0];

                //Get UUID
                URL url = null;
                try {
                    url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Scanner sc = null;
                try {
                    sc = new Scanner(url.openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) {
                    sb.append(sc.next());

                }

                String result = sb.toString();
                int start = result.lastIndexOf(":") + 2;
                int end = start + 32;
                String PlayerUUID = result.substring(start, end);

                //Get all names from UUID
                URL url1 = null;
                try {
                    url1 = new URL("https://api.mojang.com/user/profiles/" + PlayerUUID + "/names");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Scanner sc1 = null;
                try {
                    sc1 = new Scanner(url1.openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuilder sb1 = new StringBuilder();
                while (sc1.hasNext()) {
                    sb1.append(sc1.next());

                }

                String result1 = sb1.toString();
                List<String> names = new ArrayList<>();
                List<String> dates = new ArrayList<>();

                //NAMES
                int length = result1.length();
                int stringPos = 0;
                for (int i = 0; i < length; i++) {
                    char current = result1.charAt(stringPos);
                    //Checks if at beginning of section
                    if (current == 123) {
                        int start1 = stringPos + 9;
                        int count = 9;
                        while (result1.charAt(stringPos + count) != 125) {
                            if (result1.charAt(stringPos + count) == 58) {
                                int end1 = stringPos + count;
                                String name1 = result1.substring(start1, end1);
                                name1 = name1.replace("\"", "");
                                name1 = name1.replace(",", "");
                                name1 = name1.replaceAll("changedToAt", "");
                                names.add(name1);

                            }
                            count++;
                        }
                    }
                    stringPos++;
                }

                int dateLength = result1.length();
                int datePos = 0;

                //DATES
                for (int i = 0; i < dateLength; i++) {
                    char current = result1.charAt(datePos);
                    if (current == 58) {
                        int num = datePos + 1;
                        if (result1.charAt(num) > 47 && result1.charAt(num) < 58) {
                            int start1 = num;
                            int end1 = num + 13;
                            String date = result1.substring(start1, end1);
                            long milliSec = Long.parseLong(date);
                            DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
                            Date newDate = new Date(milliSec);
                            dates.add(simple.format(newDate));

                        }
                    }
                    datePos++;
                }


                int firstIndex = result1.indexOf("},");
                int start1 = 10;
                int end1 = firstIndex - 1;
                String name1 = result1.substring(start1, end1);
                names.add(0, name1);
                dates.add(0, "Account Created");

                int size = names.size();

                FancyMessage header = new FancyMessage(MessageType.INFO,
                        MCMEtesting.getMessageUtil())
                        .addSimple(ChatColor.DARK_AQUA + "Player Name History ");

                List<FancyMessage> fancyList = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    FancyMessage element = new FancyMessage(MessageType.INFO, MCMEtesting.getMessageUtil())
                            .addSimple(names.get(i) + ", " + dates.get(i));
                    fancyList.add(element);
                }

                int page;
                if(args[1]!=null){
                if (args[1].length() > 0 && NumericUtil.isInt(args[1])) {
                    page = NumericUtil.getInt(args[1]);
                        try {
                            MCMEtesting.getMessageUtil().sendFancyListMessage((Player) sender, header,
                                    fancyList,
                                    "/namehistory", page);
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                            sender.sendMessage(ChatColor.DARK_RED + "Please include player name and page number: /namehistory <name> <page>");
                        }
                    }
                }else{
                    sender.sendMessage(ChatColor.DARK_RED + "Please include player name and page number: /namehistory <name> <page>");
                }

            }
        }else if(args[0].length() == 0 && args[1].length() == 0){
            sender.sendMessage(ChatColor.DARK_RED + "Please include player name and page number: /namehistory <name> <page>");
        }
        return true;
    }
}



