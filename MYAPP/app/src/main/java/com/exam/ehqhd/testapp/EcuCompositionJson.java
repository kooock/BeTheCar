package com.exam.ehqhd.testapp;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class EcuCompositionJson {

    public JSONObject makeJSONObject (String data_01, String data_02)
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("SPEED", data_01);
            obj.put("RPM", data_02);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
