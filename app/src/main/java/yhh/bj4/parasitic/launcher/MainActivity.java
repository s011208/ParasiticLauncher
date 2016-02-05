package yhh.bj4.parasitic.launcher;

import android.app.Activity;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.widgets.loader.IconLoader;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IconLoader.getInstance(this);
    }
}
