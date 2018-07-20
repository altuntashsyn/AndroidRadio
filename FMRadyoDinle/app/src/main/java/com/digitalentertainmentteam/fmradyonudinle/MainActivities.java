package com.digitalentertainmentteam.fmradyonudinle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.digitalentertainmentteam.fmradyonudinle.model.ItemRadio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;
import dmax.dialog.SpotsDialog;

public class MainActivities extends Activity implements RadioListener {

    private List<RadioDao> list = new ArrayList<>();
    private List<RadioDao> notFilteredList = new ArrayList<>();

    private TextView text_radios, text_radioName;
    private ImageView image_favouriteRadios, imgPrew, imgNext, imgPlay;
    private EditText edit_searchRadios;
    private ListView listView_radios;
    private RadioAdapter radioAdapter;
    private String textRadio;
    private Database mDatabase;
    private RadioManager mRadioManager;
    private ProgressDialog progress;
    private int screenFavouriteStatus = 1;
    android.app.AlertDialog dialog;
    private LinearLayout controlLayout;
    private int clicked_position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        databaseOperations();
        getValues();
        radioConnection();
        favouriteClick();
        editTextChange();
        listViewClick();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("radios.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void findViews() {
        text_radios = (TextView) findViewById(R.id.text_radios);
        image_favouriteRadios = (ImageView) findViewById(R.id.image_favouriteRadios);
        edit_searchRadios = (EditText) findViewById(R.id.edit_searchRadios);
        listView_radios = (ListView) findViewById(R.id.listView_radios);
        text_radioName = (TextView) findViewById(R.id.text_radioName);
        controlLayout = (LinearLayout) findViewById(R.id.control);
        imgPrew = (ImageView) findViewById(R.id.imgPrew);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);

        dialog = new SpotsDialog(MainActivities.this);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        edit_searchRadios.setTypeface(type);
        text_radios.setTypeface(type);
        text_radioName.setTypeface(type);

    }

    private void databaseOperations() {
        mDatabase = new Database(this);

        File database = getApplicationContext().getDatabasePath(Database.DBNAME);
        if (false == database.exists()) {
            mDatabase.getReadableDatabase();

            if (!copyDatabase(this)) {
                return;
            } else {

            }
        }
    }

    private void getValues() {
        try {
            JSONArray jsonArray = new JSONObject(loadJSONFromAsset()).getJSONArray("Json");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ItemRadio item = new ItemRadio();
                item.setRadioName(object.getString("radio_name"));
                item.setRadioCategoryName(object.getString("category_name"));
                item.setRadioId(object.getString("id"));
                item.setRadioImageurl(object.getString("radio_image"));
                item.setRadiourl(object.getString("radio_url"));
                RadioDao radioDao = new RadioDao(i, object.getString("radio_name"), object.getString("radio_url"), 0, object.getString("radio_image"));
                list.add(radioDao);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //list = mDatabase.getRadios();
        //notFilteredList = mDatabase.getRadios();
        notFilteredList = list;
        radioAdapter = new RadioAdapter(this, list, notFilteredList);
        listView_radios.setTextFilterEnabled(true);
        listView_radios.setAdapter(radioAdapter);
    }

    private void radioConnection() {
        mRadioManager = RadioManager.with(getApplicationContext());
        mRadioManager.registerListener(this);
        mRadioManager.setLogging(true);
        mRadioManager.connect();
    }

    private void favouriteClick() {
        image_favouriteRadios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenFavouriteStatus == 1) {
                    screenFavouriteStatus = 2;
                    image_favouriteRadios.setImageResource(R.drawable.bos_star);
                    edit_searchRadios.setText("");
                    list = mDatabase.getRadios();
                    notFilteredList = mDatabase.getRadios();
                    radioAdapter = new RadioAdapter(MainActivities.this, list, notFilteredList);
                    listView_radios.setTextFilterEnabled(true);
                    listView_radios.setAdapter(radioAdapter);
                } else {
                    screenFavouriteStatus = 1;
                    image_favouriteRadios.setImageResource(R.drawable.starr);
                    edit_searchRadios.setText("");
                    list = mDatabase.getFavourites();
                    notFilteredList = mDatabase.getFavourites();
                    radioAdapter = new RadioAdapter(MainActivities.this, list, notFilteredList);
                    listView_radios.setTextFilterEnabled(true);
                    listView_radios.setAdapter(radioAdapter);
                }

            }
        });
    }

    private void editTextChange() {
        edit_searchRadios.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textRadio = edit_searchRadios.getText().toString().toLowerCase(Locale.getDefault());
                MainActivities.this.radioAdapter.getFilter().filter(textRadio);
            }

        });

    }

    private void listViewClick() {
        listView_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clicked_position = position;

                if (!mRadioManager.isPlaying()) {
                    mRadioManager.startRadio(list.get(position).getURL());
                    text_radioName.setText(list.get(position).getName());
                    controlLayout.setVisibility(View.VISIBLE);
                } else {
                    mRadioManager.stopRadio();
                    mRadioManager.startRadio(list.get(position).getURL());
                    text_radioName.setText(list.get(clicked_position).getName());
                }

                dialog.show();

                //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
                text_radios.setText(list.get(position).getName());

            }
        });

        imgPrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked_position > 1) {

                    clicked_position = clicked_position - 1;

                    if (!mRadioManager.isPlaying()) {
                        mRadioManager.startRadio(list.get(clicked_position).getURL());
                        text_radioName.setText(list.get(clicked_position).getName());
                        imgPlay.setImageResource(R.drawable.play);
                    } else {
                        mRadioManager.stopRadio();
                        mRadioManager.startRadio(list.get(clicked_position).getURL());
                        text_radioName.setText(list.get(clicked_position).getName());
                        imgPlay.setImageResource(R.drawable.pause);
                    }

                    dialog.show();

                    //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
                    text_radios.setText(list.get(clicked_position).getName());
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked_position < list.size() - 1) {

                    clicked_position = clicked_position + 1;

                    if (!mRadioManager.isPlaying()) {
                        mRadioManager.startRadio(list.get(clicked_position).getURL());
                        text_radioName.setText(list.get(clicked_position).getName());
                        imgPlay.setImageResource(R.drawable.play);
                    } else {
                        mRadioManager.stopRadio();
                        mRadioManager.startRadio(list.get(clicked_position).getURL());
                        text_radioName.setText(list.get(clicked_position).getName());
                        imgPlay.setImageResource(R.drawable.pause);
                    }

                    dialog.show();

                    //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
                    text_radios.setText(list.get(clicked_position).getName());
                }
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRadioManager.isPlaying()) {
                    imgPlay.setImageResource(R.drawable.pause);
                    mRadioManager.startRadio(list.get(clicked_position).getURL());
                    text_radioName.setText(list.get(clicked_position).getName());
                } else {
                    mRadioManager.stopRadio();
                    imgPlay.setImageResource(R.drawable.play);
                }
            }
        });


    }

    private boolean copyDatabase(Context context) {
        try {

            InputStream inputStream = context.getAssets().open(Database.DBNAME);
            String outFileName = Database.DBLOCATION + Database.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] buff = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }


    @Override
    public void onRadioLoading() {

    }

    @Override
    public void onRadioConnected() {
        Log.d("msg", "onRadioConnected");
    }

    @Override
    public void onRadioStarted() {
        Log.d("msg", "onRadioStarted");

        dialog.dismiss();
    }

    @Override
    public void onRadioStopped() {
        Log.d("msg", "onRadioStopped");
        dialog.dismiss();
    }

    @Override
    public void onMetaDataReceived(String s, String s2) {
        Log.d("msg", "onMetaDataReceived" + " " + s + " " + s2);

    }

    @Override
    public void onError() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}