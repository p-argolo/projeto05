package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.dto.OperationDTO;
import com.BaneseLabes.LocalSeguro.model.transaction.Operation;
import com.BaneseLabes.LocalSeguro.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Operations")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody Operation operation) {
        try {
            operationService.createTransaction(operation);
            return ResponseEntity.ok("Transação criada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar transação: " + e.getMessage());
        }
    }
}
