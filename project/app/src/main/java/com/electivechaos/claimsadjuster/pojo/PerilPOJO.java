package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by barkhasikka on 28/04/18.
 */

public class PerilPOJO implements Parcelable {
    public static final Creator<PerilPOJO> CREATOR = new Creator<PerilPOJO>() {
        @Override
        public PerilPOJO createFromParcel(Parcel in) {
            return new PerilPOJO(in);
        }

        @Override
        public PerilPOJO[] newArray(int size) {
            return new PerilPOJO[size];
        }
    };
    private String name;
    private String description;
    private int id;

    public PerilPOJO() {
        id = -1;
        name = "";
        description = "";
    }

    protected PerilPOJO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
    }
}
