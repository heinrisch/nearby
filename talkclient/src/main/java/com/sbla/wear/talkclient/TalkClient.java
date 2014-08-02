package com.sbla.wear.talkclient;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.sbla.wear.talkclient.adapters.TalkCallbackAdapter;
import com.sbla.wear.talkclient.adapters.TalkMessageAdapter;

public class TalkClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private GoogleApiClient mGoogleApiClient;
    private TalkCallbackAdapter mTalkCallbackAdapter;
    private TalkMessageAdapter mTalkMessageAdapter;

    public TalkClient(Context context) {
        mTalkCallbackAdapter = new TalkCallbackAdapter();
        mTalkMessageAdapter = new TalkMessageAdapter();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void setTalkCallbackAdapter(TalkCallbackAdapter adapter){
        mTalkCallbackAdapter = adapter;
    }

    public void setTalkMessageAdapter(TalkMessageAdapter adapter){
        mTalkMessageAdapter = adapter;
    }

    public void connectClient() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnectClient() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        mTalkCallbackAdapter.onConnected(bundle);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        mTalkCallbackAdapter.onConnectionSuspended(cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mTalkCallbackAdapter.onConnectionFailed(connectionResult);
    }

    public void sendMessage(final String path) {
        sendMessage(path, null);
    }


    public void sendMessage(final String path, DataMap dataMap) {
        final byte[] data = dataMap != null ? dataMap.toByteArray() : new byte[0];
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (final Node node : getConnectedNodesResult.getNodes()) {
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, data)
                                    .setResultCallback(getSendMessageResultCallback());
                        }
                    }
                }
        );
    }

    private ResultCallback<MessageApi.SendMessageResult> getSendMessageResultCallback() {
        return new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                mTalkMessageAdapter.onResult(sendMessageResult);
            }
        };
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        mTalkMessageAdapter.onMessageReceived(messageEvent);
    }
}
