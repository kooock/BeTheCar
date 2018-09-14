package com.exam.ehqhd.testapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    public TextInputEditText edit_box_01;
    public TextInputEditText edit_box_02;
    TextInputEditText tbox_val_01;
    TextInputEditText tbox_val_02;
    TextInputEditText tbox_val_03;
    TextInputEditText tbox_val_04;
    TextInputEditText tbox_val_05;
    TextInputEditText tbox_val_06;
    TextInputEditText tbox_val_07;
    TextInputEditText tbox_val_08;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grantExternalStoragePermission();

        ButtonOnClickListener btnClick = new ButtonOnClickListener();

        Button btn_save = (Button)findViewById(R.id.btn_save);
        Button btn_load = (Button)findViewById(R.id.btn_Load);
        Button btn_input = (Button)findViewById(R.id.btn_input);

        edit_box_01 = (TextInputEditText)findViewById(R.id.tbox_01);
        edit_box_02 = (TextInputEditText)findViewById(R.id.tbox_02);

        btn_save.setOnClickListener(btnClick);
        btn_load.setOnClickListener(btnClick);
        btn_input.setOnClickListener(btnClick);
    }

    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            }else{
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }

        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "External Storage Permission is Grant ");
            return true;
        }

    }

    class ButtonOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btn_save:
                    makeJson();
                    try { jsonWrite(); } catch (IOException e) { e.printStackTrace(); }
                    break;

                case R.id.btn_Load:
                    try { jsonRead(); } catch (IOException e) { e.printStackTrace(); }
                    break;

                case R.id.btn_input:
                    edit_box_01.setText("0x410c0bAFBBCCDD01");
                    edit_box_02.setText("0x410d0bAFBBCCDD01");
                    break;
            }
        }
    }

    public void makeJson()
    {
        JSONObject mjson_01 = new JSONObject();
        try {
            mjson_01.put("Value_01", "1");
            mjson_01.put("Value_02", "2");
            mjson_01.put("Value_03", "3");
            mjson_01.put("Value_04", "4");
            mjson_01.put("Value_05", "5");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject mjson_02 = new JSONObject();
        try {
            mjson_02.put("Value_06", "6");
            mjson_02.put("Value_07", "7");
            mjson_02.put("Value_08", "8");
            mjson_02.put("Value_09", "9");
            mjson_02.put("Value_10", "10");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        JSONArray jsonArray = new JSONArray();

        jsonArray.put(mjson_01);
        jsonArray.put(mjson_02);

        JSONObject ecu_json = new JSONObject();
        try{
            ecu_json.put("ECU", jsonArray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }



        String jsonStr = ecu_json.toString();

    }

    public void jsonWrite() throws IOException {
        EcuCompositionJson obj = new EcuCompositionJson();
        JSONObject ecu = new JSONObject();

        TextInputEditText edit_box_01 = (TextInputEditText)findViewById(R.id.tbox_01);
        TextInputEditText edit_box_02 = (TextInputEditText)findViewById(R.id.tbox_02);

        ecu = obj.makeJSONObject(edit_box_01.getText().toString(), edit_box_02.getText().toString());

        try {
            Writer output = null;
            File file = new File("/mnt/sdcard/ECU.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(ecu.toString());
            Toast.makeText(getApplicationContext(), ecu.toString(), Toast.LENGTH_LONG).show();
            output.close();
        } catch (IOException  ex) {
            ex.printStackTrace();
        }
    }

    public void jsonRead() throws IOException
    {
        try {
            File readFile = new File(Environment.getExternalStorageDirectory(), "ECU.json");
            FileInputStream stream = new FileInputStream(readFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }

            // JSON READ FILE
//            Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();

            JSONObject jsonObj = new JSONObject(jsonStr);

            String m_01 = jsonObj.getString("SPEED");
            String m_02 = jsonObj.getString("RPM");

            m_01 = m_01.replace("0x","");
            m_02 = m_02.replace("0x","");

            char[] hexArray = m_01.toCharArray();
            String[] str = new String[20];
            int count = 0;
            for (int i = 0; i < hexArray.length; i+=2)
            {
                String hex_01 = String.format("%02X", (int) hexArray[i]);
                String hex_02 = String.format("%02X", (int) hexArray[i + 1]);
                Log.e("TAAG","Work?");
                int to_01 = Integer.parseInt(hex_01);
                int to_02 = Integer.parseInt(hex_02);

                int resultNum = to_01 + to_02;
                str[count] = Integer.toString(resultNum);
                Log.e("TAAG",str[count]);
                count++;

            }
            tbox_val_01 = (TextInputEditText)findViewById(R.id.tbox_val_01);
            tbox_val_02 = (TextInputEditText)findViewById(R.id.tbox_val_02);
            tbox_val_03 = (TextInputEditText)findViewById(R.id.tbox_val_03);
            tbox_val_04 = (TextInputEditText)findViewById(R.id.tbox_val_04);
            tbox_val_05 = (TextInputEditText)findViewById(R.id.tbox_val_05);
            tbox_val_06 = (TextInputEditText)findViewById(R.id.tbox_val_06);
            tbox_val_07 = (TextInputEditText)findViewById(R.id.tbox_val_07);
            tbox_val_08 = (TextInputEditText)findViewById(R.id.tbox_val_08);

            if (str[0].equals("65"))
            { tbox_val_01.setText("정상"); }
            else { tbox_val_01.setText("에러");}

            switch ( Integer.parseInt(str[1]))
            {
                case 93: //0c
                    tbox_val_02.setText("RPM");
                    break;

                case 94: //0d
                    tbox_val_02.setText("SPEED");
                    break;
            }

            if (Integer.parseInt(str[1]) == 93)
            {
                int result = ((Integer.parseInt(str[4])*256) * Integer.parseInt(str[5]))/4;
                tbox_val_03.setText(result + "RPM");
            }

            if (Integer.parseInt(str[1]) == 94)
            {
                int result = (Integer.parseInt(str[4]));
                tbox_val_03.setText(result + "KM");
            }

            if (Integer.parseInt(str[7]) == 61)
            {
                tbox_val_04.setText("디젤");
            }

            if (Integer.parseInt(str[7]) == 62)
            {
                tbox_val_04.setText("가솔린");
            }

            tbox_val_05.setText(str[4]);
            tbox_val_06.setText(str[5]);
            tbox_val_07.setText(str[6]);
            tbox_val_08.setText(str[7]);

//            Toast.makeText(getApplicationContext(), (str[0] +":"+ str[1] +":"+ str[2] +":"+ str[3] +":"+ str[4] +":"+ str[5] +":"+ str[6] +":"+ str[7]) ,Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
