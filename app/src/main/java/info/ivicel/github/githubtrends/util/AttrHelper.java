package info.ivicel.github.githubtrends.util;


import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.AttrRes;
import android.util.TypedValue;

public class AttrHelper {
    public static TypedValue resolveAttr(Context context, @AttrRes int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue;
    }
}
