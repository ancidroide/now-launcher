package com.nowlauncher.nowlauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;

public class MainActivity extends Activity {

    /**
     * EN
     * Declaration of some variables
     *
     * IT
     * Dichiarazione di alcune variabili
     */

    // Drop Down Bar (drawer open bar)
    ImageView drawerbar;

    // Layout master
    public RelativeLayout rootlayout;

    // Value of offset of status bar
    public int statusBarOffset;

    // DisplayMetrics object
    public DisplayMetrics dm;

    // Array of all application in the device
    private ArrayList<AppInfo> mApplications;

    //Variabili per il controllo della selezione della barra e del touch
    public boolean checkbarpressed = (boolean) false;
    public int y;
    public int yiniziale=0;
    public boolean yinizialebool = (boolean) false;

    public RelativeLayout rootlayoutdrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * EN
         * Set up layout
         *
         * IT
         * Carica il layout
         */

        setContentView(R.layout.main_activity);

        /**
         * EN
         * Istance some variables
         *
         * IT
         * Istanzia alcune variabili
         */

        drawerbar = (ImageView) findViewById(R.id.drawer_bar);
        rootlayout = (RelativeLayout) findViewById(R.id.rootLayout);
        rootlayoutdrawer = (RelativeLayout) findViewById(R.id.drawer_rootlayout);




        drawerbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //imposta il booleano su true quando la barra è premuta
                    checkbarpressed=true;
                }

                return false;

            }

        });


        /**
         * EN
         * Load array of applications and set up drawer
         *
         * IT
         * Carica la lista delle applicazioni e carica il drawer
         */

        LoadApplication();
        CreateViews();

        //showUserSettings();

    }

    public void CreateViews() {

        /**
         * EN
         * Assign the adapter to grid view and set on click listener
         *
         * IT
         * Assegna l'adapter alla grid view e dichiara la funzione all'evento onItemClick
         */

        GridView gridview = (GridView) findViewById(R.id.drawerlist);
        gridview.setAdapter(new ApplicationsAdapter(this, mApplications));

        gridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView parent, View v, int position, long id) {

                AppInfo app = mApplications.get(position);
                startActivity(app.intent);
            }

        });

    }

    public void LoadApplication() {

        /**
         * EN
         * Get the list of applications by searching with intent category
         *
         * @param apps List of ResolveInfo, useful objects for getting applications informations
         *
         * IT
         * Ottiene la lista delle applicazioni cercando mediante la categoria dell'intent
         *
         * @param apps Lista degli oggetti ResolveInfo, oggetti utili per ottenere delle informazioni sulle applicazioni
         */


        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager manager = getPackageManager();

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            //final int count = apps.size();

            if (mApplications == null) {
                mApplications = new ArrayList<AppInfo>();
            }

            mApplications.clear();

            ListIterator<ResolveInfo> i=apps.listIterator();
            while(i.hasNext()) {
                AppInfo application = new AppInfo();
                ResolveInfo info = i.next();

                application.title = info.loadLabel(manager);
                application.setIntent(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                Bitmap d = ((BitmapDrawable)info.activityInfo.loadIcon(manager)).getBitmap();

                Bitmap bitmapOrig;

                dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                // Display density chooser
                if (dm.densityDpi == DisplayMetrics.DENSITY_LOW)
                    bitmapOrig = Bitmap.createScaledBitmap(d, 36, 36, false);
                else if (dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
                    bitmapOrig = Bitmap.createScaledBitmap(d, 48, 48, false);
                else if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH)
                    bitmapOrig = Bitmap.createScaledBitmap(d, 72, 72, false);
                else if (dm.densityDpi == DisplayMetrics.DENSITY_XHIGH)
                    bitmapOrig = Bitmap.createScaledBitmap(d, 96, 96, false);
                else bitmapOrig = Bitmap.createScaledBitmap(d, 72, 72, false);

                application.icon = new BitmapDrawable(bitmapOrig);

                mApplications.add(application);

            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        y = (int) event.getY();
        /**
         * EN
         * Get y value, calculate if touch is in tollerance zone of drop down bar
         *
         * IT
         * Ottiene il valore di y e calcola se il touch è nella zona di tolleranza della drop down bar
         */
        if (checkbarpressed==true) {
            if (yinizialebool==false) {
                //imposta la coordinata y iniziale per il successivo controllo dello "spostamento" del dito
                yiniziale=y;
                yinizialebool=true;
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin=y-drawerbar.getHeight();
            rootlayoutdrawer.setLayoutParams(params);


        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            yinizialebool=false;

            if (checkbarpressed==true) {
                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if (yiniziale>y){
                    params.topMargin = 0;
                    //params.height=rootlayout.getHeight();
                }

                else {
                    params.topMargin = rootlayout.getHeight()-drawerbar.getHeight();
                    //params.height=drawerbar.getHeight();
                }


                int originalPos[] = new int[2];
                rootlayoutdrawer.getLocationOnScreen( originalPos );

                Animation animation = new TranslateAnimation(0, 0, 0, 0-(rootlayoutdrawer.getTop()-params.topMargin));

                // il costruttore prevede 4 int, x iniziale, x finale, y iniziale e y finale. Tutte le coordinate sono relative al punto in cui si trova la view prima dell'animazione (es. 0,0,0,10 sposterà la view di 10dp in alto)

                // A better form is:
                // TraslateAnimation animation = new TranslateAnimation(0, 0, 0, -originalPos[1]);
                // or
                // TraslateAnimation animation = new TranslateAnimation(0, 0, 0, -(dropdownbar2.geTop()));

                animation.setDuration(300);
                animation.setFillAfter(false); // <-- fa in modo che a fine animazione la view rimanga nel posto e non ritorni al posto iniziale
                rootlayoutdrawer.startAnimation(animation);
                animation.setInterpolator(new AccelerateInterpolator(1));

                animation.setAnimationListener(new Animation.AnimationListener(){



                    @Override
                    public void onAnimationStart(Animation anim) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation anim) {

                    }

                    @Override
                    public void onAnimationEnd(Animation anim) {

                        rootlayoutdrawer.setLayoutParams(params);

                    }
                });

                checkbarpressed=false;

            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                break;

        }

        return true;

    }

	/*

	Funzione di esempio per ottenere i valori nelle impostazioni:

	private void getSettings() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		String string = sharedPrefs.getString("title", "string returned if empty"); // ottiene la stringa dell'edit_text_preference con title="title"

		boolean checkBox = sharedPrefs.getBoolean("title", false); // ottiene il valore del checkbox di title="title"


	}

	*/




}