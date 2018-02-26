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
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.TransitionModel;

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

public class GetTransitionListRequest extends JsonRequest<List<TransitionModel>> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetTransitionListRequest(final String url, final Map<String, String> headers, final Response.Listener<List<TransitionModel>> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<List<TransitionModel>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("transitions");

            Type type = new TypeToken<List<TransitionModel>>() {
            }.getType();
            List<TransitionModel> transitions = gson.fromJson(jsonArray.toString(), type);

            return Response.success(transitions,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetTransitionListRequest, List<TransitionModel>> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/transitions/{0}";

        private ActorInstanceIdentifierModel actorInstanceIdentifier;

        public Builder forActorInstanceIdentifier(final ActorInstanceIdentifierModel actorInstanceIdentifier) {
            this.actorInstanceIdentifier = actorInstanceIdentifier;

            return this;
        }

        @Override
        protected GetTransitionListRequest buildRequest() {
            final String actorInstanceId = actorInstanceIdentifier.getActorInstanceId();

            final String url = MessageFormat.format(URL_PATTERN, actorInstanceId);

            return new GetTransitionListRequest(url, getHeadersWithToken(), listener, errorListener);
        }
    }
}