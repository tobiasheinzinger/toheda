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
import com.google.gson.reflect.TypeToken;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.ActorModel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class StartActorInstanceRequest extends JsonRequest<ActorInstanceIdentifierModel> {

    private static final int method = Method.POST;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private StartActorInstanceRequest(final String url, final String payload, final Map<String, String> headers, final Response.Listener<ActorInstanceIdentifierModel> listener, final Response.ErrorListener errorListener) {
        super(method, url, payload, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<ActorInstanceIdentifierModel> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Type type = new TypeToken<ActorInstanceIdentifierModel>() {
            }.getType();

            ActorInstanceIdentifierModel actorInstanceIdentifier = gson.fromJson(jsonString, type);

            return Response.success(actorInstanceIdentifier,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, StartActorInstanceRequest, ActorInstanceIdentifierModel> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/app/{0}";

        private ActorModel actor;

        private String title;

        public Builder forActor(final ActorModel actor) {
            this.actor = actor;

            return this;
        }

        public Builder useTitle(final String title) {
            this.title = title;

            return this;
        }

        @Override
        protected StartActorInstanceRequest buildRequest() {
            final String url = MessageFormat.format(URL_PATTERN, actor.getActorId());

            Gson gson = new GsonBuilder().create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", title);
            String payload = gson.toJson(jsonObject);

            return new StartActorInstanceRequest(url, payload, getHeadersWithToken(), listener, errorListener);
        }
    }
}