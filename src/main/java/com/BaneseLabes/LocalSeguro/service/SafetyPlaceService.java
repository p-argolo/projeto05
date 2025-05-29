package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.repository.AuthorizationRepository;
import com.BaneseLabes.LocalSeguro.repository.UserRepository;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.repository.SafetyPlaceRepository;
import com.BaneseLabes.LocalSeguro.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SafetyPlaceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SafetyPlaceRepository safetyPlaceRepository;

    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private UserService userService;

    private  SafetyPlaceService(SafetyPlaceRepository safetyPlaceRepository){
        this.safetyPlaceRepository = safetyPlaceRepository;
    }

    //create
    public SafetyPlace save(String userId, SafetyPlace safetyPlace) {
        Optional<User> userOptional = Optional.ofNullable(userService.findById(userId));
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.getSafetyPlaces().add(safetyPlace);
            userService.save(user);
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
    public List<SafetyPlace> findAllByUserId(String userId) {
        User user = userService.findById(userId);
        return user.getSafetyPlaces();
    }
    public User findUserOfSafetyPlace(String id){
        List<User> users = userService.findAll();
        for(User user : users){
            userService.findSafetyPlaceById(id, user);
            return user;
        }
        return null;
    }
    //update
    public Optional<SafetyPlace> updateSafetyPlace(String id,String userId, SafetyPlace safetyPlace) {
        Optional<SafetyPlace> existingSafetyPlaceOpt = safetyPlaceRepository.findById(id);
        User user = userService.findById(userId);


        if (existingSafetyPlaceOpt.isPresent()) {
            SafetyPlace existingSafetyPlace = existingSafetyPlaceOpt.get();
            if (safetyPlace.getName() != null) {
                existingSafetyPlace.setName(safetyPlace.getName());
            }
            if (safetyPlace.getLocation() != null) {
                existingSafetyPlace.setLocation(safetyPlace.getLocation());
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
                   userService.save(user);
                   break;
               }
           }
            return  Optional.of(updatedSafetyPlace);
        }
        return existingSafetyPlaceOpt;
    }

    //delete
    public void deleteSafetyPlace(String id, String userId) {
        if (safetyPlaceRepository.existsById(id)){
            this.safetyPlaceRepository.deleteById(id);
            User user = userService.findById(userId);
            user.getSafetyPlaces().remove(safetyPlaceRepository.findById(id).get());
            userService.save(user);

        }
    }
}