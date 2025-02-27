package com.will_code_for_food.crucentralcoast.view.summermissions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will_code_for_food.crucentralcoast.controller.retrieval.Content;
import com.will_code_for_food.crucentralcoast.model.common.common.sorting.DatabaseObjectSorter;
import com.will_code_for_food.crucentralcoast.model.common.common.sorting.SortMethod;
import com.will_code_for_food.crucentralcoast.view.common.MainActivity;
import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.model.common.common.DatabaseObject;
import com.will_code_for_food.crucentralcoast.model.getInvolved.SummerMission;
import com.will_code_for_food.crucentralcoast.view.common.CardFragmentFactory;

import java.util.List;

/**
 * Created by Brian on 1/28/2016.
 */
public class SummerMissionCardFactory implements CardFragmentFactory {
    List<SummerMission> cards;

    @Override
    public boolean include(DatabaseObject object) {
        return true;
    }

    @Override
    public ArrayAdapter createAdapter(Content cardObjects) {
        DatabaseObjectSorter.sortByDate(cardObjects, SortMethod.ASCENDING);
        return new SummerMissionAdapter(SummerMissionsActivity.context,
                android.R.layout.simple_list_item_1, cardObjects);
    }

    @Override
    public AdapterView.OnItemClickListener createCardListener(
            final MainActivity currentActivity, final Content myDBObjects) {
        return new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView < ? > parent, View view,int position, long id){
                SummerMission selectedMission = (SummerMission) myDBObjects.getObjects().get(position);
                SummerMissionsActivity.setMission(selectedMission);
                currentActivity.loadFragmentById(R.layout.fragment_summermission,
                        currentActivity.getTitle() + " > " + selectedMission.getName(),
                        new SummerMissionInfoFragment(), currentActivity);
            }
        } ;
    }

    private class SummerMissionAdapter extends ArrayAdapter<SummerMission>{

        public SummerMissionAdapter(Context context, int resource, Content content) {
            super(context, resource, content.getObjects());
            cards = content.getObjects();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            SummerMission current = cards.get(position);
            String imageLabel = current.getImage();

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.fragment_summermission_card, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.sm_image);
            if (imageLabel != null && !imageLabel.equals("")) {
                Picasso.with(SummerMissionsActivity.context).load(imageLabel).fit().into(imageView);
            } else {
                imageView.setImageResource(R.drawable.crulogo);
            }

            TextView titleView = (TextView) convertView.findViewById(R.id.sm_text);
            titleView.setText(current.getName());

            TextView dateView = (TextView) convertView.findViewById(R.id.sm_date);
            dateView.setText(current.getMissionDateString());

            return convertView;
        }
    }
}
