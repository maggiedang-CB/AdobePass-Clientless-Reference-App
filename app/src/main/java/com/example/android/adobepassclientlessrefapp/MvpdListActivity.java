package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.android.adobepassclientlessrefapp.fragments.ProviderDialogFragment;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.api.MvpdListAPI;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;
import com.nbcsports.leapsdk.authentication.common.AdobeAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

//TODO: Delect class if not used
public class MvpdListActivity extends FragmentActivity {

    //Bundle extras;
    public static String TAG = "MvpdListActivity";

    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientlessService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.extras = getIntent().getExtras();
        //setContentView(R.layout.mvpd_list_layout);

        this.adobeConfig = MainActivity.getAdobeConfigFromJson(getSharedPreferences());
        Log.d(TAG, "adobeauth to string = " + adobeConfig.toString());

        this.adobeClientlessService = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());

        printMvpdList(getRId(), getSupportFragmentManager(), adobeClientlessService);

    }

    /**
     * Returns requestor Id saved from shared prefs
     * @return
     */
    private String getRId() {
        return getSharedPreferences().getString("rId", "");
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    /**
     * Static method used in other activities to displace the mvpd provider dialog list.
     * @param rId
     * @param fragmentManager
     * @param adobeClientlessService
     */
    @SuppressLint("CheckResult")
    public static void printMvpdList(String rId, FragmentManager fragmentManager, AdobeClientlessService adobeClientlessService) {

        Observable<AdobeAuth> mvpdListObservable = adobeClientlessService.getMpvdList(rId);

        mvpdListObservable.flatMap((Function<AdobeAuth, ObservableSource<List<MvpdListAPI.Mvpd>>>)
                adobeAuth -> Observable.just(adobeAuth.getMvpds()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mvpdList -> {
                    Log.d(TAG, "MVPD LIST = " + new ArrayList<>(mvpdList));

                    showProviderDialogFrag(new ArrayList<>(mvpdList), fragmentManager);

                });
    }

    public static void showProviderDialogFrag(ArrayList mvpds, FragmentManager fragmentManager) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.show(fragmentManager, null);
    }

}
