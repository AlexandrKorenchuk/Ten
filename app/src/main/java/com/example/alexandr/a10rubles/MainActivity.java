package com.example.alexandr.a10rubles;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.IntRange;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Timer;

import io.fabric.sdk.android.Fabric;
import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "AnhcZWgW2JVz4REbAs6Bhwzqu";
    private static final String TWITTER_SECRET = "fNr2dIXpJQGZaLAABFu2sar5zxd2vwOpK2KBN2Kq2Ji4iQJYNR";
    int countClick = 0;
    SharedPreferences sPref;
    WaveLoadingView waveLoadingView;
    AlertDialog.Builder ad;
    Context context;
    final String SAVED_CLICK = "saved_click";
    public TextView txtTitle, txtTerm, txtWork;
    Button btnRepay;
    Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTerm = (TextView)findViewById(R.id.txtTerm);
        txtWork = (TextView)findViewById(R.id.txtWork);
        btnRepay = (Button)findViewById(R.id.btnRepay);
        LoadClick();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        waveLoadingView = (WaveLoadingView)findViewById(R.id.waveLoadingView);
        waveLoadingView.setProgressValue(100);
        waveLoadingView.setCenterTitle("10");
        context = MainActivity.this;
        if(countClick == 1){
            Occupe();
        }
        waveLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countClick == 0){
                    waveLoadingView.setProgressValue(0);
                    countClick++;
                    SaveClick();
                    String title = "Привяжи карту";
                    String text = "Чтобы получить 10 рублей свою банковскую карту";
                    String cancle = "Отменить";
                    String agre = "Привязать";
                    ad = new AlertDialog.Builder(context);
                    ad.setTitle(title);
                    ad.setMessage(text);
                    ad.setPositiveButton(agre, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, TwitterRegistration.class));
                        }
                    });
                    ad.setNegativeButton(cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            waveLoadingView.setProgressValue(100);
                            countClick = 0;
                        }
                    });
                    ad.setCancelable(true);
                    ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            waveLoadingView.setProgressValue(100);
                            countClick = 0;
                        }
                    });
                    ad.show();
                }
                else {
                    countClick++;
                    Occupe();

                }
            }
        });
        txtWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Investing.class));
            }
        });
    }

    private void Occupe() {
        if(countClick>2&&countClick<=5)
        {
            waveLoadingView.setProgressValue(100-(33*(countClick-2)));
            btnRepay.setVisibility(View.VISIBLE);
            txtWork.setVisibility(View.GONE);
            double cash = 11.50*(countClick-2);
            txtTitle.setText("-"+cash+" BYN");
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtTerm.setText(millisUntilFinished / 1000+ "\nдо автопогашения");
                    if(countClick==5){
                        waveLoadingView.setClickable(false);
                    }
                }
                public void onFinish() {
                    waveLoadingView.setProgressValue(100);
                    waveLoadingView.setClickable(true);
                    txtTitle.setText("ЗАНЯТЬ");
                    txtTerm.setText("на "+3);
                    btnRepay.setVisibility(View.GONE);
                    txtWork.setVisibility(View.VISIBLE);
                }
            }.start();
        }else if(countClick>5&&countClick<=10){
            waveLoadingView.setProgressValue(100-(20*(countClick-5)));
            btnRepay.setVisibility(View.VISIBLE);
            txtWork.setVisibility(View.GONE);
            double cash = 11.50*(countClick-5);
            txtTitle.setText("-"+cash+" BYN");
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtTerm.setText(millisUntilFinished / 1000+ "\nдо автопогашения");
                    if(countClick>=10){
                        waveLoadingView.setClickable(false);
                        Toast.makeText(getApplicationContext(), "Чтобы взять 50 рублей, заполни паспортные данные", Toast.LENGTH_LONG).show();

                    }
                }

                public void onFinish() {
                    waveLoadingView.setProgressValue(100);
                    waveLoadingView.setClickable(true);
                    if(countClick>=10){
                        waveLoadingView.setClickable(false);
                    }
                    txtTitle.setText("ЗАНЯТЬ");
                    txtTerm.setText("на "+3);
                    btnRepay.setVisibility(View.GONE);
                    txtWork.setVisibility(View.VISIBLE);
                }
            }.start();
        }
        else {
            waveLoadingView.setProgressValue(0);
            btnRepay.setVisibility(View.VISIBLE);
            txtWork.setVisibility(View.GONE);
            txtTitle.setText("-11.50 BYN");
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtTerm.setText(millisUntilFinished / 1000+ "\nдо автопогашения");
                        waveLoadingView.setClickable(false);

                }

                public void onFinish() {
                    waveLoadingView.setProgressValue(100);
                    waveLoadingView.setClickable(true);
                    txtTitle.setText("ЗАНЯТЬ");
                    txtTerm.setText("на "+3);
                    btnRepay.setVisibility(View.GONE);
                    txtWork.setVisibility(View.VISIBLE);
                }
            }.start();
        }
    }

    void SaveClick(){
        sPref= getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_CLICK, countClick);
        ed.apply();
        ed.commit();
    }
    void LoadClick(){
        sPref = getPreferences(MODE_PRIVATE);
        countClick = sPref.getInt(SAVED_CLICK, 0);
    }
}
