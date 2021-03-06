package com.example.shmuel.myapplication.controller.branches;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shmuel.myapplication.R;
import com.example.shmuel.myapplication.controller.InputWarningDialog;
import com.example.shmuel.myapplication.controller.TabFragments;
import com.example.shmuel.myapplication.model.backend.BackEndFunc;
import com.example.shmuel.myapplication.model.backend.DataSourceType;
import com.example.shmuel.myapplication.model.backend.FactoryMethod;
import com.example.shmuel.myapplication.model.datasource.MySqlDataSource;
import com.example.shmuel.myapplication.model.entities.Branch;
import com.example.shmuel.myapplication.model.entities.MyAddress;
import com.example.shmuel.myapplication.model.entities.MyDate;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BranchActivity extends AppCompatActivity {
    BackEndFunc backEndFunc= FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
    public ActionMode actionMode;
    private MyAddress myAddress =new MyAddress();
    private MyDate myDate=new MyDate();
    private int parkingSpotsNum;
    private int numOfCars;
    private int avaibaleSpots;
    private int branchNum;
    private String imgUrl;
    private double branchRevenue;
    private String establishedDate;
    private boolean inUse;
    private ArrayList<Integer>carList=new ArrayList<>();
    private ProgressDialog progDailog;
    ImageView imageView;
    ProgressBar progressBar;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        BranchActivity.MyActionModeCallbackClient callback=new BranchActivity.MyActionModeCallbackClient();
        actionMode=startActionMode(callback);
        imageView=(ImageView)findViewById(R.id.mainImage);
        progressBar=findViewById(R.id.downloadProgressBar);
        Intent intent =getIntent();
        imgUrl=intent.getStringExtra("imgUrl");
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.rental)
                .placeholder(R.drawable.rental)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        branchNum=intent.getIntExtra("id",0);

        myAddress.setCountry(intent.getStringExtra("country"));
        myAddress.setLocality(intent.getStringExtra("locality"));
        myAddress.setAddressName(intent.getStringExtra("addressName"));
        myAddress.setLatitude(intent.getDoubleExtra("latitude",0));
        myAddress.setLongitude(intent.getDoubleExtra("longitude",0));
        myDate.setYear(intent.getIntExtra("year",0));
        myDate.setMonth(intent.getStringExtra("month"));
        myDate.setDay(intent.getIntExtra("day",0));
        establishedDate=intent.getStringExtra("established");
        parkingSpotsNum=intent.getIntExtra("parkingSpotsNum",0);
        avaibaleSpots=intent.getIntExtra("available",0);
        inUse=intent.getBooleanExtra("inUse",false);
        branchRevenue=intent.getDoubleExtra("revenue",0);
        numOfCars=intent.getIntExtra("numOfCars",0);
        carList=intent.getIntegerArrayListExtra("carList");
        position=intent.getIntExtra("position",0);
        TextView branchIDText=(TextView)findViewById(R.id.branchid);
        TextView addressText =(TextView)findViewById(R.id.address);
        TextView numOfCarsText =(TextView)findViewById(R.id.numOfCars);
        TextView numOfSpotsText =(TextView)findViewById(R.id.numOfSpots);
        TextView revenueText =(TextView)findViewById(R.id.revenue);
        TextView inUseText=(TextView)findViewById(R.id.inUse_branch);
        TextView establish=(TextView)findViewById(R.id.established);

        branchIDText.setText(String.valueOf("#"+branchNum));
        addressText.setText(myAddress.getAddressName());
        numOfCarsText.setText(String.valueOf(numOfCars));
        numOfSpotsText.setText(String.valueOf(String.valueOf(avaibaleSpots)));
        revenueText.setText(String.valueOf(branchRevenue));
        inUseText.setText(String.valueOf(inUse));
        establish.setText(establishedDate);

        actionMode.setTitle(myAddress.getAddressName());
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
                    if(inUse==true)
                    {
                        Toast.makeText(BranchActivity.this,
                                "cannot delete branch! branch in use!!!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(BranchActivity.this);

                    builder.setTitle("Delete Branch");

                    builder.setMessage("are you sure?");

                    builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                          //backEndFunc.deleteBranch(branchNum);
                           new BackGroundDeleteBranch().execute();
                            //TabFragments.branchesTab.updateView2(position);

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
                    if(inUse==true)
                    {
                        Toast.makeText(BranchActivity.this,
                                "cannot update branch! branch in use!!!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent=new Intent(BranchActivity.this, BranchEditActivity.class);
                    intent.putExtra("update","true");
                    intent.putExtra("addressName", myAddress.getAddressName());
                    intent.putExtra("latitude", myAddress.getLatitude());
                    intent.putExtra("longitude", myAddress.getLongitude());
                    intent.putExtra("country", myAddress.getCountry());
                    intent.putExtra("locality",myAddress.getLocality());
                    intent.putExtra("branchID",branchNum);
                    intent.putExtra("imgUrl",imgUrl);
                    intent.putExtra("inUse",inUse);
                    intent.putExtra("established",establishedDate);
                    intent.putExtra("parkingSpotsNum",parkingSpotsNum);
                    intent.putExtra("available",avaibaleSpots);
                    intent.putExtra("revenue",branchRevenue);
                    intent.putExtra("year",myDate.getYear());
                    intent.putExtra("month",myDate.getMonth());
                    intent.putExtra("day",myDate.getDay());
                    intent.putExtra("numOfCars",numOfCars);
                    intent.putExtra("carList",carList);
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
    public class BackGroundDeleteBranch extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(BranchActivity.this);
            progDailog.setMessage("Updating...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean b=backEndFunc.deleteBranch(branchNum);
            if(!b)
            {
                return b;
            }
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference imageRef;
            imageRef = storageRef.child("branch"+"/"+branchNum+".jpg");

            imageRef.delete();
            MySqlDataSource.branchList=backEndFunc.getAllBranches();
            return b;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if(!b)
            {
                InputWarningDialog.showWarningDialog("Server error","sorry, branch wasn't deleted! \nplease try again soon!",BranchActivity.this);
                progDailog.dismiss();
                return;
            }
            BranchesFragment.mAdapter.objects= (ArrayList<Branch>) MySqlDataSource.branchList;
            BranchesFragment.branches= (ArrayList<Branch>) MySqlDataSource.branchList;
            BranchesFragment.mAdapter.notifyDataSetChanged();
            Toast.makeText(BranchActivity.this,
                    "branch deleted", Toast.LENGTH_SHORT).show();
            actionMode.finish();
            progDailog.dismiss();
        }
    }


}
