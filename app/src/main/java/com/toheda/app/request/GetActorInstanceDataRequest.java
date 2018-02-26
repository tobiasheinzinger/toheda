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
import com.toheda.app.model.FieldModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tobia on 16.02.2018.
 */

public class GetActorInstanceDataRequest extends JsonRequest<List<FieldModel>> {

    private static final int method = Method.GET;

    private static final Gson gson = new GsonBuilder().create();

    private final Map<String, String> headers;

    private GetActorInstanceDataRequest(final String url, final Map<String, String> headers, final Response.Listener<List<FieldModel>> listener, final Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<List<FieldModel>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("data");

            Type type = new TypeToken<List<FieldModel>>() {
            }.getType();
//          List<FieldModel> fields  = gson.fromJson(jsonObject.toString(), type);
            List<FieldModel> fields = new ArrayList<>();
            fields.add(new FieldModel("Id 1", "FAKE 1", "text 1"));
            fields.add(new FieldModel("Id 2", "FAKE 2", "text 2"));
            fields.add(new FieldModel("Id 3", "FAKE 3", "text 3"));
            fields.add(new FieldModel("Id 4", "FAKE 4", "text 4"));

            return Response.success(fields,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public static class Builder extends AbstractRequestBuilder<Builder, GetActorInstanceDataRequest, List<FieldModel>> {

        private static final String URL_PATTERN = "http://www.actorsphere.de/api/app/{0}/instance/{1}";

        private ActorInstanceIdentifierModel actorInstanceIdentifier;

        public Builder forActorInstanceIdentifier(final ActorInstanceIdentifierModel actorInstanceIdentifier) {
            this.actorInstanceIdentifier = actorInstanceIdentifier;

            return this;
        }

        @Override
        protected GetActorInstanceDataRequest buildRequest() {
            final String actorId = this.actorInstanceIdentifier.getActorId();
            final String actorInstanceId = this.actorInstanceIdentifier.getActorInstanceId();

            final String url = MessageFormat.format(URL_PATTERN, actorId, actorInstanceId);

            return new GetActorInstanceDataRequest(url, getHeadersWithToken(), listener, errorListener);
        }
    }
}