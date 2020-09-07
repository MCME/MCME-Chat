package com.mcmiddleearth.mcmechat.helper;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.naming.spi.NamingManager;
import java.util.logging.Logger;

public class HintManager implements Listener {

    NamespacedKey hint_shift_f = new NamespacedKey(ChatPlugin.getInstance(),"hint_shift_f");
    public HintManager() {
        Logger.getGlobal().info("hint loaded!");
        Bukkit.getUnsafe().removeAdvancement(hint_shift_f);
        Bukkit.getUnsafe().loadAdvancement(hint_shift_f,
                                    "{\"display\":{\"title\":\"Did you know?\\nYou can press SHIFT+F\\nor just F\\nbut SHIFT+F is cool\","
                                                    + "\"icon\":{\"item\":\"minecraft:diamond_axe\","
                                                              + "\"nbt\":\"{Damage:35}\"},"
                                                    + "\"description\":\"You can press SHIFT+F\\n to get a collection of similar blocks.\","
                                                    + "\"show_toast\":\"true\","
                                                    + "\"announce_to_chat\":\"false\","
                                                    + "\"frame\":\"challenge\"},"
                                                    + "\"criteria\":{\"shift\":{\"trigger\":\"minecraft:impossible\"}}}");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Logger.getGlobal().info("hint event fired");
        event.getPlayer().getAdvancementProgress(Bukkit.getAdvancement(hint_shift_f)).awardCriteria("shift");
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().getAdvancementProgress(Bukkit.getAdvancement(hint_shift_f)).revokeCriteria("shift");
            }
        }.runTaskLater(ChatPlugin.getInstance(),5);
    }

    public void disable() {
        Bukkit.getUnsafe().removeAdvancement(hint_shift_f);
    }
}
