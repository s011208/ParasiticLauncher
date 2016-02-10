package yhh.bj4.parasitic.launcher.utils.iconsorting;

import java.util.Comparator;

import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;

/**
 * Created by yenhsunhuang on 2016/2/11.
 */
public class SortFromAToZ implements Comparator<ActivityInfoCache> {

    @Override
    public int compare(ActivityInfoCache lhs, ActivityInfoCache rhs) {
        return lhs.getTitle().compareTo(rhs.getTitle());
    }
}
