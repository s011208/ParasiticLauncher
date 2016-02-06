package yhh.bj4.parasitic.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView grid = (GridView)findViewById(R.id.test_grid);
        grid.setAdapter(new IconGridAdapter(MainActivity.this));
    }
}
