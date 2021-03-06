package com.edu.erp.bean.general.support;

import android.content.Context;

import com.edu.erp.bean.database.SQLiteHelper;

/**
 * Created by Admin on 18-07-2017.
 */

public class DeleteTableRecords {

    private Context context;
    SQLiteHelper database;

    public DeleteTableRecords(Context context) {
        this.context = context;
    }

    public void deleteAllRecords() {
        database = new SQLiteHelper(context);
        try {
            database.deleteTeacherHandlingSubjects();
            database.deleteLeaveTypes();
            database.deleteAcademicExamMarks();
            database.deleteExamDetails();
            database.deleteExamOfClasses();
            database.deleteClassTestMark();
            database.deleteHomeWorkClassTest();
            database.deleteAcademicMonths();
            database.deleteStudentAttendanceHistory();
            database.deleteStudentAttendance();
            database.deleteTeachersClassStudentDetails();
            database.deleteTeacherTimeTable();
            database.deleteStudentInfo();
            database.deleteStudentAttendanceFlag();
            database.deleteAcademicExamSubjectMarksStatus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
