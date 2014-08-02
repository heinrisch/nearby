package com.sbla.nearby.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbla.wear.shared.model.Departure;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartureResponse {

    @JsonProperty("buses")
    public List<BussBundle> mBusses;

    public DepartureResponse(){}

    public List<Departure> getBussesDepartures(){
        ArrayList<Departure> departures = new ArrayList();

        if(mBusses == null){
            mBusses = new ArrayList<BussBundle>(0);
        }

        for(BussBundle bundle : mBusses){
            departures.addAll(bundle.departures);
        }

        return departures;
    }
}
