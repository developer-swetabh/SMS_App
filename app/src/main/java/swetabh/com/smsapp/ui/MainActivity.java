package swetabh.com.smsapp.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import swetabh.com.smsapp.R;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends BaseActivity {

    private final int REQUEST_READ_SMS = 707;
    private CoordinatorLayout mCoordinatorLayout;
    private List<String> mSmsList;
    private static FragmentManager mFragmentManager;
    private static Stack<Fragment> mFragmentStack = null;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        initToolbar();
        initUi();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if (mFragmentManager.findFragmentById(R.id.container_frame) instanceof ListParentSmsFragment) {
                    ((ListParentSmsFragment) mFragmentManager.findFragmentById(R.id.container_frame))
                            .sendSms();
                } else {
                    ((ListChildSmsFragment) mFragmentManager.findFragmentById(R.id.container_frame))
                            .sendSms();
                }
            }
        });

        mayRequestSMS();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initUi() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private boolean mayRequestSMS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            replaceFragment(new ListParentSmsFragment());
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_SMS)) {
            Snackbar.make(mCoordinatorLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_CONTACTS}, REQUEST_READ_SMS);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_SMS}, REQUEST_READ_SMS);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // Read SMS
                    replaceFragment(new ListParentSmsFragment());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    MainActivity.this.finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Method of replacing fragment
     *
     * @param fragment - takes Fragment as argument
     */
    public void replaceFragment(Fragment fragment) {
        if (mFragmentStack != null) {
            Log.e("Size", mFragmentStack.size() + "");
        }
        mFragmentStack = new Stack<>();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container_frame, fragment);
        mFragmentStack.push(fragment);
        transaction.commitAllowingStateLoss();

    }

    /**
     * Method of adding fragment to the stack
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.container_frame, fragment);
        mFragmentStack.lastElement().onPause();
        transaction.hide(mFragmentStack.lastElement());
        mFragmentStack.push(fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        onBackpressed();
    }

    /*
    * Method will be called when the back button is pressed
    * It will start poping out the fragment from the stack
    * */
    public void onBackpressed() {
        if (mFragmentStack != null && mFragmentStack.size() >= 2) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragmentStack.lastElement().onPause();
            ft.remove(mFragmentStack.pop());
            mFragmentStack.lastElement().onResume();
            ft.show(mFragmentStack.lastElement());
            ft.commit();
        } else {
            super.onBackPressed();
            return;
        }

    }
}
