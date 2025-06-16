package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.dto.SafetyPlaceDTO;
import com.BaneseLabes.LocalSeguro.dto.MetadataInfoDTO;
import com.BaneseLabes.LocalSeguro.model.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MetadataService {
    @Autowired
    private OperationService operationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwe.secret}")
    private String jweKey;

    @Value("${jws.secret}")
    private String jwsKey;

    @Autowired
    private UserService userService;

    public String createResponse(String token, LocationDTO locationDTO) throws Exception {
        Claims claims = jwtUtil.extractClaims(token);

        String clientId = claims.getSubject();

        Object cnpjObj = claims.get("CNPJ");
        if (!(cnpjObj instanceof String cnpj)) {
            throw new RuntimeException("Claim 'CNPJ' inválida ou ausente: " + cnpjObj);
        }

        User user = userService.findByClientId(cnpj, clientId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + clientId));

        if (user.getSafetyPlaces().isEmpty()) {
            throw new RuntimeException("Usuário não possui locais seguros cadastrados");
        }

        MetadataInfoDTO metadata = new MetadataInfoDTO(
                operationService.isInSafetyPlace(locationDTO, user),
                operationService.safetyPlaceMatch(locationDTO, user),
                userService.canMakePixOutSafetyPlace(user),
                userService.canMakeLoanOutSafetyPlace(user),
                userService.canMakeBankSplitOutSafetyPlace(user),
                userService.canMakeTedOutSafetyPlace(user)
        );

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()

                .issueTime(new Date())
                .claim("In Safety Place", metadata.InSafetyPlace())
                .claim("Can make Pix", metadata.canMakePix())
                .claim("Can make loan", metadata.canMakeLoan())
                .claim("Can make bank split", metadata.canMakeBankSplit())
                .claim("Can make TED", metadata.canMakeTed())
                .claim("Can register virtual card", metadata.authorization().canRegisterVirtualCard())
                .claim("Can change password", metadata.authorization().canChangePassword())
                .claim("Does pix have a limit", metadata.authorization().pixHasLimit())
                .claim("Does loan have a limit", metadata.authorization().loanHasLimit())
                .claim("Does bank split have a limit", metadata.authorization().bankSplitHasLimit())
                .claim("Does ted have a limit", metadata.authorization().tedHasLimit())
                .claim("Pix limit", metadata.authorization().getPix())
                .claim("Loan limit", metadata.authorization().getLoan())
                .claim("Bank split limit", metadata.authorization().getBanksplit())
                .claim("Ted limit", metadata.authorization().getTed())
                .build();

        JWSSigner signer = new MACSigner(jwsKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signedJWT.sign(signer);

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                        .contentType("JWT")
                        .build(),
                new Payload(signedJWT)
        );

        byte[] chaveSecreta = jweKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (chaveSecreta.length != 32) {
            throw new IllegalArgumentException("Chave JWE deve ter 32 bytes (256 bits). Tamanho atual: " + chaveSecreta.length);
        }

        jweObject.encrypt(new DirectEncrypter(chaveSecreta));

        String tokenJwe = jweObject.serialize();

        // Verifica se tem exatamente 5 partes
        String[] parts = tokenJwe.split("\\.");
        if (parts.length != 5) {
            throw new RuntimeException("Token JWE inválido: esperado 5 partes, encontrado " + parts.length);
        }

        return tokenJwe;
    }
}
