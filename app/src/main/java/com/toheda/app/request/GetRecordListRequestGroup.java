package com.toheda.app.request;

import android.content.Context;

import com.android.volley.Response;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.ActorInstanceModel;
import com.toheda.app.model.RecordModel;

import java.util.List;

/**
 * Created by tobias on 20.02.2018.
 */

public class GetRecordListRequestGroup {

    private GetRecordListRequestGroup() {
    }

    public static class Builder {
        protected Context context;

        protected Response.Listener<List<RecordModel>> listener;

        protected Response.ErrorListener errorListener;

        private ActorInstanceIdentifierModel actorInstanceIdentifier;

        public Builder forContext(final Context context) {
            this.context = context;

            return this;
        }

        public Builder forActorInstanceIdentifier(ActorInstanceIdentifierModel actorInstanceIdentifier) {
            this.actorInstanceIdentifier = actorInstanceIdentifier;
            return this;
        }

        public Builder onError(final Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public Builder onSuccess(final Response.Listener<List<RecordModel>> listener) {
            this.listener = listener;
            return this;
        }


        public void execute() {
            new GetActorInstanceRequest.Builder()
                    .forContext(context)
                    .forActorInstanceIdentifier(actorInstanceIdentifier)
                    .onError(errorListener)
                    .onSuccess(new Response.Listener<ActorInstanceModel>() {

                        @Override
                        public void onResponse(ActorInstanceModel actorInstance) {
                            new GetRecordListRequest.Builder()
                                    .forContext(context)
                                    .forActorInstance(actorInstance)
                                    .onError(errorListener)
                                    .onSuccess(listener)
                                    .execute();
                        }
                    })
                    .execute();
        }
    }
}
