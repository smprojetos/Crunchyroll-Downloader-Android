package com.hama.crdl;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.hama.crdl.MainActivity.HARDSUBS;
import static com.hama.crdl.MainActivity.RESOLUTION;
import static com.hama.crdl.MainActivity.SHARED_PREFS;



public class SettingsActivity extends MainActivity {
Spinner CB_HarsSubLang;
Spinner CB_Resolution;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        CB_HarsSubLang = (Spinner) findViewById(R.id.CB_Lang) ;
        CB_Resolution = (Spinner) findViewById(R.id.CB_Reso) ;
        //CB_HarsSubLang.setSelection();
        List<CB_Lang_Class> CB_LangList = new ArrayList<>();
        CB_Lang_Class lang1 = new CB_Lang_Class("Deutsch", "\"deDE\"");
        CB_LangList.add(lang1);
        CB_Lang_Class lang2 = new CB_Lang_Class("English", "\"enUS\"");
        CB_LangList.add(lang2);
        CB_Lang_Class lang3 = new CB_Lang_Class("Português (Brasil)", "\"ptBR\"");
        CB_LangList.add(lang3);
        CB_Lang_Class lang4 = new CB_Lang_Class("Español (LA)", "\"esLA\"");
        CB_LangList.add(lang4);
        CB_Lang_Class lang5 = new CB_Lang_Class("Français (France)", "\"frFR\"");
        CB_LangList.add(lang5);
        CB_Lang_Class lang6 = new CB_Lang_Class("العربية (Arabic)", "\"arME\"");
        CB_LangList.add(lang6);
        CB_Lang_Class lang7 = new CB_Lang_Class("Русский (Russian)", "\"ruRU\"");
        CB_LangList.add(lang7);
        CB_Lang_Class lang8 = new CB_Lang_Class("Italiano (Italian)", "\"itIT\"");
        CB_LangList.add(lang8);
        CB_Lang_Class lang9 = new CB_Lang_Class("Español (España)", "\"esES\"");
        CB_LangList.add(lang9);
        CB_Lang_Class lang10 = new CB_Lang_Class("[ without  (none) ]", "null");
        CB_LangList.add(lang10);
        ArrayAdapter<CB_Lang_Class> adapter = new ArrayAdapter<CB_Lang_Class>(this,android.R.layout.simple_spinner_item, CB_LangList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        CB_HarsSubLang.setAdapter(adapter);

        if (hardsub_value != null)
        {
            //Toast.makeText(this, hardsub_value, Toast.LENGTH_SHORT).show();
            //int spinnerPosition = CB_LangList.indexOf();

            for (int i=0;i<adapter.getCount();i++){
                 if (adapter.getItem(i).getValue().equalsIgnoreCase(hardsub_value)){
                    CB_HarsSubLang.setSelection(i);
                }
            }
        }
         else
             {
            Toast.makeText(this, hardsub_value, Toast.LENGTH_SHORT).show();
             }

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Reso, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CB_Resolution.setAdapter(adapter2);
        if (resolution_value != null) {
            int spinnerPosition = adapter2.getPosition(resolution_value);
            CB_Resolution.setSelection(spinnerPosition);
        } else
        {
            Toast.makeText(this, resolution_value, Toast.LENGTH_SHORT).show();
        }

    }




    public void saveData(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        CB_Lang_Class Lang = (CB_Lang_Class) CB_HarsSubLang.getSelectedItem();
        hardsub_value = Lang.getValue();
        editor.putString(HARDSUBS, Lang.getValue());

        resolution_value = CB_Resolution.getSelectedItem().toString();
        editor.putString(RESOLUTION, CB_Resolution.getSelectedItem().toString());
        //System.out.printf("Reso", resolution_value);
        //Toast.makeText(this, hardsub_value, Toast.LENGTH_SHORT).show();
        editor.apply();
        Toast.makeText(this, "saved!", Toast.LENGTH_SHORT).show();
        finish();

    }
}
