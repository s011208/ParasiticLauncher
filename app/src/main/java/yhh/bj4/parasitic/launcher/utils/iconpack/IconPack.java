package yhh.bj4.parasitic.launcher.utils.iconpack;

import android.graphics.drawable.Drawable;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class IconPack {
    private String mIconPackPackageTitle;
    private String mIconPackPackageName;
    private Drawable mIconPackPackageIcon;

    public IconPack(String iconPackTitle, String iconPackPkgName, Drawable icon) {
        mIconPackPackageTitle = iconPackTitle;
        mIconPackPackageName = iconPackPkgName;
        mIconPackPackageIcon = icon;
    }

    public String getIconPackPackageTitle() {
        return mIconPackPackageTitle;
    }

    public String getIconPackPackageName() {
        return mIconPackPackageName;
    }

    public Drawable getIconPackPackageDrawable() {
        return mIconPackPackageIcon;
    }
}
