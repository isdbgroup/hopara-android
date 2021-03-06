package waikato.ac.nz.hopara_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

    public void startMap(View view) {
        Intent intent = new Intent(this, PaMapsActivity.class);
        startActivity(intent);
    }

    public void startDefinitions(View view) {
        Intent intent = new Intent(this, DefinitionActivity.class);
        startActivity(intent);
    }

    public void startPukete(View view) {
        Intent intent = new Intent(this, PuketeActivity.class);
        startActivity(intent);
    }

    public void startKirikiriroa(View view) {
        Intent intent = new Intent(this, KirikiriroaActivity.class);
        startActivity(intent);
    }

}
