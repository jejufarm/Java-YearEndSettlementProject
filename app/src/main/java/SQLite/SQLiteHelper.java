package SQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class SQLiteHelper extends android.database.sqlite.SQLiteOpenHelper implements Serializable {

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_query = "CREATE TABLE if not exists RECORD(" +
                "record_uid INT," +
                "record_year SMALLINT," +
                "record_month SMALLINT," +
                "record_day SMALLINT," +
                "record_money INT);";
        db.execSQL(create_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_query = "drop table RECORD;";
        db.execSQL(drop_query);

        onCreate(db);
    }
}
