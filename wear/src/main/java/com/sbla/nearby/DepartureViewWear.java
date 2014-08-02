package com.sbla.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.sbla.wear.shared.CommunicationVariables;
import com.sbla.wear.shared.model.Departure;
import com.sbla.wear.talkclient.TalkClient;
import com.sbla.wear.talkclient.adapters.TalkMessageAdapter;

import java.util.List;

public class DepartureViewWear extends Activity {

    private static final String TAG = "DepartureViewWear";
    private static final String EXTRA_SITE_ID = "departure-view-site-id";

    private TextView mStatusTextView;
    private Adapter mAdapter;
    private TalkClient mTalkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        mStatusTextView = (TextView) findViewById(R.id.status);
        WearableListView wearableListView = (WearableListView) findViewById(R.id.list);
        mAdapter = new Adapter(this);
        wearableListView.setAdapter(mAdapter);


        mStatusTextView.setText("Getting realtime data...");

        mTalkClient = new TalkClient(getApplicationContext());
        mTalkClient.setTalkMessageAdapter(talkMessageAdapter);

        DataMap dataMap = new DataMap();
        dataMap.putString(CommunicationVariables.DATA_STOP_ID, getIntent().getStringExtra(EXTRA_SITE_ID));

        mTalkClient.sendMessage(CommunicationVariables.GET_DEPARTURES, dataMap);
    }

    private final TalkMessageAdapter talkMessageAdapter = new TalkMessageAdapter() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            super.onMessageReceived(messageEvent);
            final DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());

            if (messageEvent.getPath().equals(CommunicationVariables.GET_DEPARTURES_RESULTS)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        success();
                        List<Departure> departures = Departure.fromDataMap(CommunicationVariables.DATA_DEPARTURES_BUSSES, dataMap);
                        mAdapter.setList(departures);
                    }
                });
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mTalkClient.connectClient();
    }

    @Override
    protected void onDestroy() {
        mTalkClient.disconnectClient();
        super.onDestroy();
    }


    private void success() {
        mStatusTextView.setVisibility(View.INVISIBLE);
    }

    private void fail(String failReason) {
        mStatusTextView.setText(failReason);
    }

    class Adapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;
        private List<Departure> mList;

        protected Adapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Departure> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(mInflater.inflate(R.layout.list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            Departure departure = mList.get(position);
            view.setText(departure.mLineNumber + " " + departure.mDestination + " " + departure.mDisplayTime);
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }
    }

    public static Intent createIntent(Context context, String siteId){
        Intent intent = new Intent(context, DepartureViewWear.class);
        intent.putExtra(EXTRA_SITE_ID, siteId);
        return intent;
    }
}
