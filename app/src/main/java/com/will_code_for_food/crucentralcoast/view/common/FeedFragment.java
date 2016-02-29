package com.will_code_for_food.crucentralcoast.view.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.retrieval.MultiMemoryRetriever;
import com.will_code_for_food.crucentralcoast.controller.retrieval.MultiRetriever;
import com.will_code_for_food.crucentralcoast.controller.retrieval.Retriever;
import com.will_code_for_food.crucentralcoast.controller.retrieval.RetrieverSchema;
import com.will_code_for_food.crucentralcoast.controller.retrieval.SingleRetriever;
import com.will_code_for_food.crucentralcoast.controller.retrieval.VideoRetriever;
import com.will_code_for_food.crucentralcoast.model.common.common.Event;
import com.will_code_for_food.crucentralcoast.tasks.AsyncResponse;
import com.will_code_for_food.crucentralcoast.tasks.RetrievalTask;
import com.will_code_for_food.crucentralcoast.values.Android;
import com.will_code_for_food.crucentralcoast.values.Database;
import com.will_code_for_food.crucentralcoast.values.Youtube;
import com.will_code_for_food.crucentralcoast.view.events.EventCardFactory;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RideShareActivity;
import com.will_code_for_food.crucentralcoast.view.ridesharing.RideShareEventCardFactory;

import java.util.ArrayList;

/**
 * Created by MasonJStevenson on 2/18/2016.
 */
public class FeedFragment extends CruFragment implements TextView.OnEditorActionListener {
    SwipeRefreshLayout layout;
    MenuItem sortItem;
    ListView listView;
    EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View hold = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        sortItem = menu.findItem(R.id.sort);

        searchItem.setActionView(R.layout.action_search);
        //menu.findItem(R.id.sort).setActionView(R.layout.action_sort);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                sortItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ((FeedCardAdapter) listView.getAdapter()).clearSearch();
                search.setText("");
                sortItem.setVisible(true);
                return true;
            }
        });

        search = (EditText)menu.findItem(R.id.search).getActionView().findViewById(R.id.text);

        search.setOnEditorActionListener(this);
        search.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event == null || event.getAction() == KeyEvent.KEYCODE_ENTER) {
            ((FeedCardAdapter) listView.getAdapter()).search(v.getText().toString());

            InputMethodManager imm = (InputMethodManager) getParent().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_newest) {
            Log.i("FeedFragment", "sorting by newest");
            ((FeedCardAdapter) listView.getAdapter()).sortByNewest();
            return true;
        } else if (item.getItemId() == R.id.sort_oldest) {
            Log.i("FeedFragment", "sorting by oldest");
            ((FeedCardAdapter) listView.getAdapter()).sortByOldest();
            return true;
        } else if (item.getItemId() == R.id.sort_type) {
            Log.i("FeedFragment", "sorting by type");
            ((FeedCardAdapter) listView.getAdapter()).sortByType();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets objects loaded at application start.
     */
    private void loadList() {
        Log.i("FeedFragment", "Loading feed for the first time");

        ArrayList<String> keyList = new ArrayList<String>();
        keyList.add(Database.REST_EVENT);
        keyList.add(Database.REST_RESOURCE);
        keyList.add(Database.VIDEOS);

        MultiMemoryRetriever retriever = new MultiMemoryRetriever(keyList);
        CardFragmentFactory factory = new FeedCardFactory();

        new RetrievalTask<Event>(retriever, factory, R.string.toast_no_events,
                new AsyncResponse(getParent()) {
                    @Override
                    public void otherProcessing() {
                        layout.setRefreshing(false);
                    }
                }).execute();
    }

    private void refreshList() {
        Log.i("FeedFragment", "Refreshing Feed");

        ArrayList<Retriever> retrieverList = new ArrayList<Retriever>();
        retrieverList.add(new SingleRetriever(RetrieverSchema.EVENT));
        retrieverList.add(new SingleRetriever(RetrieverSchema.RESOURCE));
        retrieverList.add(new VideoRetriever());

        MultiRetriever retriever = new MultiRetriever(retrieverList);
        CardFragmentFactory factory = new FeedCardFactory();

        new RetrievalTask<Event>(retriever, factory, R.string.toast_no_events,
                new AsyncResponse(getParent()) {
                    @Override
                    public void otherProcessing() {
                        layout.setRefreshing(false);
                    }
                }).execute();
    }
}
