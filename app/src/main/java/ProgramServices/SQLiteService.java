package ProgramServices;

import android.content.Context;

import com.example.yearendsettlement.MainActivity;

import SQLite.SQLiteControl;
import SQLite.SQLiteHelper;
import UserException.NotConnectSQLiteException;

public class SQLiteService {
    private static SQLiteHelper helper;
    private static SQLiteControl sqlite;

    public static boolean StartSQLite(Context content){
        if (sqlite == null) {
            helper = new SQLiteHelper(content,
                    "data.db",
                    null,
                    1);
            sqlite = new SQLiteControl(helper);
            return true;
        }
        return false;
    }

    public static boolean StopSQLite(Context content){
        if (sqlite != null) {
            sqlite.Close();
            return true;
        }
        return false;
    }

    public static SQLiteControl GetSQLite() throws NotConnectSQLiteException {
        if (sqlite == null){
            throw new NotConnectSQLiteException("SQLite가 연결되지 않았습니다.");
        }
        return sqlite;
    }
}
