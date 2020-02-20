/*
 * Copyright (C) 2020 MCME
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
package com.mcmiddleearth.mcmechat.console;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import github.scarsz.discordsrv.dependencies.jda.core.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
//org.apache.logging.log4j.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class DiscordPublishHandler extends Handler {

    private Handler buffer;
            
    public DiscordPublishHandler() {
        buffer = new MemoryHandler(this,100,Level.ALL);
        buffer.setLevel(Level.ALL);
        buffer.setFilter(record -> true);
        Logger.getLogger("").addHandler(buffer);
        /*Enumeration<String> names =LogManager.getLogManager().getLoggerNames();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            Logger.getGlobal().info(name);
            Logger.getLogger(name).addHandler(buffer);
        }
        //Bukkit.getServer().getLogger().addHandler(buffer);
        //Logger.getGlobal().addHandler(buffer);
        //((StatusLogger)MinecraftServer.LOGGER).registerListener(new MyStatusListener());
        /*for(Handler han: Bukkit.getServer().getLogger().getHandlers()) {
            Logger.getGlobal().info(han.getClass().getName());
        }
        Logger.getGlobal().info(""+Bukkit.getServer().getLogger().getLevel());
        System.out.*/
/*Logger.getGlobal().info("Add handler");
        new BukkitRunnable() {
            @Override
            public void run() {
                String test=null;
                //TextChannel channel = DiscordUtil.getTextChannelById("678638557560176691");
                //DiscordUtil.sendMessage(channel, "btest");
                //channel = DiscordUtil.getTextChannelById("678638557560176691");
                //DiscordUtil.sendMessage(channel, "test");
                Logger.getGlobal().info(test.toLowerCase());
            }
        }.runTaskTimer(ChatPlugin.getInstance(), 200, 400);*/
    }
    
    @Override
    public void publish(LogRecord record) {
        //System.out.println("Console message: "+record.getMessage());
        if(record.getLoggerName().equalsIgnoreCase("DiscordSRV") || ChatPlugin.getDiscordConsoleChannel()==null) {
            return;
        }
        try{
            TextChannel channel = ChatPlugin.getDiscordConsoleChannel();//DiscordUtil.getTextChannelById(ChatPlugin.getDiscordConsoleChannel());
            if(channel!=null) {
                Date date = new Date(record.getMillis());
                
                String message = "["+new SimpleDateFormat("hh:mm:ss").format(date)+" "
                                                    +record.getLevel()
                                                    +"]: ["+record.getLoggerName()+"] "
                                                    +record.getMessage();
                if(record.getThrown()!=null) {
                    message = message+"\n"+record.getThrown().getClass().getName()+" "+record.getThrown().getMessage();
                    for(StackTraceElement element: record.getThrown().getStackTrace()) {
                        message = message+ "\n      at "+element.toString();   
                    }
                }
                DiscordUtil.sendMessage(channel, message);
            }
        } catch(Throwable ex) {
            //System.out.println(ex.getMessage());
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
    
    public void remove() {
        Bukkit.getServer().getLogger().removeHandler(buffer);
        Logger.getGlobal().removeHandler(buffer);
        buffer.close();
    }
    
    public class MyStatusListener implements StatusListener{
        
        @Override
        public void log(StatusData data) {
            System.out.println(data.getMessage());
        }
        
        @Override
        public org.apache.logging.log4j.Level getStatusLevel() {
            return org.apache.logging.log4j.Level.ALL;
        }

        @Override
        public void close() throws IOException {
        }
    }
}
