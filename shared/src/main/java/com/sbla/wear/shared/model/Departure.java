package com.sbla.wear.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Departure {

    @JsonProperty("line_number")
    public String mLineNumber;

    @JsonProperty("display_time")
    public String mDisplayTime;

    @JsonProperty("destination")
    public String mDestination;

    private final static String DATAMAP_DEPARTURE_LINE_NUMBER = "datamap-departure-line-number";
    private final static String DATAMAP_DEPARTURE_DISPLAY_TIME = "datamap-departure-display-time";
    private final static String DATAMAP_DEPARTURE_DESTINATION = "datamap-departure-mDestination";
    public static Departure fromDataMap(DataMap dataMap){
        Departure departure = new Departure();
        departure.mLineNumber = dataMap.getString(DATAMAP_DEPARTURE_LINE_NUMBER);
        departure.mDisplayTime = dataMap.getString(DATAMAP_DEPARTURE_DISPLAY_TIME);
        departure.mDestination = dataMap.getString(DATAMAP_DEPARTURE_DESTINATION);

        return departure;
    }

    public static DataMap toDataMap(Departure departure){
        DataMap dataMap = new DataMap();
        dataMap.putString(DATAMAP_DEPARTURE_LINE_NUMBER, departure.mLineNumber);
        dataMap.putString(DATAMAP_DEPARTURE_DISPLAY_TIME, departure.mDisplayTime);
        dataMap.putString(DATAMAP_DEPARTURE_DESTINATION, departure.mDestination);

        return dataMap;
    }

    public static ArrayList<DataMap> toDataMap(List<Departure> departures){
        ArrayList<DataMap> list = new ArrayList<>(departures.size());
        for(Departure departure : departures){
            list.add(toDataMap(departure));
        }
        return list;
    }

    public static ArrayList<Departure> fromDataMap(String key, DataMap dataMap){
        ArrayList<DataMap> maps = dataMap.getDataMapArrayList(key);
        ArrayList<Departure> departures = new ArrayList<>(maps.size());
        for(DataMap map : maps){
            departures.add(fromDataMap(map));
        }
        return departures;
    }
}
