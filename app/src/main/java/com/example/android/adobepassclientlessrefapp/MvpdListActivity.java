package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.android.adobepassclientlessrefapp.fragments.ProviderDialogFragment;
import com.example.android.adobepassclientlessrefapp.adobeauth.TypeAdapterStringToList;
import com.example.android.adobepassclientlessrefapp.adobeauth.TypeAdapterStringToObject;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.api.MvpdListAPI;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;
import com.nbcsports.leapsdk.authentication.adobepass.config.TempPassSelectionConfig;
import com.nbcsports.leapsdk.authentication.common.AdobeAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MvpdListActivity extends FragmentActivity {

    //Bundle extras;
    public static String TAG = "MvpdListActivity";

    SharedPreferences sharedPreferences;
    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientlessService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.extras = getIntent().getExtras();
        //setContentView(R.layout.mvpd_list_layout);

        this.adobeConfig = getAdobeConfigFromJson();
        Log.d(TAG, "adobeauth to string = " + adobeConfig.toString());

        this.adobeClientlessService = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());

        printMvpdList();

    }

    private AdobeConfig getAdobeConfigFromJson() {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        return MainActivity.getAdobeConfigFromJson(sharedPreferences);
    }

    @SuppressLint("CheckResult")
    private void printMvpdList() {

        Observable<AdobeAuth> mvpdListObservable = adobeClientlessService.getMpvdList(sharedPreferences.getString("rId", ""));

        mvpdListObservable.flatMap((Function<AdobeAuth, ObservableSource<List<MvpdListAPI.Mvpd>>>)
                adobeAuth -> Observable.just(adobeAuth.getMvpds()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mvpdList -> {
                    Log.d(TAG, "MVPD LIST = " + new ArrayList<>(mvpdList));

                    showProviderDialogFrag(new ArrayList<>(mvpdList));

                });
    }

    private void showProviderDialogFrag(ArrayList mvpds) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.show(getSupportFragmentManager(), null);

    }

}
