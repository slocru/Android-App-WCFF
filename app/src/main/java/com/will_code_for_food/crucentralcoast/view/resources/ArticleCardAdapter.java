package com.will_code_for_food.crucentralcoast.view.resources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.retrieval.Content;
import com.will_code_for_food.crucentralcoast.model.common.common.DatabaseObject;
import com.will_code_for_food.crucentralcoast.model.resources.Resource;
import com.will_code_for_food.crucentralcoast.values.Database;
import com.will_code_for_food.crucentralcoast.view.common.CardAdapter;

import java.util.ArrayList;

/**
 * Created by Kayla on 2/29/2016.
 */
public class ArticleCardAdapter extends CardAdapter {

    public ArticleCardAdapter(Context context, Content content) {
        super(context, android.R.layout.simple_list_item_1, content);
    }

    @Override
    public void sortByType() {
        ArrayList<DatabaseObject> audio = new ArrayList<>();
        ArrayList<DatabaseObject> videos = new ArrayList<>();
        ArrayList<DatabaseObject> articles = new ArrayList<>();

        for (DatabaseObject card : cards) {
            Resource resource = (Resource)card;
            if (resource.getType().equals(Database.RESOURCE_AUDIO)) {
                audio.add(resource);
            } else if (resource.getType().equals(Database.RESOURCE_VIDEO)) {
                videos.add(resource);
            } else if (resource.getType().equals(Database.RESOURCE_ARTICLE)) {
                articles.add(resource);
            }
        }
        cards.clear();
        cards.addAll(articles);
        cards.addAll(videos);
        cards.addAll(audio);

        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Resource current = (Resource) cards.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_resources_card, parent, false);
        }
        
        ImageView imageView = (ImageView) convertView.findViewById(R.id.card_image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.cru_bracket_no_line);

        ImageView typeView = (ImageView) convertView.findViewById(R.id.resource_type);
        String type = current.getType();
        if (type.equals(Database.RESOURCE_ARTICLE)) {
            typeView.setImageResource(R.drawable.ic_web_grey_36dp);
        } else if (type.equals(Database.RESOURCE_VIDEO)) {
            typeView.setImageResource(R.drawable.ic_video_grey);
        } else if (type.equals(Database.RESOURCE_AUDIO)) {
            typeView.setImageResource(R.drawable.ic_volume_up_grey_36dp);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.card_text);
        titleView.setText(current.getTitle());

        return convertView;
    }
}