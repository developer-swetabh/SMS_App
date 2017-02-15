package swetabh.com.smsapp.models;

import java.io.Serializable;

/**
 * Created by abhi on 14/02/17.
 */
public class SMSModel implements Serializable {

    private String strAddress;
    private int intPerson;
    private String strbody;
    private long longDate;
    private int int_Type;
    private int intCount;

    public SMSModel(String strAddress, int intPerson, String strbody, long longDate, int int_Type, int intCount) {
        this.strAddress = strAddress;
        this.intPerson = intPerson;
        this.strbody = strbody;
        this.longDate = longDate;
        this.int_Type = int_Type;
        this.intCount = intCount;
    }

    public SMSModel(String strAddress, int intPerson, String strbody, long longDate, int int_Type) {
        this.strAddress = strAddress;
        this.intPerson = intPerson;
        this.strbody = strbody;
        this.longDate = longDate;
        this.int_Type = int_Type;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public int getIntPerson() {
        return intPerson;
    }

    public void setIntPerson(int intPerson) {
        this.intPerson = intPerson;
    }

    public String getStrbody() {
        return strbody;
    }

    public void setStrbody(String strbody) {
        this.strbody = strbody;
    }

    public long getLongDate() {
        return longDate;
    }

    public void setLongDate(long longDate) {
        this.longDate = longDate;
    }

    public int getInt_Type() {
        return int_Type;
    }

    public void setInt_Type(int int_Type) {
        this.int_Type = int_Type;
    }

    public int getIntCount() {
        return intCount;
    }

    public void setIntCount(int intCount) {
        this.intCount = intCount;
    }
}
