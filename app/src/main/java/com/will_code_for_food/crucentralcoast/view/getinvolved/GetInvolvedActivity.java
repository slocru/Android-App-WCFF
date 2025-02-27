package com.will_code_for_food.crucentralcoast.view.getinvolved;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.will_code_for_food.crucentralcoast.R;
import com.will_code_for_food.crucentralcoast.controller.Logger;
import com.will_code_for_food.crucentralcoast.model.common.common.Util;
import com.will_code_for_food.crucentralcoast.model.common.form.Form;
import com.will_code_for_food.crucentralcoast.model.getInvolved.MinistryQuestionRetriever;
import com.will_code_for_food.crucentralcoast.model.getInvolved.MinistryTeam;
import com.will_code_for_food.crucentralcoast.values.Android;
import com.will_code_for_food.crucentralcoast.view.common.MainActivity;
import com.will_code_for_food.crucentralcoast.view.dynamic_form.FormListFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mallika on 1/14/16.
 */
public class GetInvolvedActivity extends MainActivity {

    private static String getInvolvedTitle = Util.getString(R.string.get_involved_header);
    private static String ministryTeamTitle = Util.getString(R.string.ministry_team_header);
    private static String communityGroupsTitle = "Community Group Forms";
    private static MinistryTeam team = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFragmentById(R.layout.fragment_get_involved, getInvolvedTitle, new GetInvolvedFragment(), this);
    }

    public void viewMinistryTeams(){
        loadFragmentById(R.layout.fragment_card_list, ministryTeamTitle, new MinistryTeamFragment(), this);
    }

    public void viewJoinCommunityGroup() {
        HashMap<String, Form> forms = MinistryQuestionRetriever.getAllCommunityGroupForms();
        ArrayList<String> ministryList = new ArrayList<String>(Util.loadStringSet(Android.PREF_MINISTRIES));
        Logger.i("Community Groups", "Displaying...");
        FormListFragment flFrag = new FormListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("ministryList", ministryList);
        args.putSerializable("formsMap", forms);
        flFrag.setArguments(args);
        loadFragmentById(R.layout.fragment_form_list_layout, communityGroupsTitle, flFrag, this);
    }

    public static void setMinistryTeam(MinistryTeam team){
        GetInvolvedActivity.team = team;
    }

    public static MinistryTeam getMinistryTeam() {
        return team;
    }

    public void joinMinistryTeam(View view){
        MinistryTeamSignupDialog popup = MinistryTeamSignupDialog.newInstance(team);
        FragmentManager manager = getFragmentManager();
        popup.show(manager, "ministry_team_signup"); //todo: fix this (not sure which string to use)
    }
}
