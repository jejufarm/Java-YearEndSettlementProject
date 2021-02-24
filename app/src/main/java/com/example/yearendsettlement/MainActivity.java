package com.example.yearendsettlement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapter.RecordListViewAdapter;
import Entity.RecordForm;
import ProgramServices.SQLiteService;

public class MainActivity extends AppCompatActivity {
    //region define
    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private RecordForm selected_item = null;
    private int[] month_day = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private long backKeyPressedTime = 0;
    private Toast toast;
    /////////////////////////////////////////////////
    //             recordListView 구현             //
    /////////////////////////////////////////////////
    private ListView recordListView;
    private RecordListViewAdapter recordListViewAdapter;

    private EditText addMoneyText;
    private TextView totalText;
    //region spinner
    private Spinner select_spinner;

    private Spinner year_spinner;
    private Spinner month_spinner;
    private Spinner day_spinner;

    private SpinnerAdapter select_spinner_adapter;

    private SpinnerAdapter year_spinner_adapter;
    private SpinnerAdapter month_spinner_adapter;
    private SpinnerAdapter day_spinner_adapter;

    private ArrayList<Integer> select_spinner_list;
    private ArrayList<Integer> year_spinner_list;
    private ArrayList<Integer> month_spinner_list;
    private ArrayList<Integer> day_spinner_list;

    //endregion
    private Time current_time;

    //endregion
    //region Permission Func
    //////////////////////////////////////////////////////////////////////////////////////////////////.////////////////////////////////
    //                                                        Permission 설정                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////.//////////////////////////
    private void setPermission() {
        int permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionInfo != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                toastSend("SD Card 쓰기권한 승인", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            else
                toastSend("SD Card 쓰기권한 거부", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //endregion
    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }

    //region init
    private void init() {
        init_time();
        init_Spinner();
        addMoneyText = findViewById(R.id.addMoneyText);
        totalText = findViewById(R.id.totalText);
        init_RecordListView();
        select_spinner.setSelection(select_spinner_list.size() - 1);
        GetDataAll();
        setPermission();
    }

    private void init_RecordListView() {
        recordListViewAdapter = new RecordListViewAdapter();
        recordListView = findViewById(R.id.recordListView);
        recordListView.setAdapter(recordListViewAdapter);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                selected_item = (RecordForm) vo;
                System.out.print("");
            }
        });
    }

    //region spinner

    private int set_Day_Spinner(int year, int month) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            if (month == 2)
                return 29;
            else
                return 28;
        } else {
            return month_day[month - 1];
        }
    }

    private void init_Spinner_ArrayList() {
        select_spinner_list = new ArrayList<>();
        year_spinner_list = new ArrayList<>();
        month_spinner_list = new ArrayList<>();
        day_spinner_list = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            month_spinner_list.add(i);
        }
        for (int i = 2000; i <= current_time.year; i++) {
            select_spinner_list.add(i);
            year_spinner_list.add(i);
        }
        for (int i = 1; i <= set_Day_Spinner(current_time.year, current_time.month + 1); i++) {
            day_spinner_list.add(i);
        }
    }

    private void init_Day_Spinner() {
        day_spinner_adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, day_spinner_list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(25);
                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
    }

    private void init_Spinner_Adapter() {
        select_spinner_adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, select_spinner_list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(25);
                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };

        year_spinner_adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, year_spinner_list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(25);
                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        month_spinner_adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, month_spinner_list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(25);
                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        init_Day_Spinner();
    }

    private void init_Spinner() {

        select_spinner = findViewById(R.id.select_spinner);

        year_spinner = findViewById(R.id.year_spinner);
        month_spinner = findViewById(R.id.month_spinner);
        day_spinner = findViewById(R.id.day_spinner);
        init_Spinner_ArrayList();
        init_Spinner_Adapter();
        select_spinner.setAdapter(select_spinner_adapter);
        year_spinner.setAdapter(year_spinner_adapter);
        year_spinner.setSelection(year_spinner_list.size() - 1, true);
        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  int temp = set_Day_Spinner(Integer.parseInt(year_spinner.getSelectedItem().toString()), Integer.parseInt(month_spinner.getSelectedItem().toString()));

                day_spinner_list.clear();
                for (int i = 1; i <= temp; i++) {
                    day_spinner_list.add(i);
                }
                day_spinner.setSelection(0);
                month_spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        month_spinner.setAdapter(month_spinner_adapter);
        month_spinner.setSelection(current_time.month, false);
        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int temp = set_Day_Spinner(Integer.parseInt(year_spinner.getSelectedItem().toString()), Integer.parseInt(month_spinner.getSelectedItem().toString()));

                day_spinner_list.clear();
                for (int i = 1; i <= temp; i++) {
                    day_spinner_list.add(i);
                }
                day_spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // set_Day_Spinner(Integer.parseInt(year_spinner.getSelectedItem().toString()), Integer.parseInt(month_spinner.getSelectedItem().toString()));
        day_spinner.setAdapter(day_spinner_adapter);
        day_spinner.setSelection(current_time.monthDay - 1, false);
    }


    //endregion

    private void init_time() {
        current_time = new Time();
        current_time.setToNow();
    }

    //endregion

    //region Override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); // 양방향 세로모드 고정
        setContentView(R.layout.activity_main);
        SQLiteService.StartSQLite(MainActivity.this);
        setTitle("제주농원 판매기록");
        init();

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    //endregion

    //region ButtonEvents

    // 데이터 조회버튼
    public void GetDataAll() {
        try {
            long total = 0;
            DecimalFormat df = new DecimalFormat("#,###");
            ArrayList<RecordForm> temp = SQLiteService.GetSQLite().GetData(Integer.parseInt(select_spinner.getSelectedItem().toString()));
            recordListViewAdapter.clear();
            for (RecordForm item : temp) {
                total += item.getMoney();
                recordListViewAdapter.append(item);
            }
            totalText.setText(df.format(total));
            recordListView.setSelection(2);
            selected_item = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SelectButton_onClick(View view) {
        GetDataAll();
    }

    // 데이터 추가 버튼
    public void InsertButton_onClick(View view) {
        try {

            int year = (int) year_spinner.getSelectedItem();
            int month = (int) month_spinner.getSelectedItem();
            int day = (int) day_spinner.getSelectedItem();
            long money = Long.parseLong(addMoneyText.getText().toString());
            if (money > 2100000000) {
                toastSend("21억이 넘는 금액은 입력할 수 없습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
                return;
            }
            SQLiteService.GetSQLite().InsertData(new RecordForm(0, year, month, day, money));
            addMoneyText.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void TempButton_onClick(View view) {
        exportDB();
    }

    private void exportDB() {
        try {
            File data = Environment.getDataDirectory();


            String currentDBPath = "//data//" + "com.example.yearendsettlement"
                    + "//databases//" + "data.db";
            File currentDB = new File(data, currentDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream("mnt/sdcard/backup.sqlite").getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(getApplicationContext(), "Backup Successful!",
                    Toast.LENGTH_SHORT).show();


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void DeleteButton_onClick(View view) {
        try {
            if (selected_item != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제 확인");
                builder.setMessage(selected_item.toString() + "\r\n 을 정말로 삭제하시겠습니까?\r\n삭제를 하면 되돌릴 수 없습니다.");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            SQLiteService.GetSQLite().DeleteData(selected_item.getUid());
                            GetDataAll();
                            selected_item = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                builder.setNeutralButton("취소", null);
                builder.create().show();
            } else {
                toastSend("삭제할 데이터가 없습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            }
//            SQLiteService.GetSQLite().DeleteData(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void month1Button_onClick(View view) {
        GetListView(1);
    }

    public void month2Button_onClick(View view) {
        GetListView(2);

    }

    public void month3Button_onClick(View view) {
        GetListView(3);

    }

    public void month4Button_onClick(View view) {
        GetListView(4);
    }

    public void month5Button_onClick(View view) {
        GetListView(5);
    }

    public void month6Button_onClick(View view) {
        GetListView(6);
    }

    public void month7Button_onClick(View view) {
        GetListView(7);
    }

    public void month8Button_onClick(View view) {
        GetListView(8);
    }

    public void month9Button_onClick(View view) {
        GetListView(9);
    }

    public void month10Button_onClick(View view) {
        GetListView(10);
    }

    public void month11Button_onClick(View view) {
        GetListView(11);
    }

    public void month12Button_onClick(View view) {
        GetListView(12);
    }

    public void monthAllButton_onClick(View view) {

    }


    private void GetListView(int month) {
        try {
            long total = 0;
            DecimalFormat df = new DecimalFormat("#,###");
            ArrayList<RecordForm> temp = SQLiteService.GetSQLite().GetData(Integer.parseInt(select_spinner.getSelectedItem().toString()), month);
            recordListViewAdapter.clear();
            for (RecordForm item : temp) {
                total += item.getMoney();
                recordListViewAdapter.append(item);
            }
            totalText.setText(df.format(total));
            selected_item = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //endregion
}