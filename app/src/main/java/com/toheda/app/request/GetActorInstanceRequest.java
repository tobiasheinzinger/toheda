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
import com.toheda.app.model.ActorInstanceIdentifierModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class GetActorInstanceRequest extends JsonRequest<ActorInstanceModel> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetActorInstanceRequest(final String url, final Map<String, String> headers, final Response.Listener<ActorInstanceModel> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<ActorInstanceModel> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("actorInstance");

            Type type = new TypeToken<ActorInstanceModel>() {
            }.getType();
            ActorInstanceModel actorInstance = gson.fromJson(jsonObject.toString(), type);

            return Response.success(actorInstance,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetActorInstanceRequest, ActorInstanceModel> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/app/{0}/instance/{1}";

        private ActorInstanceIdentifierModel actorInstanceIdentifier;

        public Builder forActorInstanceIdentifier(final ActorInstanceIdentifierModel actorInstanceIdentifier) {
            this.actorInstanceIdentifier = actorInstanceIdentifier;

            return this;
        }

        @Override
        protected GetActorInstanceRequest buildRequest() {
            final String actorId = this.actorInstanceIdentifier.getActorId();
            final String actorInstanceId = this.actorInstanceIdentifier.getActorInstanceId();

            final String url = MessageFormat.format(URL_PATTERN, actorId, actorInstanceId);

            return new GetActorInstanceRequest(url, getHeadersWithToken(), listener, errorListener);
        }
    }
}