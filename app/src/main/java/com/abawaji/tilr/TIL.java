package com.abawaji.tilr;

import org.json.JSONArray;

/**
 * Created by iObsa on 1/22/15.
 */
public class TIL {
   private String after;
   private JSONArray tilArray;

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public JSONArray getTilArray() {
        return tilArray;
    }

    public void setTilArray(JSONArray tilArray) {
        this.tilArray = tilArray;
    }
}
