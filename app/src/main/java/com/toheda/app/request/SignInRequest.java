package com.toheda.app.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class SignInRequest extends JsonRequest<String> {

    private static final int method = Method.POST;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private SignInRequest(final String url, final String payload, final Map<String, String> headers, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        super(method, url, payload, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            String token = new JSONObject(jsonString).getString("token");

            return Response.success(token,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, SignInRequest, String> {

        private static final String URL = "http://www.actorsphere.de/auth/login";

        private String username;

        private String password;

        public Builder useUsername(final String username) {
            this.username = username;
            return this;
        }

        public Builder usePassword(final String password) {
            this.password = password;
            return this;
        }

        @Override
        protected SignInRequest buildRequest() {
            Gson gson = new GsonBuilder().create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("password", password);
            String payload = gson.toJson(jsonObject);

            return new SignInRequest(URL, payload, new HashMap<String, String>(), listener, errorListener);
        }
    }
}