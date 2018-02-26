package com.toheda.app.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toheda.app.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class GetSignedInUserRequest extends JsonRequest<UserModel> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetSignedInUserRequest(final String url, final Map<String, String> headers, final Response.Listener<UserModel> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<UserModel> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            Type type = new TypeToken<UserModel>() {
            }.getType();
            UserModel user = gson.fromJson(jsonString, type);

            return Response.success(user,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetSignedInUserRequest, UserModel> {

        private static final String URL = "http://www.actorsphere.de/api/user";

        @Override
        protected GetSignedInUserRequest buildRequest() {

            return new GetSignedInUserRequest(URL, getHeadersWithToken(), listener, errorListener);
        }
    }
}