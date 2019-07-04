package org.semi.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.Contract;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    public MutableLiveData<List<Store>> listStore = new MutableLiveData<>();
    //public MutableLiveData<List<Store>> listStoreAddMore = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProduct = new MutableLiveData<>();
    public MutableLiveData<Location> currentLocation = new MutableLiveData<>();


    //Mode Config
    //Navigation 4 Direction -> config here:
    public MutableLiveData<Integer> modeMenuNavigationHome = new MutableLiveData<>();

    //Load Store or Product List
    public MutableLiveData<Integer> modeLoadDataInHome = new MutableLiveData<>();
    //Type of stores or products
    public MutableLiveData<Integer> categoryStore = new MutableLiveData<>();
    public MutableLiveData<Integer> categoryProduct = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        //First menu home
        modeMenuNavigationHome.setValue(Contract.MODE_MENU_HOME);
        //First data store
        modeLoadDataInHome.setValue(Contract.MODE_HOME_LOAD_STORE);
        //First data store type all
        categoryStore.setValue(Contract.MODE_HOME_LOAD_STORE_TYPE_ALL);
    }
    public void updateListStore(List<Store> stores){
        List<Store> temp = listStore.getValue();
        temp.addAll(stores);
        listStore.setValue(temp);
    }
}
