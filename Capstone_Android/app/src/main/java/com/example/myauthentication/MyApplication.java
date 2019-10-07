package com.example.myauthentication;

import android.app.Application;

public class MyApplication extends Application {

    private String student_id;

    public String getGlobalStudentId()
    {
        return student_id;
    }

    public void setGlobalStudentId(String globalStudentId)
    {
        this.student_id = globalStudentId;
    }
}
