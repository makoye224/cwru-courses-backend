package com.example.lambda.util;

import java.util.ArrayList;
import java.util.List;

public class Roster {
    public int maxCount = 24;
    public List<String> studentNames = new ArrayList<>();

    public List<String> organizeRoster(List<String> studentNames, int maxCount) {

        return new ArrayList<>(studentNames);
    }

    public String findName(List<String> roster, String name) {
        return find(roster, name, 0);
    }

    private String find(List<String> roster, String name, int index) {
        if (index >= roster.size()) {
            return null;
        }
        if(name.equals(roster.get(index))) {
            return name;
        }
        return find(roster, name, index + 1);
    }
 }
