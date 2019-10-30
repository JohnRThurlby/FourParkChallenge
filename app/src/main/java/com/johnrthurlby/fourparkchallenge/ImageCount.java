package com.johnrthurlby.fourparkchallenge;

import java.lang.reflect.Array;

public class ImageCount {

    private int rowcount;
    private String userprefix, selectdate;

    public ImageCount(String userprefix, String selectdate, int rowcount) {
        this.userprefix = userprefix;
        this.selectdate = selectdate;
        this.rowcount = rowcount;
    }

    public String getUserprefix() {
        return userprefix;
    }

    public String getSelectdate() {
        return selectdate;
    }

    public int getRowcount() {
        return rowcount;
    }

}

