package com.will_code_for_food.crucentralcoast.model.common.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.values.Database;

import java.util.ArrayList;

/**
 * Created by Gavin on 11/12/2015.
 * Updated by Mason on 11/22/2015
 */
public class Ministry extends DatabaseObject {
    private ArrayList<String> campuses;

    public Ministry(JsonObject obj) {
        super(obj);

        campuses = new ArrayList<String>();

        JsonElement campusList = this.getField(Database.JSON_KEY_MINISTRY_CAMPUSES);

        if (campusList.isJsonArray()) {
            for (JsonElement campus : campusList.getAsJsonArray()) {
                campuses.add(campus.getAsString());
            }
        }

        else {
            System.out.println("campusList is not an array");
        }
    }

    public ArrayList<String> getCampuses() {
        return campuses;
    }
}
