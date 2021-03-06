package com.edu.erp.activity.teachermodule;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.edu.erp.R;
import com.edu.erp.adapter.teachermodule.TeacherTimeTableAdapter;
import com.edu.erp.bean.database.SQLiteHelper;
import com.edu.erp.bean.teacher.support.SaveTeacherData;
import com.edu.erp.helper.AlertDialogHelper;
import com.edu.erp.helper.ProgressDialogHelper;
import com.edu.erp.interfaces.DialogClickListener;
import com.edu.erp.servicehelpers.ServiceHelper;
import com.edu.erp.serviceinterfaces.IServiceListener;
import com.edu.erp.utils.EnsyfiConstants;
import com.edu.erp.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class TeacherTimeTableNewnew extends AppCompatActivity implements IServiceListener, AdapterView.OnItemClickListener, View.OnClickListener, DialogClickListener {

    private static final String TAG = TeacherTimeTableNewnew.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    TabLayout tab;
    ViewPager viewPager;
    SQLiteHelper db;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> list1 = new ArrayList<String>();
    int dayCount = 0;
    private String resString = "";
    private SaveTeacherData teacherData = new SaveTeacherData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_table_newnew);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        db = new SQLiteHelper(getApplicationContext());
        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.view_reviews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TimeTableReview.class);
                startActivity(i);
            }
        });
        tab = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        if (PreferenceStorage.getUserType(this).equalsIgnoreCase("1")) {
            getDaysfromDB();
            initialiseTabs();
            findViewById(R.id.view_reviews).setVisibility(View.GONE);
        }else if (PreferenceStorage.getUserType(this).equalsIgnoreCase("2")) {
            getDays();
        } else {
            loadday();
            findViewById(R.id.view_reviews).setVisibility(View.GONE);
        }
    }

    private void getDaysfromDB() {
        list.clear();
        list1.clear();
        Cursor c = db.selectTimeTableDays();
        dayCount = c.getCount();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    list.add("" + c.getString(0));
                    list1.add("" + c.getString(1));
                } while (c.moveToNext());
            }
        }
    }

    private void loadday() {
        resString = "student_days";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getStudentClassIdPreference(this);
        try {
            jsonObject.put(EnsyfiConstants.CT_HW_CLASS_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_TIME_TABLE_DAYS_API;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getDays() {
        resString = "days";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(EnsyfiConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_TEACHER_TIME_TABLE_DAYS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getDaysAdmin() {
        resString = "days";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getTeacherId(this);
        try {
            jsonObject.put(EnsyfiConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_TEACHER_TIME_TABLE_DAYS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getTimeTable() {
        resString = "TTT";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserType(this).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getTeacherId(this);
        } else {
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(EnsyfiConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_TEACHER_TIME_TABLE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    public void getHolsList() {
        resString = "TT";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getStudentClassIdPreference(this);
        try {
            jsonObject.put(EnsyfiConstants.PARAMS_CLASS_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(this) + EnsyfiConstants.GET_STUDENT_TIME_TABLE_API;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void initialiseTabs() {
        for (int i = 0; i < dayCount; i++) {
            tab.addTab(tab.newTab().setText(list1.get(i)));
        }
        TeacherTimeTableAdapter adapter = new TeacherTimeTableAdapter
                (getSupportFragmentManager(), tab.getTabCount(), list1);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.performClick();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        tab.removeOnTabSelectedListener(TabLayout.OnTabSelectedListener);
//Bonus Code : If your tab layout has more than 2 tabs then tab will scroll other wise they will take whole width of the screen
        if (tab.getTabCount() <= 2) {
            tab.setTabMode(TabLayout.
                    MODE_FIXED);
        } else {
            tab.setTabMode(TabLayout.
                    MODE_SCROLLABLE);
        }
    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(EnsyfiConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");

                        AlertDialogHelper.showSimpleAlertDialog(this, msg);


                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                if (resString.equalsIgnoreCase("days")) {
                    JSONArray getTimeTableDaysArray = response.getJSONArray("days_list");
                    if (getTimeTableDaysArray != null && getTimeTableDaysArray.length() > 0) {
                        teacherData.saveTimeTableDays(getTimeTableDaysArray);
                    }
                    getTimeTable();
                } else if (resString.equalsIgnoreCase("TTT")) {
                    JSONArray getTimeTableArray = response.getJSONArray("timetableDetails");
                    if (getTimeTableArray != null && getTimeTableArray.length() > 0) {
                        teacherData.saveTeacherTimeTable(getTimeTableArray);
                    }
                    getDaysfromDB();
                    initialiseTabs();
                } else if (resString.equalsIgnoreCase("student_days")) {
                    JSONArray getTimeTableDaysArray = response.getJSONArray("timetableDays");
                    if (getTimeTableDaysArray != null && getTimeTableDaysArray.length() > 0) {
                        teacherData.saveTimeTableDays(getTimeTableDaysArray);
                    }
                    getHolsList();
                } else if (resString.equalsIgnoreCase("TT")) {
                    JSONArray getTimeTableArray = response.getJSONArray("timetableDetails");
                    if (getTimeTableArray != null && getTimeTableArray.length() > 0) {
                        teacherData.saveTeacherTimeTable(getTimeTableArray);
                    }
                    getDaysfromDB();
                    initialiseTabs();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}