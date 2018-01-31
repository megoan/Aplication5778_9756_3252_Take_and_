package com.example.shmuel.myapplication.controller.Clients;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shmuel.myapplication.R;
import com.example.shmuel.myapplication.controller.InputWarningDialog;
import com.example.shmuel.myapplication.controller.MainActivity;
import com.example.shmuel.myapplication.controller.TabFragments;
import com.example.shmuel.myapplication.controller.branches.BranchesFragment;
import com.example.shmuel.myapplication.model.backend.BackEndFunc;
import com.example.shmuel.myapplication.model.backend.DataSourceType;
import com.example.shmuel.myapplication.model.backend.FactoryMethod;
import com.example.shmuel.myapplication.model.backend.SelectedDataSource;
import com.example.shmuel.myapplication.model.datasource.MySqlDataSource;
import com.example.shmuel.myapplication.model.entities.Client;

import java.util.ArrayList;

public class ClientActivity extends AppCompatActivity {
    BackEndFunc backEndFunc= FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
    public ActionMode actionMode;
    int id;
    String name;
    String lastName;
    String phone;
    String email;
    String creditCardNum;
    private ProgressDialog progDailog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
       // MyActionModeCallbackClient callback=new MyActionModeCallbackClient();
       // actionMode=startActionMode(callback);

        Intent intent =getIntent();
        name=intent.getStringExtra("name");
        lastName=intent.getStringExtra("lastName");
        phone=intent.getStringExtra("phone");
        email=intent.getStringExtra("email");
        id=intent.getIntExtra("id",-1);
        creditCardNum=intent.getStringExtra("credit");

        TextView nameclient =(TextView)findViewById(R.id.numOfCars);
        TextView lastnameclient =(TextView)findViewById(R.id.numOfSpots);
        TextView idclient =(TextView)findViewById(R.id.id_client);
        TextView phoneclient =(TextView)findViewById(R.id.phoneClient);
        TextView emailclient =(TextView)findViewById(R.id.emailClient);
        TextView credit=(TextView)findViewById(R.id.creditClient);

        nameclient.setText(name);
        lastnameclient.setText(lastName);
        idclient.setText("#"+String.valueOf(id));
        phoneclient.setText(phone);
        emailclient.setText(email);
        credit.setText(creditCardNum);
//        actionMode.setTitle(name+" "+lastName);
    }
    public class MyActionModeCallbackClient implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.preview,menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.delete_item:{

                    AlertDialog.Builder builder = new AlertDialog.Builder(ClientActivity.this);

                    builder.setTitle("Delete Client");

                    builder.setMessage("are you sure?");

                    builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            new BackGroundDeleteClient().execute();
                        }
                    });

                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    break;
                }
                case R.id.edit_item:
                {
                    Intent intent=new Intent(ClientActivity.this,ClientEditActivity.class);
                    intent.putExtra("update","true");
                    intent.putExtra("name",name);
                    intent.putExtra("lastName",lastName);
                    intent.putExtra("my_id",id);
                    intent.putExtra("phone",phone);
                    intent.putExtra("email",email);
                    intent.putExtra("credit",creditCardNum);
                    finish();
                    startActivity(intent);
                    break;
                }
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            finish();
        }

    }
    public class BackGroundDeleteClient extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(ClientActivity.this);
            progDailog.setMessage("Updating...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Boolean b=backEndFunc.deleteClient(id);
            if(!b)
            {
                return b;
            }
            MySqlDataSource.clientList = backEndFunc.getAllClients();
            return b;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if(!b)
            {
                InputWarningDialog.showWarningDialog("Server error","sorry, client wasn't deleted! \nplease try again soon!",ClientActivity.this);
                progDailog.dismiss();
                return;
            }
            ClientTabFragment.mAdapter.objects= (ArrayList<Client>) MySqlDataSource.clientList;
            ClientTabFragment.clients=MySqlDataSource.clientList;
            ClientTabFragment.mAdapter.notifyDataSetChanged();
            TabFragments.clientsTab.updateView2();
            Toast.makeText(ClientActivity.this,
                    "client deleted", Toast.LENGTH_SHORT).show();
            //actionMode.finish();
            progDailog.dismiss();
        }
    }

}
