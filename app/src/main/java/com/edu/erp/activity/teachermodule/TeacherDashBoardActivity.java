package com.edu.erp.activity.teachermodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.erp.R;
import com.edu.erp.activity.general.CircularActivity;
import com.edu.erp.activity.general.EventsActivity;
import com.edu.erp.activity.general.GroupingActivity;
import com.edu.erp.activity.general.LeaveStatusActivity;
import com.edu.erp.activity.general.OnDutyActivity;
import com.edu.erp.activity.general.SyncRecordsActivity;
import com.edu.erp.activity.loginmodule.ProfileActivityNew;
import com.edu.erp.activity.loginmodule.SettingsActivity;
import com.edu.erp.activity.loginmodule.SplashScreenActivity;
import com.edu.erp.activity.parentsmodule.ParentDashBoardActivity;
import com.edu.erp.adapter.NavDrawerAdapter;
import com.edu.erp.bean.general.support.DeleteTableRecords;
import com.edu.erp.bean.teacher.support.SaveTeacherData;
import com.edu.erp.helper.AlertDialogHelper;
import com.edu.erp.helper.ProgressDialogHelper;
import com.edu.erp.interfaces.DialogClickListener;
import com.edu.erp.servicehelpers.ServiceHelper;
import com.edu.erp.serviceinterfaces.IServiceListener;
import com.edu.erp.syncadapter.UploadDataSyncAdapter;
import com.edu.erp.utils.EnsyfiConstants;
import com.edu.erp.utils.PreferenceStorage;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 04-07-2017.
 */

public class TeacherDashBoardActivity extends AppCompatActivity implements DialogClickListener, IServiceListener {

    private static final String TAG = ParentDashBoardActivity.class.getName();
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
//    private ListView navDrawerList;
    boolean doubleBackToExitPressedOnce = false;
    private ImageView imgNavProfileImage;
    private ArrayAdapter<String> navListAdapter;
//    private String[] values = {"Profile", "Attendance", "Homeworks/Class Tests", "Substitution", "Special Class", "Exams & Results",
//            "Exam Duty", "Timetable", "Events", "Circulars", "On Duty", "Groups", "Apply Leave",
//            "Settings", "Sync", "Sign Out"};
    TextView navUserProfileName = null, classAttendanceInfo, classWorkInfo;
    LinearLayout dashAttendance, dashTimeTable, dashClassTest, dashExam, dashEvent, dashCommunication, classinfo;
    private String mCurrentUserProfileUrl = "";
    Context context;
    private DeleteTableRecords deleteTableRecords;
    protected ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
    private SaveTeacherData teacherData;
    NavigationView navigationView;

    String checkRes = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash_board);
        setTitle("ERP - Teachers");
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.new_navi);
        initializeNavigationDrawer();
        initializeViews();
        context = getApplicationContext();
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        teacherData = new SaveTeacherData(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        checkLogg();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LandingonPause", "LandingonPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LandingonResume", "LandingonResume");
    }

    private void initializeViews() {
        Log.d(TAG, "initializin the views");
        Log.d(TAG, "initializing view pager");
        navigationView = findViewById(R.id.nav_view);
        navUserProfileName = navigationView.getHeaderView(0).findViewById(R.id.user_profile_name);
        imgNavProfileImage = navigationView.getHeaderView(0).findViewById(R.id.img_profile_image);

        classinfo = (LinearLayout) findViewById(R.id.class_info);
        classAttendanceInfo = (TextView) findViewById(R.id.class_attendance_info);
        classAttendanceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClassTeacherAttendanceView.class);
                startActivity(intent);
            }
        });
        classWorkInfo = (TextView) findViewById(R.id.class_work_info);
        classWorkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClassTeacherCtHwOverallView.class);
                startActivity(intent);
            }
        });

        if (PreferenceStorage.getClassTeacher(this).equalsIgnoreCase("1")) {
            classinfo.setVisibility(View.VISIBLE);
        }

        dashAttendance = (LinearLayout) findViewById(R.id.attendance);
        dashClassTest = (LinearLayout) findViewById(R.id.class_test);
        dashExam = (LinearLayout) findViewById(R.id.exam);
        dashTimeTable = (LinearLayout) findViewById(R.id.time_table);
        dashEvent = (LinearLayout) findViewById(R.id.events);
        dashCommunication = (LinearLayout) findViewById(R.id.communication);

        deleteTableRecords = new DeleteTableRecords(this);

        dashAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AttendanceStatusActivity.class);
                startActivity(intent);
            }
        });

        dashClassTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClassTestHomeWorkTeacherViewActivity.class);
                startActivity(intent);
            }
        });

        dashExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExamDetails();
                Intent intent = new Intent(getApplicationContext(), AcademicExamViewActivity.class);
                startActivity(intent);
            }
        });

        dashTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TeacherTimeTableNewnew.class);
                startActivity(intent);
            }
        });

        dashEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                startActivity(intent);
            }
        });

        dashCommunication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CircularActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.side_teacher_attendance:
                        onNavigationMenuSelected(1);
                        break;
                    case R.id.side_teacher_homework:
                        onNavigationMenuSelected(2);
                        break;
                    case R.id.side_teacher_sub:
                        onNavigationMenuSelected(3);
                        break;
                    case R.id.side_teacher_spl:
                        onNavigationMenuSelected(4);
                        break;
                    case R.id.side_teacher_examresults:
                        onNavigationMenuSelected(5);
                        break;
                    case R.id.side_teacher_examduty:
                        onNavigationMenuSelected(6);
                        break;
                    case R.id.side_teacher_timetable:
                        onNavigationMenuSelected(7);
                        break;
                    case R.id.side_teacher_events:
                        onNavigationMenuSelected(8);
                        break;
                    case R.id.side_teacher_circulars:
                        onNavigationMenuSelected(9);
                        break;
                    case R.id.side_teacher_OD:
                        onNavigationMenuSelected(10);
                        break;
                    case R.id.side_teacher_groups:
                        onNavigationMenuSelected(11);
                        break;
                    case R.id.side_teacher_leave:
                        onNavigationMenuSelected(12);
                        break;
                    case R.id.side_teacher_settings:
                        onNavigationMenuSelected(13);
                        break;
                    case R.id.side_teacher_sync:
                        onNavigationMenuSelected(14);
                        break;
                    case R.id.side_teacher_signout:
                        onNavigationMenuSelected(15);
                        break;
                }
                return true;
            }
        });

        String name = PreferenceStorage.getName(getApplicationContext());
        Log.d(TAG, "user name value" + name);
        if ((name != null) && !name.isEmpty()) {
            navUserProfileName.setText("Hi, " + name);
        }
        String url = PreferenceStorage.getUserPicture(this);
        mCurrentUserProfileUrl = url;

        if (((url != null) && !(url.isEmpty()))) {
            Log.d(TAG, "image url is " + url);
            Picasso.get().load(url).placeholder(R.drawable.ic_profile_default).error(R.drawable.ic_profile_default).into(imgNavProfileImage);
        }
        Log.d(TAG, "Set the selected page to 0");//default page

        UploadDataSyncAdapter.initializeSyncAdapter(this);

    }

    private void initializeNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {
                String userProfileName = PreferenceStorage.getName(getApplicationContext());
                String url = PreferenceStorage.getUserPicture(TeacherDashBoardActivity.this);

                Log.d(TAG, "user name value" + userProfileName);
                if ((userProfileName != null) && !userProfileName.isEmpty()) {
                    String[] splitStr = userProfileName.split("\\s+");
                    navUserProfileName.setText("Hi, " + splitStr[0]);
                }

                if (((url != null) && !(url.isEmpty())) && !(url.equalsIgnoreCase(mCurrentUserProfileUrl))) {
                    Log.d(TAG, "image url is " + url);
                    mCurrentUserProfileUrl = url;
                    Picasso.get().load(url).noPlaceholder().error(R.drawable.profile_pic).into(imgNavProfileImage);
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        navDrawerList = (ListView) findViewById(R.id.nav_drawer_options_list);
//
//        NavDrawerAdapter navDrawerAdapter = new NavDrawerAdapter(getApplicationContext(), R.layout.nav_list_item, values);
//        navListAdapter = new ArrayAdapter<String>(this, R.layout.nav_list_item, values);
//        navDrawerList.setAdapter(navDrawerAdapter);

        imgNavProfileImage = (ImageView) findViewById(R.id.img_profile_image);
//        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onNavigationMenuSelected(position);
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//            }
//        });
    }

    private void onNavigationMenuSelected(int position) {

        if (position == 1) {
            Intent navigationIntent = new Intent(this, AttendanceStatusActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 2) {
            Intent navigationIntent = new Intent(this, ClassTestHomeWorkTeacherViewActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 3) {
            Intent navigationIntent = new Intent(this, SubstitutionActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 4) {
            Intent navigationIntent = new Intent(this, SpecialClassActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 5) {
            getExamDetails();
            Intent navigationIntent = new Intent(this, AcademicExamViewActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 6) {
            Intent navigationIntent = new Intent(this, ExamDutyActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 7) {
            Intent navigationIntent = new Intent(this, TeacherTimeTableNewnew.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 8) {
            Intent navigationIntent = new Intent(this, EventsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 9) {
            Intent navigationIntent = new Intent(this, CircularActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 10) {
            Intent navigationIntent = new Intent(this, OnDutyActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 11) {
            Intent navigationIntent = new Intent(this, GroupingActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
//        else if (position == 11) {
//            Intent navigationIntent = new Intent(this, LeaveCalendarActivity.class);
//            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(navigationIntent);
//        }
        else if (position == 12) {
            Intent navigationIntent = new Intent(this, LeaveStatusActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 13) {
            Intent navigationIntent = new Intent(this, SettingsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 14) {
            Intent navigationIntent = new Intent(this, SyncRecordsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (position == 15) {
            Log.d(TAG, "Perform Logout");
            doLogout();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void doLogout() {

        deleteTableRecords.deleteAllRecords();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }


    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void getExamDetails() {
        checkRes = "examData";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(EnsyfiConstants.TEACHER_ID, PreferenceStorage.getTeacherId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_EXAM_TEACHER_API;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void checkLogg() {
        checkRes = "checkVersion";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(EnsyfiConstants.KEY_APP_VERSION, EnsyfiConstants.KEY_APP_VERSION_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(this) + EnsyfiConstants.CHECK_VERSION_TEACHER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void sendLogi() {
        checkRes = "sendLogin";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(EnsyfiConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(this) + EnsyfiConstants.DAILY_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInsuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(EnsyfiConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInsuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);
                    } else {
                        signInsuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInsuccess;
    }
    
    @Override
    public void onResponse(JSONObject response) {
//        progressDialogHelper.hideProgressDialog();
        if (checkRes.equalsIgnoreCase("checkRes")) {
            if (validateSignInResponse(response)) {
                JSONArray getExamsOfClassArray = null;
                try {
                    getExamsOfClassArray = response.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getExamsOfClassArray != null && getExamsOfClassArray.length() > 0) {
                    teacherData.saveExamsOfClass(getExamsOfClassArray);
                }
            }
        } else if (checkRes.equalsIgnoreCase("checkVersion")) {
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    String ab = "success";
                } else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TeacherDashBoardActivity.this);
                    alertDialogBuilder.setTitle("Update");
                    alertDialogBuilder.setMessage("A new version of SkilEx is available!");
                    alertDialogBuilder.setPositiveButton("Get it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                finish();
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendLogi();
        }
    }

    @Override
    public void onError(String error) {

    }
}
