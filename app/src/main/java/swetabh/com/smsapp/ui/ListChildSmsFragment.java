package swetabh.com.smsapp.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import swetabh.com.smsapp.R;
import swetabh.com.smsapp.adapter.SmsAdapter;
import swetabh.com.smsapp.constants.AppConstant;
import swetabh.com.smsapp.models.SMSModel;
import swetabh.com.smsapp.util.Utils;

/**
 * Created by abhi on 14/02/17.
 */

public class ListChildSmsFragment extends Fragment {

    private Context mContext;
    private List<SMSModel> mSmsList;
    private RecyclerView mSmsParentRecyclerView;
    private String mAddress = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
            mAddress = getArguments().getString(AppConstant.ADDRESS);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_sms, container, false);
        initView(view);
        getAllParticularAddressSMS();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((MainActivity) mContext).setToolbarTitle(mAddress);
    }



    private void getAllParticularAddressSMS() {
        class GetInboxSMS extends AsyncTask<Void, Void, List<SMSModel>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((MainActivity) mContext).showProgress();
            }

            @Override
            protected List<SMSModel> doInBackground(Void... voids) {

                return Utils.getParticularAddressSms(mAddress, mContext);
            }

            @Override
            protected void onPostExecute(List<SMSModel> smsModels) {
                super.onPostExecute(smsModels);
                ((MainActivity) mContext).hideProgress();
                SmsAdapter adapter = new SmsAdapter(smsModels);
                mSmsParentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mSmsParentRecyclerView.setAdapter(adapter);
            }
        }
        new GetInboxSMS().execute();
    }

    private void initView(View view) {
        mSmsParentRecyclerView = (RecyclerView) view.findViewById(R.id.sms_recyclerView);
    }

    public void sendSms() {
        SendSMSFragment dialog = new SendSMSFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.ADDRESS, mAddress);
        dialog.setArguments(bundle);
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
}
