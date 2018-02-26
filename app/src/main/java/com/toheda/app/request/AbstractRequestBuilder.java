package com.toheda.app.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.toheda.app.util.RequestQueueSingleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public abstract class AbstractRequestBuilder<B extends AbstractRequestBuilder<B, R, T>, R extends Request<?>, T> {

    protected Context context;

    protected Response.Listener<T> listener;

    protected Response.ErrorListener errorListener;

    public B forContext(final Context context) {
        this.context = context;

        return (B) this;
    }

    public B onError(final Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return (B) this;
    }

    public B onSuccess(final Response.Listener<T> listener) {
        this.listener = listener;
        return (B) this;
    }

    protected Map<String, String> getHeadersWithToken() {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        final String token = settings.getString("x-auth-token", ""/*default value*/);

        final Map<String, String> headers = new HashMap<>();
        headers.put("X-AUTH-TOKEN", token);

        return headers;
    }

    public void execute() {
        R request = buildRequest();
        request.setRetryPolicy(new DefaultRetryPolicy(
                2 * 60 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueueSingleton.getInstance(context).
                addToRequestQueue(request);
    }

    protected abstract R buildRequest();
}
