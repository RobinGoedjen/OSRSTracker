package com.example.osrstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String USER_TB = "USER_TB";
    private static final String SKILL_TB = "SKILL_TB";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USER_ID = "UserID";
    private static final String COLUMN_CREATION_TIME = "CreationTime";

    private static final String COLUMN_OVERALL_RANK = "Overall_Rank";
    private static final String COLUMN_OVERALL_XP = "Overall_XP";
    private static final String COLUMN_ATTACK_RANK = "Attack_Rank";
    private static final String COLUMN_ATTACK_XP = "Attack_XP";
    private static final String COLUMN_DEFENCE_RANK = "Defence_Rank";
    private static final String COLUMN_DEFENCE_XP = "Defence_XP";
    private static final String COLUMN_STRENGTH_RANK = "Strength_Rank";
    private static final String COLUMN_STRENGTH_XP = "Strength_XP";
    private static final String COLUMN_HITPOINTS_RANK = "Hitpoints_Rank";
    private static final String COLUMN_HITPOINTS_XP = "Hitpoints_XP";
    private static final String COLUMN_RANGED_RANK = "Ranged_Rank";
    private static final String COLUMN_RANGED_XP = "Ranged_XP";
    private static final String COLUMN_PRAYER_RANK = "Prayer_Rank";
    private static final String COLUMN_PRAYER_XP = "Prayer_XP";
    private static final String COLUMN_MAGIC_RANK = "Magic_Rank";
    private static final String COLUMN_MAGIC_XP = "Magic_XP";
    private static final String COLUMN_COOKING_RANK = "Cooking_Rank";
    private static final String COLUMN_COOKING_XP = "Cooking_XP";
    private static final String COLUMN_WOODCUTTING_RANK = "Woodcutting_Rank";
    private static final String COLUMN_WOODCUTTING_XP = "Woodcutting_XP";
    private static final String COLUMN_FLETCHING_RANK = "Fletching_Rank";
    private static final String COLUMN_FLETCHING_XP = "Fletching_XP";
    private static final String COLUMN_FISHING_RANK = "Fishing_Rank";
    private static final String COLUMN_FISHING_XP = "Fishing_XP";
    private static final String COLUMN_FIREMAKING_RANK = "Firemaking_Rank";
    private static final String COLUMN_FIREMAKING_XP = "Firemaking_XP";
    private static final String COLUMN_CRAFTING_RANK = "Crafting_Rank";
    private static final String COLUMN_CRAFTING_XP = "Crafting_XP";
    private static final String COLUMN_SMITHING_RANK = "Smithing_Rank";
    private static final String COLUMN_SMITHING_XP = "Smithing_XP";
    private static final String COLUMN_MINING_RANK = "Mining_Rank";
    private static final String COLUMN_MINING_XP = "Mining_XP";
    private static final String COLUMN_HERBLORE_RANK = "Herblore_Rank";
    private static final String COLUMN_HERBLORE_XP = "Herblore_XP";
    private static final String COLUMN_AGILITY_RANK = "Agility_Rank";
    private static final String COLUMN_AGILITY_XP = "Agility_XP";
    private static final String COLUMN_THIEVING_RANK = "Thieving_Rank";
    private static final String COLUMN_THIEVING_XP = "Thieving_XP";
    private static final String COLUMN_SLAYER_RANK = "Slayer_Rank";
    private static final String COLUMN_SLAYER_XP = "Slayer_XP";
    private static final String COLUMN_FARMING_RANK = "Farming_Rank";
    private static final String COLUMN_FARMING_XP = "Farming_XP";
    private static final String COLUMN_RUNECRAFT_RANK = "Runecraft_Rank";
    private static final String COLUMN_RUNECRAFT_XP = "Runecraft_XP";
    private static final String COLUMN_HUNTER_RANK = "Hunter_Rank";
    private static final String COLUMN_HUNTER_XP = "Hunter_XP";
    private static final String COLUMN_CONSTRUCTION_RANK = "Construction_Rank";
    private static final String COLUMN_CONSTRUCTION_XP = "Construction_XP";

    private static final String[] skillColumns = {COLUMN_OVERALL_RANK, COLUMN_OVERALL_XP, COLUMN_ATTACK_RANK, COLUMN_ATTACK_XP, COLUMN_DEFENCE_RANK, COLUMN_DEFENCE_XP, COLUMN_STRENGTH_RANK, COLUMN_STRENGTH_XP, COLUMN_HITPOINTS_RANK, COLUMN_HITPOINTS_XP,
            COLUMN_RANGED_RANK, COLUMN_RANGED_XP, COLUMN_PRAYER_RANK, COLUMN_PRAYER_XP, COLUMN_MAGIC_RANK, COLUMN_MAGIC_XP, COLUMN_COOKING_RANK, COLUMN_COOKING_XP, COLUMN_WOODCUTTING_RANK, COLUMN_WOODCUTTING_XP, COLUMN_FLETCHING_RANK, COLUMN_FLETCHING_XP,
            COLUMN_FISHING_RANK, COLUMN_FISHING_XP, COLUMN_FIREMAKING_RANK, COLUMN_FIREMAKING_XP, COLUMN_CRAFTING_RANK, COLUMN_CRAFTING_XP, COLUMN_SMITHING_RANK, COLUMN_SMITHING_XP, COLUMN_MINING_RANK, COLUMN_MINING_XP, COLUMN_HERBLORE_RANK, COLUMN_HERBLORE_XP,
            COLUMN_AGILITY_RANK, COLUMN_AGILITY_XP, COLUMN_THIEVING_RANK, COLUMN_THIEVING_XP, COLUMN_SLAYER_RANK, COLUMN_SLAYER_XP, COLUMN_FARMING_RANK, COLUMN_FARMING_XP, COLUMN_RUNECRAFT_RANK, COLUMN_RUNECRAFT_XP, COLUMN_HUNTER_RANK, COLUMN_HUNTER_XP,
            COLUMN_CONSTRUCTION_RANK, COLUMN_CONSTRUCTION_XP};

    public DatabaseManager(@Nullable Context context) {
        super(context, "OSRSTracker.db", null, 1);
    }

    public  boolean registerUser(String userName) {
        if (this.getUserIDByName(userName) > -1)
            return true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, userName);
        long result = db.insert(USER_TB, null, cv);
        return result != -1;
    }

    public boolean addTimestamp(String userName, PlayerStats playerStats) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        int currentColumn = 0;
        cv.put(COLUMN_USER_ID, this.getUserIDByName(userName));
        for (PlayerStats.Skill currentSkill : PlayerStats.Skill.values()) {
            cv.put(skillColumns[currentColumn], playerStats.getRank(currentSkill));
            currentColumn++;
            cv.put(skillColumns[currentColumn], playerStats.getXP(currentSkill));
            currentColumn++;
            Log.i("TEST", skillColumns[currentColumn - 1]);
        }
        long result = db.insert(SKILL_TB, null, cv);
        return result != -1;
    }

    private int getUserIDByName(String userName) {
        int result = -1;
        String query = "SELECT " + COLUMN_ID + " FROM " + USER_TB + " WHERE " + COLUMN_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{userName});
        if (cursor.moveToFirst())
            result = cursor.getInt(0);

        cursor.close();
        return result;
    }


    public  boolean deleteUser(String userName) {
        return true;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        String createTableStatement = "CREATE TABLE \"" + USER_TB + "\" (" + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT(12) NOT NULL);";
        db.execSQL(createTableStatement);
        createTableStatement = "CREATE TABLE " + SKILL_TB + " (\n" +
                "\t" + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t" + COLUMN_USER_ID + " INTEGER NOT NULL,\n" +
                "\t" + COLUMN_CREATION_TIME + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\t" + COLUMN_OVERALL_RANK + " INTEGER,\n" +
                "\t" + COLUMN_OVERALL_XP + " INTEGER,\n" +
                "\t" + COLUMN_ATTACK_RANK + " INTEGER,\n" +
                "\t" + COLUMN_ATTACK_XP + " INTEGER,\n" +
                "\t" + COLUMN_DEFENCE_RANK + " INTEGER,\n" +
                "\t" + COLUMN_DEFENCE_XP + " INTEGER,\n" +
                "\t" + COLUMN_STRENGTH_RANK + " INTEGER,\n" +
                "\t" + COLUMN_STRENGTH_XP + " INTEGER,\n" +
                "\t" + COLUMN_HITPOINTS_RANK + " INTEGER,\n" +
                "\t" + COLUMN_HITPOINTS_XP + " INTEGER,\n" +
                "\t" + COLUMN_RANGED_RANK + " INTEGER,\n" +
                "\t" + COLUMN_RANGED_XP + " INTEGER,\n" +
                "\t" + COLUMN_PRAYER_RANK + " INTEGER,\n" +
                "\t" + COLUMN_PRAYER_XP + " INTEGER,\n" +
                "\t" + COLUMN_MAGIC_RANK + " INTEGER,\n" +
                "\t" + COLUMN_MAGIC_XP + " INTEGER,\n" +
                "\t" + COLUMN_COOKING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_COOKING_XP + " INTEGER,\n" +
                "\t" + COLUMN_WOODCUTTING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_WOODCUTTING_XP + " INTEGER,\n" +
                "\t" + COLUMN_FLETCHING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_FLETCHING_XP + " INTEGER,\n" +
                "\t" + COLUMN_FISHING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_FISHING_XP + " INTEGER,\n" +
                "\t" + COLUMN_FIREMAKING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_FIREMAKING_XP + " INTEGER,\n" +
                "\t" + COLUMN_CRAFTING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_CRAFTING_XP + " INTEGER,\n" +
                "\t" + COLUMN_SMITHING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_SMITHING_XP + " INTEGER,\n" +
                "\t" + COLUMN_MINING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_MINING_XP + " INTEGER,\n" +
                "\t" + COLUMN_HERBLORE_RANK + " INTEGER,\n" +
                "\t" + COLUMN_HERBLORE_XP + " INTEGER,\n" +
                "\t" + COLUMN_AGILITY_RANK + " INTEGER,\n" +
                "\t" + COLUMN_AGILITY_XP + " INTEGER,\n" +
                "\t" + COLUMN_THIEVING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_THIEVING_XP + " INTEGER,\n" +
                "\t" + COLUMN_SLAYER_RANK + " INTEGER,\n" +
                "\t" + COLUMN_SLAYER_XP + " INTEGER,\n" +
                "\t" + COLUMN_FARMING_RANK + " INTEGER,\n" +
                "\t" + COLUMN_FARMING_XP + " INTEGER,\n" +
                "\t" + COLUMN_RUNECRAFT_RANK + " INTEGER,\n" +
                "\t" + COLUMN_RUNECRAFT_XP + " INTEGER,\n" +
                "\t" + COLUMN_HUNTER_RANK + " INTEGER,\n" +
                "\t" + COLUMN_HUNTER_XP + " INTEGER,\n" +
                "\t" + COLUMN_CONSTRUCTION_RANK + " INTEGER,\n" +
                "\t" + COLUMN_CONSTRUCTION_XP + " INTEGER,\n" +
                "\tCONSTRAINT FK_" + COLUMN_USER_ID + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES USER_TB(" + COLUMN_ID + ") ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
