package info.ivicel.github.githubtrends.request;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import info.ivicel.github.githubtrends.model.Repo;

public class GsonRequest extends Request<List<Repo>> {
    private Response.Listener<List<Repo>> mSuccessListener;
    private static final Object mLock = new Object();
    
    public GsonRequest(String url, Response.ErrorListener errorListener,
            Response.Listener<List<Repo>> listener) {
        super(Method.GET, url, errorListener);
        this.mSuccessListener = listener;
    
        setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    
    @Override
    protected Response<List<Repo>> parseNetworkResponse(NetworkResponse response) {
        try {
            String stringJson = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Gson gson = new Gson();
            List<Repo> repos = gson.fromJson(stringJson, new TypeToken<List<Repo>>(){}.getType());
            return Response.success(repos, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    @Override
    protected void deliverResponse(List<Repo> response) {
        Response.Listener<List<Repo>> listener;
        synchronized (mLock) {
            listener = mSuccessListener;
        }
        if (listener != null) {
            listener.onResponse(response);
        }
    }
}
