package com.BaneseLabes.LocalSeguro.model.Wifi;
import lombok.*;

@Data
public class Wifi {
    private String ssid;
    private String bssid;
    private WifiSecurity security;

}
