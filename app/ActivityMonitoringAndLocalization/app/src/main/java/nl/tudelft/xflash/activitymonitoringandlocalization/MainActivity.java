package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        Intent startActivityMon = new Intent(this, ActivityMonitoring.class);
        startActivity(startActivityMon);
    }

    public void startLocalization(View v) {
        Intent startLocal = new Intent(this, Localization.class);
        startActivity(startLocal);
    }

}
