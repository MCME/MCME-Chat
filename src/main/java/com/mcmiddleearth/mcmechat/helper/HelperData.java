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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Eriol_Eandur
 */
public class HelperData {
    
    private static Map<String,List<String>> helpTopics = new HashMap<>();
    
    public static void init() {
        helpTopics.clear();
        ConfigurationSection config = ChatPlugin.getInstance().getConfig();
        ConfigurationSection section = config.getConfigurationSection("helper");
        if(section==null) {
            section = config.createSection("helper");
        }
        for(String command: section.getKeys(false)) {
            List<String> help = section.getStringList(command);
            helpTopics.put(command, help);
        }
    }
    
    public static List<String> getHelp(String command) {
        return helpTopics.get(command.toLowerCase());
    }
    
    public static List<String> getHelpTopics(String prefix) {
        List<String> topics = new ArrayList<>();
        for(String topic: helpTopics.keySet()) {
            if(prefix.equals("") || topic.startsWith(prefix)) {
                topics.add(topic);
            }
        }
        Collections.sort(topics);
        return topics;
    }
    
    public static boolean copyLog() {
        File log = new File("logs" + System.getProperty("file.separator") + "latest.log");
        File copy = new File(System.getProperty("file.separator")+"home"
                           +System.getProperty("file.separator")+"devserver"
                           +System.getProperty("file.separator")+"latest.log");
        try(Scanner in = new Scanner(log)) {
            PrintStream out = new PrintStream(new FileOutputStream(copy));
            while(in.hasNextLine()) {
                out.println(in.nextLine());
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
}
