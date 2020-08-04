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

import com.earth2me.essentials.Essentials;
import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.mcmechat.playerhistory.HistoryData;
import com.mcmiddleearth.mcmechat.playerhistory.PlayerHistoryData;
import com.mcmiddleearth.mcmechat.util.LuckPermsUtil;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.ess3.api.IUser;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class MCMEChatPlaceholder extends PlaceholderExpansion {

    private static final String LF = "\n";
    
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if(!offlinePlayer.isOnline()) {
            return "player not online";
        }
Logger.getGlobal().info("itentifier: "+identifier);
        Player p = offlinePlayer.getPlayer();
        if(p==null) {
            return "null player";
        }
        switch(identifier) {
            case "color":
                return getRankColor(p);
            case "moderator_prefix":
                return getModerator(p);
            case "badge_suffix":
                return getBadgeSuffix(p);
            case "rank":
                return getRank(p);
            case "name":
                return p.getName()+(hasHistory(p)?ChatPlugin.getConfigString("historyBadge", ChatColor.DARK_GRAY+"H"):"");
            case "prefix_hover_text": 
                return getDescription(p, "prefixHover.intro","prefixHover.details","prefixHover.finish" );
            case "prefix_click_text":
                return getDescription(p, "prefixClick", "none","none");
            case "name_hover_text":
                return getDescription(p, "nameHover.intro", "nameHover.details", "nameHover.finish");
            case "name_click_text":
                return getDescription(p, "nameClick", "none","none");
            case "suffix_hover_text":
                String result = "";
                boolean hasBadge = hasBadge(p);
                boolean hasHistory = hasHistory(p);
//Logger.getGlobal().info("hasbadge: "+hasBadge+" hasHistory: "+hasHistory);
                if(hasBadge) {
                    result = getDescription(p, "suffixHover.intro","suffixHover.details","suffixHover.finish" );
                }
                /*if(hasBadge && hasHistory) {
                    result = result + LF +LF;
                }
                if(hasHistory) {
                    result = result +getHistory(p);
                }*/
                return result;
            case "suffix_click_text":
                return getDescription(p, "suffixClick", "none","none");
            default:
                Logger.getGlobal().info("ERROR - unknown identifier: "+identifier);
                return "";
        }
    }
    
    private String getModerator(Player p) {
        if(p==null) {
            return "null Player";
        }
        if(p.hasPermission("group.badge_moderator")) {
            return "&6M";
        }
        return "";
    }
    
    private String getRank(Player p) {
        if (p == null) {
            return "null Player";
        }

        if(p.hasPermission("group.root")) {
            return getRankColor(p)+"Eru";
        } else if(p.hasPermission("group.buildvalar")
                || p.hasPermission("group.enforcervalar")
                || p.hasPermission("group.valar")
                || p.hasPermission("group.headdeveloper")
                || p.hasPermission("group.headguide")) {
            return getRankColor(p)+"Head";
        } else if(p.hasPermission("group.designer")
                || p.hasPermission("group.assistant")) {
            return getRankColor(p)+"Staff";
        } else if(p.hasPermission("group.guide") 
                || p.hasPermission("group.artist")
                || p.hasPermission("group.foreman")
                || p.hasPermission("group.badge_moderator")) {
            return getRankColor(p)+"Team";
        } else if (p.hasPermission("group.default")
                || p.hasPermission("group.adventurer")
                || p.hasPermission("group.commoner")) {
            return getRankColor(p)+"Fellow";
        } else if (p.hasPermission("group.oathbreaker")) {
            return getRankColor(p)+"Oathbreaker";
        }
        return "???";
    }
    
    private String getBadgeSuffix(Player p) {
        if(p==null) {
            return "null Player";
        }
        if(p.hasPermission("group.badge_minigames")
        || p.hasPermission("group.badge_tours")
        || p.hasPermission("group.badge_animations")
        || p.hasPermission("group.badge_worldeditfull")
        || p.hasPermission("group.badge_worldeditlimited")
        || p.hasPermission("group.badge_voxel")) {
            return "~";
        }
        return "";
    }
    
    private String getRankColor(Player p) {
        if(p==null) {
            return "null Player";
        }
        Plugin essPlugin = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if(essPlugin != null) {
            Essentials ess = (Essentials) essPlugin;
            IUser iUser = ess.getUser(p);
            if(iUser!=null && iUser.isAfk()) {
                return "&8";
            }
        }
        if(ChatPlugin.isLuckPerms()) {
            LuckPerms api = LuckPermsUtil.getApi();
            User user = api.getUserManager().getUser(p.getUniqueId());
            if(user == null) {
                return "";
            }
            SortedMap<Integer, String> prefixes = user.getCachedData().getMetaData(QueryOptions.nonContextual()).getPrefixes();
            //Optional<Entry<Integer, String>> maxPrefix = user.getNodes()
                //.filter(node -> node instanceof PrefixNode)
                //.map(node -> new SimpleEntry<>(((PrefixNode) node).getPriority(),((PrefixNode) node).getMetaValue()))
                //.max((entry1, entry2) -> entry1.getKey() > entry2.getKey() ? 1 : -1);
            String color;
            if(!prefixes.isEmpty()) {
                color = prefixes.get(prefixes.firstKey());
                if(color.length()>1 && color.charAt(0) == '&') {
                    if(color.length()>3 && color.charAt(2) == '&') {
                        color = color.substring(0, 4);
                    } else {
                        color = color.substring(0, 2);
                    }
                } else {
                    color = "";
                }
            } else {
                color = "";
            }
            
//Logger.getGlobal().info("tt"+color+"test");
            return color;
        }
        return "";
    }
    
    private String getDescription(Player p, String introKey, String detailKey, String finishKey) {
        String result = ChatPlugin.getConfigStringFromList("chatDecorations."+introKey, "");
        Entry<Integer,String> maxSuffix = null;
        Entry<Integer,String> maxPrefix = null;
        if(p==null) {
            return "null Player";
        }
        if(ChatPlugin.isLuckPerms()) {
            LuckPerms api = LuckPermsUtil.getApi();
            User user = api.getUserManager().getUser(p.getUniqueId());
            if(user == null) {
                return "";
            }
            SortedSet<Node> nodes = (SortedSet<Node>) user.getDistinctNodes();
            List<ComparableDescription> lines = new ArrayList<>();
            for(Node userNode: nodes) {
                if(userNode instanceof InheritanceNode) {
                    Group group = api.getGroupManager().getGroup(((InheritanceNode)userNode).getGroupName());
                    if(group != null) {
                        SimpleEntry<Integer,String> prefix = null;
                        for(Node groupNode:group.getDistinctNodes()) {
                            if(groupNode instanceof PrefixNode 
                                    && (prefix == null || prefix.getKey() < ((PrefixNode)groupNode).getPriority())) { 
                                prefix = new SimpleEntry(((PrefixNode)groupNode).getPriority(),((PrefixNode)groupNode).getMetaValue());
                            }
                        }
                        Entry<Integer,String> suffix = null;
                        for(Node groupNode:group.getDistinctNodes()) {
                            if(groupNode instanceof SuffixNode 
                                    && (suffix == null || suffix.getKey() < ((SuffixNode)groupNode).getPriority())) { 
                                suffix = new SimpleEntry(((SuffixNode)groupNode).getPriority(),((SuffixNode)groupNode).getMetaValue());
                            }
                        }
                        String description = ChatPlugin.getConfigStringFromList("chatDecorations."+detailKey+"."
                                                                   +((InheritanceNode)userNode).getGroupName(),"");
                        int sortKey = 0;
                        if(prefix != null) {
                            description = description.replace("{Prefix}", prefix.getValue().trim());
                            if(maxPrefix == null || maxPrefix.getKey() < prefix.getKey()) { 
                                maxPrefix = prefix;
                            }
                            sortKey = prefix.getKey();
                        }
                        if(suffix != null) {
                            description = description.replace("{Suffix}", suffix.getValue().trim());
                            if(maxSuffix == null || maxSuffix.getKey() < suffix.getKey()) { 
                                maxSuffix = suffix;
                            }
                            sortKey = suffix.getKey();
                        }
                        if(!description.equals("")) {
                            lines.add(new ComparableDescription(sortKey,description));
                            //result = result + LF + description;
                        }
                    }
                }
            }
            lines.sort(null);
            for(ComparableDescription line: lines) {
                result = result + LF + line.getDescription();
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
                              formatTimespan(p.getStatistic(Statistic.PLAY_ONE_MINUTE)*50l)) //50long
                     .replace("{player_world}", p.getWorld().getName())
                     .replace("{player_walked}", 
                              formatDistance(p.getStatistic(Statistic.WALK_ONE_CM)/100l)) //100long
                     .replace("{player_flown}", 
                              formatDistance(p.getStatistic(Statistic.FLY_ONE_CM)/100l)) //100long
                     .replace("{player_color}",color)
                     .replace("{player_history}", getHistory(p));
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
    
    private String getHistory(Player p) {
        if(p!=null && PlayerHistoryData.hasPlayerHistory(p)) {
            HistoryData data = PlayerHistoryData.getPlayerHistory(p);
            if(data!=null) {
                String result = ChatPlugin.getConfigStringFromList("chatDecorations.playerHistory.intro", "");
                List<String> colors = ChatPlugin.getConfigStringList("chatDecorations.playerHistory.details.colors");
                for(int i=0; i<data.getHistory().length;i++) {
                    String entry = data.getHistory()[i];
                    String color = colors.get(i%colors.size());
                    result = result + LF +color+entry.replace(" ", " "+color);
                }
                String finish = ChatPlugin.getConfigStringFromList("playerHistory.finish", "");
                if(!finish.equalsIgnoreCase("")) {
                    result = result + LF + finish;
                }
                return result;
            }
        }
        return "";
    }
    
    private boolean hasHistory(Player p) {
        return PlayerHistoryData.hasPlayerHistory(p);
    }
    
    private boolean hasBadge(Player p) {
        if(ChatPlugin.isLuckPerms()) {
            LuckPerms api = LuckPermsUtil.getApi();
            User user = api.getUserManager().getUser(p.getUniqueId());
            if(user == null) {
                return false;
            }
            SortedSet<Node> nodes = (SortedSet<Node>) user.getDistinctNodes();
            for(Node userNode: nodes) {
                if(userNode instanceof InheritanceNode) {
                    Group group = api.getGroupManager().getGroup(((InheritanceNode)userNode).getGroupName());
                    if(group != null) {
                        for(Node groupNode:group.getDistinctNodes()) {
                            if(groupNode instanceof SuffixNode) { 
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getIdentifier() {
        return "mcmeChat";
    }

    @Override
    public String getAuthor() {
        return "Eriol_Eandur";
    }

    @Override
    public String getVersion() {
        return "1";
    }

    class ComparableDescription implements Comparable {

        private final Integer key;
        
        private final String description;
        
        public ComparableDescription(int key, String description) {
            this.description = description;
            this.key = key;
        }
        
        @Override
        public int compareTo(Object o) {
            if(o instanceof ComparableDescription) {
                return this.getKey().compareTo(((ComparableDescription)o).getKey());
            } else {
                return -1;
            }
        }

        public Integer getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }
    }
}
