package com.digitalentertainmentteam.fmradyonudinle;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalentertainmentteam.fmradyonudinle.fragments.FavorilerFragment;
import com.digitalentertainmentteam.fmradyonudinle.fragments.RadyolarFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;
import dmax.dialog.SpotsDialog;

/**
 * Created by huseyin on 18/01/18.
 */
public class HomeActivity extends AppCompatActivity implements RadioListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout control;
    private TextView text_radioName;
    private ImageView imgPrew, imgNext, imgPlay;
    private RadioManager mRadioManager;
    private SpotsDialog dialog;
    private int mClickedPosition;
    private String mRadioName, mRadioURL;
    private List<RadioDao> list = new ArrayList<>();
    private Database mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        control = (LinearLayout) findViewById(R.id.control);
        text_radioName = (TextView) findViewById(R.id.text_radioName);
        imgPrew = (ImageView) findViewById(R.id.imgPrew);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);

        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setIcon(R.mipmap.icon);

        dialog = new SpotsDialog(this);

        databaseOperations();

        radioConnection();

        listeners();

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        text_radioName.setTypeface(type);


    }

    private void listeners() {

        imgPrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mClickedPosition > 1) {

                    mClickedPosition = mClickedPosition - 1;

                    if (!mRadioManager.isPlaying()) {
                        mRadioManager.startRadio(list.get(mClickedPosition).getURL());
                        text_radioName.setText(list.get(mClickedPosition).getName());
                        imgPlay.setImageResource(R.drawable.play);
                    } else {
                        mRadioManager.stopRadio();
                        mRadioManager.startRadio(list.get(mClickedPosition).getURL());
                        text_radioName.setText(list.get(mClickedPosition).getName());
                        imgPlay.setImageResource(R.drawable.pause);
                    }

                    dialog.show();

                    //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
                    //text_radios.setText(list.get(clicked_position).getName());
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClickedPosition < list.size() - 1) {

                    mClickedPosition = mClickedPosition + 1;

                    if (!mRadioManager.isPlaying()) {
                        mRadioManager.startRadio(list.get(mClickedPosition).getURL());
                        text_radioName.setText(list.get(mClickedPosition).getName());
                        imgPlay.setImageResource(R.drawable.play);
                    } else {
                        mRadioManager.stopRadio();
                        mRadioManager.startRadio(list.get(mClickedPosition).getURL());
                        text_radioName.setText(list.get(mClickedPosition).getName());
                        imgPlay.setImageResource(R.drawable.pause);
                    }

                    dialog.show();

                    //text_radios.setText(list.get(clicked_position).getName());
                }
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRadioManager.isPlaying()) {
                    imgPlay.setImageResource(R.drawable.pause);
                    mRadioManager.startRadio(mRadioURL);
                    text_radioName.setText(mRadioName);
                } else {
                    mRadioManager.stopRadio();
                    imgPlay.setImageResource(R.drawable.play);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RadyolarFragment(), "RADYOLAR");
        adapter.addFragment(new FavorilerFragment(), "FAVORİLERİM");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void playPauseRadio(boolean isClickedFromFavorites, String radioName, String radioURL, int clickedPosition) {
        control.setVisibility(View.VISIBLE);
        mClickedPosition = clickedPosition;
        mRadioName = radioName;
        mRadioURL = radioURL;
        list.clear();

        if (isClickedFromFavorites)
            list = mDatabase.getFavourites();
        else list = mDatabase.getRadios();


        if (!mRadioManager.isPlaying()) {
            mRadioManager.startRadio(mRadioURL);
            text_radioName.setText(mRadioName);
        } else {
            mRadioManager.stopRadio();
            mRadioManager.startRadio(mRadioURL);
            text_radioName.setText(mRadioName);
        }

        dialog.show();

        //progress = ProgressDialog.show(Radios.this, "", getResources().getString(R.string.lutfenBekleyiniz), true);
        //text_radios.setText(list.get(position).getName());
    }


    private void radioConnection() {
        mRadioManager = RadioManager.with(this);
        mRadioManager.registerListener(this);
        mRadioManager.setLogging(true);
        mRadioManager.connect();
    }


    private void databaseOperations() {
        mDatabase = new Database(this);

        File database = getDatabasePath(Database.DBNAME);
        if (false == database.exists()) {
            mDatabase.getReadableDatabase();

            if (!copyDatabase(this)) {
                return;
            } else {

            }
        }
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
    public void onMetaDataReceived(String s, final String s2) {
        Log.d("msg", "onMetaDataReceived");
        if (s != null)
            if (s.equalsIgnoreCase("StreamTitle"))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_radioName.setText(text_radioName.getText() + " / " + s2);
                    }
                });
    }

    @Override
    public void onError() {

    }
}