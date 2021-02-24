package SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import Entity.RecordForm;


public class SQLiteControl implements Serializable {
    SQLiteHelper helper;
    SQLiteDatabase sqlite;

    public int GetMaxUid() {
        try {
            sqlite = helper.getReadableDatabase();
            Cursor c = sqlite.rawQuery("SELECT MAX(record_uid) FROM RECORD", null);
            int temp = 0;
            if (c.moveToFirst()) {
                temp = c.getInt(0);
            } else {
                throw new Exception();
            }
            c.close();
            return temp + 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public void Close() {
        helper.close();
        sqlite.close();
    }

    public SQLiteControl(SQLiteHelper _helper) {
        this.helper = _helper;
    }

    public ArrayList<RecordForm> GetDataSQL(String sql) {
        ArrayList<RecordForm> temp = new ArrayList<RecordForm>();
        sqlite = helper.getReadableDatabase();
        Cursor c = sqlite.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                temp.add(new RecordForm(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4)));
            } while (c.moveToNext());
        }
        c.close();
        return temp;
    }

    public boolean ExecuteSQL(String sql) {
        try {
            sqlite = helper.getWritableDatabase();
            sqlite.execSQL(sql);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean DeleteData(int uid){
        try {
            ExecuteSQL("DELETE FROM RECORD WHERE record_uid = " + uid);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean InsertData(RecordForm form) {
        try {
            int temp_uid = GetMaxUid();
            ExecuteSQL("INSERT INTO RECORD(record_uid, record_year, record_month, record_day, record_money) VALUES " +
                    "(" + temp_uid + ", " + form.getYear() + ", " + form.getMonth() + ", " + form.getDay() + ", " + form.getMoney() + ");");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public ArrayList<RecordForm> GetData(int year, int month) {
        return GetDataSQL("SELECT * FROM RECORD WHERE record_year = " + year + " and record_month = " + month);
    }

    public ArrayList<RecordForm> GetData(int year) {
        return GetDataSQL("SELECT * FROM RECORD WHERE record_year = " + year);
    }

}