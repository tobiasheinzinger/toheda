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
import com.toheda.app.model.ActorInstanceModel;
import com.toheda.app.model.RecordModel;

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

public class GetRecordListRequest extends JsonRequest<List<RecordModel>> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetRecordListRequest(final String url, final Map<String, String> headers, final Response.Listener<List<RecordModel>> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<List<RecordModel>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONArray apps = new JSONObject(jsonString).getJSONArray("result_records_list");

            Type type = new TypeToken<List<RecordModel>>() {
            }.getType();
            List<RecordModel> records = gson.fromJson(apps.toString(), type);

            return Response.success(records,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetRecordListRequest, List<RecordModel>> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/sa/sa_recording_fetcher/fetch_records_context/search?contextId={0}";

        private ActorInstanceModel actorInstance;


        public Builder forActorInstance(final ActorInstanceModel actorInstance) {
            this.actorInstance = actorInstance;

            return this;
        }

        @Override
        protected GetRecordListRequest buildRequest() {
            final String url = MessageFormat.format(URL_PATTERN, actorInstance.getContextId());

            return new GetRecordListRequest(url, getHeadersWithToken(), listener, errorListener);
        }
    }
}