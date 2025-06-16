package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationService {
    @Autowired
    private UserService userService;

    @Autowired
    private OperationRepository operationRepository;


public Authorization safetyPlaceMatch(LocationDTO locationDTO, User sender) throws Exception {
    SafetyPlace wifiMatch = checkWifi(locationDTO.wifi(), sender);
    if (wifiMatch != null) {
        return wifiMatch.getAuthorizationInSafetyPlace();
    }

    SafetyPlace locationMatch = checkDistanceFromSafePlaces(
        sender,
        locationDTO.location().getLat(),
        locationDTO.location().getLng()
    );
    if (locationMatch != null) {
        return locationMatch.getAuthorizationInSafetyPlace();
    }

    return sender.getAuthorizationOutSafetyPlace();
}

public boolean isInSafetyPlace(LocationDTO locationDTO, User sender) throws Exception {
   return  safetyPlaceMatch(locationDTO, sender) != sender.getAuthorizationOutSafetyPlace();
}



    public static double calculateDistanceBetweenCoordinates(
            double transactionLat, double transactionLng,
            double safetyPlaceLat, double safetyPlaceLng) {
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(safetyPlaceLat - transactionLat);
        double dLng = Math.toRadians(safetyPlaceLng - transactionLng);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(Math.toRadians(transactionLat)) * Math.cos(Math.toRadians(safetyPlaceLat)) *
                   Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public SafetyPlace checkDistanceFromSafePlaces(User sender, double transactionLat, double transactionLng ) {

        List<SafetyPlace> userSafetyPlaces = sender.getSafetyPlaces();
        if (userSafetyPlaces.isEmpty()) {
            return null;
        }

        SafetyPlace closestPlace = null;
        double minDistance = Double.MAX_VALUE;

        for (SafetyPlace safetyPlace : userSafetyPlaces) {
            if (safetyPlace.getLocation() == null){
                continue;
            }
            double distance = calculateDistanceBetweenCoordinates(
                    transactionLat,
                    transactionLng,
                    safetyPlace.getLocation().getLat(),
                    safetyPlace.getLocation().getLng());

            if (distance < minDistance) {
                minDistance = distance;
                closestPlace = safetyPlace;
            }
        }

        if (minDistance <= 0.01) {
            System.out.println("location distance: " + String.format("%.2f", minDistance) + "KM  safetyPlace: " + closestPlace.getName());
            return closestPlace;
        }
        return null;



    }
    public SafetyPlace checkWifi(Wifi wifi, User sender)  {
        for(SafetyPlace safetyPlace : sender.getSafetyPlaces()) {
            if (safetyPlace.getWifi().equals(wifi)) {
                System.out.println("Dentro do local seguro");
                return safetyPlace;
            }
        }
return  null;
    }

}
