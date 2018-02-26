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
import com.toheda.app.model.TaskModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class GetTaskListRequest extends JsonRequest<List<TaskModel>> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetTaskListRequest(final String url, final Map<String, String> headers, final Response.Listener<List<TaskModel>> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<List<TaskModel>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONArray apps = new JSONObject(jsonString).getJSONArray("tasklist");

            Type type = new TypeToken<List<TaskModel>>() {
            }.getType();
            List<TaskModel> actors = gson.fromJson(apps.toString(), type);

            return Response.success(actors,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetTaskListRequest, List<TaskModel>> {

        private static final String URL = "http://www.actorsphere.de/api/tasks";

        @Override
        protected GetTaskListRequest buildRequest() {
            return new GetTaskListRequest(URL, getHeadersWithToken(), listener, errorListener);
        }
    }
}