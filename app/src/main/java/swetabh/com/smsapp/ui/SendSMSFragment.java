package swetabh.com.smsapp.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import swetabh.com.smsapp.R;
import swetabh.com.smsapp.constants.AppConstant;
import swetabh.com.smsapp.util.Utils;

/**
 * Created by abhi on 14/02/17.
 */

public class SendSMSFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_send_sms, container, false);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_send_sms, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.send_sms);
        builder.setCancelable(false);
        final EditText editPhoneNo = (EditText) dialogView.findViewById(R.id.edit_phone_no);
        final EditText editBody = (EditText) dialogView.findViewById(R.id.edit_message);

        if (getArguments() != null) {
            editPhoneNo.setText(getArguments().getString(AppConstant.ADDRESS));
        }

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("", "Phone No :" + editPhoneNo.getText().toString().trim() + " : body :" + editBody.getText().toString().trim());
                if (TextUtils.isEmpty(editPhoneNo.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Phone No Should not be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editBody.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Message Should not be empty", Toast.LENGTH_SHORT).show();
                } else if (!Utils.validatePhone(editPhoneNo.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    sendSMS(editPhoneNo.getText().toString().trim(), editBody.getText().toString().trim());
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getActivity(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

   /* if (TextUtils.isEmpty(editPhoneNo.getText().toString().trim())) {
        Toast.makeText(getActivity(), "Phone No Should not be empty", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(editBody.getText().toString().trim())) {
        Toast.makeText(getActivity(), "Message Should not be empty", Toast.LENGTH_SHORT).show();
    } else if (!Utils.validatePhone(editPhoneNo.getText().toString().trim())) {
        Toast.makeText(getActivity(), "Enter a valid phone number.", Toast.LENGTH_SHORT).show();
    } else {
        sendSMS(editPhoneNo.getText().toString().trim(), editBody.getText().toString().trim());
    }*/
}
