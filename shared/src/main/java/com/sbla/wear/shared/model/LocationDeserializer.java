package com.sbla.wear.shared.model;

import android.location.Location;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class LocationDeserializer extends JsonDeserializer<Location> {

    private static final String TAG = "LocationDeserializer";

    public LocationDeserializer() {
        super();
    }

    @Override
    public Location deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return parseLocation(jp.getText());
    }

    public Location parseLocation(String locationData) {
        String[] longAndLat = locationData.split(",");
        //Defualt to the world center, The Royal Palace
        float latitude = 59.3270053f;
        float longitude = 18.0723166f;
        try {
            latitude = Float.parseFloat(longAndLat[0]);
            longitude = Float.parseFloat(longAndLat[1]);
        } catch (NumberFormatException nfe) {
            Log.e(TAG, nfe.toString());
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Log.e(TAG, aioobe.toString());
        }

        Location location = new Location("sthlmtraveling");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }
}