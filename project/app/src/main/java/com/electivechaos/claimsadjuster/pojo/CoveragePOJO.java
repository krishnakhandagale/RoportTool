package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class CoveragePOJO implements Parcelable {
    public static final Creator<CoveragePOJO> CREATOR = new Creator<CoveragePOJO>() {
        @Override
        public CoveragePOJO createFromParcel(Parcel in) {
            return new CoveragePOJO(in);
        }

        @Override
        public CoveragePOJO[] newArray(int size) {
            return new CoveragePOJO[size];
        }
    };
    private String name;
    private String description;
    private int id;

    public CoveragePOJO() {
        id = -1;
        name = "";
        description = "";
    }

    protected CoveragePOJO(Parcel in) {
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
