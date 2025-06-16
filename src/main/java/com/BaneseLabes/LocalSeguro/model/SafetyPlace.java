package com.BaneseLabes.LocalSeguro.model;

import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.model.location.Address;
import com.BaneseLabes.LocalSeguro.model.location.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Document(collection = "safetyPlace" )
public class SafetyPlace {
    @Id
    private String id ;

    private String name;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Location location;
    private Address address;
    private Wifi wifi;
    private Authorization authorizationInSafetyPlace;



    public SafetyPlace(){}
}