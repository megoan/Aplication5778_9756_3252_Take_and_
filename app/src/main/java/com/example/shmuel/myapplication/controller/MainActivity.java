package com.example.shmuel.myapplication.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shmuel.myapplication.controller.Clients.ClientEditActivity;
import com.example.shmuel.myapplication.controller.Clients.ClientTabFragment;
import com.example.shmuel.myapplication.R;
import com.example.shmuel.myapplication.controller.branches.BranchEditActivity;
import com.example.shmuel.myapplication.controller.branches.BranchesFragment;
import com.example.shmuel.myapplication.controller.carmodels.CarModelEditActivity;
import com.example.shmuel.myapplication.controller.carmodels.CarModelsFragment;
import com.example.shmuel.myapplication.controller.cars.CarEditActivity;
import com.example.shmuel.myapplication.controller.cars.CarsTabFragment;
import com.example.shmuel.myapplication.model.backend.BackEndFunc;
import com.example.shmuel.myapplication.model.backend.DataSourceType;
import com.example.shmuel.myapplication.model.backend.FactoryMethod;
import com.example.shmuel.myapplication.model.backend.SelectedDataSource;
import com.example.shmuel.myapplication.model.datasource.ListDataSource;
import com.example.shmuel.myapplication.model.entities.Branch;
import com.example.shmuel.myapplication.model.entities.Car;
import com.example.shmuel.myapplication.model.entities.CarModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    public static DataSourceType dataSourceType = DataSourceType.DATA_LIST;

    public MainActivity() {
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    public MainActivity(DataSourceType dataSourceType) {
        SelectedDataSource.dataSourceType = dataSourceType;
    }


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ListDataSource listDataSource;
    private BackEndFunc backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    TabFragments tabFragments;
    //private ViewPager mViewPager;
    private TabsType tabsType = TabsType.CARS;
    private TabsType tabsTypeBeforeChange = TabsType.CARS;
    //private PageAdapter pageAdapter;
    int updatedTab = 0;

    ProgressDialog progDailog;
    public static ProgressDialog progressDialog;


    FloatingActionButton fab;
    SearchView searchView;
    boolean check = true;
    public boolean is_in_action_mode = false;
    public boolean client_is_in_action_mode = false;
    public boolean car_model_is_in_action_mode = false;
    public boolean branch_is_in_action_mode = false;
    boolean searchClicked = false;
    boolean searchViewOn = false;
    public String filter = "";
    public Map<String, Boolean> carCom = new HashMap<String, Boolean>();
    public Map<String, Boolean> carModelCom = new HashMap<String, Boolean>();
    public Map<String, Boolean> branchesCom = new HashMap<String, Boolean>();

    //filtering companies
    String[] carCompanies;
    boolean[] carCompaniesChecked;
    ArrayList<String> carCompanySet = new ArrayList<>();
    Set<String> s;


    String[] carModelCompanies;
    boolean[] carModelCompaniesChecked;
    ArrayList<String> carModelCompanySet = new ArrayList<>();
    Set<String> cm;


    String[] branchesCities;
    boolean[] branchesCitiesChecked;
    ArrayList<String> branchesCitiesSet = new ArrayList<>();
    Set<String> b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        searchClicked = false;
        super.onCreate(savedInstanceState);
        if (TabFragments.carsTab == null) {
            tabFragments = new TabFragments();
        }


      /*  //TODO BON
       new BackGroundLoad().execute();*/
        /*if (ListDataSource.carList==null) {
            listDataSource=new ListDataSource();
        }*/

        // activateFilters ();

        backEndFunc = FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tabsType) {
                    case CARS: {
                        Intent intent = new Intent(MainActivity.this, CarEditActivity.class);
                        intent.putExtra("update", "false");
                        startActivity(intent);
                        return;
                    }
                    case CLIENTS: {
                        Intent intent = new Intent(MainActivity.this, ClientEditActivity.class);
                        intent.putExtra("update", "false");
                        startActivity(intent);
                        return;
                    }
                    case BRANCHES: {
                        Intent intent = new Intent(MainActivity.this, BranchEditActivity.class);
                        intent.putExtra("update", "false");
                        startActivity(intent);
                        return;
                    }
                    case CAR_MODELS: {
                        Intent intent = new Intent(MainActivity.this, CarModelEditActivity.class);
                        intent.putExtra("update", "false");
                        startActivity(intent);
                        return;
                    }

                }
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_blue)));

        if (savedInstanceState != null) {
            updatedTab = savedInstanceState.getInt("CHILD");
        } else {

            check = true;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabFragments.pageAdapter = new PageAdapter(getSupportFragmentManager());
        TabFragments.mViewPager = (ViewPager) findViewById(R.id.container);
        TabFragments.mViewPager.setOffscreenPageLimit(3);
        TabFragments.mViewPager.setAdapter(TabFragments.pageAdapter);


        // Set up the ViewPager with the sections adapter.


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(TabFragments.mViewPager);

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setFocusable(false);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchClicked = true;
                return false;
            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                searchViewOn = true;
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchClicked && check && tabsType == tabsTypeBeforeChange) {
                    switch (tabsType) {
                        case CARS: {
                            CarsTabFragment.mAdapter.getFilter().filter(newText);
                            break;
                        }
                        case CAR_MODELS: {
                            CarModelsFragment.mAdapter.getFilter().filter(newText);
                            break;
                        }
                        case BRANCHES: {
                            BranchesFragment.mAdapter.getFilter().filter(newText);
                            break;
                        }
                        case CLIENTS: {
                            ClientTabFragment.mAdapter.getFilter().filter(newText);
                            break;
                        }
                    }
                } else {
                    CarsTabFragment.mAdapter.getFilter().filter(newText);
                    CarModelsFragment.mAdapter.getFilter().filter(newText);
                    BranchesFragment.mAdapter.getFilter().filter(newText);
                    ClientTabFragment.mAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        TabFragments.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                closeAction();
                tabsTypeBeforeChange = TabsType.ALL;
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);

            }

            @Override
            public void onPageSelected(int position) {
                closeAction();
                TabFragments.mViewPager.getAdapter().notifyDataSetChanged();

                switch (position) {
                    case 0: {
                        tabsType = tabsTypeBeforeChange = TabsType.CARS;
                        searchView.setQueryHint("cars");
                        break;
                    }
                    case 1: {
                        tabsType = tabsTypeBeforeChange = TabsType.CAR_MODELS;
                        searchView.setQueryHint("car models");
                        break;
                    }
                    case 2: {
                        tabsType = tabsTypeBeforeChange = TabsType.BRANCHES;
                        searchView.setQueryHint("branches");
                        break;
                    }
                    case 3: {
                        tabsType = tabsTypeBeforeChange = TabsType.CLIENTS;
                        searchView.setQueryHint("clients");
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        changeFab();
                        if (tabsType != TabsType.CLIENTS)
                            fab.show();
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                    case ViewPager.SCROLL_STATE_SETTLING:
                        fab.hide();
                        break;
                }
            }
        });


    }

    public void onClickSort(View view) {
        closeAction();
        switch (tabsType) {
            case CARS: {
                final CarsTabFragment carsTabFragment = (CarsTabFragment) TabFragments.mViewPager.getAdapter().instantiateItem(TabFragments.mViewPager, TabFragments.mViewPager.getCurrentItem());
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sort by:");
                // add a radio button list
                String[] options = {"company name", "branch", "year", "mileage", "daily price", "mile price", "rating"};
                int checkedItem = 0; // cow
                builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                    }
                });
                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkedItem == "company name") {
                            carsTabFragment.sortCarByCarCompanyName();
                        } else if (checkedItem == "branch") {
                            carsTabFragment.sortCarByCarBranch();
                        } else if (checkedItem == "year") {
                            carsTabFragment.sortCarByYear();
                        } else if (checkedItem == "mileage") {
                            carsTabFragment.sortCarByMileage();
                        } else if (checkedItem == "daily price") {
                            carsTabFragment.sortCarBydailyPrice();
                        } else if (checkedItem == "mile price") {
                            carsTabFragment.sortCarByMilePrice();
                        } else {
                            carsTabFragment.sortCarByRating();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case CAR_MODELS: {
                final CarModelsFragment carModelsFragment = (CarModelsFragment) TabFragments.mViewPager.getAdapter().instantiateItem(TabFragments.mViewPager, TabFragments.mViewPager.getCurrentItem());
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sort by:");

                // add a radio button list
                String[] options = {"company name", "passengers", "luggage"};
                int checkedItem = 0; // cow
                builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                    }
                });
                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkedItem == "company name") {
                            carModelsFragment.sortCarModelsByCompanyName();
                        } else if (checkedItem == "passengers") {
                            carModelsFragment.sortCarModelsByPassengers();
                        } else {
                            carModelsFragment.sortCarModelsByLuggage();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case BRANCHES: {
                final BranchesFragment branchesFragment = (BranchesFragment) TabFragments.mViewPager.getAdapter().instantiateItem(TabFragments.mViewPager, TabFragments.mViewPager.getCurrentItem());
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sort by:");

                // add a radio button list
                String[] options = {"address", "revenue", "number of cars", "branch number"};
                int checkedItem = 0; // cow
                builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                    }
                });
                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkedItem == "address") {
                            branchesFragment.sortBranchByAddress();
                        } else if (checkedItem == "revenue") {
                            branchesFragment.sortBranchByRevenue();
                        } else if (checkedItem == "number of cars") {
                            branchesFragment.sortBranchByNumberOfCars();
                        } else {
                            branchesFragment.sortBranchByBranchNum();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case CLIENTS: {
                final ClientTabFragment clientTabFragment = (ClientTabFragment) TabFragments.mViewPager.getAdapter().instantiateItem(TabFragments.mViewPager, TabFragments.mViewPager.getCurrentItem());
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sort by:");

                // add a radio button list
                String[] options = {"first name", "last name", "id"};
                int checkedItem = 0; // cow
                builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                    }
                });
                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        if (checkedItem == "first name") {
                            clientTabFragment.SortCliantByFirstName();
                        } else if (checkedItem == "last name") {
                            clientTabFragment.SortCliantByLastName();
                        } else {
                            clientTabFragment.SortCliantById();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
        }

    }

    public void onClickFilter(View view) {
        closeAction();
        switch (tabsType) {
            case CARS: {
                filter = "";
                updateAbstractFilter(carCom, carCompanySet);
                carCompanies = new String[carCom.size()];
                carCompaniesChecked = new boolean[carCom.size()];
                int i = 0;
                for (Map.Entry<String, Boolean> entry : carCom.entrySet()
                        ) {
                    carCompanies[i] = entry.getKey();
                    carCompaniesChecked[i] = entry.getValue();
                    i++;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Filter by:");

                // add a checkbox button list
                builder.setMultiChoiceItems(carCompanies, carCompaniesChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Update the current focused item's checked status
                        carCompaniesChecked[which] = isChecked;
                        // Get the current focused item
                        String currentItem = carCompanies[which];

                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        for (int i = 0; i < carCompanies.length; i++) {
                            carCom.put(carCompanies[i], carCompaniesChecked[i]);
                            if (carCompaniesChecked[i] == true) {
                                filter += carCompanies[i];
                            }
                        }
                        if (filter.length() == 0) filter = "you got no cars dude";
                        CarsTabFragment.mAdapter.getFilter().filter(filter);
                        CarsTabFragment.mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case CAR_MODELS: {
                filter = "";
                updateAbstractFilter(carModelCom, carModelCompanySet);
                carModelCompanies = new String[carModelCom.size()];
                carModelCompaniesChecked = new boolean[carModelCom.size()];
                int i = 0;
                for (Map.Entry<String, Boolean> entry : carModelCom.entrySet()
                        ) {
                    carModelCompanies[i] = entry.getKey();
                    carModelCompaniesChecked[i] = entry.getValue();
                    i++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Filter by:");

                // add a checkbox button list
                builder.setMultiChoiceItems(carModelCompanies, carModelCompaniesChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Update the current focused item's checked status
                        carModelCompaniesChecked[which] = isChecked;
                        // Get the current focused item
                        String currentItem = carModelCompanies[which];
                        // Notify the current action
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < carModelCompanies.length; i++) {
                            carModelCom.put(carModelCompanies[i], carModelCompaniesChecked[i]);
                            if (carModelCompaniesChecked[i] == true) {
                                filter += carModelCompanies[i];
                            }
                        }
                        if (filter.length() == 0) filter = "you got no cars dude";
                        CarModelsFragment.mAdapter.getFilter().filter(filter);
                        CarModelsFragment.mAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case BRANCHES: {
                filter = "";
                updateAbstractFilter(branchesCom, branchesCitiesSet);
                branchesCities = new String[branchesCom.size()];
                branchesCitiesChecked = new boolean[branchesCom.size()];
                int i = 0;
                for (Map.Entry<String, Boolean> entry : branchesCom.entrySet()
                        ) {
                    branchesCities[i] = entry.getKey();
                    branchesCitiesChecked[i] = entry.getValue();
                    i++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Filter by:");

                // add a checkbox button list
                builder.setMultiChoiceItems(branchesCities, branchesCitiesChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Update the current focused item's checked status
                        branchesCitiesChecked[which] = isChecked;
                        // Get the current focused item
                        String currentItem = branchesCities[which];
                        // Notify the current action
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < branchesCities.length; i++) {
                            branchesCom.put(branchesCities[i], branchesCitiesChecked[i]);
                            if (branchesCitiesChecked[i] == true) {
                                filter += branchesCities[i];
                            }
                        }
                        if (filter.length() == 0) filter = "you got no cars dude";
                        BranchesFragment.mAdapter.getFilter().filter(filter);
                        BranchesFragment.mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case CLIENTS: {

                Toast.makeText(getApplicationContext(),
                        "No information to filter", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("CHILD", TabFragments.mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabsType.toString());
        outState.putBoolean("is_in_action_mode", is_in_action_mode);
        outState.putBoolean("client_is_in_action_mode", client_is_in_action_mode);
        outState.putBoolean("car_model_is_in_action_mode", car_model_is_in_action_mode);
        outState.putBoolean("branch_is_in_action_mode", branch_is_in_action_mode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        is_in_action_mode = savedInstanceState.getBoolean("is_in_action_mode");
        client_is_in_action_mode = savedInstanceState.getBoolean("client_is_in_action_mode");
        car_model_is_in_action_mode = savedInstanceState.getBoolean("car_model_is_in_action_mode");
        branch_is_in_action_mode = savedInstanceState.getBoolean("branch_is_in_action_mode");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.MissionAndValues) {
            Intent intent = new Intent(MainActivity.this, MissionAndValuesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_statistics) {
            Intent intent = new Intent(MainActivity.this, RevenueActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //activate class filters
    public void activateFilters() {


        //activate car filter
        if (ListDataSource.carList.size() > 0) {
            for (Car car : ListDataSource.carList
                    ) {
                CarModel carModel = backEndFunc.getCarModel(car.getCarModel());
                carCompanySet.add(carModel.getCompanyName());
            }

            s = new LinkedHashSet<>(carCompanySet);
            carCompanySet.clear();
            carCompanySet.addAll(s);

            for (int i = 0; i < carCompanySet.size(); i++) {
                carCom.put(carCompanySet.get(i), true);
            }
        }

        //activate cae model filter

        if (ListDataSource.carModelList.size() > 0) {
            for (CarModel carmodel : ListDataSource.carModelList
                    ) {
                carModelCompanySet.add(carmodel.getCompanyName());
            }

            s = new LinkedHashSet<>(carModelCompanySet);
            carModelCompanySet.clear();
            carModelCompanySet.addAll(s);

            carModelCompanies = new String[carModelCompanySet.size()];
            for (int i = 0; i < carModelCompanies.length; i++) {
                carModelCompanies[i] = carModelCompanySet.get(i);

            }
            carModelCompaniesChecked = new boolean[carModelCompanies.length];
            for (int i = 0; i < carModelCompaniesChecked.length; i++) {
                carModelCompaniesChecked[i] = true;
            }
        }


        //activate branch filter
        for (Branch branch : ListDataSource.branchList
                ) {
            branchesCitiesSet.add(branch.getMyAddress().getLocality());
        }

        b = new LinkedHashSet<>(branchesCitiesSet);
        branchesCitiesSet.clear();
        branchesCitiesSet.addAll(b);

        branchesCities = new String[branchesCitiesSet.size()];
        for (int i = 0; i < branchesCities.length; i++) {
            branchesCities[i] = branchesCitiesSet.get(i);

        }
        branchesCitiesChecked = new boolean[branchesCities.length];
        for (int i = 0; i < branchesCitiesChecked.length; i++) {
            branchesCitiesChecked[i] = true;

        }
    }

    public void updateAbstractFilter(Map<String, Boolean> hashMap, ArrayList<String> set) {

        set.clear();
        switch (tabsType) {
            case CARS: {
                for (Car car : backEndFunc.getAllCars()
                        ) {
                    CarModel carModel = backEndFunc.getCarModel(car.getCarModel());
                    set.add(carModel.getCompanyName());
                }
                break;
            }
            case CAR_MODELS: {
                for (CarModel carmodel : ListDataSource.carModelList
                        ) {
                    set.add(carmodel.getCompanyName());
                }
                break;
            }
            case BRANCHES: {
                for (Branch branch : ListDataSource.branchList
                        ) {
                    branchesCitiesSet.add(branch.getMyAddress().getLocality());
                }
                break;
            }
            case CLIENTS: {
                break;
            }
        }
        s = new LinkedHashSet<>(set);
        set.clear();
        set.addAll(s);

        for (int i = 0; i < set.size(); i++) {
            if (!hashMap.containsKey(set.get(i))) {
                hashMap.put(set.get(i), true);
            }
        }
        boolean check;
        ArrayList<String> r = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : hashMap.entrySet()
                ) {
            check = false;
            for (int j = 0; j < set.size(); j++) {
                if (entry.getKey() == set.get(j)) {
                    check = true;
                }
            }
            if (check == false) {
                r.add(entry.getKey());
            }
        }
        for (String s : r
                ) {
            hashMap.remove(s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (tabsType) {
            case CARS: {
                if (TabFragments.carsTab.mAdapter != null) {
                    TabFragments.carsTab.mAdapter.notifyDataSetChanged();
                    TabFragments.branchesTab.mAdapter.notifyDataSetChanged();
                }
                break;
            }
            case CAR_MODELS: {
                if (TabFragments.carModelsTab.mAdapter != null) {
                    TabFragments.carModelsTab.mAdapter.notifyDataSetChanged();
                }
                break;
            }
            case BRANCHES: {
                if (TabFragments.branchesTab.mAdapter != null) {
                    TabFragments.branchesTab.mAdapter.notifyDataSetChanged();
                }
                break;
            }
            case CLIENTS: {
                if (TabFragments.clientsTab.mAdapter != null) {
                    TabFragments.clientsTab.mAdapter.notifyDataSetChanged();
                }
                break;
            }
        }

    }

    public void closeAction() {
        if (TabFragments.carsTab.mAdapter != null && TabFragments.carsTab.mAdapter.actionMode != null) {
            TabFragments.carsTab.mAdapter.actionMode.finish();
        }
        if (TabFragments.clientsTab.mAdapter != null && TabFragments.clientsTab.mAdapter.actionMode != null) {
            TabFragments.clientsTab.mAdapter.actionMode.finish();
        }
        if (TabFragments.carModelsTab.mAdapter != null && TabFragments.carModelsTab.mAdapter.actionMode != null) {
            TabFragments.carModelsTab.mAdapter.actionMode.finish();
        }
        if (TabFragments.branchesTab.mAdapter != null && TabFragments.branchesTab.mAdapter.actionMode != null) {
            TabFragments.branchesTab.mAdapter.actionMode.finish();
        }
    }

    public void changeFab() {
        switch (tabsType) {
            case CARS: {
                fab.setImageResource(R.drawable.ic_madd);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_blue)));
                break;
            }
            case CLIENTS: {
                fab.setImageResource(R.drawable.ic_add_client);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_darkred)));
                break;
            }
            case BRANCHES: {
                fab.setImageResource(R.drawable.ic_add_branch);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_darkgreen)));
                break;
            }
            case CAR_MODELS: {
                fab.setImageResource(R.drawable.ic_madd);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_darkorange)));
            }
        }
    }

}
