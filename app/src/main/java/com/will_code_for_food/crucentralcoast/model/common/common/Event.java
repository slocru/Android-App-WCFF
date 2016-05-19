package com.will_code_for_food.crucentralcoast.model.common.common;


import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.Logger;
import com.will_code_for_food.crucentralcoast.controller.api_interfaces.CalendarAccessor;
import com.will_code_for_food.crucentralcoast.values.Database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Gavin on 1/13/2016.
 */
public class Event extends DatabaseObject {
    private CalendarEvent calendarEvent;
    private ArrayList<String> parentMinistries;

    //for testing...
    public Event (String id) {
        super(id);
    }

    public Event(JsonObject json) {
        super(json);

        loadParentMinistries();
        updateCalendarEvent();
    }

    public Event(JsonObject json, boolean testMode) {
        super(json);

        if (!testMode) {
            loadParentMinistries();
            updateCalendarEvent();
        }
    }

    // Gets the description of this event
    public String getDescription() {
        return getFieldAsString(Database.JSON_KEY_COMMON_DESCRIPTION);
    }

    // Gets whether ride sharing is enabled for this event
    public boolean hasRideSharing() {
        return getField(Database.JSON_KEY_EVENT_HASRIDES).getAsBoolean();
    }

    // Gets the url of the Facebook page for this event
    public String getFacebookLink() {
        return getFieldAsString(Database.JSON_KEY_COMMON_URL);
    }

    // Checks if there is a Facebook page for this event
    public boolean hasFacebook() {
        String url = getFacebookLink();
        return url != null && !url.equals("");
    }

    public Date getDate() {
        return super.getDate(Database.JSON_KEY_EVENT_STARTDATE);
    }

    // Gets the date of the event in reader format
    public String getEventDate() {
        return super.getFormattedDate(Database.JSON_KEY_EVENT_STARTDATE, Database.EVENT_DATE_FORMAT);
    }

    // Gets the start and end dates of the event in reader format
    public String getEventFullDate() {
        return getEventDate() + " - " +
                super.getFormattedDate(Database.JSON_KEY_EVENT_ENDDATE, Database.EVENT_DATE_FORMAT);
    }

    // Gets the address of the event in reader format
    public String getEventLocation() {
        JsonObject eventLoc = getField(Database.JSON_KEY_COMMON_LOCATION).getAsJsonObject();
        String street = eventLoc.get(Database.JSON_KEY_COMMON_LOCATION_STREET).getAsString();
        String suburb = eventLoc.get(Database.JSON_KEY_COMMON_LOCATION_SUBURB).getAsString();
        String state = eventLoc.get(Database.JSON_KEY_COMMON_LOCATION_STATE).getAsString();

        return street + ", " + suburb + " " + state;
    }

    // Checks if the event has a valid location
    public boolean hasLocation() {
        JsonObject eventLoc = getField(Database.JSON_KEY_COMMON_LOCATION).getAsJsonObject();
        String street = eventLoc.get(Database.JSON_KEY_COMMON_LOCATION_STREET).getAsString();
        String suburb = eventLoc.get(Database.JSON_KEY_COMMON_LOCATION_SUBURB).getAsString();

        return !street.equals(Database.EVENT_BAD_LOCATION) &&
                !suburb.equals(Database.EVENT_BAD_LOCATION);
    }

    public ArrayList<String> getParentMinistries() {
        return parentMinistries;
    }

    /**
     * Saves the event to the user's calendar
     */
    public void saveToCalendar(final Activity currentActivity) {
        updateCalendarEvent();
        Logger.i("Accessing Calendar", "Creating new event");
        CalendarAccessor.addEventToCalendar(calendarEvent, currentActivity);
    }

    /**
     * Parses the JsonArray that contains the parent Ministries associated with this object.
     */
    private void loadParentMinistries() {
        parentMinistries = new ArrayList<>();
        JsonArray ministriesJson = this.getField(Database.JSON_KEY_EVENT_MINISTRIES).getAsJsonArray();

        for (JsonElement ministry : ministriesJson) {
            if (ministry.isJsonPrimitive()) {
                parentMinistries.add(ministry.getAsString());
            }
        }
    }

    /**
     * Refills all fields of the event from the information in the database
     */
    private void updateCalendarEvent() {
        final DateFormat isoFormater = new SimpleDateFormat(Database.ISO_FORMAT);
        Date startTime;
        Date endTime;
        Date reminderTime;
        long reminderMinutesBefore = CalendarEvent.DEFAULT_REMINDER_TIME;

        try {
            startTime = isoFormater.parse(getFieldAsString(Database.JSON_KEY_EVENT_STARTDATE));
            try {
                endTime = isoFormater.parse(getFieldAsString(Database.JSON_KEY_EVENT_ENDDATE));
            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(startTime);
                calendar.add(Calendar.HOUR, 1);
                endTime = calendar.getTime();
                Logger.e("Event Creation", "Unable to get event end time for " + getName());
            }

            try {
                reminderTime = isoFormater.parse(getFieldAsString(Database.JSON_KEY_EVENT_REMINDER));
                long diff =(int)(endTime.getTime() - reminderTime.getTime());
                if (diff > 0) {
                    reminderMinutesBefore = diff / (60 * 1000) % 60;
                    // TODO switch to Joda-Time instead of java.util.Date
                }
            } catch (java.text.ParseException | NullPointerException ex) {
                Logger.i("Event Creation", "Unable to get event reminder time for " + getName());
            }

            calendarEvent = new CalendarEvent(getId(), getName(), getDescription(),
                    getEventLocation(), startTime.getTime(),
                    endTime.getTime(), reminderMinutesBefore);
            
        } catch (java.text.ParseException ex) {
            Logger.e("Event Creation", "Unable to get mandatory data for" + getName());
            ex.printStackTrace();
        }
    }

    public boolean isInCalendarAlready() {
        return CalendarAccessor.isAlreadyAdded(this);
    }
}