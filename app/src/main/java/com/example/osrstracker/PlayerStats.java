package com.example.osrstracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PlayerStats {
    public enum Skill {
        Overall, Attack, Defence, Strength, Hitpoints, Ranged, Prayer, Magic, Cooking, Woodcutting, Fletching, Fishing, Firemaking, Crafting, Smithing, Mining, Herblore, Agility, Thieving, Slayer, Farming, Runecrafting, Hunter, Construction;
    }

    private Map<Skill, Integer> skillRankMap;
    private Map<Skill, Integer> skillExperienceMap;


    public PlayerStats(String response) {
        skillExperienceMap = new HashMap<>();
        skillRankMap = new HashMap<>();
        loadFieldsFromResponse(response);
    }

    public void loadFieldsFromResponse(String response) {
        Scanner scanner = new Scanner(response);
        for (Skill currentSkill : Skill.values()) {
            String currentLine = scanner.nextLine();
            Scanner smallScanner = new Scanner(currentLine).useDelimiter(",");
            skillRankMap.put(currentSkill, Integer.parseInt(smallScanner.next()));
            smallScanner.next();
            skillExperienceMap.put(currentSkill, Integer.parseInt(smallScanner.next()));
            smallScanner.close();
        }
        scanner.close();
    }


    public static int xpToLevel(int experience) {
        return 0;
    }

    public int getLevel(Skill skill) {
        return xpToLevel(getXP(skill));
    }

    public int getXP(Skill skill) {
        return skillExperienceMap.get(skill);
    }

    public int getRank(Skill skill) {
        return skillRankMap.get(skill);
    }


}
