package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.service.AuthorizationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/get")
    public List<Authorization> getAuthorization() {
        return this.authorizationService.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<Authorization> addAuthorization(@RequestBody Authorization authorization) {
        Authorization createdAuthorization = this.authorizationService.save(authorization);
        return ResponseEntity.ok().body(createdAuthorization);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Authorization> updateAuthorization(@PathVariable String id, @RequestBody Authorization authorization) {
        return authorizationService.updateAuthorization(id, authorization)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Authorization> deleteAuthorization(@PathVariable String id) {
        authorizationService.deleteAuthorization(id);
        return  ResponseEntity.ok().build();
    }

}
