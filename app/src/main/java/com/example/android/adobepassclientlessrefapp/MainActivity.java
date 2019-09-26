package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.adobeauth.TypeAdapterStringToList;
import com.example.android.adobepassclientlessrefapp.adobeauth.TypeAdapterStringToObject;
import com.example.android.adobepassclientlessrefapp.fragments.ProviderDialogFragment;
import com.example.android.adobepassclientlessrefapp.ui.AboutClientlessActivity;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.example.android.adobepassclientlessrefapp.utils.NetworkReceiver;
import com.example.android.adobepassclientlessrefapp.utils.NetworkUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // shared preference key
    public static String SHARED_PREFERENCES = "myPrefs";
    public static String TAG = "MainActivity";

    @BindView(R.id.btn_adobe_auth)
    Button btnAdobeAuth;

    @BindView(R.id.requestorId)
    EditText etRId;
    @BindView(R.id.btn_rId)
    Button btnSaveRId;

    @BindView(R.id.btn_isSignedIn)
    Button btnIsSignedIn;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_login_temp_pass)
    Button btnLoginTempPass;

    @BindView(R.id.btn_logout)
    Button btnLogout;

    @BindView(R.id.btn_getmvpdlist)
    Button btnGetMvpdList;
    @BindView(R.id.progressSpinner)
    ProgressBar progressSpinner;

    @BindView(R.id.btn_media_info)
    Button btnMediaInfo;

    @BindView(R.id.btn_authorize)
    Button btnAuthorize;

    SharedPreferences sharedPreferences;
    private String rId;
    private AdobeConfig adobeConfig;
    private AdobeClientlessService adobeClientlessService;
    private NetworkReceiver networkReceiver;

    public enum sharedPrefKeys {
        REQUESTOR_ID, ADOBE_AUTH, MEDIA_INFO
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set button listeners
        btnAdobeAuth.setOnClickListener(adobeAuthListener);
        btnIsSignedIn.setOnClickListener(isSignedInListener);
        btnLogin.setOnClickListener(loginListener);
        btnLoginTempPass.setOnClickListener(loginTempPassListener);
        btnLogout.setOnClickListener(logoutListener);
        btnGetMvpdList.setOnClickListener(getMvpdListListener);
        btnAuthorize.setOnClickListener(authorizeListener);
        btnSaveRId.setOnClickListener(saveRIdListener);
        btnMediaInfo.setOnClickListener(mediaInfoListener);

        showSavedData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Show saved data from shared preferences such as: requestorId.
     */
    private void showSavedData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
            rId = sharedPreferences.getString(sharedPrefKeys.REQUESTOR_ID.toString(), "");
            etRId.setText(rId);
        }
    }

    // Button OnClick Listeners

    private View.OnClickListener adobeAuthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdobeAuthActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener saveRIdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Change colour of Save RId button to red if unsaved
            String rid = etRId.getText().toString();
            // Check if rId is valid. If it is, save to shared preferences
            if(isValidRId(rid)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(sharedPrefKeys.REQUESTOR_ID.toString(), rid);
                editor.apply();

                // Toast
                Toast.makeText(MainActivity.this, "Requestor Id Saved", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener isSignedInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, IsSignedInActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_AUTH.toString())) {
                Toast.makeText(MainActivity.this, "Adobe Auth has not been set up", Toast.LENGTH_SHORT).show();
            } else if (!sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
                Toast.makeText(MainActivity.this, "Requestor Id Has not been saved", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // init default empty values
                intent.putExtra("mvpd", "");
                intent.putExtra("requestorId", "");
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener loginTempPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LoginTempPassActivity.class);
            intent.putExtra("requestorId", "");
            intent.putExtra("tempPassId", "");
            startActivity(intent);
        }
    };

    private View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Use the user entered rId value from edit text to logout


//            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
//            intent.putExtra("requestorId", rId);
//            startActivity(intent);
        }
    };

    private View.OnClickListener getMvpdListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //TODO: Show a spinner for loading progress
            //progressSpinner.setVisibility(View.VISIBLE);

            sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_AUTH.toString())) {
                Toast.makeText(MainActivity.this, "Adobe Auth has not been set up", Toast.LENGTH_SHORT).show();
            } else if (!isWifiConnected()) {
                Toast.makeText(MainActivity.this, getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
            } else {
                printMvpdList();
            }

        }
    };

    private View.OnClickListener mediaInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MediaInfoActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener authorizeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AuthorizeActivity.class);
            startActivity(intent);
        }
    };


    /**
     * Checks if the rId is not null and empty. If it is, show toast message.
     * Example of rId: nbcsports
     * @param rId
     * @return true if rId is not null or empty
     */
    private boolean isValidRId(String rId) {

        //TODO: Instead of typing a value, make user radio dial select out of a list of current rIds (If possible)

        if (rId == null || rId.isEmpty()) {
            Toast toast = Toast.makeText(this, "Invalid Requestor Id", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    // Adobe Config

    /**
     * After the adobe auth is set up and the json is saved in shared prefs, we can use it to create
     * an adobe config object by converting it from a json string to adobeconfig object.
     * @param sharedPreferences
     * @return
     */
    public static AdobeConfig getAdobeConfigFromJson(SharedPreferences sharedPreferences) {
        String jsonString = sharedPreferences.getString(sharedPrefKeys.ADOBE_AUTH.toString(), "");

        Log.d(TAG, "jsonString = " + jsonString);

        if (jsonString == null || jsonString.equals("")) {
            Log.e(TAG, "Error when getting adobe config from json");
            return null;
        }

        // adobe config object
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<String>>(){}.getType(), new TypeAdapterStringToList())
                .registerTypeAdapter(new TypeToken<TempPassSelectionConfig>(){}.getType(), new TypeAdapterStringToObject())
                .create();

        AdobeConfig adobeConfig = gson.fromJson(jsonString, new TypeToken<AdobeConfig>(){}.getType());

        return adobeConfig;
    }

    @SuppressLint("CheckResult")
    private void printMvpdList() {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

        adobeConfig = getAdobeConfigFromJson(sharedPreferences);

        Log.d(TAG, "adobeauth to string = " + adobeConfig.toString());

        adobeClientlessService = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());

        Observable<AdobeAuth> mvpdListObservable = adobeClientlessService.getMpvdList(sharedPreferences.getString(sharedPrefKeys.REQUESTOR_ID.toString(), ""));

        mvpdListObservable.flatMap((Function<AdobeAuth, ObservableSource<List<MvpdListAPI.Mvpd>>>)
                adobeAuth -> Observable.just(adobeAuth.getMvpds()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mvpdList -> {
                    Log.d(TAG, "MVPD LIST = " + new ArrayList<>(mvpdList));
                    showProviderDialogFrag(new ArrayList<>(mvpdList));
                    //progressSpinner.setVisibility(View.GONE);
                });

    }

    private void showProviderDialogFrag(ArrayList mvpds) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), null);

        // TODO: Figure out how to back press on hardware to dismiss frag dialog (instead of exiting main activity)
//        if (fragment.getView() != null) {
//            fragment.getView().setFocusableInTouchMode(true);
//            fragment.getView().requestFocus();
//            fragment.getView().setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
//                        Log.d(TAG, ">>>back pressed!!!");
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .addToBackStack(null)
//                .add(fragment, null)
//                .commit();
        //fragment.show(fragmentManager, null);

    }

    /**
     * Returns true if there is internet connection.
     * @return
     */
    private boolean isWifiConnected() {
        return NetworkUtils.isWifiConnected(this);
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
        if (id == R.id.action_adobe_clientless) {
            startActivity(new Intent(MainActivity.this, AboutClientlessActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
