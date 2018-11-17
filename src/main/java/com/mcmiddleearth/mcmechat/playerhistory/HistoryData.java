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
package com.mcmiddleearth.mcmechat.playerhistory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eriol_Eandur
 */
public class HistoryData {
    
    
    private List<String> historyData = new ArrayList<>();
    
    public HistoryData() {
    }
    
    public HistoryData(List<String> data) {
        historyData = data;
    }
    
    public void add(String history) {
        historyData.add(history);
    }
    
    public void insert(int index, String history) {
        List<String> newHistory = new ArrayList<>();
        for(int i=0; i<index && i<historyData.size(); i++) {
            newHistory.add(historyData.get(i));
        }
        newHistory.add(history);
        for(int i=index; i<historyData.size();i++) {
            newHistory.add(historyData.get(i));
        }
        historyData = newHistory;
    }
    
    public void remove(int index) {
        List<String> newHistory = new ArrayList<>();
        for(int i=0; i<index && i<historyData.size(); i++) {
            newHistory.add(historyData.get(i));
        }
        for(int i=index+1; i<historyData.size();i++) {
            newHistory.add(historyData.get(i));
        }
        historyData = newHistory;
    }
    
    public boolean isEmpty() {
        return historyData.isEmpty();
    }
    
    public String get(int index) {
        return historyData.get(index);
    }
    
    public void set(int index, String history) {
        remove(index);
        insert(index, history);
    }
    
    public String[] getHistory() {
        return historyData.toArray(new String[0]);
    }
    
    public String getHistory(int index) {
        return historyData.get(index);
    }
}
