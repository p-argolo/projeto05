package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    private  UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //create
    public User save(User user) {
        this.userRepository.save(user);
        return user;
    }

    //read
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    //update
    public Optional<User> updateUser(String id, User  user) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if(existingUser.getName()!=null){
                existingUser.setName(existingUser.getName());
            }
            if(existingUser.getEmail()!=null){
                existingUser.setEmail(existingUser.getEmail());
            }
            User updatedUser = this.userRepository.save(existingUser);
            return Optional.of(updatedUser);
        }
        return existingUserOpt;
    }

    //delete
    public void deleteUser(String id) {
        if(userRepository.existsById(id)){
            this.userRepository.deleteById(id);
        }
        }

    public User findById(String id) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            return existingUserOpt.get();
        }



    return null;}

    public List<SafetyPlace> getSafetyPlaces(User user) {
        return user.getSafetyPlaces();
    }

    public SafetyPlace findSafetyPlaceById(String id, User user) {
    for (SafetyPlace safetyPlace : user.getSafetyPlaces()) {
        if (id.equals(safetyPlace.getId())) {
            return safetyPlace;
        }
    }
    return null;
}

    public boolean canMakePixOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getPix() != 0.0;
    }
    public boolean canMakeLoanOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getLoan() != 0.0;
    }
    public boolean canMakeBankSplitOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getBankSplit() != 0.0;
    }
    public boolean canMakeTedOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getTED() != 0.0;



}}
