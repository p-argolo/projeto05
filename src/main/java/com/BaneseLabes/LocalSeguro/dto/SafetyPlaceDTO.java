package com.BaneseLabes.LocalSeguro.dto;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.wifi.Wifi;
import com.BaneseLabes.LocalSeguro.model.location.Location;

public record SafetyPlaceDTO(Wifi wifi, Location location, Authorization authorizationInSafetyPlace) {
}
