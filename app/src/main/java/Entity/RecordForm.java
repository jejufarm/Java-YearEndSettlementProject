package Entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RecordForm implements Serializable, Cloneable {
    int uid;
    int year;
    int month;
    int day;
    long money;

    public RecordForm() {
        year = 0;
        month = 0;
        day = 0;
        money = 0;
    }

    public RecordForm(int uid, int year, int month, int day, long money) {
        this.uid = uid;
        this.year = year;
        this.month = month;
        this.day = day;
        this.money = money;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return year + "년" + month + "월" + day + "일 " + money + "원";
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.money = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
