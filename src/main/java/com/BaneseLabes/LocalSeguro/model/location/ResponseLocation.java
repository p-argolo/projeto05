package com.BaneseLabes.LocalSeguro.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@Document(collection = "SafePlaces")
public class ResponseLocation {
    @Id
    private String locationId;
    @JsonProperty("results")
    private LocationDetails[] locationDetails;


    /*public LocationDetails getLocationDetails() {
        return locationDetails[0];
    }
    public void setLocationDetails(LocationDetails locationDetails) {
        this.locationDetails = new LocationDetails[1];
        this.locationDetails[0] = locationDetails;
    }*/
}
