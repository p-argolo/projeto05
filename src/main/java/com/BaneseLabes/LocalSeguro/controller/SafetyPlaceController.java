package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.service.SafetyPlaceService;
import com.BaneseLabes.LocalSeguro.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/safetyPlace")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SafetyPlaceController {
    @Autowired
    private SafetyPlaceService safetyPlaceService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @GetMapping("/get")
    public List<SafetyPlace> getSafetyPlaces() {
        return this.safetyPlaceService.findAll();
    }

    @GetMapping("/get-all")
    public List<SafetyPlace> getUserSafetyPlaces(@RequestHeader ("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return this.safetyPlaceService.findAllByUserId(userSubject,cnpj);
    }


    @PostMapping("/add")
    public ResponseEntity<SafetyPlace> saveSafetyPlace(@RequestHeader ("Authorization") String authHeader, @RequestBody SafetyPlace safetyPlace) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();

        SafetyPlace createdSafetyPlace = safetyPlaceService.save(userSubject, safetyPlace,cnpj);
        return ResponseEntity.ok().body(createdSafetyPlace);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<SafetyPlace> updateSafetyPlace(@PathVariable String id,@RequestHeader ("Authorization") String authHeader, @RequestBody SafetyPlace safetyPlace) throws Exception {
         String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return safetyPlaceService.updateSafetyPlace(cnpj,id,userSubject, safetyPlace)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SafetyPlace> deleteSafetyPlace(@PathVariable String id,@RequestHeader ("Authorization") String authHeader) throws Exception {
         String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        safetyPlaceService.deleteSafetyPlace(cnpj,id,userSubject);
        return ResponseEntity.ok().build();
    }
}
