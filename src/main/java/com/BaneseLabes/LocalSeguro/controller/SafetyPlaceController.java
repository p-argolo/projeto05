package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.service.SafetyPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/safetyPlace")
public class SafetyPlaceController {
    @Autowired
    private SafetyPlaceService safetyPlaceService;

    @GetMapping("/get")
    public List<SafetyPlace> getSafetyPlaces() {
        return this.safetyPlaceService.findAll();
    }

    @GetMapping("/{userId}")
    public List<SafetyPlace> getSafetyPlaces(@PathVariable String userId) {
        return this.safetyPlaceService.findAllByUserId(userId);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<SafetyPlace> saveSafetyPlace(@PathVariable String userId, @RequestBody SafetyPlace safetyPlace) {
        SafetyPlace createdSafetyPlace = safetyPlaceService.save(userId, safetyPlace);
        return ResponseEntity.ok().body(createdSafetyPlace);
    }

    @PatchMapping("/update/{userId}/{id}")
    public ResponseEntity<SafetyPlace> updateSafetyPlace(@PathVariable String id,@PathVariable String userId,   @RequestBody SafetyPlace safetyPlace) {
        return safetyPlaceService.updateSafetyPlace(id,userId, safetyPlace)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SafetyPlace> deleteSafetyPlace(@PathVariable String id, @PathVariable String userId) {
        safetyPlaceService.deleteSafetyPlace(id,userId);
        return ResponseEntity.ok().build();
    }
}
