package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.service.UserService;
import com.BaneseLabes.LocalSeguro.repository.SafetyPlaceRepository;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //Get user
    @GetMapping("/get")
    public List<User> getUser(){
        return this.userService.findAll();
    }

    //post
    @PostMapping("/add")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User createdUser = userService.save(user);
        return ResponseEntity.ok().body(createdUser);
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();

    }
    @GetMapping("/permissions{id}")
public ResponseEntity<Map<String, Boolean>> getPermissions(@PathVariable String id) {
    User user = userService.findById(id);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    Map<String, Boolean> permissions = new HashMap<>();
    permissions.put("canMakePix", userService.canMakePixOutSafetyPlace(user));
    permissions.put("canMakeLoan", userService.canMakeLoanOutSafetyPlace(user));
    permissions.put("canMakeBankSplit", userService.canMakeBankSplitOutSafetyPlace(user));
    permissions.put("canMakeTED", userService.canMakeTedOutSafetyPlace(user));

    return ResponseEntity.ok(permissions);
}



}
