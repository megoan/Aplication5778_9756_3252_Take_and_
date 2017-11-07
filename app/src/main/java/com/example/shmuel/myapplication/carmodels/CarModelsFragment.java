package com.example.shmuel.myapplication.carmodels;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shmuel.myapplication.Clients.ClientRecyclerViewAdapter;
import com.example.shmuel.myapplication.DividerItemDecoration;
import com.example.shmuel.myapplication.R;
import com.example.shmuel.myapplication.cars.CarRecyclerViewAdapter;
import com.example.shmuel.myapplication.model.backend.BackEndFunc;
import com.example.shmuel.myapplication.model.backend.DataSourceType;
import com.example.shmuel.myapplication.model.backend.FactoryMethod;
import com.example.shmuel.myapplication.model.entities.Branch;
import com.example.shmuel.myapplication.model.entities.Car;
import com.example.shmuel.myapplication.model.entities.CarModel;
import com.example.shmuel.myapplication.model.entities.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CarModelsFragment extends Fragment {

    private RecyclerView recyclerView;
    public CarCompaniesInnerRecyclerViewAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CarModel> carModels;
    private BackEndFunc backEndFunc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        backEndFunc= FactoryMethod.getBackEndFunc(DataSourceType.DATA_LIST);
        //carModels=new ArrayList<>(backEndFunc.getAllCarModels());
        View view1=inflater.inflate(R.layout.recycle_view_layout, container, false);
        recyclerView= view1.findViewById(R.id.recycleView);
        mAdapter=new CarCompaniesInnerRecyclerViewAdapter(backEndFunc.getAllCarModels(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        return view1;
    }
    public void filterByCompanyName(String[] carCompanies,boolean[] carCompaniesChecked)
    {
        carModels=new ArrayList<>(backEndFunc.getAllCarModels());
        ArrayList<CarModel> tmp=new ArrayList<>();
        for (int j = 0; j < carCompanies.length; j++) {
            for (CarModel carModel:carModels
                    ) {
                if(carModel.getCompanyName()==carCompanies[j])
                {
                    if (carCompaniesChecked[j]==false) {
                        tmp.add(carModel);
                    }
                }
            }

        }
        carModels.removeAll(tmp);
        updateView();
    }

    public void sortCarModelsByCompanyName()
    {
        Collections.sort(carModels, new Comparator<CarModel>(){
            public int compare(CarModel o1, CarModel o2){
                return o1.getCompanyName().compareToIgnoreCase(o2.getCompanyName());
            }
        });
        updateView();
    }
    public void sortCarModelsByPassengers()
    {
        Collections.sort(carModels, new Comparator<CarModel>(){
            public int compare(CarModel o1, CarModel o2){
                return String.valueOf(o1.getPassengers()).compareToIgnoreCase(String.valueOf(o2.getPassengers()));
            }
        });
        updateView();
    }
    public void sortCarModelsByLuggage()
    {
        Collections.sort(carModels, new Comparator<CarModel>(){
            public int compare(CarModel o1, CarModel o2){
                return String.valueOf(o1.getLuggage()).compareToIgnoreCase(String.valueOf(o2.getLuggage()));
            }
        });
        updateView();
    }

    public void updateView()
    {
        mAdapter=new CarCompaniesInnerRecyclerViewAdapter(carModels,getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public String carModelSearchString(CarModel carModel)
    {
        return (carModel.getCompanyName()+" "+carModel.getCarModelName()).toLowerCase();
    }
    public void filterCardsSearch(String string)
    {
        carModels=new ArrayList<>(backEndFunc.getAllCarModels());
        ArrayList<CarModel> tmp=new ArrayList<>();
        for (int j = 0; j < carModels.size(); j++) {
            for (CarModel carModel:carModels) {

                if(!carModelSearchString(carModel).contains(string.toLowerCase()))
                {
                    tmp.add(carModel);
                }
            }
        }
        carModels.removeAll(tmp);
        updateView();
    }

}