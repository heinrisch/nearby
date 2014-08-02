package com.sbla.wear.shared.model;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Site {

    @JsonProperty("site_id")
    public String mId;

    @JsonProperty("name")
    public String mName;

    @JsonDeserialize(using = LocationDeserializer.class)
    @JsonProperty("location")
    public Location mLocation;

    public Site(){}

    private final static String DATAMAP_SITE_ID = "datamap-site-id";
    private final static String DATAMAP_SITE_NAME = "datamap-site-name";
    private final static String DATAMAP_SITE_LATITUDE = "datamap-site-latitude";
    private final static String DATAMAP_SITE_LONGITUDE = "datamap-site-longitude";
    public static Site fromDataMap(DataMap dataMap){
        Site site = new Site();
        site.mId = dataMap.getString(DATAMAP_SITE_ID);
        site.mName = dataMap.getString(DATAMAP_SITE_NAME);
        double latitude = dataMap.getDouble(DATAMAP_SITE_LATITUDE);
        double longitude = dataMap.getDouble(DATAMAP_SITE_LONGITUDE);
        site.mLocation = new Location("sbla");
        site.mLocation.setLatitude(latitude);
        site.mLocation.setLongitude(longitude);

        return site;
    }

    public static DataMap toDataMap(Site site){
        DataMap dataMap = new DataMap();
        dataMap.putString(DATAMAP_SITE_ID, site.mId);
        dataMap.putString(DATAMAP_SITE_NAME, site.mName);
        dataMap.putDouble(DATAMAP_SITE_LATITUDE, site.mLocation.getLatitude());
        dataMap.putDouble(DATAMAP_SITE_LONGITUDE, site.mLocation.getLongitude());

        return dataMap;
    }

    public static ArrayList<DataMap> toDataMap(List<Site> sites){
        ArrayList<DataMap> list = new ArrayList<>(sites.size());
        for(Site site : sites){
            list.add(toDataMap(site));
        }
        return list;
    }

    public static ArrayList<Site> fromDataMap(String key, DataMap dataMap){
        ArrayList<DataMap> maps = dataMap.getDataMapArrayList(key);
        ArrayList<Site> sites = new ArrayList<>(maps.size());
        for(DataMap map : maps){
            sites.add(fromDataMap(map));
        }
        return sites;
    }
}
