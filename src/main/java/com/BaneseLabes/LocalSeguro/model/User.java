package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;




@Data
@Document(collection = "user" )
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    private Double balance;
    private List<SafetyPlace> safetyPlaces;
    private Authorization authorizationOutSafetyPlace;



     public User() {
        this.safetyPlaces = new ArrayList<>();
    }

}
