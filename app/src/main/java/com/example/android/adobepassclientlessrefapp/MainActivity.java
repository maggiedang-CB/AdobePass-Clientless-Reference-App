package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.ui.AboutClientlessActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // shared preference key
    public static String SHARED_PREFERENCES = "myPrefs";

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

    @BindView(R.id.btn_authorize)
    Button btnAuthorize;

    SharedPreferences sharedPreferences;
    private String rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

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

        showSavedData();
    }

    /**
     * Show saved data from shared preferences such as: requestorId.
     */
    private void showSavedData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.contains("rId")) {
            rId = sharedPreferences.getString("rId", "");
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
            String rid = etRId.getText().toString();
            // Check if rId is valid. If it is, save to shared preferences
            if(isValidRId(rid)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("rId", rid);
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
            // init rId
            intent.putExtra("requestorId", "");
            startActivity(intent);
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // init default empty values
            intent.putExtra("mvpd", "");
            intent.putExtra("requestorId", "");
            startActivity(intent);
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

            //TODO: Check if adobe auth has been set up

            //TODO: Check if Requestor Id has been saved

            Intent intent = new Intent(MainActivity.this, MvpdListActivity.class);
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
