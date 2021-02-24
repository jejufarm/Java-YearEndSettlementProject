package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.yearendsettlement.R;
import com.example.yearendsettlement.MainActivity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import Entity.RecordForm;

public class RecordListViewAdapter extends BaseAdapter implements Serializable, Cloneable {
    public ArrayList<RecordForm> list;

    public RecordListViewAdapter() {
        list = new ArrayList<>();
    }


    public Object clone() {
        RecordListViewAdapter vo = null;
        try {
            vo = (RecordListViewAdapter) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return vo;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        RecordForm listItem = list.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.control_recordlistview, viewGroup, false);
        }
        DecimalFormat df = new DecimalFormat("#,###");

        TextView uid = (TextView) view.findViewById(R.id.uidText);
        TextView year = (TextView) view.findViewById(R.id.yearText);
        TextView month = (TextView) view.findViewById(R.id.monthText);
        TextView day = (TextView) view.findViewById(R.id.dayText);
        TextView money = (TextView) view.findViewById(R.id.moneyText);

        uid.setText(listItem.getUid() + "");
        year.setText(listItem.getYear() + "");
        month.setText(listItem.getMonth() + "");
        day.setText(listItem.getDay() + "");
        money.setText(listItem.getMoney() + "");
        money.setText(df.format(listItem.getMoney()));


        return view;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    public ArrayList<RecordForm> getList() {
        return this.list;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void append(int uid, int year, int month, int day, int money) {
        list.add(new RecordForm(uid, year, month, day, money));
        notifyDataSetChanged();
    }

    public void append(RecordForm form) {
        list.add(form);
        notifyDataSetChanged();
    }
}
