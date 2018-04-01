package info.ivicel.github.githubtrends;

import android.app.Application;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import info.ivicel.github.githubtrends.helper.CustomX509TrustManager;
import info.ivicel.github.githubtrends.helper.HttpsUtils;
import info.ivicel.github.githubtrends.helper.TLSSocketFactory;
import info.ivicel.github.githubtrends.util.FavorHelper;
import info.ivicel.github.githubtrends.util.LangsHelper;


public class AppApplication extends Application {
    private static final String TAG = "AppApplication";
    private static RequestQueue sQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        LangsHelper.init(this);
        FavorHelper.init(this);
        initRequestQueue();
    }

    /* as android sdk 16-19, d enabled TSL1.2 by default,
     * we should check this, our app is supported 19+
     * more information see also: https://github.com/google/volley/issues/77
     */
    private void initRequestQueue() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SSLSocketFactory factory = null;
            try {
                ProviderInstaller.installIfNeeded(this);
            } catch (GooglePlayServicesNotAvailableException |
                    GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                /* if google play services is not available, we use custom
                 * SSLSocketFactory and TrustManager to trust all connection
                 * even though it's dangerous
                 */
                factory = new TLSSocketFactory(new CustomX509TrustManager());
            }

            sQueue = Volley.newRequestQueue(this, new HurlStack(null,
                    factory));

        } else {
            sQueue = Volley.newRequestQueue(this);
        }
    }

    public static RequestQueue getQueue() {
        return sQueue;
    }
}
