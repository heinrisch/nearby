package com.sbla.nearby.api.response;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbla.wear.shared.model.Site;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteResponse {

    @JsonProperty("sites")
    public List<Site> mSites;

    public SiteResponse(){}

    public List<Site> getSortedSites(final Location location) {
        Collections.sort(mSites, new Comparator<Site>() {
            @Override
            public int compare(Site site1, Site site2) {
                float distanceToSite1 = location.distanceTo(site1.mLocation);
                float distanceToSite2 = location.distanceTo(site2.mLocation);
                if (distanceToSite1 > distanceToSite2) {
                    return 1;
                } else if (distanceToSite1 < distanceToSite2) {
                    return -1;
                }
                return 0;
            }
        });
        return mSites;
    }
}
