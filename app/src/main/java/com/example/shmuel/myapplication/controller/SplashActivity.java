package com.example.shmuel.myapplication.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.example.shmuel.myapplication.model.datasource.ListDataSource;
import com.example.shmuel.myapplication.model.datasource.MySqlDataSource;

/**
 * Created by shmuel on 28/11/2017.
 */

public class SplashActivity extends AppCompatActivity {
    ListDataSource listDataSource;
    MySqlDataSource sqlDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        new loadInfo().execute();

    }
    class loadInfo extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

           /* if (ListDataSource.carList==null) {
                listDataSource=new ListDataSource();
            }*/
            if (MySqlDataSource.carList==null) {
                sqlDataSource=new MySqlDataSource();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
}