package com.BaneseLabes.LocalSeguro.model.wifi;
import lombok.*;

@Data
public class Wifi {
    private String ssid;
    private String bssid;
    private WifiSecurity security;

}
