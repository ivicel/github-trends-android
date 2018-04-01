package info.ivicel.github.githubtrends.helper;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import info.ivicel.github.githubtrends.util.HexUtil;

public class ImageLruCache implements ImageLoader.ImageCache {
    private static final int MAX_SIZE = 10 * 1024 * 1024;
    private static final String PATH_NAME = "bitmap";
    private static final int VERSION = 1;
    private static final int CACHE_ENTRY = 1;
    
    private DiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mMemLruCache;
    private WeakReference<Context> mContextRef;
    
    
    public ImageLruCache(Context context) {
        try {
            mDiskLruCache = DiskLruCache.open(getCacheDirectory(context, PATH_NAME),
                    getAppVersion(context), CACHE_ENTRY, MAX_SIZE);
        } catch (IOException e) {
            mDiskLruCache = null;
        }
        mContextRef = new WeakReference<>(context);
        mMemLruCache = new LruCache<String, Bitmap>(MAX_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getAllocationByteCount();
            }
        };
    }
    
    @Override
    public Bitmap getBitmap(String url) {
        Context context = mContextRef.get();
        if (context == null) {
            return null;
        }
        String key = getPathHash(url);
        Bitmap bitmap = mMemLruCache.get(key);
        
        if (bitmap == null && mDiskLruCache != null) {
            try {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                if (snapshot != null) {
                    InputStream in = snapshot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(in);
                }
            } catch (IOException e) {
                bitmap = null;
                // e.printStackTrace();
            }
        }
        
        return bitmap;
    }
    
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        final String key = getPathHash(url);
        mMemLruCache.put(key, bitmap);
        
        if (mDiskLruCache != null) {
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                OutputStream out = editor.newOutputStream(0);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 50, out)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private File getCacheDirectory(Context context, String pathName) {
        File cacheFile;
        if (!Environment.isExternalStorageRemovable() ||
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheFile = context.getExternalCacheDir();
        } else {
            cacheFile = context.getCacheDir();
        }
        
        return new File(cacheFile, pathName);
    }
    
    private int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return VERSION;
        }
    }
    
    private String getPathHash(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            byte[] bytes = md.digest();
            return HexUtil.bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(url.hashCode());
        }
    }
    
    
}
