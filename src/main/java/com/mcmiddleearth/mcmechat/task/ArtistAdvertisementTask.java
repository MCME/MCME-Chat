package com.mcmiddleearth.mcmechat.task;

import com.avaje.ebean.text.json.JsonReadBeanVisitor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.mcmechat.util.LuckPermsUtil;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.Bukkit;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

public class ArtistAdvertisementTask implements Runnable {

    @Override
    public void run() {
        List<String> lines = ChatPlugin.getConfigStringList("artistAdvertisementMessage");
        Bukkit.getOnlinePlayers().forEach(player -> {
            String group = LuckPermsUtil.getApi().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
            if(group.equalsIgnoreCase("commoner") || group.equalsIgnoreCase("default")) {
                for (String line : lines) {
                    player.sendMessage(ComponentSerializer.parse(line));
                }
            }
        });

    }
}
