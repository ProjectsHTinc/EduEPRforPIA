package com.edu.erp.activity.adminmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.edu.erp.R;
import com.edu.erp.adapter.adminmodule.ParentStudentListAdapter;
import com.edu.erp.bean.admin.support.StoreClass;
import com.edu.erp.bean.admin.support.StoreSection;
import com.edu.erp.bean.admin.viewlist.ParentStudent;
import com.edu.erp.bean.admin.viewlist.ParentStudentList;
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

import java.util.ArrayList;

/**
 * Created by Admin on 18-07-2017.
 */

public class ParentsViewActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = ParentsViewActivity.class.getName();
    private ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
    private Spinner spnClassList, spnSectionList;
    private String checkSpinner = "", storeClassId, storeSectionId;
    ListView loadMoreListView;
    ParentStudentListAdapter parentStudentListAdapter;
    ArrayList<ParentStudent> parentStudentArrayList;
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_view);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        spnClassList = (Spinner) findViewById(R.id.class_list_spinner);
        spnSectionList = (Spinner) findViewById(R.id.section_list_spinner);
        loadMoreListView = (ListView) findViewById(R.id.listView_events);
        loadMoreListView.setOnItemClickListener(this);
        parentStudentArrayList = new ArrayList<>();

        GetClassData();
        ImageView bckbtn = (ImageView) findViewById(R.id.back_res);
        bckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spnClassList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    StoreClass classList = (StoreClass) parent.getSelectedItem();
                    storeClassId = classList.getClassId();
                    GetSectionData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnSectionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0) {
                    StoreSection sectionList = (StoreSection) parent.getSelectedItem();
                    storeSectionId = sectionList.getSectionId();
                    GetStudentParentData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void GetSectionData() {
        checkSpinner = "section";
        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EnsyfiConstants.PARAMS_CLASS_ID_LIST, storeClassId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_SECTION_LISTS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void GetClassData() {
        checkSpinner = "class";
        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EnsyfiConstants.PARAMS_CLASS_ID, PreferenceStorage.getStudentClassIdPreference(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_CLASS_LISTS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void GetStudentParentData() {

        checkSpinner = "students";
        if (parentStudentArrayList != null)
            parentStudentArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EnsyfiConstants.PARAMS_CLASS_ID_NEW, storeClassId);
                jsonObject.put(EnsyfiConstants.PARAMS_SECTION_ID, storeSectionId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(getApplicationContext()) + EnsyfiConstants.GET_PARENT_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
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

                        if (parentStudentArrayList != null) {
                            parentStudentArrayList.clear();
                            parentStudentListAdapter = new ParentStudentListAdapter(this, this.parentStudentArrayList);
                            loadMoreListView.setAdapter(parentStudentListAdapter);
                        }

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        ParentStudent parentStudent = null;
        if ((parentStudentListAdapter != null) && (parentStudentListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = parentStudentListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            parentStudent = parentStudentArrayList.get(actualindex);
        } else {
            parentStudent = parentStudentArrayList.get(position);
        }
        Intent intent = new Intent(this, ParentsViewDetailsActivity.class);
        intent.putExtra("eventObj", parentStudent);
        startActivity(intent);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {

            try {
                if (checkSpinner.equalsIgnoreCase("class")) {

                    JSONArray getData = response.getJSONArray("data");
                    JSONObject userData = getData.getJSONObject(0);
                    int getLength = getData.length();
                    Log.d(TAG, "userData dictionary" + userData.toString());

                    String classId = "Default id";
                    String className = "Select Class";
                    ArrayList<StoreClass> classesList = new ArrayList<>();
                    classesList.add(new StoreClass(classId, className));

                    for (int i = 0; i < getLength; i++) {

                        classId = getData.getJSONObject(i).getString("class_id");
                        className = getData.getJSONObject(i).getString("class_name");

                        classesList.add(new StoreClass(classId, className));
                    }

                    //fill data in spinner
                    ArrayAdapter<StoreClass> adapter = new ArrayAdapter<StoreClass>(getApplicationContext(), R.layout.spinner_item_ns, classesList);
                    spnClassList.setAdapter(adapter);
                } else if (checkSpinner.equalsIgnoreCase("section")) {
                    JSONArray getData = response.getJSONArray("data");
                    JSONObject userData = getData.getJSONObject(0);
                    int getLength = getData.length();
                    Log.d(TAG, "userData dictionary" + userData.toString());

                    String sectionId = "Default id";
                    String sectionclass = "Select section";
                    ArrayList<StoreSection> sectionList = new ArrayList<>();
                    sectionList.add(new StoreSection(sectionId, sectionclass));

                    for (int i = 0; i < getLength; i++) {

                        sectionId = getData.getJSONObject(i).getString("sec_id");
                        sectionclass = getData.getJSONObject(i).getString("sec_name");

                        sectionList.add(new StoreSection(sectionId, sectionclass));
                    }

                    //fill data in spinner
                    ArrayAdapter<StoreSection> adapter = new ArrayAdapter<StoreSection>(getApplicationContext(), R.layout.spinner_item_ns, sectionList);
                    spnSectionList.setAdapter(adapter);
                } else {
                    JSONArray getData = response.getJSONArray("data");
                    if (getData != null && getData.length() > 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialogHelper.hideProgressDialog();
                                Gson gson = new Gson();
                                ParentStudentList parentStudentList = gson.fromJson(response.toString(), ParentStudentList.class);
                                if (parentStudentList.getParentStudent() != null && parentStudentList.getParentStudent().size() > 0) {
                                    totalCount = parentStudentList.getCount();
                                    isLoadingForFirstTime = false;
                                    updateListAdapter(parentStudentList.getParentStudent());
                                }
                            }
                        });
                    } else {
                        if (parentStudentArrayList != null) {
                            parentStudentArrayList.clear();
                            parentStudentListAdapter = new ParentStudentListAdapter(this, this.parentStudentArrayList);
                            loadMoreListView.setAdapter(parentStudentListAdapter);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                AlertDialogHelper.showSimpleAlertDialog(ParentsViewActivity.this, error);
            }
        });
    }

    protected void updateListAdapter(ArrayList<ParentStudent> parentStudentArrayList) {
        this.parentStudentArrayList.addAll(parentStudentArrayList);
        if (parentStudentListAdapter == null) {
            parentStudentListAdapter = new ParentStudentListAdapter(this, this.parentStudentArrayList);
            loadMoreListView.setAdapter(parentStudentListAdapter);
        } else {
            parentStudentListAdapter.notifyDataSetChanged();
        }
    }
}
