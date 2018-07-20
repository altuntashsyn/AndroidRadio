package com.digitalentertainmentteam.fmradyonudinle.fragments;

/**
 * Created by huseyin on 18/01/18.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.digitalentertainmentteam.fmradyonudinle.Database;
import com.digitalentertainmentteam.fmradyonudinle.HomeActivity;
import com.digitalentertainmentteam.fmradyonudinle.R;
import com.digitalentertainmentteam.fmradyonudinle.RadioAdapter;
import com.digitalentertainmentteam.fmradyonudinle.RadioDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RadyolarFragment extends Fragment {

    private List<RadioDao> list = new ArrayList<>();
    private List<RadioDao> notFilteredList = new ArrayList<>();

    private TextView text_radios;
    private ImageView image_favouriteRadios;
    private EditText edit_searchRadios;
    private ListView listView_radios;
    private RadioAdapter radioAdapter;
    private String textRadio;
    private Database mDatabase;
    //private RadioManager mRadioManager;
    private ProgressDialog progress;
    private int screenFavouriteStatus = 1;

    public RadyolarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);


        findViews(view);
        databaseOperations();
        getValues();
        favouriteClick();
        editTextChange();
        listViewClick();
        return view;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("radios.json");
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

    private void findViews(View view) {
        text_radios = (TextView) view.findViewById(R.id.text_radios);
        image_favouriteRadios = (ImageView) view.findViewById(R.id.image_favouriteRadios);
        edit_searchRadios = (EditText) view.findViewById(R.id.edit_searchRadios);
        listView_radios = (ListView) view.findViewById(R.id.listView_radios);
        /*text_radioName = (TextView) view.findViewById(R.id.text_radioName);
        controlLayout = (LinearLayout) view.findViewById(R.id.control);
        imgPrew = (ImageView) view.findViewById(R.id.imgPrew);
        imgNext = (ImageView) view.findViewById(R.id.imgNext);
        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);*/
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
        edit_searchRadios.setTypeface(type);
        text_radios.setTypeface(type);
        //text_radioName.setTypeface(type);

    }

    private void databaseOperations() {
        mDatabase = new Database(getActivity());

        File database = getActivity().getDatabasePath(Database.DBNAME);
        if (false == database.exists()) {
            mDatabase.getReadableDatabase();

            if (!copyDatabase(getActivity())) {
                return;
            } else {

            }
        }
    }

    private void getValues() {
       /* try {
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
        }*/

        list = mDatabase.getRadios();
        notFilteredList = mDatabase.getRadios();
        notFilteredList = list;
        radioAdapter = new RadioAdapter(getActivity(), list, notFilteredList);
        listView_radios.setTextFilterEnabled(true);
        listView_radios.setAdapter(radioAdapter);
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
                    radioAdapter = new RadioAdapter(getActivity(), list, notFilteredList);
                    listView_radios.setTextFilterEnabled(true);
                    listView_radios.setAdapter(radioAdapter);
                } else {
                    screenFavouriteStatus = 1;
                    image_favouriteRadios.setImageResource(R.drawable.starr);
                    edit_searchRadios.setText("");
                    list = mDatabase.getFavourites();
                    notFilteredList = mDatabase.getFavourites();
                    radioAdapter = new RadioAdapter(getActivity(), list, notFilteredList);
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
                radioAdapter.getFilter().filter(textRadio);
            }

        });

    }

    private void listViewClick() {
        listView_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((HomeActivity) getActivity()).playPauseRadio(false, list.get(position).getName(), list.get(position).getURL(), position);

                //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
                text_radios.setText(list.get(position).getName());

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
}