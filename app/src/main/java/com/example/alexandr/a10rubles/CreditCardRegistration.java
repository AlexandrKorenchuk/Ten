package com.example.alexandr.a10rubles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.card.payment.CardIOActivity;
import io.card.payment.CardType;
import io.card.payment.CreditCard;
import me.itangqi.waveloadingview.WaveLoadingView;

public class CreditCardRegistration extends AppCompatActivity {
    protected static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_SCAN = 100;
    private static final int REQUEST_AUTOTEST = 200;
    private boolean autotestMode;
    String mResultLabel;
    private int numAutotestsPassed;
    ImageView imgCard;
    AlertDialog.Builder ad;
    String outStr;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_registration);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        imgCard = (ImageView)findViewById(R.id.imgCreditCard);
        imgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScan(view);
            }
        });
    }

    public void onScan(View pressed) {
        Intent intent = new Intent(this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_NO_CAMERA, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                .putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false)
                .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true)
                .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, true)
                .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false)
                .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.GREEN)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, false)
                .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, true)
                .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

        try {
            int unblurDigits = 4;
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurDigits);
        } catch(NumberFormatException ignored) {}

        startActivityForResult(intent, REQUEST_SCAN);
    }

    public void onAutotest(View v) {

        Intent intent = new Intent(this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false)
                .putExtra("debug_autoAcceptResult", true);
        startActivity(new Intent(CreditCardRegistration.this, MainActivity.class));
        startActivityForResult(intent, REQUEST_AUTOTEST);
    }

    @Override
    public void onStop() {
        super.onStop();

        mResultLabel = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        if ((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null
                && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard result = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            if (result != null) {
                outStr += "Card number: " + result.getRedactedCardNumber() + "\n";
                CardType cardType = result.getCardType();
                outStr += "Card type: " + cardType.name();
                outStr += "Expiry: " + result.expiryMonth + "/" + result.expiryYear + "\n";
                outStr += "CVV: " + result.cvv + "\n";
                outStr += "Cardholder Name: " + result.cardholderName + "\n";
            }

            if (autotestMode) {
                numAutotestsPassed++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onAutotest(null);
                    }
                }, 500);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            autotestMode = false;
        }
        String title = "Закончить регистрацию?";
        String cancle = "Отменить";
        String agre = "Закончить";
        ad = new AlertDialog.Builder(CreditCardRegistration.this);
        ad.setTitle(title);
        ad.setPositiveButton(agre, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(CreditCardRegistration.this, MainActivity.class));
            }
        });
        ad.setNegativeButton(cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        ad.show();
    }
}
