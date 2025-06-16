package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.model.transaction.Operation;
import com.BaneseLabes.LocalSeguro.service.OperationService;
import com.BaneseLabes.LocalSeguro.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Operations")
public class OperationController {

    @Autowired
    private OperationService operationService;
    @Autowired
    private UserService userService;
    private final JwtUtil  jwtUtil;

    public OperationController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/SafetyPlaceMatch")
    public ResponseEntity<Authorization> SafetyPlaceMatch(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody LocationDTO locationDTO) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("cnpj").toString();

        try {
            User user = userService.findById(cnpj, userSubject);
            Authorization match = operationService.safetyPlaceMatch(locationDTO, user);

            if (match != null) {
                return ResponseEntity.ok(match);
            } else {
                return ResponseEntity.ok(user.getAuthorizationOutSafetyPlace());
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
}

}
