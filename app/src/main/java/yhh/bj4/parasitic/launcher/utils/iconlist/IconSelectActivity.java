package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.InfoCache;
import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;
import yhh.bj4.parasitic.launcher.views.grid.IconGridView;

/**
 * Created by yenhsunhuang on 2016/2/17.
 */
public class IconSelectActivity extends Activity {

//    private IconGridView mIconGridView;
//    private IconGridAdapter mIconGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icon_select_view);
//        mIconGridView = (IconGridView) findViewById(R.id.grid);
//        mIconGridAdapter = new IconGridAdapter(IconSelectActivity.this);
//        mIconGridAdapter.setCustomizedList(new ArrayList<InfoCache>());
//        mIconGridView.setAdapter(mIconGridAdapter);
    }
}
