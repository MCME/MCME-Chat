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
package com.mcmiddleearth.mcmechat.placeholder;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.logging.Logger;
import me.clip.placeholderapi.PlaceholderHook;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class MCMEChatPlaceholder extends PlaceholderHook{

    private static final String LF = "\n";
    
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        switch(identifier) {
            case "prefix_hover_text": 
                return getDescription(p, "prefixHover.intro","prefixHover.details","prefixHover.finish" );
            case "prefix_click_text":
                return getDescription(p, "prefixClick", "none","none");
            case "name_hover_text":
                return getDescription(p, "nameHover.intro", "nameHover.details", "nameHover.finish");
            case "name_click_text":
                return getDescription(p, "nameClick", "none","none");
            case "suffix_hover_text":
                return getDescription(p, "suffixHover.intro","suffixHover.details","suffixHover.finish" );
            case "suffix_click_text":
                return getDescription(p, "suffixClick", "none","none");
            default:
                Logger.getGlobal().info("ERROR - unknown identifier: "+identifier);
                return "";
        }
    }
    
    private String getDescription(Player p, String introKey, String detailKey, String finishKey) {
        String result = ChatPlugin.getConfigStringFromList("chatDecorations."+introKey, "");
        Entry<Integer,String> maxSuffix = null;
        Entry<Integer,String> maxPrefix = null;
        if(ChatPlugin.isLuckPerms()) {
            LuckPermsApi api = LuckPerms.getApi();
            User user = api.getUser(p.getUniqueId());
            if(user == null) {
                return "";
            }
            SortedSet<Node> nodes = (SortedSet<Node>) user.getPermissions();
            for(Node userNode: nodes) {
                if(userNode.isGroupNode()) {
                    Group group = api.getGroup(userNode.getGroupName());
                    if(group != null) {
                        Entry<Integer,String> prefix = null;
                        for(Node groupNode:group.getPermissions()) {
                            if(groupNode.isPrefix() 
                                    && (prefix == null || prefix.getKey() < groupNode.getPrefix().getKey())) { 
                                prefix = groupNode.getPrefix();
                            }
                        }
                        Entry<Integer,String> suffix = null;
                        for(Node groupNode:group.getPermissions()) {
                            if(groupNode.isSuffix() 
                                    && (suffix == null || suffix.getKey() < groupNode.getSuffix().getKey())) { 
                                suffix = groupNode.getSuffix();
                            }
                        }
                        String description = ChatPlugin.getConfigStringFromList("chatDecorations."+detailKey+"."
                                                                   +userNode.getGroupName(),"");
                        if(prefix != null) {
                            description = description.replace("{Prefix}", prefix.getValue().trim());
                            if(maxPrefix == null || maxPrefix.getKey() < prefix.getKey()) { 
                                maxPrefix = prefix;
                            }
                        }
                        if(suffix != null) {
                            description = description.replace("{Suffix}", suffix.getValue().trim());
                            if(maxSuffix == null || maxSuffix.getKey() < suffix.getKey()) { 
                                maxSuffix = suffix;
                            }
                        }
                        if(!description.equals("")) {
                            result = result + LF + description;
                        }
                    }
                }
            }
        }
        String finish = ChatPlugin.getConfigStringFromList("chatDecorations."+finishKey,"");
        if(!finish.equals("")) {
            result = result + LF + finish;
        }
        if(maxSuffix!=null) {
            result = result.replace("{Suffix}", maxSuffix.getValue());
        }
        String color = "&7";
        if(maxPrefix!=null) {
            if(maxPrefix.getValue().startsWith("&")) {
                color = maxPrefix.getValue().substring(0,2);
            }
            result = result.replace("{Prefix}", maxPrefix.getValue());
        }
        return result.replace("{player_name}", p.getName())
                     .replace("{player_first_join}", 
                               new SimpleDateFormat("MMM d, yyyy")
                                       .format(new Date(p.getFirstPlayed())))
                     .replace("{player_time_played}", 
                              formatTimespan(p.getStatistic(Statistic.PLAY_ONE_TICK)*50l))
                     .replace("{player_world}", p.getWorld().getName())
                     .replace("{player_walked}", 
                              formatDistance(p.getStatistic(Statistic.WALK_ONE_CM)/100l))
                     .replace("{player_flown}", 
                              formatDistance(p.getStatistic(Statistic.FLY_ONE_CM)/100l))
                     .replace("{player_color}",color);
    }    

    private String formatTimespan(long timespan) {
        timespan = timespan / 1000l;
        String result = "";
        long years = timespan /(3600l*24l*365l);
        if(years > 0l) {
            result = result + years +" year";
            if(years>1) {
                result = result + "s ";
            } else {
                result = result + " ";
            }
        } 
        if(years < 5l) {
            long months = timespan / (3600l*24l*30l) - years * 12l;
            if(months > 0l) {
                result = result + months + " month";
                if(months>1l) {
                    result = result + "s ";
                } else {
                    result = result + " ";
                }
            }
            if(years < 1l && months < 5l) {
                long days =  timespan / (3600l*24l) - years * 12l - months *30l;
                if(days > 0l) {
                    result = result + days + " day";
                    if(days>1l) {
                        result = result + "s ";
                    } else {
                        result = result + " ";
                    }
                }
                if(years < 1l && months < 1l && days < 5l) {
                    long hours =  timespan / (3600l) - years * 12l - months *30l - days * 24l;
                    if(hours > 0l) {
                        result = result + hours + " hour";
                        if(hours>1l) {
                            result = result + "s ";
                        } else {
                            result = result + " ";
                        }
                    }
                    if(years < 1l && months < 1l && days < 1l && hours < 5l) {
                        long min =  timespan / 60l - years * 12l - months *30l - days * 24l - hours * 60l;
                        if(min > 0l) {
                            result = result + min + " minute";
                            if(min>1l) {
                                result = result + "s ";
                            } else {
                                result = result + " ";
                            }
                        }
                        if(years < 1l && months < 1l && days < 1l && hours < 1l && min < 1l) {
                            long sec = timespan - years * 12l - months *30l - days * 24l - hours * 60l - min * 60l;
                            if(sec > 0l) {
                                result = result + sec + " second";
                                if(sec>1l) {
                                    result = result + "s ";
                                } else {
                                    result = result + " ";
                                }
                            }
                        }
                    }
                }
            }
        }
        return result.trim();
    }
    
    private String formatDistance(long distance) {
        if(distance < 0) {
            return "too far to tell";
        }
        String result = "";
        long gm = distance / 1000000000;
        if(gm > 0 ) {
            result = result + gm + " Mm ";
        }
            long km = distance / 1000 - gm * 1000000;
            if(km > 0 ) {
                result = result + km +" km ";
            }
            if(km < 5) {
                long m =  distance - gm * 1000000 - km * 1000;
                if(m > 0) {
                    result = result + m + " m ";
                }
            }
        return result.trim();
    }
}
