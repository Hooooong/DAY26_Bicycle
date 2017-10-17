package com.hooooong.bicycle.model;

/**
 * Created by Android Hong on 2017-10-17.
 */

public class Bicycle {

    private GeoInfoBikeConvenientFacilitiesWGS GeoInfoBikeConvenientFacilitiesWGS;

    public GeoInfoBikeConvenientFacilitiesWGS getGeoInfoBikeConvenientFacilitiesWGS() {
        return GeoInfoBikeConvenientFacilitiesWGS;
    }

    public void setGeoInfoBikeConvenientFacilitiesWGS(GeoInfoBikeConvenientFacilitiesWGS GeoInfoBikeConvenientFacilitiesWGS) {
        this.GeoInfoBikeConvenientFacilitiesWGS = GeoInfoBikeConvenientFacilitiesWGS;
    }

    @Override
    public String toString() {
        return "ClassPojo [GeoInfoBikeConvenientFacilitiesWGS = " + GeoInfoBikeConvenientFacilitiesWGS + "]";
    }
}