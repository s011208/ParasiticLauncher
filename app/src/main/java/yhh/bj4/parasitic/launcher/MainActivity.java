package yhh.bj4.parasitic.launcher;

import android.app.Activity;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;
import yhh.bj4.parasitic.launcher.views.grid.IconGridView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IconGridView grid = (IconGridView) findViewById(R.id.grid);
        IconGridAdapter adapter = new IconGridAdapter(this);
        grid.setAdapter(adapter);
    }
}
