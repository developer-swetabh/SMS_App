package swetabh.com.smsapp.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import swetabh.com.smsapp.R;
import swetabh.com.smsapp.adapter.SmsAdapter;
import swetabh.com.smsapp.constants.AppConstant;
import swetabh.com.smsapp.models.SMSModel;
import swetabh.com.smsapp.util.Utils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by abhi on 14/02/17.
 */

public class ListParentSmsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {
    private Context mContext;
    private List<SMSModel> mSmsList;
    private RecyclerView mSmsParentRecyclerView;
    private TextView mNoMessage;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private GoogleApiClient mGoogleApiClient;
    public DriveFile file;
    private SmsAdapter adapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_sms, container, false);
        initView(view);
        getAllInboxSMS();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((MainActivity) mContext).setToolbarTitle(getString(R.string.app_name));

        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void initView(View view) {
        mSmsParentRecyclerView = (RecyclerView) view.findViewById(R.id.sms_recyclerView);
        mNoMessage = (TextView) view.findViewById(R.id.textView_no_items);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_backup:
                //Utils.backupAllSms(mContext, mSmsList);
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(driveContentsCallback);
                break;
        }
        return true;
    }

    private void getAllInboxSMS() {
        class GetInboxSMS extends AsyncTask<Void, Void, List<SMSModel>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((MainActivity) mContext).showProgress();
            }

            @Override
            protected List<SMSModel> doInBackground(Void... voids) {

                return Utils.getInboxSms(mContext);
            }

            @Override
            protected void onPostExecute(List<SMSModel> smsModels) {
                super.onPostExecute(smsModels);
                ((MainActivity) mContext).hideProgress();
                mSmsList = smsModels;
                adapter = new SmsAdapter(smsModels, ListParentSmsFragment.this);
                mSmsParentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mSmsParentRecyclerView.setAdapter(adapter);
            }
        }
        new GetInboxSMS().execute();
    }

    public void onClickSms(String strAddress) {

        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
        }

        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.ADDRESS, strAddress);
        ListChildSmsFragment fragment = new ListChildSmsFragment();
        fragment.setArguments(bundle);
        ((MainActivity) mContext).addFragment(fragment);
    }

    public void sendSms() {
        SendSMSFragment dialog = new SendSMSFragment();
        showAddListDialog(dialog);
    }

    private void showAddListDialog(SendSMSFragment sendSMSFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment prevFragment = fragmentManager.findFragmentByTag("dialog");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);
        sendSMSFragment.show(fragmentManager, "dialog");
    }


    /**
     * This is Result result handler of Drive contents.
     * this callback method call CreateFileOnGoogleDrive() method
     * and also call OpenFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
     */
    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {

                    if (result.getStatus().isSuccess()) {
                        CreateFileOnGoogleDrive(result);
                    }


                }
            };


    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result) {


        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try {
                    Gson gson = new Gson();
                    String smsList = gson.toJson(Utils.sAllSMSList);
                    writer.write(smsList);
                    writer.close();
                } catch (IOException e) {
                    Log.e("", e.getMessage());
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("All SMS Backup")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {

                        Toast.makeText(mContext, "Successfully created backup of all your sms ", Toast.LENGTH_LONG).show();

                    }

                    return;

                }
            };

    @Override
    public void onConnectionFailed(ConnectionResult result) {


        // Called whenever the API client fails to connect.
        Log.i("", "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e("", "Exception while starting resolution activity", e);
        }
    }

    /**
     * It invoked when Google API client connected
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i("GoogleApi Client", "GoogleApiClient connected");
    }

    /**
     * It invoked when connection suspend
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {

        Log.i("", "GoogleApiClient connection suspended");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

}
