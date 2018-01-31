package com.example.shmuel.myapplication.model.backend;

import com.example.shmuel.myapplication.model.datasource.ListDataSource;
import com.example.shmuel.myapplication.model.entities.Branch;
import com.example.shmuel.myapplication.model.entities.Car;
import com.example.shmuel.myapplication.model.entities.CarModel;
import com.example.shmuel.myapplication.model.entities.Client;
import com.example.shmuel.myapplication.model.entities.Order;

import java.util.ArrayList;

/**
 * Created by shmuel on 19/10/2017.
 */

public class BackEndForList implements BackEndFunc {


    @Override
    public Updates addClient(Client client) {
        if(ListDataSource.clientList.add(client))return Updates.NOTHING;
       return Updates.ERROR;
//
    }

    @Override
    public Updates addCarModel(CarModel carModel) {
        if(ListDataSource.carModelList.add(carModel))return Updates.NOTHING;
      return Updates.ERROR;
    }


    @Override
    public Updates addCar(Car car) {
         if(ListDataSource.carList.add(car)){
            return Updates.NOTHING;
         }
        return Updates.ERROR;
    }
    


    @Override
    public Updates addBranch(Branch branch)
    {
        if(ListDataSource.branchList.add(branch))return Updates.NOTHING;
       return Updates.ERROR;
    }




    @Override
    public boolean updateClient(Client client) {

        for(int i=0;i< ListDataSource.clientList.size();i++)
        {
            if(ListDataSource.clientList.get(i).getId()==client.getId())
            {
                ListDataSource.clientList.set(i,client);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateCarModel(CarModel carModel) {
        for(int i=0;i< ListDataSource.carModelList.size();i++)
        {
            if(ListDataSource.carModelList.get(i).getCarModelCode()==carModel.getCarModelCode())
            {
                ListDataSource.carModelList.set(i,carModel);
                return true;
            }
        }
        return false;
    }

    @Override
    public Updates updateCar(Car car,int originalCarModel,int originalbranch)
    {
        updateCar(car);

        CarModel carModel=getCarModel(car.getCarModel());
        {
            if(carModel.isInUse()==false)
            {
                carModel.setInUse(true);
                updateCarModel(carModel);
            }
        }

        CarModel originalCarModelTmp=getCarModel(originalCarModel);
        for(Car car1:ListDataSource.carList)
        {
            if(car1.getCarModel()==carModel.getCarModelCode()){
                return Updates.NOTHING;
            }
        }
        carModel.setInUse(false);
        updateCarModel(originalCarModelTmp);
       return Updates.CARMODEL;
    }
    @Override
    public Updates updateCar(Car car) {
        Updates updates=Updates.NOTHING;
        boolean sameBranch=false;
        for(Branch branch:ListDataSource.branchList)
        {
            if(car.getBranchNum()==branch.getBranchNum())
            {
                for(int i=0;i<branch.getCarIds().size();i++)
                {
                    if(car.getCarNum()==branch.getCarIds().get(i))
                    {
                        sameBranch=true;
                        break;
                    }
                }
                if(sameBranch==true)break;
                else
                {
                    updates=Updates.BRANCH;
                    removeCarFromBranch(car.getCarNum());
                    addCarToBranch(car.getCarNum(),branch.getBranchNum());
                }
            }
        }

        for(int i=0;i< ListDataSource.carList.size();i++)
        {
            if(ListDataSource.carList.get(i).getCarNum()==car.getCarNum())
            {
                ListDataSource.carList.set(i,car);
                return Updates.NOTHING;
            }
        }
        return Updates.ERROR;
    }

    private void removeCarFromBranch(int carNum) {
        boolean remove=false;
        Branch branch1=null;
        for(Branch branch:ListDataSource.branchList)
        {
            branch1=branch;
            for(int i=0;i<branch.getCarIds().size();i++)
            {
                if(carNum==branch.getCarIds().get(i))
                {
                    remove =true;
                    break;

                }
            }
            if (remove==true)break;
        }
        if (remove==true)
        {
            branch1.getCarIds().remove(new Integer(carNum));
            if(branch1.getCarIds().size()==0){
                branch1.setInUse(false);
                updateBranch(branch1);
            }
        }

    }

    @Override
    public boolean updateBranch(Branch branch) {
        for(int i=0;i< ListDataSource.branchList.size();i++)
        {
            if(ListDataSource.branchList.get(i).getBranchNum()==branch.getBranchNum())
            {
              ListDataSource.branchList.set(i,branch);
                return true;
            }
        }
return false;
    }



    @Override
    public boolean deleteClient(int clientID) {
        Client clientTmp=null;
        for (Client client: ListDataSource.clientList
             ) {
            if(client.getId()==clientID){
                clientTmp=client;
                break;
            }
        }
        return ListDataSource.clientList.remove(clientTmp);
    }

    @Override
    public boolean deleteCarModel(int carModelID) {
        CarModel carModelTmp=null;
        for (CarModel carModel: ListDataSource.carModelList
                ) {
            if(carModel.getCarModelCode()==carModelID){
                carModelTmp=carModel;
                break;

            }
        }
        return ListDataSource.carModelList.remove(carModelTmp);
    }

    @Override
    public Updates deleteCar(int carID, int branchID) {
        Updates updates=Updates.NOTHING;
        int carModelCode=0;
        Car carTmp=null;
        boolean dontneedToUpdateModel=false;
        for (Car car: ListDataSource.carList
                ) {
            if(car.getCarNum()==carID){
                carTmp=car;
                carModelCode=(car.getCarModel());
                break;
            }
        }
        ListDataSource.carList.remove(carTmp);

        for (Car car: ListDataSource.carList)
        {
            if(car.getCarModel()==carModelCode)
            {
                 dontneedToUpdateModel=true;
            }
        }
        if (!dontneedToUpdateModel) {
            CarModel carModel=getCarModel(carModelCode);
            carModel.setInUse(false);
            updateCarModel(carModel);
            updates=Updates.CARMODEL;
        }
        return updates;
    }

    @Override
    public boolean deleteBranch(int branchID) {
        Branch branchTmp=null;
        for (Branch branch: ListDataSource.branchList
                ) {
            if(branch.getBranchNum()==branchID){
                branchTmp=branch;
                break;
            }
        }
        return ListDataSource.branchList.remove(branchTmp);
    }

    @Override
    public Client getClient(int id) {
        for (Client client: ListDataSource.clientList
                ) {
            if(id==client.getId())return client;
        }
        return null;
    }

    @Override
    public CarModel getCarModel(int carModelNumber) {
        for (CarModel carModel: ListDataSource.carModelList
                ) {
            if(carModelNumber==carModel.getCarModelCode())return carModel;
        }
        return null;
    }

    @Override
    public Car getCar(int carNumber) {
        for (Car car: ListDataSource.carList
                ) {
            if(carNumber==car.getCarNum())return car;
        }
        return null;
    }

    @Override
    public Branch getBranch(int branchNumber) {
        int i=8;
        i++;
        for (Branch branch: ListDataSource.branchList
                ) {
            if(branchNumber==branch.getBranchNum())return branch;
        }
        return null;
    }




    @Override
    public ArrayList<CarModel> getAllCarModels() {
        return (ArrayList<CarModel>) ListDataSource.carModelList;
    }

    @Override
    public ArrayList<Client> getAllClients() {
        return (ArrayList<Client>) ListDataSource.clientList;
    }

    @Override
    public ArrayList<Branch> getAllBranches() {
        return (ArrayList<Branch>) ListDataSource.branchList;
    }

    @Override
    public ArrayList<Car> getAllCars() {
        return (ArrayList<Car>) ListDataSource.carList;
    }

    @Override
    public boolean removeCarFromBranch(int carID, int branch)
    {
        Branch branch1=getBranch(branch);
        branch1.getCarIds().remove(new Integer(carID));
        if(branch1.getCarIds().size()==0){
            branch1.setInUse(false);
            updateBranch(branch1);
        }
       return true;
    }

    @Override
    public boolean addCarToBranch(int carID, int branch){
        Branch branch1=getBranch(branch);
        if(branch1.getCarIds().size()==0){
            branch1.setInUse(true);
            updateBranch(branch1);
        }
        return getBranch(branch).getCarIds().add(new Integer(carID));
    }

    @Override
    public ArrayList<Order> getAllOrders() {
        return null;
    }

    @Override
    public ArrayList<Order> getAllClosedOrders() {
        return null;
    }

}
