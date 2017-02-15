package swetabh.com.smsapp.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import swetabh.com.smsapp.models.SMSModel;

/**
 * Created by abhi on 14/02/17.
 */

public class Utils {

    public static List<SMSModel> sAllSMSList = new ArrayList<>();

    public static List<SMSModel> getParticularAddressSms(String addres, Context context) {
        final String SMS_URI_INBOX = "content://sms/inbox";
        List<SMSModel> smsModelList = new ArrayList<>();
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = context.getContentResolver().query(uri, projection, "address='" + addres + "'", null, "date desc");
            int index_Address = cur.getColumnIndex("address");
            int index_Person = cur.getColumnIndex("person");
            int index_Body = cur.getColumnIndex("body");
            int index_Date = cur.getColumnIndex("date");
            int index_Type = cur.getColumnIndex("type");
            if (cur.moveToFirst()) {
                do {
                    addres = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);
                    smsModelList.add(new SMSModel(addres, intPerson, strbody, longDate, int_Type));
                } while (cur.moveToNext());
            } else {
                //No Data found
            }
            Log.e("test", smsModelList.toString());
            if (!cur.isClosed()) {
                cur.close();
                cur = null;
            }

            // end if
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        return smsModelList;
    }

    public static List<SMSModel> getInboxSms(Context mContext) {
        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_INBOX = "content://sms/inbox";
        List<SMSModel> smsModelList = new ArrayList<>();
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur1 = mContext.getContentResolver().query(uri, projection, null, null, "date desc");
            String previousAddress = "";
            if (cur1.moveToFirst())
                do {
                    String strAddress = cur1.getString(cur1.getColumnIndex("address"));
                    if (!previousAddress.equals(strAddress)) {
                        Cursor cur = mContext.getContentResolver().query(uri, projection, "address='" + strAddress + "'", null, "date desc");
                        int index_Address = cur1.getColumnIndex("address");
                        int index_Person = cur1.getColumnIndex("person");
                        int index_Body = cur1.getColumnIndex("body");
                        int index_Date = cur1.getColumnIndex("date");
                        int index_Type = cur1.getColumnIndex("type");

                        //  if (cur.moveToFirst())
                        //  do {
                        strAddress = cur1.getString(index_Address);
                        int intPerson = cur1.getInt(index_Person);
                        String strbody = cur1.getString(index_Body);
                        long longDate = cur1.getLong(index_Date);
                        int int_Type = cur1.getInt(index_Type);
                        smsModelList.add(new SMSModel(strAddress, intPerson, strbody, longDate, int_Type, cur.getCount()));
                        //    } while (cur.moveToNext());
                        sAllSMSList.add(new SMSModel(strAddress, intPerson, strbody, longDate, int_Type));


                    } else {
                        smsBuilder.append("no result!");
                        int index_Address = cur1.getColumnIndex("address");
                        int index_Person = cur1.getColumnIndex("person");
                        int index_Body = cur1.getColumnIndex("body");
                        int index_Date = cur1.getColumnIndex("date");
                        int index_Type = cur1.getColumnIndex("type");

                        //  if (cur.moveToFirst())
                        //  do {
                        strAddress = cur1.getString(index_Address);
                        int intPerson = cur1.getInt(index_Person);
                        String strbody = cur1.getString(index_Body);
                        long longDate = cur1.getLong(index_Date);
                        int int_Type = cur1.getInt(index_Type);
                        sAllSMSList.add(new SMSModel(strAddress, intPerson, strbody, longDate, int_Type));
                    }
                    previousAddress = strAddress;

                } while (cur1.moveToNext());
            Log.e("test", smsModelList.toString());
            //getSms(previousAddress);
            // end if
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        return smsModelList;
    }

    public static boolean validatePhone(String phoneNumber) {
        if (phoneNumber.isEmpty() || isValidMobile(phoneNumber)) {
            return false;
        } else {
            return true;
        }

    }

    private static boolean isValidMobile(String phone) {
        String Regex = "[^\\d]";
        String PhoneDigits = phone.replaceAll(Regex, "");
        return (PhoneDigits.length() != 10);
    }

    public static void backupAllSms(Context mContext, List<SMSModel> mSmsList) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "/SMSApp/smsbackup/");
            if (!root.exists()) {
                root.mkdirs();
            }
            Gson gson = new Gson();
            String smsList = gson.toJson(mSmsList);
            File gpxfile = new File(root, "smsBackup.txt");
            Log.d("", gpxfile.getPath() + " Absolute path : " + gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(smsList);
            writer.flush();
            writer.close();
            Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
