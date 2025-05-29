package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.repository.AuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private AuthorizationRepository authorizationRepository;

    private AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    //create
    public Authorization save(Authorization authorization) {
        this.authorizationRepository.save(authorization);
        return authorization;
    }

    //read
    public List<Authorization> findAll() {
        return this.authorizationRepository.findAll();
    }

    //update
    public Optional<Authorization> updateAuthorization(String Id,Authorization authorization) {
        Optional<Authorization> authorizationOpt = authorizationRepository.findById(Id);
        if (authorizationOpt.isPresent()) {
            Authorization existingAuthorization = authorizationOpt.get();
            Authorization updatedAuthorization = this.authorizationRepository.save(existingAuthorization);
            return Optional.of(updatedAuthorization);
        }
        return authorizationOpt;
    }

    //delete
    public void deleteAuthorization(String Id) {
        if (this.authorizationRepository.existsById(Id)){
            this.authorizationRepository.deleteById(Id);
        }
    }
}
