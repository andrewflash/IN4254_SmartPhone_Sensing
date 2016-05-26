package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;

public class MainActivity extends AppCompatActivity {

    Button btnActivity;
    Button btnLocalization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnLocalization = (Button) findViewById(R.id.btnLocalization);
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
        Intent startLocal = new Intent(this, LocalizationActivity.class);
        startActivity(startLocal);
    }

}
