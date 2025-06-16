package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.repository.AuthorizationRepository;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.repository.SafetyPlaceRepository;
import com.BaneseLabes.LocalSeguro.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SafetyPlaceService {


    @Autowired
    private SafetyPlaceRepository safetyPlaceRepository;

    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ClientService clientService;

    private  SafetyPlaceService(SafetyPlaceRepository safetyPlaceRepository){
        this.safetyPlaceRepository = safetyPlaceRepository;
    }

    //create
    public SafetyPlace save(String userId, SafetyPlace safetyPlace, String cnpj) throws Exception {
        if (safetyPlace.getId() == null || safetyPlace.getId().isEmpty()) {
            safetyPlace.setId(new ObjectId().toString()); // gera um novo ObjectId como String
        }
        String collectionName = userService.resolveCollection(cnpj);
        Optional<User> userOptional = userService.findByClientId(cnpj,userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.getSafetyPlaces().add(safetyPlace);
            userService.save(user,cnpj);
            return safetyPlaceRepository.save(safetyPlace);

        } else {
            throw new RuntimeException("User not found");
        }
    }

    //read all
    public List<SafetyPlace> findAll() {
        return this.safetyPlaceRepository.findAll();
    }

    //read all by id
    public List<SafetyPlace> findAllByUserId(String userId, String cnpj) throws Exception {
        String collectionName = userService.resolveCollection(cnpj);
        User user = userService.findById(cnpj,userId);
        return user.getSafetyPlaces();
    }
    public User findUserOfSafetyPlace(String clientId, String cnpj) throws Exception {
        List<User> users = userService.findAll(cnpj);
        for(User user : users){
            userService.findSafetyPlaceById(clientId, user);
            return user;
        }
        return null;
    }
    //update
    public Optional<SafetyPlace> updateSafetyPlace(String cnpj,String id,String clientId, SafetyPlace safetyPlace) throws Exception {
        Optional<SafetyPlace> existingSafetyPlaceOpt = safetyPlaceRepository.findById(id);
        User user = userService.findById(cnpj,clientId);
        if (existingSafetyPlaceOpt.isPresent()) {
            SafetyPlace existingSafetyPlace = existingSafetyPlaceOpt.get();
            if (safetyPlace.getName() != null) {
                existingSafetyPlace.setName(safetyPlace.getName());
            }
            if (safetyPlace.getAddress() != null) {
                existingSafetyPlace.setAddress(safetyPlace.getAddress());
            }
            if (safetyPlace.getWifi() != null) {
                existingSafetyPlace.setWifi(safetyPlace.getWifi());
            }
            if (safetyPlace.getDataInicio() != null) {
                existingSafetyPlace.setDataInicio(safetyPlace.getDataInicio());
            }
            if (safetyPlace.getDataFim() != null) {
                existingSafetyPlace.setDataFim(safetyPlace.getDataFim());
            }
            if (safetyPlace.getAuthorizationInSafetyPlace() != null) {
                existingSafetyPlace.setAuthorizationInSafetyPlace(safetyPlace.getAuthorizationInSafetyPlace());
            }
            SafetyPlace updatedSafetyPlace = this.safetyPlaceRepository.save(existingSafetyPlace);
            List<SafetyPlace> userSafetyPlaces = user.getSafetyPlaces();

           for (int i = 0; i < userSafetyPlaces.size(); i++) {
               if(id.equals(userSafetyPlaces.get(i).getId())){
                   userSafetyPlaces.set(i, existingSafetyPlace);
                   userService.save(user,cnpj);
                   break;
               }
           }
            return  Optional.of(updatedSafetyPlace);
        }
        return existingSafetyPlaceOpt;
    }

    //delete
    public void deleteSafetyPlace(String cnpj,String id, String clientId) throws Exception {
        Optional<SafetyPlace> safetyPlaceOpt = safetyPlaceRepository.findById(id);
        User user = userService.findById(cnpj,clientId);
        if (safetyPlaceOpt.isPresent()) {
            safetyPlaceRepository.deleteById(id);
            List<SafetyPlace> safetyPlaces = user.getSafetyPlaces();
            safetyPlaces.removeIf(sp -> id.equals(sp.getId()));
            user.setSafetyPlaces(safetyPlaces);
            userService.save(user, cnpj);
        }
    }

}