package com.will_code_for_food.crucentralcoast.view.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.Logger;
import com.will_code_for_food.crucentralcoast.model.common.common.Event;
import com.will_code_for_food.crucentralcoast.model.common.common.Util;
import com.will_code_for_food.crucentralcoast.values.Database;
import com.will_code_for_food.crucentralcoast.view.common.CruFragment;
import com.will_code_for_food.crucentralcoast.view.common.MainActivity;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RideShareActivity;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RidesFragment;

/**
 * Created by MasonJStevenson on 2/2/2016.
 */
public class EventInfoFragment extends CruFragment {

    private ImageButton calendarButton;
    private ImageButton ridesharingButton;
    private ImageButton facebookButton;
    private ImageButton mapButton;
    private Event event = EventsActivity.getEvent();

    private MainActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = super.onCreateView(inflater, container, savedInstanceState);

        parent = getParent();

        initComponents(fragmentView);
        populateView(fragmentView);

        return fragmentView;
    }

    private void initComponents(final View fragmentView) {
        calendarButton = (ImageButton) fragmentView.findViewById(R.id.button_calendar);
        ridesharingButton = (ImageButton) fragmentView.findViewById(R.id.button_rideshare);
        facebookButton = (ImageButton) fragmentView.findViewById(R.id.button_facebook);
        mapButton = (ImageButton) fragmentView.findViewById(R.id.button_map);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarButtonSelected();
            }
        });

        ridesharingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRidesharing();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFacebook();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMap();
            }
        });
    }

    private void populateView(View fragmentView) {

        // Display the header image of the event
        ImageView imageView = (ImageView) fragmentView.findViewById(R.id.image_event);
        if (event.getImage() != null && event.getImage() != "") {
            Picasso.with(getParent()).load(event.getImage()).fit().into(imageView);
        }

        // Display the location of the event
        TextView locationLabel = (TextView) fragmentView.findViewById(R.id.text_event_location);
        locationLabel.setText(event.getEventLocation());
        // Grey out button to note no chosen location
        if (!event.hasLocation()) {
            ImageButton mapButton = (ImageButton)fragmentView.findViewById(R.id.button_map);
            mapButton.setImageResource(R.drawable.map_no);
        }

        // Display the time of the event
        TextView dateLabel = (TextView)fragmentView.findViewById(R.id.text_event_date);
        dateLabel.setText(event.getEventFullDate());

        // Display the description of the event
        TextView descriptionLabel = (TextView)fragmentView.findViewById(R.id.text_event_description);
        descriptionLabel.setText(event.getDescription());

        // Grey out FB button if invalid url link
        if (!event.hasFacebook()) {
            ImageButton fbButton = (ImageButton)fragmentView.findViewById(R.id.button_facebook);
            fbButton.setImageResource(R.drawable.facebook_no);
        }

        // Color RideShare button if enabled
        if (event.hasRideSharing()) {
            ImageButton rideButton = (ImageButton)fragmentView.findViewById(R.id.button_rideshare);
            rideButton.setImageResource(R.drawable.car_yes);
        }

        modifyAddToCalendarButton();
    }

    /**
     * Changes the "add to calendar" button to reflect whether or not
     * the event is already in the calendar
     */
    private void modifyAddToCalendarButton() {
        Logger.i("Changing button", "changing button");
        if (event.isInCalendarAlready()) {
            calendarButton.setImageResource(R.drawable.calendar_added);
        } else {
            calendarButton.setImageResource(R.drawable.calendar_add);
        }
    }

    // Adds the event to the user's Google Calendar
    public void calendarButtonSelected() {
        if (event != null) {
            if (event.isInCalendarAlready()) {
                new AlertDialog.Builder(getParent()).setTitle("Add Event to Calendar")
                        .setMessage("You already added this event, are you sure you want to add it again?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        event.saveToCalendar(parent);
                        calendarButton.setImageResource(R.drawable.calendar_added);
                        dialog.cancel();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            } else {
                event.saveToCalendar(parent);
                calendarButton.setImageResource(R.drawable.calendar_added);
            }
        } else {
            Toast.makeText(parent, Util.getString(R.string.cal_fail_msg),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Opens the event's ridesharing page, if one exists
    public void viewRidesharing() {
        if (event.hasRideSharing()) {
            RideShareActivity activity = new RideShareActivity();
            parent.loadFragmentById(R.layout.fragment_ride_list, event.getName() + " > " + "Rides", new RidesFragment(), parent);
            //lcurrentActivity.loadFragmentById(R.layout.fragment_ride_list, "Rides", new RidesFragment(), currentActivity);

        }
        else {
            Toast.makeText(parent, Util.getString(R.string.toast_no_ridesharing),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Opens the event's Facebook page
    public void viewFacebook() {
        String url = event.getFacebookLink();

        // Check for valid url
        if (event.hasFacebook()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
        // Invalid url
        else {
            Toast.makeText(parent, Util.getString(R.string.toast_no_facebook),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Opens the event's location in Google Maps
    public void viewMap() {
        // No map for this location
        if (!event.hasLocation()) {
            Toast.makeText(parent, Util.getString(R.string.toast_no_map),
                    Toast.LENGTH_LONG).show();
        }
        // Link to the Google Map page
        else {
            String map = Database.GOOGLE_MAP + event.getEventLocation();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            startActivity(i);
        }
    }
}