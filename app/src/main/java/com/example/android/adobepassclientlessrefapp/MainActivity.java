package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.nbcsports.leapsdk.authentication.common.AdobeMediaInfo;
import com.nbcsports.leapsdk.authentication.common.AuthZException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * AdobePass Clientless Reference App displays the usages of methods in AdobeClientlessService
 * from the LEAP-SDK.
 * <p>
 *      NOTE: To see the LOGCAT, click on the action bar located at the top right corner of the
 *      MainActivity.
 *</p>
 * Created by: maggiedang-CB 18-09-19
 * (https://github.com/maggiedang-CB/AdobePass-Clientless-Reference-App)
 */
public class MainActivity extends AppCompatActivity {

    // shared preference key
    public static String SHARED_PREFERENCES = "myPrefs";
    public static String TAG = "MainActivity";

    @BindView(R.id.btn_adobe_config)
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
    @BindView(R.id.authorize_main_page_presenter)
    TextView tvAuthorize;

    @BindView(R.id.btn_play_media)
    Button btnPlay;

    @BindView(R.id.btn_entitlement_setup)
    Button btnSetupEntitlement;

    @BindView(R.id.main_activity_scroll_view)
    ScrollView scrollView;
    @BindView(R.id.logcat)
    LinearLayout llLogcat;
    @BindView(R.id.logcat_output)
    TextView tvLogcat;
    @BindView(R.id.empty_view_for_logcat)
    LinearLayout logcatSpace;

    SharedPreferences sharedPreferences;
    private String rId;
    private AdobeConfig adobeConfig;
    private AdobeClientlessService adobeClientlessService;
    private NetworkReceiver networkReceiver;

    public enum sharedPrefKeys {
        REQUESTOR_ID, ADOBE_CONFIG, MEDIA_INFO, LOGCAT, TOKENIZED_URL
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
        btnAdobeAuth.setOnClickListener(adobeConfigListener);
        btnIsSignedIn.setOnClickListener(isSignedInListener);
        btnLogin.setOnClickListener(loginListener);
        btnLoginTempPass.setOnClickListener(loginTempPassListener);
        btnLogout.setOnClickListener(logoutListener);
        btnGetMvpdList.setOnClickListener(getMvpdListListener);
        btnAuthorize.setOnClickListener(authorizeListener);
        btnSaveRId.setOnClickListener(saveRIdListener);
        btnMediaInfo.setOnClickListener(mediaInfoListener);
        btnPlay.setOnClickListener(playListener);
        btnSetupEntitlement.setOnClickListener(setUpEntitlementListener);

        showSavedData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
        tvAuthorize.setText(R.string.authorize_click_here);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        updateLogcat();
    }

    /**
     * Show on the UI saved data from shared preferences. Currently only REQUESTOR_ID is shown.
     */
    private void showSavedData() {
        sharedPreferences = getSharedPreferences();

        if (sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
            rId = sharedPreferences.getString(sharedPrefKeys.REQUESTOR_ID.toString(), "");
            etRId.setText(rId);
        }
    }

    // Button OnClick Listeners

    private View.OnClickListener adobeConfigListener = v -> {
        Intent intent = new Intent(MainActivity.this, AdobeConfigActivity.class);
        startActivity(intent);
    };

    private View.OnClickListener saveRIdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String rid = etRId.getText().toString();
            // Check if rId is valid. If it is, save to shared preferences
            if (isValidRId(rid)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(sharedPrefKeys.REQUESTOR_ID.toString(), rid);
                editor.apply();

                // Toast
                showToast(getString(R.string.setup_rId_true));
            }
        }
    };

    private View.OnClickListener isSignedInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String rId = getSharedPreferences().getString(sharedPrefKeys.REQUESTOR_ID.toString(), "");
            // Check if rId and adobe config has been setup.
            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_CONFIG.toString())) {
                showToast(getString(R.string.setup_adobeauth_false));
            } else if (!sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
                showToast(getString(R.string.setup_rId_false));
            } else {
                progressSpinner.setVisibility(View.VISIBLE);
                isSignedIn(rId);
            }
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_CONFIG.toString())) {
                showToast(getString(R.string.setup_adobeauth_false));
            } else if (!sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
                showToast(getString(R.string.setup_rId_false));
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener loginTempPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isWifiConnected()) {
                showToast(getString(R.string.no_internet_toast));
            } else if (isLoggedIn()) {
                // Login temp pass only if the user is not logged in
                alertDialog(getString(R.string.temppass_loggedIn_error), getString(R.string.temppass_loggedIn_error_msg));
                addToLogcat("Temporary Pass Error: User is already logged into an MVPD");
            } else {
                Intent intent = new Intent(MainActivity.this, LoginTempPassActivity.class);
                startActivity(intent);
            }
        }
    };

    @SuppressLint("CheckResult")
    private View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Use the user entered rId value from edit text to logout
            String rId = getSharedPreferences().getString(sharedPrefKeys.REQUESTOR_ID.toString(), "");

            if (!isWifiConnected()) {
                showToast(getString(R.string.no_internet_toast));
            } else if (isLoggedIn()) {
                progressSpinner.setVisibility(View.VISIBLE);
                logout(rId);
            } else if (isTempPass()) {
                // Temp pass is ON. Logout Temp Pass.
                progressSpinner.setVisibility(View.VISIBLE);
                logout(rId);
                changeTempPassLoginStatusToOff();
                Toast.makeText(MainActivity.this, getString(R.string.temppass_log_off_msg), Toast.LENGTH_LONG).show();
            } else {
                // User is already logged out!
                Log.d(TAG, "LOGOUT: USER IS ALREADY LOGGED OUT!");
                alertDialog(getString(R.string.logout_false), getString(R.string.logout_false_msg));
            }
        }
    };

    private View.OnClickListener getMvpdListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_CONFIG.toString())) {
                showToast(getString(R.string.setup_adobeauth_false));
            } else if (!isWifiConnected()) {
                showToast(getString(R.string.no_internet_toast));
            } else {
                progressSpinner.setVisibility(View.VISIBLE);
                printMvpdList();
            }

        }
    };

    private View.OnClickListener mediaInfoListener = v -> {
        Intent intent = new Intent(MainActivity.this, MediaInfoActivity.class);
        startActivity(intent);
    };

    private View.OnClickListener authorizeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!sharedPreferences.contains(sharedPrefKeys.ADOBE_CONFIG.toString())) {
                showToast(getString(R.string.setup_adobeauth_false));
            } else if (!sharedPreferences.contains(sharedPrefKeys.REQUESTOR_ID.toString())) {
                showToast(getString(R.string.setup_rId_false));
            } else if (!sharedPreferences.contains(sharedPrefKeys.MEDIA_INFO.toString())) {
                showToast(getString(R.string.setup_media_false));
            } else if (!isWifiConnected()) {
                showToast(getString(R.string.no_internet_toast));
            } else if (isTempPass()) {
                // Temp pass is active
                progressSpinner.setVisibility(View.VISIBLE);
                authorize(false);
            } else if (!sharedPreferences.contains(LoginActivity.LoginStatus.LOGIN_STATUS.toString())) {
                showToast(getString(R.string.setup_not_logged_in));
            } else {
                String status = sharedPreferences.getString(LoginActivity.LoginStatus.LOGIN_STATUS.toString(),
                        getString(R.string.login_status_not_logged));

                if (status.equals(getString(R.string.login_status_signed_in))) {
                    // Only authorize if the user is signed in
                    progressSpinner.setVisibility(View.VISIBLE);
                    authorize(false);
                } else {
                    showToast(getString(R.string.setup_not_logged_in));
                }

            }
        }
    };

    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Before playing video, internet needs to be connected and the media should be
            // successfully authorized already
            if (!isWifiConnected()) {
                showToast(getString(R.string.no_internet_toast));
            } else {
                // Authorize and launch video player
                progressSpinner.setVisibility(View.VISIBLE);
                showToast("Authorizing Video...");
                authorize(true);
                addToLogcat("ExoPlayer Launched");
            }
        }
    };

    private View.OnClickListener setUpEntitlementListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Check if adobe config, rId, and media info are set

            Intent intent = new Intent(MainActivity.this, EntitlementActivity.class);
            startActivity(intent);
        }
    };

    /**
     * Authorize is a success if the user is logged in (By MVPD or by Temp Pass) and has compatible
     * Requestor Ids between the user's saved Requestor Id and Media Info's Requestor Id.
     * @param playVideo Pass in True to play tokenizedUrl on ExoPlayer right after auth success
     */
    @SuppressLint("CheckResult")
    private void authorize(boolean playVideo) {
        adobeConfig = getAdobeConfigFromJson(getSharedPreferences());
        AdobeClientlessService adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
        AdobeMediaInfo adobeMediaInfo = getMediaInfoFromJson(getSharedPreferences());

        AdobeAuth adobeAuth = new AdobeAuth();

        adobeClientless.authorize(adobeAuth, adobeMediaInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(auth -> {
                    Log.d(TAG, "AUTHORIZE SUCCESS");
                    tvAuthorize.setText(getString(R.string.authorize_success));

                    // Get and save tokenized url
                    String tokenizedUrl = auth.getNbcToken().getTokenizedUrl();
                    saveTokenizedUrl(tokenizedUrl);
                    Log.d(TAG, "Auth Success: tokenizedUrl = " + tokenizedUrl);

                    // Play video
                    if (playVideo) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        startActivity(intent);
                    }

                    addToLogcat("AUTHORIZE SUCCESS");
                    progressSpinner.setVisibility(View.GONE);

                }, throwable -> {
                    Log.e(TAG, "AUTHORIZE FAILURE");
                    if (throwable instanceof AuthZException) {
                        // AuthZ Error
                        alertDialog(getString(R.string.authorize_error), getString(R.string.authorize_error_message));
                    } else {
                        // Unknown Error
                        String message = getString(R.string.player_restricted_error) + "\n\n" + throwable.toString();
                        alertDialog(getString(R.string.player_restricted_title), message);
                    }
                    tvAuthorize.setText(getString(R.string.authorize_failure));
                    addToLogcat("AUTHORIZE FAILURE: " + throwable.toString());
                    progressSpinner.setVisibility(View.GONE);
                });
    }

    /**
     * Saves the tokenized Url to shared preferences after a successful authorization.
     * The Url will be used in ExoPlayer to play the video that was set up in media info.
     * @param tokenizedUrl
     */
    private void saveTokenizedUrl(String tokenizedUrl) {
        sharedPreferences = getSharedPreferences();
        String tokenKey = sharedPrefKeys.TOKENIZED_URL.toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenKey, tokenizedUrl);
        editor.apply();

        showToast(getString(R.string.tokenized_saved));
        addToLogcat("Tokenized Url = " + tokenizedUrl);
    }

    /**
     * Uses the isSignedIn method from AdobeClientlessService to determine if the user is already
     * logged in. In addition to the observable calling success if logged in, success is also
     * called if Temp Pass is ON.
     * @param rId
     */
    @SuppressLint("CheckResult")
    private void isSignedIn(String rId) {
        adobeConfig = getAdobeConfigFromJson(getSharedPreferences());
        AdobeClientlessService adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
        adobeClientless.isSignedIn(rId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        adobeAuth -> {
                            // User is signed in so we can get its user or mvpd info.
                            if (adobeAuth.isCheckAuthNSuccess()) {
                                alertDialog(getString(R.string.isSignedIn_true), getString(R.string.isSignedIn_true_msg));
                                Log.d(TAG, "ISSIGNEDIN SUCCESS: MVPD Display = " + adobeAuth.getMvpdDisplayName());

                                addToLogcat("isSignedIn = True. User is Signed in.");
                                addToLogcat("Current MVPD: " + adobeAuth.getMvpdDisplayName());
                            } else {
                                // User is not signed in.
                                alertDialog(getString(R.string.isSignedIn_false), getString(R.string.isSignedIn_false_msg));
                                addToLogcat("isSignedIn = False. User is not Signed in.");
                            }

                            progressSpinner.setVisibility(View.GONE);
                        },
                        throwable -> {
                            // Error: User is not signed in.
                            alertDialog(getString(R.string.isSignedIn_false), getString(R.string.isSignedIn_false_msg));
                            addToLogcat("isSignedIn = False. " + throwable.toString());
                            progressSpinner.setVisibility(View.GONE);
                        });
    }

    /**
     * User can only logout if they are signed in OR if temp pass is ON.
     * @param rId
     */
    @SuppressLint("CheckResult")
    public void logout(String rId) {
        adobeConfig = getAdobeConfigFromJson(getSharedPreferences());
        AdobeClientlessService adobeClientless = new AdobeClientlessService(MainActivity.this,
                adobeConfig, DeviceUtils.getDeviceInfo());

        adobeClientless.logout(rId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        adobeAuth -> {
                            // User is logged out.
                            Log.d(TAG, "LOGOUT: Logged out");
                            alertDialog(getString(R.string.logout_true), getString(R.string.logout_true_msg));
                            // Change login status
                            changeLoginStatusToLogout();

                            addToLogcat("LOGOUT SUCCESS");
                            progressSpinner.setVisibility(View.GONE);
                        },
                        throwable -> {
                            // Error when logging out.
                            Log.d(TAG, "LOGOUT: ERROR");
                            String logoutMessage = getString(R.string.logout_fail_msg) + throwable.toString();
                            alertDialog(getString(R.string.logout_false), logoutMessage);

                            addToLogcat("LOGOUT ERROR: " + throwable.toString());
                            progressSpinner.setVisibility(View.GONE);
                        });
    }

    /**
     * Sets up and displays an alert dialog in the main activity
     * @param title
     * @param message
     */
    protected void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Back to main activity
        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setLayout(900, 700);
    }

    /**
     * Checks if the rId is not null and empty. If it is, show toast message.
     * Example of rId: nbcsports
     * @param rId
     * @return true if rId is not null or empty
     */
    private boolean isValidRId(String rId) {
        if (rId == null || rId.isEmpty()) {
            showToast(getString(R.string.setup_rId_invalid));
            return false;
        }
        return true;
    }

    // Adobe Config

    /**
     * After the adobe config is set up and the json is saved in shared prefs, we can use it to create
     * an adobe config object by converting it from a json string to adobeconfig object.
     * @param sharedPreferences
     * @return
     */
    public static AdobeConfig getAdobeConfigFromJson(SharedPreferences sharedPreferences) {
        String jsonString = sharedPreferences.getString(sharedPrefKeys.ADOBE_CONFIG.toString(), "");

        Log.d(TAG, "Adobe Config jsonString = " + jsonString);

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

    /**
     * Return the AdobeMediaInfo Object by converting its stored json string data from shared
     * preference into AdobeMediaInfo Object.
     * @param sharedPreferences
     * @return
     */
    public static AdobeMediaInfo getMediaInfoFromJson(SharedPreferences sharedPreferences) {
        String jsonString = sharedPreferences.getString(sharedPrefKeys.MEDIA_INFO.toString(), "");

        Log.d(TAG, "Media Info jsonString = " + jsonString);

        if (jsonString == null || jsonString.equals("")) {
            Log.e(TAG, "Error when getting adobe media info from json");
            return null;
        }

        // adobe config object
        Gson gson = new GsonBuilder().create();

        AdobeMediaInfo mediaInfo = gson.fromJson(jsonString, new TypeToken<AdobeMediaInfo>(){}.getType());

        return mediaInfo;
    }

    /**
     * Displays a list of all the available MVPDs (Providers) the user can log into. Clicking on
     * a MVPD on this list will not do anything. This here is just to display the list.
     * The interactive MVPD list is found in LoginActivity.
     */
    @SuppressLint("CheckResult")
    private void printMvpdList() {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

        adobeConfig = getAdobeConfigFromJson(sharedPreferences);

        Log.d(TAG, "adobeauth to string = " + adobeConfig.toString());

        adobeClientlessService = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
        String rId = sharedPreferences.getString(sharedPrefKeys.REQUESTOR_ID.toString(), "");
        Observable<AdobeAuth> mvpdListObservable = adobeClientlessService.getMpvdList(rId);

        mvpdListObservable.flatMap((Function<AdobeAuth, ObservableSource<List<MvpdListAPI.Mvpd>>>)
                adobeAuth -> Observable.just(adobeAuth.getMvpds()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> Log.d(TAG, "getMvpdList onError: " + throwable))
                .subscribe(mvpdList -> {
                    Log.d(TAG, "MVPD LIST = " + new ArrayList<>(mvpdList));
                    // Show MVPD list
                    showProviderDialogFrag(new ArrayList<>(mvpdList));
                    progressSpinner.setVisibility(View.GONE);
                }, throwable -> {
                    // The Error most likely came from invalid adobe config data
                    Log.d(TAG, "getMvpdList subscribe Error: " + throwable.toString());
                    String errorMessage = getString(R.string.mvpdlist_error_msg) + "\n\n" + throwable.toString();
                    alertDialog("getMvpdList Error", errorMessage);
                    addToLogcat(throwable.toString());
                    progressSpinner.setVisibility(View.GONE);
                });

    }

    private void showProviderDialogFrag(ArrayList mvpds) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), null);

        // TODO: Figure out how to back press on hardware to dismiss frag dialog (instead of exiting main activity)

    }

    /**
     * Edit shared preferences so that the value that saves the login status is changed back to not logged in
     */
    private void changeLoginStatusToLogout() {
        sharedPreferences = getSharedPreferences();
        String statusKey = LoginActivity.LoginStatus.LOGIN_STATUS.toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(statusKey, getString(R.string.login_status_not_logged));
        editor.apply();
    }

    /**
     * Shared preferences is edited with the status that temp pass has been turned off
     */
    private void changeTempPassLoginStatusToOff() {
        String key = LoginTempPassActivity.LoginStatus.TEMPPASS_ID.toString();
        String offStatus = getString(R.string.temppass_id_not_loggedIn);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, offStatus);
        editor.apply();
    }

    /**
     * Returns true if the user is logged in.
     * @return
     */
    private boolean isLoggedIn() {
        sharedPreferences = getSharedPreferences();
        String statusKey = LoginActivity.LoginStatus.LOGIN_STATUS.toString();
        String loginStatus = getString(R.string.login_status_signed_in);
        String currentStatus = sharedPreferences.getString(statusKey, loginStatus);

        if (sharedPreferences.contains(statusKey)) {
            if (currentStatus.equals(loginStatus)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there is internet connection.
     * @return
     */
    private boolean isWifiConnected() {
        return NetworkUtils.isWifiConnected(this);
    }

    /**
     * Returns true if the user is on Temporary pass.
     * @return
     */
    private boolean isTempPass() {
        String offStatus = getString(R.string.temppass_id_not_loggedIn);
        return LoginActivity.isTempPass(getSharedPreferences(), offStatus);
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
            // Currently disabled
            startActivity(new Intent(MainActivity.this, AboutClientlessActivity.class));
            return true;
        } else if (id == R.id.action_show_logcat) {
            showAndHideLogcat(item);
            return true;
        } else if (id == R.id.action_clear_logcat) {
            clearLogCat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The visual Logcat displayed on the App can be Shown/Hidden by the user using the action bar
     * found on the top right corner of the MainActivity.
     * @param item
     */
    private void showAndHideLogcat(MenuItem item) {
        if (llLogcat.getVisibility() == View.GONE) {
            // Update logcat with new logs
            updateLogcat();
            // Show logcat
            llLogcat.setVisibility(View.VISIBLE);
            item.setTitle(getString(R.string.hide_logcat));
            // Move the main activity buttons higher up for the user to see
            logcatSpace.setVisibility(View.VISIBLE);
            // Scroll all the way down
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        } else {
            // Hide logcat
            llLogcat.setVisibility(View.GONE);
            item.setTitle(getString(R.string.show_logcat));
            // Remove the empty space created for the logcat
            logcatSpace.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the logcat view. The output of logs is in order of new logs placed above older logs.
     *
     * I.e) "new Log Message" + "\n" + old log messages
     *
     */
    private void updateLogcat() {
        sharedPreferences = getSharedPreferences();
        String logcatKey = sharedPrefKeys.LOGCAT.toString();
        String updatedLogcat = sharedPreferences.getString(logcatKey, "");
        tvLogcat.setText(updatedLogcat);
    }

    /**
     * Adds onto the logcat string in shared preferences.
     * @param sharedPreferences
     * @param logKey
     * @param logMessage new log message to output
     */
    public static void addToLogcat(SharedPreferences sharedPreferences, String logKey, String TAG, String logMessage) {
        String oldLogMessages = sharedPreferences.getString(logKey, "");
        String addedTagToNewLog = TAG + ": " + logMessage;
        // Add a new line each time a new log message is added for readability
        String newLogMessage = addedTagToNewLog + "\n" + oldLogMessages;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(logKey, newLogMessage);
        editor.apply();
    }

    private void addToLogcat(String logMessage) {
        sharedPreferences = getSharedPreferences();
        String logcatKey = sharedPrefKeys.LOGCAT.toString();
        addToLogcat(sharedPreferences, logcatKey, TAG, logMessage);
        // update logcat immediately after adding changes to it
        updateLogcat();
    }

    /**
     * Resets logcat view data and update it in its shared preferences.
     */
    private void clearLogCat() {
        // Clear on UI
        tvLogcat.setText("");
        // Update in shared preferences
        sharedPreferences = getSharedPreferences();
        String logcatKey = sharedPrefKeys.LOGCAT.toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(logcatKey, "");
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        addToLogcat(message);
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
