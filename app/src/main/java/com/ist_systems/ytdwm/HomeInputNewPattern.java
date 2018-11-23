package com.ist_systems.ytdwm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class HomeInputNewPattern extends AppCompatActivity {

    PatternLockView mPatternLockView;

    SQLiteHelper SQLiteHelper;
    Button btConfirm;
    Button btCancel;
    AlertDialog alrtLog;
    EditText etPassword1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pattern);

        SQLiteHelper = new SQLiteHelper(this);

        mPatternLockView = findViewById(R.id.patternInput);
        btCancel = findViewById(R.id.btCancel);
        btConfirm = findViewById(R.id.btConfirm);
        etPassword1 = findViewById(R.id.etPassword1);

        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {

                final String pass = PatternLockUtils.patternToString(mPatternLockView, pattern);

                btConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("password", pass);
                        editor.apply();
                        mPatternLockView.clearPattern();

                        Intent intent = new Intent(getApplicationContext(), HomeSaveInputPattern.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCleared() {

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            alrtLog = new AlertDialog.Builder(HomeInputNewPattern.this).setMessage("Cancel input pattern?")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(HomeInputNewPattern.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                    .show();
        }
    }
}






















































































