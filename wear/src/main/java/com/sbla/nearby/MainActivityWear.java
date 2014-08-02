package com.sbla.nearby;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.sbla.nearby.handler.LocationHandler;
import com.sbla.wear.shared.CommunicationVariables;
import com.sbla.wear.shared.model.Site;
import com.sbla.wear.talkclient.TalkClient;
import com.sbla.wear.talkclient.adapters.TalkMessageAdapter;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivityWear extends Activity {

    private static final String TAG = "MainActivityWear";

    private TextView mStatusTextView;
    private TextView mRetryTextView;
    private Adapter mAdapter;
    private Location mLocation;
    private TalkClient mTalkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mRetryTextView = (TextView) findViewById(R.id.retry);
        WearableListView wearableListView = (WearableListView) findViewById(R.id.list);
        mAdapter = new Adapter(this);
        wearableListView.setAdapter(mAdapter);

        wearableListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                startActivity(DepartureViewWear.createIntent(MainActivityWear.this, (String) viewHolder.itemView.getTag()));
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });


        mRetryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", "click");
                retry();
            }
        });
        mRetryTextView.setVisibility(View.VISIBLE);
        mStatusTextView.setText("Getting Location...");

        mTalkClient = new TalkClient(getApplicationContext());
        mTalkClient.setTalkMessageAdapter(talkMessageAdapter);

        getLocation();
    }

    private void retry() {
        mTalkClient.disconnectClient();
        mTalkClient.connectClient();
        getLocation();
    }

    private void getLocation() {
        LocationHandler.getLocation(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationHandler, locationErrorHandler);
    }

    private final TalkMessageAdapter talkMessageAdapter = new TalkMessageAdapter() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            super.onMessageReceived(messageEvent);
            final DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());

            if (messageEvent.getPath().equals(CommunicationVariables.GET_NEARBY_RESULT)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        success();
                        List<Site> sites = Site.fromDataMap(CommunicationVariables.DATA_NEARBY_LIST, dataMap);
                        mAdapter.setList(sites);
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

    private Action1<Location> locationHandler = new Action1<Location>() {
        @Override
        public void call(Location location) {
            Log.e("Location", location + "");
            mStatusTextView.setText("Got location!");
            mLocation = location;

            DataMap map = new DataMap();
            map.putDouble(CommunicationVariables.DATA_LATITUDE, mLocation.getLatitude());
            map.putDouble(CommunicationVariables.DATA_LONGITUDE, mLocation.getLongitude());
            mTalkClient.sendMessage(CommunicationVariables.GET_NEARBY, map);
        }
    };

    private Action1<Throwable> locationErrorHandler = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(TAG, throwable.toString());
            fail("Could not get location");
        }
    };


    private void success() {
        mStatusTextView.setVisibility(View.GONE);
        mRetryTextView.setVisibility(View.GONE);
    }

    private void fail(String failReason) {
        mStatusTextView.setText(failReason);
    }

    class Adapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;
        private List<Site> mList;

        protected Adapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Site> list) {
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
            Site site = mList.get(position);
            view.setText(site.mName);
            holder.itemView.setTag(site.mId);
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }
    }
}
