package com.toheda.app.request;

import android.text.TextUtils;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class GetUserRequest extends JsonRequest<UserModel> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final String userId;

    private final Map<String, String> headers;

    private GetUserRequest(final String userId, final String url, final Map<String, String> headers, final Response.Listener<UserModel> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.userId = userId;
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
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("users");

            Type type = new TypeToken<List<UserModel>>() {
            }.getType();
            List<UserModel> users = gson.fromJson(jsonArray.toString(), type);

            for (UserModel user : users) {
                if (TextUtils.equals(userId, user.getUserId())) {
                    return Response.success(user,
                            HttpHeaderParser.parseCacheHeaders(response));
                }
            }

            return null;

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetUserRequest, UserModel> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/tenant/users?userId={0}";

        private String userId;

        public Builder forUserId(final String userId) {
            this.userId = userId;

            return this;
        }

        @Override
        protected GetUserRequest buildRequest() {
            final String url = MessageFormat.format(URL_PATTERN, userId);

            return new GetUserRequest(userId, url, getHeadersWithToken(), listener, errorListener);
        }
    }
}