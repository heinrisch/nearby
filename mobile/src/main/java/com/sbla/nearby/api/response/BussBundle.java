package com.sbla.nearby.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbla.wear.shared.model.Departure;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BussBundle {
    @JsonProperty("departures")
    List<Departure> departures;
}
