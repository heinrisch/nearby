package com.sbla.nearby.api;


import com.sbla.nearby.api.response.DepartureResponse;
import com.sbla.nearby.api.response.SiteResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface APIService {
    @GET("/semistatic/site/near/")
    Observable<SiteResponse> listNearby(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("max_distance") Double maxDistance, @Query("max_results") int maxResults);

    @GET("/v1/departures/{site_id}")
    Observable<DepartureResponse> listDepartures(@Path("site_id") int siteId, @Query("timewindow") int timeWindow);
}
