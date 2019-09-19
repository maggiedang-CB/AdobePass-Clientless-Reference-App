package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
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

    @BindView(R.id.btn_isSignedIn)
    Button btnIsSignedIn;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_login_temp_pass)
    Button btnLoginTempPass;

    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.logout_rId)
    EditText etLogoutRId;

    @BindView(R.id.btn_getmvpdlist)
    Button btnGetMvpdList;
    @BindView(R.id.getMvpdList_rId)
    EditText etGetMvpdListRId;

    @BindView(R.id.btn_authorize)
    Button btnAuthorize;

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
        btnIsSignedIn.setOnClickListener(isSignedInListener);
        btnLogin.setOnClickListener(loginListener);
        btnLoginTempPass.setOnClickListener(loginTempPassListener);
        btnLogout.setOnClickListener(logoutListener);
        btnGetMvpdList.setOnClickListener(getMvpdListListener);
        btnAuthorize.setOnClickListener(authorizeListener);
    }


    // Button OnClick Listeners

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
            String rId = etLogoutRId.getText().toString();

            if (isValidRId(rId)) {
                // do stuff here to logout
            }

//            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
//            intent.putExtra("requestorId", rId);
//            startActivity(intent);
        }
    };

    private View.OnClickListener getMvpdListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Use the user entered rId value from edit text to logout
            String rId = etGetMvpdListRId.getText().toString();

            if (isValidRId(rId)) {
                // do stuff here to logout
            }

//            Intent intent = new Intent(MainActivity.this, MvpdListActivity.class);
//            intent.putExtra("requestorId", rId);
//            startActivity(intent);
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
        if (rId == null || rId.isEmpty()) {
            Toast toast = Toast.makeText(this, "Please Enter a Requestor Id.", Toast.LENGTH_SHORT);
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
