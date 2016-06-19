package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button btnActivity;
    Button btnLocalization;
    Button btnPFLocalization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnLocalization = (Button) findViewById(R.id.btnLocalization);
        btnPFLocalization = (Button) findViewById(R.id.btnAdvLocalization);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startActivityMonitoring(View v) {
        Log.d("SPS", "Start Activity Monitoring...");
        Intent startActivityMon = new Intent(this, ActivityMonActivity.class);
        startActivity(startActivityMon);
        Log.d("SPS", "Activity Monitoring Started");
    }

    public void startLocalization(View v) {
        Log.d("SPS", "Start Localization");
        Intent startLocal = new Intent(this, LocalizationActivity.class);
        startActivity(startLocal);
        Log.d("SPS", "Localization Started");
    }

    public void startPFLocalization(View v) {
        Log.d("SPS", "Start PF Localization");
        Intent startPFLocal = new Intent(this, PFLocalizationActivity.class);
        startActivity(startPFLocal);
        Log.d("SPS", "PFLocalization Started");
    }

}
