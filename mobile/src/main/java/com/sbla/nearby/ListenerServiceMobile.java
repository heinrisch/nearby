package com.sbla.nearby;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.sbla.nearby.api.API;
import com.sbla.nearby.api.response.DepartureResponse;
import com.sbla.nearby.api.response.SiteResponse;
import com.sbla.wear.shared.CommunicationVariables;
import com.sbla.wear.shared.model.Departure;
import com.sbla.wear.shared.model.Site;
import com.sbla.wear.talkclient.TalkClient;

import java.util.ArrayList;

import rx.functions.Action1;

public class ListenerServiceMobile extends WearableListenerService {

    private TalkClient mTalkClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mTalkClient = new TalkClient(getApplicationContext());
        mTalkClient.connectClient();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        final DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());

        if (messageEvent.getPath().equals(CommunicationVariables.GET_NEARBY)) {
            getNearby(dataMap);
        } else if (messageEvent.getPath().equals(CommunicationVariables.GET_DEPARTURES)) {
            getDepartures(dataMap);
        }

    }

    private void getDepartures(DataMap dataMap) {
        final String siteId = dataMap.getString(CommunicationVariables.DATA_STOP_ID);
        API.getAPIService().listDepartures(Integer.parseInt(siteId), 30).subscribe(new Action1<DepartureResponse>() {
            @Override
            public void call(DepartureResponse departureResponse) {
                DataMap response = new DataMap();
                response.putDataMapArrayList(CommunicationVariables.DATA_DEPARTURES_BUSSES, Departure.toDataMap(departureResponse.getBussesDepartures()));
                mTalkClient.sendMessage(CommunicationVariables.GET_DEPARTURES_RESULTS, response);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("Failed", throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    private void getNearby(DataMap dataMap) {
        final double latitide = dataMap.getDouble(CommunicationVariables.DATA_LATITUDE);
        final double longitude = dataMap.getDouble(CommunicationVariables.DATA_LONGITUDE);
        API.getAPIService().listNearby(latitide, longitude, 0.8d, 20)
                .subscribe(new Action1<SiteResponse>() {
                    @Override
                    public void call(final SiteResponse siteResponse) {
                        Location location = new Location("sbla");
                        location.setLatitude(latitide);
                        location.setLongitude(longitude);

                        ArrayList<DataMap> dataMapsSites = Site.toDataMap(siteResponse.getSortedSites(location));
                        DataMap dataMapOut = new DataMap();
                        dataMapOut.putDataMapArrayList(CommunicationVariables.DATA_NEARBY_LIST, dataMapsSites);
                        mTalkClient.sendMessage(CommunicationVariables.GET_NEARBY_RESULT, dataMapOut);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Failed", throwable.toString());
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onDestroy() {
        mTalkClient.disconnectClient();
        super.onDestroy();
    }
}
