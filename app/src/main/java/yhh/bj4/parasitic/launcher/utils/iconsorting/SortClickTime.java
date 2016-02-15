package yhh.bj4.parasitic.launcher.utils.iconsorting;

import java.util.Comparator;

import yhh.bj4.parasitic.launcher.loader.InfoCache;

/**
 * Created by yenhsunhuang on 2016/2/11.
 */
public class SortClickTime implements Comparator<InfoCache> {

    @Override
    public int compare(InfoCache lhs, InfoCache rhs) {
        return Long.compare(rhs.getClickTime(), lhs.getClickTime());
    }
}
