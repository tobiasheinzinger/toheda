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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class FollowTransitionRequest extends JsonRequest<TransitionModel> {

    private static final int method = Method.PUT;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private FollowTransitionRequest(final String url, final Map<String, String> headers, final Response.Listener<TransitionModel> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<TransitionModel> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Type type = new TypeToken<TransitionModel>() {
            }.getType();

            TransitionModel transition = new TransitionModel();

            return Response.success(transition,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, FollowTransitionRequest, TransitionModel> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/transition/{0}/follow/{1}";

        private TransitionModel transitionModel;

        private ActorInstanceIdentifierModel actorInstanceIdentifier;

        public Builder forActorInstanceIdentifier(final ActorInstanceIdentifierModel actorInstanceIdentifier) {
            this.actorInstanceIdentifier = actorInstanceIdentifier;

            return this;
        }

        public Builder useTransition(final TransitionModel transitionModel) {
            this.transitionModel = transitionModel;

            return this;
        }

        @Override
        protected FollowTransitionRequest buildRequest() {
            final String actorInstanceId = actorInstanceIdentifier.getActorInstanceId();
            final String transitionId = transitionModel.getId();

            final String url = MessageFormat.format(URL_PATTERN, actorInstanceId, transitionId);

            return new FollowTransitionRequest(url, getHeadersWithToken(), listener, errorListener);
        }
    }
}