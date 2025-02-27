package com.will_code_for_food.crucentralcoast.model.getInvolved;

import android.os.AsyncTask;

import com.will_code_for_food.crucentralcoast.controller.Logger;
import com.will_code_for_food.crucentralcoast.model.common.common.RestUtil;
import com.will_code_for_food.crucentralcoast.model.common.common.Util;
import com.will_code_for_food.crucentralcoast.model.common.form.Form;
import com.will_code_for_food.crucentralcoast.model.community_groups.CommunityGroupQuestion;
import com.will_code_for_food.crucentralcoast.values.Android;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Gavin on 5/5/2016.
 */
public final class MinistryQuestionRetriever {
    public static HashMap<String, Form> getAllCommunityGroupForms() {
        Logger.i("Community Groups", "Getting questions for all subscribed ministries");
        AsyncTask task = new AsyncTask<String, Void, HashMap<String, Form>>() {
            @Override
            protected HashMap<String, Form> doInBackground(String... params) {
                HashMap<String, Form> forms = new HashMap<>();
                for (String id : params) {
                    forms.put(id, RestUtil.getMinistryQuestions(id));
                }
                return forms;
            }
        };

        HashMap<String, Form> forms = null;
        try {
            Set<String> ids = Util.loadStringSet(Android.PREF_MINISTRIES);
            forms = (HashMap<String, Form>)task.execute(ids.toArray(new String[ids.size()])).get();
        } catch (Exception ex) {
            Logger.e("Getting questions", "Failure to retrieve community group forms");
            ex.printStackTrace();
        }
        return forms;
    }
}
