package com.will_code_for_food.crucentralcoast.view.events;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.Logger;
import com.will_code_for_food.crucentralcoast.controller.retrieval.RetrieverSchema;
import com.will_code_for_food.crucentralcoast.controller.retrieval.SingleMemoryRetriever;
import com.will_code_for_food.crucentralcoast.model.common.common.DBObjectLoader;
import com.will_code_for_food.crucentralcoast.tasks.AsyncResponse;
import com.will_code_for_food.crucentralcoast.values.Database;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RideShareActivity;
import com.will_code_for_food.crucentralcoast.controller.retrieval.Retriever;
import com.will_code_for_food.crucentralcoast.model.common.common.Event;
import com.will_code_for_food.crucentralcoast.tasks.RetrievalTask;
import com.will_code_for_food.crucentralcoast.view.common.CruFragment;
import com.will_code_for_food.crucentralcoast.view.common.CardFragmentFactory;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RideShareEventCardFactory;

/**
 * The fragment displaying the list of all CRU events
 */
public class EventsFragment extends CruFragment {
    SwipeRefreshLayout layout;
    ListView listView;
    private int index, top;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View hold = super.onCreateView(inflater, container, savedInstanceState);
        layout = (SwipeRefreshLayout) hold.findViewById(R.id.card_refresh_layout);
        listView = (ListView) hold.findViewById(R.id.list_cards);
        loadList();
        return hold;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    @Override
    public void onPause(){
        index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        super.onPause();
    }

    private void loadList() {
        SingleMemoryRetriever retriever = new SingleMemoryRetriever(Database.REST_EVENT);
        populateList(retriever);
    }

    private void refreshList() {
        Logger.i("EventsFragment", "refreshing events and rides");

        if (!DBObjectLoader.loadObjects(RetrieverSchema.EVENT, Database.DB_TIMEOUT)) {
            Toast.makeText(getParent(), "Unable to refresh events", Toast.LENGTH_SHORT).show();
        }

        if (!DBObjectLoader.loadObjects(RetrieverSchema.RIDE, Database.DB_TIMEOUT) &&
                !DBObjectLoader.loadObjects(RetrieverSchema.PASSENGER, Database.DB_TIMEOUT)) {
            Toast.makeText(getParent(), "Unable to refresh rides", Toast.LENGTH_SHORT).show();
        }

        loadList();
    }

    private void populateList(Retriever retriever) {
        CardFragmentFactory factory;

        if (getParent() instanceof RideShareActivity) {
            factory = new RideShareEventCardFactory();
        } else {
            factory = new EventCardFactory();
        }

        new RetrievalTask<Event>(retriever, factory, R.string.toast_no_events,
                new AsyncResponse(getParent()) {
                    @Override
                    public void otherProcessing() {
                        layout.setRefreshing(false);
                    }
                }).execute(index, top);
    }
}
