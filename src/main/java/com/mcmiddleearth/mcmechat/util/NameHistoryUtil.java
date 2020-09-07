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
package com.mcmiddleearth.mcmechat.util;

import com.mcmiddleearth.mcmechat.ChatPlugin;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
*
*@author Gilan
*
*/
public class NameHistoryUtil{

     public static List<FancyMessage> getFancyList(String name) {
        //Get UUID
        URL url = null;
        try {
            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        } catch (MalformedURLException e) {
            Logger.getLogger(ChatPlugin.getInstance().getClass().getSimpleName()).log(Level.WARNING,null, e);
        }

        Scanner sc = null;
        try {
            sc = new Scanner(url.openStream());
        } catch (IOException e) {
             Logger.getLogger(ChatPlugin.getInstance().getClass().getSimpleName()).log(Level.WARNING,null, e);
        }
        

        StringBuilder sb = new StringBuilder();
        while (sc.hasNext()) {
            sb.append(sc.next());

        }

        String result = sb.toString();


         List<FancyMessage> fancyList = new ArrayList<>();

         if(result.length()<1){
             return fancyList;
        } 
          
        int start = result.lastIndexOf(":") + 2;
        int end = start + 32;
        String playerUUID = result.substring(start, end);

        //Get all names from UUID
        URL url1 = null;
        try {
            url1 = new URL("https://api.mojang.com/user/profiles/" + playerUUID + "/names");
        } catch (MalformedURLException e) {
             Logger.getLogger(ChatPlugin.getInstance().getClass().getSimpleName()).log(Level.WARNING,null, e);
        }

        Scanner sc1 = null;
        try {
            sc1 = new Scanner(url1.openStream());
        } catch (IOException e) {
             Logger.getLogger(ChatPlugin.getInstance().getClass().getSimpleName()).log(Level.WARNING,null, e);
        }

        StringBuilder sb1 = new StringBuilder();
        while (sc1.hasNext()) {
            sb1.append(sc1.next());

        }

        String info = sb1.toString();


        //NAMES
        List<String> names = new ArrayList<>();

        int length = info.length();
        int stringPos = 0;
        for (int i = 0; i < length; i++) {
            char current = info.charAt(stringPos);
            //Checks if at beginning of section
            if (current == 123) {
                int start1 = stringPos + 9;
                int count = 9;
                while (info.charAt(stringPos + count) != 125) {
                    if (info.charAt(stringPos + count) == 58) {
                        int end1 = stringPos + count;
                        String name1 = info.substring(start1, end1);
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


        //DATES
        List<String> dates = new ArrayList<>();

        int dateLength = info.length();
        int datePos = 0;
        for (int i = 0; i < dateLength; i++) {
            char current = info.charAt(datePos);
            if (current == 58) {
                int num = datePos + 1;
                if (info.charAt(num) > 47 && info.charAt(num) < 58) {
                    int start1 = num;
                    int end1 = num + 13;
                    String date = info.substring(start1, end1);
                    long milliSec = Long.parseLong(date);
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
                    Date newDate = new Date(milliSec);
                    dates.add(simple.format(newDate));

                }
            }
            datePos++;
        }

        int firstIndex = info.indexOf("},");
        if(firstIndex==-1){
            firstIndex = info.indexOf("}]");
        }
        int start1 = 10;
        int end1 = firstIndex - 1;
        String name1 = info.substring(start1, end1);
        names.add(0, name1);
        dates.add(0, "Account Created");


        int size = names.size();


        for (int i = 0; i < size; i++) {
            FancyMessage element = new FancyMessage(MessageType.INFO, ChatPlugin.getMessageUtil())
                    .addSimple(names.get(i) + ", " + dates.get(i));
            fancyList.add(element);
        }


        return fancyList;
    }
}



