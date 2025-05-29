package com.BaneseLabes.LocalSeguro.model;

import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Setter
@Getter
@Document(collection = "safetyPlace" )
public class SafetyPlace {
    @Id
    private String id;

    private String name;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private ResponseLocation location;
    private Wifi wifi;
    private Authorization authorizationInSafetyPlace;



    public SafetyPlace(){}
}