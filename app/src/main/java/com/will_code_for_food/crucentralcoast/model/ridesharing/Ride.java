package com.will_code_for_food.crucentralcoast.model.ridesharing;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.will_code_for_food.crucentralcoast.model.common.common.DatabaseObject;
import com.will_code_for_food.crucentralcoast.model.common.common.Location;
import com.will_code_for_food.crucentralcoast.model.common.common.RestUtil;
import com.will_code_for_food.crucentralcoast.model.common.common.Util;
import com.will_code_for_food.crucentralcoast.model.common.common.users.Gender;
import com.will_code_for_food.crucentralcoast.model.common.common.users.Passenger;
import com.will_code_for_food.crucentralcoast.values.Database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A ride, stored in the database.
 */
public class Ride extends DatabaseObject {
    /**
     * these could be final, but it would mean
     * creating a JSON object in the second
     * constructor to satisfy the need to call
     * super()
     */

    private String eventId;
    private String driverName;
    private String driverNumber;
    private String gcmId;
    private Location location;
    private Double radius;
    private Integer numSeats;
    private RideDirection direction;
    private String gender;
    private String time;

    private List<String> riderIds;
    private List<Passenger> ridersToEvent;
    private List<Passenger> ridersFromEvent;

    /**
     * The inherited constructor, used to build a ride when
     * it's pulled from the database.
     */
    public Ride(final JsonObject obj) {
        super(obj);
        ridersToEvent = new ArrayList<>();
        ridersFromEvent = new ArrayList<>();
        riderIds = new ArrayList<>();
        refreshFields();
    }

    /**
     * Manually building a ride based on it's fields, which will
     * be converted into a JSON object like the ones stored in
     * the database, and created using the inherited constructor.
     */
    public Ride(String eventId, String driverName, String driverNumber, String gcmId,
                Location location, Double radius, Integer numSeats, RideDirection direction,
                String gender) {
        super(new JsonObject()); // satisfies need to call super

        this.eventId = eventId;
        this.driverName = driverName;
        this.driverNumber = driverNumber;
        this.gcmId = gcmId;
        this.location = location;
        this.radius = radius;
        this.numSeats = numSeats;
        this.direction = direction;
        this.gender = gender;

        if (this.direction == null) {
            Log.e("Ride Error", "Given NULL ride direction. Setting as default.");
            this.direction = RideDirection.ONE_WAY_TO_EVENT;
            this.direction.setLeaveTimeFromEvent(Calendar.getInstance());
        }
        SimpleDateFormat f = new SimpleDateFormat(Database.ISO_FORMAT);
        this.time = f.format(direction.getLeaveTimeToEvent().getTime());

        ridersToEvent = new ArrayList<>();
        ridersFromEvent = new ArrayList<>();
        riderIds = new ArrayList<>();
    }

    public void refreshFields() {
        // TODO need to update BOTH passenger lists
        eventId = getFieldAsString(Database.JSON_KEY_RIDE_EVENT);
        driverName = getFieldAsString(Database.JSON_KEY_RIDE_DRIVER_NAME);
        driverNumber = getFieldAsString(Database.JSON_KEY_RIDE_DRIVER_NUMBER);
        gcmId = getFieldAsString(Database.JSON_KEY_RIDE_GCM);
        location = (getField(Database.JSON_KEY_RIDE_LOCATION) != null) ? new Location(getField(Database.JSON_KEY_RIDE_LOCATION)) : null;
        time = getFieldAsString(Database.JSON_KEY_RIDE_TIME);
        radius = getFieldAsDouble(Database.JSON_KEY_RIDE_RADIUS);
        numSeats = getFieldAsInt(Database.JSON_KEY_RIDE_SEATS);
        direction = RideDirection.fromString(getFieldAsString(Database.JSON_KEY_RIDE_DIRECTION));
        gender = getFieldAsString(Database.JSON_KEY_RIDE_GENDER);

        if (getField(Database.JSON_KEY_RIDE_PASSENGERS) != null) {
            for (JsonElement passenger : getField(Database.JSON_KEY_RIDE_PASSENGERS).getAsJsonArray()) {
                riderIds.add(passenger.getAsString());
            }
        }

        // TODO this keeps happening, and is a problem
        if (direction == null) {
            Log.e("Ride Error", "Could not get direction from the database. Setting as default.");
            direction = RideDirection.ONE_WAY_TO_EVENT;
        }
    }

    public boolean isFullToEvent() {
        if (isToEvent()) {
            return getNumAvailableSeatsToEvent() == 0;
        } else {
            return true;
        }
    }

    public boolean isFullFromEvent() {
        if (isFromEvent()) {
            return getNumAvailableSeatsFromEvent() == 0;
        } else {
            return true;
        }
    }

    public int getNumSeats() {
        JsonElement field = getField(Database.JSON_KEY_RIDE_SEATS);
        int numSeats = 0;

        if (field != null) {
            numSeats = field.getAsInt();
        }

        return numSeats;
    }

    public int getNumAvailableSeatsToEvent() {
        //TODO: Make this work with only to riders
        JsonArray passengers = getField(Database.JSON_KEY_RIDE_PASSENGERS).getAsJsonArray();
        /*
        JsonArray allRiders = RestUtil.get(Database.REST_PASSENGER);
        int numSeats = getNumSeats();

        for (int i = 0; i < allRiders.size(); i++) {
            JsonObject rider = allRiders.get(i).getAsJsonObject();
            String riderDirection = rider.get("direction").getAsString();
            if (passengers.contains(rider.get("_id")) &&
                    (riderDirection == "both" || riderDirection == "to")) {
                numSeats--;
            }
        }
        return numSeats;
        */
        return getNumSeats() - passengers.size();
    }

    public int getNumAvailableSeatsFromEvent() {
        //TODO: Make this work with only from riders
        JsonArray passengers = getField(Database.JSON_KEY_RIDE_PASSENGERS).getAsJsonArray();
        /*
        JsonArray allRiders = RestUtil.get(Database.REST_PASSENGER);
        int numSeats = getNumSeats();

        for (int i = 0; i < allRiders.size(); i++) {
            JsonObject rider = allRiders.get(i).getAsJsonObject();
            String riderDirection = rider.get("direction").getAsString();
            if (passengers.contains(rider.get("_id")) &&
                    (riderDirection == "both" || riderDirection == "from")) {
                numSeats--;
            }
        }
        return numSeats;
        */
        return getNumSeats() - passengers.size();
    }

    public boolean addRiderToEvent(final Passenger rider) {
        if (!isFullToEvent()) {
            ridersToEvent.add(rider);
        }

        //driver.notify(Resources.getSystem().getString(R.string.ridesharing_driver_notification));
        return false;
    }

    public boolean addRiderFromEvent(final Passenger rider) {
        if (!isFullFromEvent()) {
            ridersFromEvent.add(rider);
        }

        //driver.notify(Resources.getSystem().getString(R.string.ridesharing_driver_notification));
        return false;
    }

    public void addRiderTwoWay(final Passenger rider) {
        addRiderFromEvent(rider);
        addRiderToEvent(rider);
    }

    /**
     * Checks this ride for a particular passenger with phone number phoneNum
     */
    public boolean hasPassenger(String id) {
        return riderIds.contains(id);
    }

    public String getEventId() {
        return getFieldAsString(Database.JSON_KEY_RIDE_EVENT);
    }

    public String getDriverName() {
        return getFieldAsString(Database.JSON_KEY_RIDE_DRIVER_NAME);
    }

    public String getDriverNumber() {
        return getFieldAsString(Database.JSON_KEY_RIDE_DRIVER_NUMBER);
    }

    public String getLeaveTime() {
        JsonElement date = this.getField(Database.JSON_KEY_RIDE_TIME);
        String rideTime;

        // Convert ISODate to Java Date format
        try {
            DateFormat dateFormat = new SimpleDateFormat(Database.ISO_FORMAT);
            Date start = dateFormat.parse(date.getAsString());
            SimpleDateFormat format = new SimpleDateFormat(Database.RIDE_TIME_FORMAT);
            rideTime = format.format(start);
        } catch (ParseException e) {
            // Can't be parsed; just use the default ISO format
            rideTime = date.getAsString();
        }
        return rideTime;
    }

    public String getLeaveDate() {
        JsonElement date = this.getField(Database.JSON_KEY_RIDE_TIME);
        String rideDate;

        // Convert ISODate to Java Date format
        try {
            DateFormat dateFormat = new SimpleDateFormat(Database.ISO_FORMAT);
            Date start = dateFormat.parse(date.getAsString());
            SimpleDateFormat format = new SimpleDateFormat(Database.RIDE_DATE_FORMAT);
            rideDate = format.format(start);
        } catch (ParseException e) {
            // Can't be parsed; just use the default ISO format
            rideDate = date.getAsString();
        }
        return rideDate;
    }

    public Calendar getLeaveTimeToEvent() {
        return direction.getLeaveTimeToEvent();
    }

    public Calendar getLeaveTimeFromEvent() {
        return direction.getLeaveTimeFromEvent();
    }

    public Location getLocation() {
        return location;
    }

    public List<Passenger> getRidersToEvent() {
        return ridersToEvent;
    }

    public List<Passenger> getRidersFromEvent() {
        return ridersFromEvent;
    }

    public boolean isTwoWay() {
        return direction.equals(RideDirection.TWO_WAY);
    }

    public boolean isToEvent() {
        return direction.hasTimeLeavingToEvent();
    }

    public boolean isFromEvent() {
        return direction.hasTimeLeavingFromEvent();
    }

    /*public void addToDb() {
        this.update(RestUtil.create(this.toJSON(), Database.REST_RIDE)); //updates the JSON object held by the parent class
        this.refreshFields(); //updates the fields for this class based on the parent class
    }*/

    public RideDirection getDirection() {
        return direction;
    }

    public void setDirection(RideDirection direction) {
        this.direction = direction;
    }

    public String getGcmId() {
        return gcmId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getRadius() {
        return radius;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public JsonObject toJSON() {
        JsonObject thisObj = new JsonObject();

        //TODO implement gcm key retrieval
        thisObj.add(Database.JSON_KEY_RIDE_EVENT, new JsonPrimitive(eventId));
        thisObj.add(Database.JSON_KEY_RIDE_DRIVER_NAME, new JsonPrimitive(driverName));
        thisObj.add(Database.JSON_KEY_RIDE_DRIVER_NUMBER, new JsonPrimitive(driverNumber));
        thisObj.add(Database.JSON_KEY_RIDE_GCM, new JsonPrimitive(gcmId));
        thisObj.add(Database.JSON_KEY_RIDE_LOCATION, location.toJSON());
        thisObj.add(Database.JSON_KEY_RIDE_TIME, new JsonPrimitive(time));
        thisObj.add(Database.JSON_KEY_RIDE_RADIUS, new JsonPrimitive(radius));
        thisObj.add(Database.JSON_KEY_RIDE_SEATS, new JsonPrimitive(Integer.toString(numSeats)));
        thisObj.add(Database.JSON_KEY_RIDE_DIRECTION, new JsonPrimitive(direction.toString()));
        thisObj.add(Database.JSON_KEY_RIDE_GENDER, new JsonPrimitive(gender));

        return thisObj;
    }

    //Example of how to add a new Ride to the database and store it in a ride object for later use:
    // Ride newRide = new Ride(RestUtil.create(Ride.toJSON(event, driver, seats, loc, dir)));
    public static JsonObject toJSON(
            final String eventId,
            final String driverName,
            final String driverNumber,
            final String gcmKey,
            final Location location,
            final String time,
            final double radius,
            final int numSeats,
            final RideDirection direction,
            final String gender) {

        JsonObject thisObj = new JsonObject();

        //TODO implement gcm key retrieval
        thisObj.add(Database.JSON_KEY_RIDE_EVENT, new JsonPrimitive(eventId));
        thisObj.add(Database.JSON_KEY_RIDE_DRIVER_NAME, new JsonPrimitive(driverName));
        thisObj.add(Database.JSON_KEY_RIDE_DRIVER_NUMBER, new JsonPrimitive(driverNumber));
        thisObj.add(Database.JSON_KEY_RIDE_GCM, new JsonPrimitive(gcmKey));
        thisObj.add(Database.JSON_KEY_RIDE_LOCATION, location.toJSON());
        thisObj.add(Database.JSON_KEY_RIDE_TIME, new JsonPrimitive(time));
        thisObj.add(Database.JSON_KEY_RIDE_RADIUS, new JsonPrimitive(radius));
        thisObj.add(Database.JSON_KEY_RIDE_SEATS, new JsonPrimitive(Integer.toString(numSeats)));
        thisObj.add(Database.JSON_KEY_RIDE_DIRECTION, new JsonPrimitive(direction.toString()));
        thisObj.add(Database.JSON_KEY_RIDE_GENDER, new JsonPrimitive(gender));

        return thisObj;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Ride) {
            Ride ride = (Ride) other;
            return eventId.equals(ride.getEventId()) &&
                    gcmId.equals(ride.getGcmId()) &&
                    driverName.equals(ride.getDriverName()) &&
                    driverNumber.equals(ride.getDriverNumber()) &&
                    direction == ride.getDirection() &&
                    radius.equals(ride.getRadius()) &&
                    numSeats.equals(ride.getNumSeats()) &&
                    getNumAvailableSeatsFromEvent() == ride.getNumAvailableSeatsFromEvent() &&
                    getNumAvailableSeatsToEvent() == ride.getNumAvailableSeatsToEvent() &&
                    gender == ride.getGender() &&
                    getLeaveDate().equals(ride.getLeaveDate()) &&
                    getLeaveTime().equals(ride.getLeaveTime()) &&
                    location.equals(ride.getLocation());
        }
        return false;
    }
}
