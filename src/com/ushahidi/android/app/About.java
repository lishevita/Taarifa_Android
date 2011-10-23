/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends DashboardActivity {

    private Button mediaUrlBtn;

    private Button teamUrlBtn;

    private Button twitterUrlBtn;

    private Button facebookUrlBtn;

    private Button contactUrlBtn;

    private TextView version;
    
    private ImageButton searchButton;

    private static final String MEDIA_URL = "http://www.waterhackathon.org";

    private static final String TEAM_URL = "http://www.rhok.org/event/waterhackathon-london";

    private static final String TWITTER_URL = "https://twitter.com/randomhacks";

    private static final String FACEBOOK_URL = "https://www.facebook.com/RandomHacks";

    private static final String CONTACT_URL = "http://www.rhok.org/event/waterhackathon-london";

    private static final int HOME = Menu.FIRST + 1;

    private static final int SETTINGS = Menu.FIRST + 2;

    private static final int GOTOHOME = 0;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int DIALOG_ERROR = 0;

    private String versionName = "";

    private String dialogErrorMsg = "An error occurred fetching the reports. "
            + "Make sure you have entered an Ushahidi instance.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        setTitleFromActivityLabel(R.id.title_text);
     // Check if default domain has been set.
        if (!TextUtils.isEmpty(getString(R.string.default_deployment))) {
            //remove search icon
            searchButton = (ImageButton) findViewById(R.id.search_report_btn);
            searchButton.setVisibility(View.GONE);
        }
        mediaUrlBtn = (Button)findViewById(R.id.media_link);
        teamUrlBtn = (Button)findViewById(R.id.team_link);
        twitterUrlBtn = (Button)findViewById(R.id.twitter_link);
        facebookUrlBtn = (Button)findViewById(R.id.facebook_link);
        contactUrlBtn = (Button)findViewById(R.id.contact_link);

        version = (TextView)findViewById(R.id.version);

        try {
            versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText(versionName);
        // Dipo Fix
        
        mediaUrlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Dip Fix
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(MEDIA_URL)));
            }
        });

        teamUrlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(TEAM_URL)));
            }
        });

        twitterUrlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(TWITTER_URL)));
            }
        });

        facebookUrlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL)));
            }
        });

        contactUrlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(CONTACT_URL)));
            }
        });
    }

    private void populateMenu(Menu menu) {

        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.ushahidi_home);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_about);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // applyMenuChoice(item);

        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(About.this, Ushahidi.class);
                startActivityForResult(intent, GOTOHOME);
                return true;

            case SETTINGS:
                intent = new Intent(About.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return (true);
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(R.string.alert_dialog_error_title);
                dialog.setMessage(dialogErrorMsg);
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent launchPreferencesIntent = new Intent(About.this, Settings.class);

                        // Make it a sub activity so we know when it returns
                        startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }
        }
        return null;
    }

    final Runnable mDisplayErrorPrompt = new Runnable() {
        public void run() {
            showDialog(DIALOG_ERROR);
        }
    };

}
