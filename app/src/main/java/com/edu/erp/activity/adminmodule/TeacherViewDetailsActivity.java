package com.edu.erp.activity.adminmodule;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.erp.R;
import com.edu.erp.activity.teachermodule.TeacherTimeTableNewnew;
import com.edu.erp.bean.admin.viewlist.TeacherView;
import com.edu.erp.bean.database.SQLiteHelper;
import com.edu.erp.bean.teacher.support.SaveTeacherData;
import com.edu.erp.helper.AlertDialogHelper;
import com.edu.erp.helper.ProgressDialogHelper;
import com.edu.erp.interfaces.DialogClickListener;
import com.edu.erp.servicehelpers.ServiceHelper;
import com.edu.erp.serviceinterfaces.IServiceListener;
import com.edu.erp.utils.CommonUtils;
import com.edu.erp.utils.EnsyfiConstants;
import com.edu.erp.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 18-07-2017.
 */

public class TeacherViewDetailsActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private ImageView teacherImg, btnBack;
    SQLiteHelper database;

    private static final String TAG = TeacherViewDetailsActivity.class.getName();
    private TeacherView teacherView;
    private Button btnTeacherTimeTable;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private SaveTeacherData teacherData;

    private TextView teacherId, teacherName, teacherGender, teacherAge, teacherNationality, teacherReligion, teacherCaste,
            teacherCommunity, teacherAddress, teacherSubject, classTeacher, teacherMobile, teacherSecondaryMobile, teacherMail,
            teacherSecondaryMail, teacherSectionName, teacherClassName, teacherClassTaken;
    private boolean checkTimeTable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_details);

        teacherView = (TeacherView) getIntent().getSerializableExtra("eventObj");

        btnTeacherTimeTable = (Button) findViewById(R.id.btnTeacherTimeTable);
        btnTeacherTimeTable.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        teacherData = new SaveTeacherData(this);

        btnBack = (ImageView) findViewById(R.id.back_res);
        btnBack.setOnClickListener(this);

        teacherImg = (ImageView) findViewById(R.id.img_teacher_profile);

        teacherId = (TextView) findViewById(R.id.txtTeacherid);
        teacherName = (TextView) findViewById(R.id.txtTeacherName);
        teacherGender = (TextView) findViewById(R.id.txtTeacherGender);
        teacherAge = (TextView) findViewById(R.id.txtTeacherAge);
        teacherNationality = (TextView) findViewById(R.id.txtTeacherNationality);
        teacherReligion = (TextView) findViewById(R.id.txtTeacherReligion);
        teacherCaste = (TextView) findViewById(R.id.txtTeacherCaste);
        teacherCommunity = (TextView) findViewById(R.id.txtTeacherCommunity);
        teacherAddress = (TextView) findViewById(R.id.txtTeacherAddress);
        teacherSubject = (TextView) findViewById(R.id.txtTeacherSubject);
        classTeacher = (TextView) findViewById(R.id.txtClassTeacher);
        teacherMobile = (TextView) findViewById(R.id.txtTeacherMobile);
        teacherSecondaryMobile = (TextView) findViewById(R.id.txtTeacherSecondaryMobile);
        teacherMail = (TextView) findViewById(R.id.txtTeacherMail);
        teacherSecondaryMail = (TextView) findViewById(R.id.txtTeacherSecondaryMail);
        teacherSectionName = (TextView) findViewById(R.id.txtTeacherSectionName);
        teacherClassName = (TextView) findViewById(R.id.txtTeacherClassName);
        teacherClassTaken = (TextView) findViewById(R.id.txtTeacherClassTaken);

        populateData();

    }

    private void populateData() {

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EnsyfiConstants.PARAMS_TEACHER_ID_SHOW, teacherView.getTeacherId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_TEACHERS_INFO;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnTeacherTimeTable) {
            if (checkTimeTable) {
                PreferenceStorage.saveTeacherId(this, teacherId.getText().toString());
                Intent intent = new Intent(this, TeacherTimeTableNewnew.class);
                startActivity(intent);
            }
        }
        if (v == btnBack) {
            finish();
            clearTeacherInfo();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
//                JSONObject getTimeTable = response.getJSONObject("timeTable");
                JSONArray getTimeTableDaysArray = response.getJSONArray("timeTable");
//                if (getTimeTableDaysArray.get(0).) {
//                    JSONArray getTimeTableDaysArray = getTimeTable.getJSONArray("data");
                    if (getTimeTableDaysArray != null && getTimeTableDaysArray.length() > 0) {
                        teacherData.saveTeacherTimeTable(getTimeTableDaysArray);
                        checkTimeTable = true;
                    }
//                } else {
//                    checkTimeTable = false;
//                }

                JSONArray getClassSubject = response.getJSONArray("class_name");
                if (getClassSubject != null && getClassSubject.length() > 0) {
                    teacherData.saveTeacherHandlingSubject(getClassSubject);
                }

                JSONArray getTeacherProfile = response.getJSONArray("teacherProfile");
                if (getTeacherProfile != null && getTeacherProfile.length() > 0) {
                    teacherData.saveTeacherProfile(getTeacherProfile);
                }

                JSONObject getTeacherDays = response.getJSONObject("timeTabledays");
                JSONArray getTeacherDaysData = getTeacherDays.getJSONArray("data");
                if (getTeacherDaysData != null && getTeacherDaysData.length() > 0) {
                    saveTimeTableDays(getTeacherDaysData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            getTeacherInfo();

        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
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

    private void getTeacherInfo() {

        teacherId.setText(PreferenceStorage.getTeacherId(getApplicationContext()));
        teacherName.setText(PreferenceStorage.getTeacherName(getApplicationContext()));
        teacherGender.setText(PreferenceStorage.getTeacherGender(getApplicationContext()));
        teacherAge.setText(PreferenceStorage.getTeacherAge(getApplicationContext()));
        teacherNationality.setText(PreferenceStorage.getTeacherNationality(getApplicationContext()));
        teacherReligion.setText(PreferenceStorage.getTeacherReligion(getApplicationContext()));
        teacherCaste.setText(PreferenceStorage.getTeacherCaste(getApplicationContext()));
        teacherCommunity.setText(PreferenceStorage.getTeacherCommunity(getApplicationContext()));
        teacherAddress.setText(PreferenceStorage.getTeacherAddress(getApplicationContext()));
//        teacherSubject.setText(PreferenceStorage.getTeacherSubjectName(getApplicationContext()));
        classTeacher.setText(PreferenceStorage.getClassTeacher(getApplicationContext()));
        teacherMobile.setText(PreferenceStorage.getTeacherMobile(getApplicationContext()));
        teacherSecondaryMobile.setText(PreferenceStorage.getTeacherSecondaryMobile(getApplicationContext()));
        teacherMail.setText(PreferenceStorage.getTeacherMail(getApplicationContext()));
        teacherSecondaryMail.setText(PreferenceStorage.getTeacherSecondaryMail(getApplicationContext()));
//        teacherSectionName.setText(PreferenceStorage.getTeacherSectionName(getApplicationContext()));
//        teacherClassName.setText(PreferenceStorage.getTeacherClassName(getApplicationContext()));
        teacherClassTaken.setText(PreferenceStorage.getTeacherClassTaken(getApplicationContext()));
    }

    private void clearTeacherInfo() {
        teacherId.clearComposingText();
        teacherName.clearComposingText();
        teacherGender.clearComposingText();
        teacherAge.clearComposingText();
        teacherNationality.clearComposingText();
        teacherReligion.clearComposingText();
        teacherCaste.clearComposingText();
        teacherCommunity.clearComposingText();
        teacherAddress.clearComposingText();
//        teacherSubject.clearComposingText();
        classTeacher.clearComposingText();
        teacherMobile.clearComposingText();
        teacherSecondaryMobile.clearComposingText();
        teacherMail.clearComposingText();
        teacherSecondaryMail.clearComposingText();
//        teacherSectionName.clearComposingText();
//        teacherClassName.clearComposingText();
        teacherClassTaken.clearComposingText();
    }

    public void saveTimeTableDays(JSONArray timetableDays) {
        database = new SQLiteHelper(this);
        try {
            database.deleteTimeTableDays();

            for (int i = 0; i < timetableDays.length(); i++) {
                JSONObject jsonObject = timetableDays.getJSONObject(i);

                String dayId = "";
                String dayName = "";

                dayId = jsonObject.getString("day_id");
                dayName = jsonObject.getString("list_day");

                System.out.println("Day Id :" + dayId);
                System.out.println("Day Name :" + dayName);

                String v1 = dayId, v2 = dayName;

                database.timetable_days_insert(v1, v2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
