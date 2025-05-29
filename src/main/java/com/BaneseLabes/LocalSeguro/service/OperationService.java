package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.dto.OperationDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.model.location.Location;
import com.BaneseLabes.LocalSeguro.model.transaction.Operation;
import com.BaneseLabes.LocalSeguro.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationService {
    @Autowired
    private UserService userService;

    @Autowired
    private OperationRepository operationRepository;

    public Operation createTransaction(Operation operation) throws Exception {


        User sender = userService.findById(operation.getSenderId());
        User receiver = userService.findById(operation.getReceiverId());

        if (sender == null) throw new Exception("Sender not found");
        if (receiver == null) throw new Exception("Receiver not found");



        operation.setTimestamp(LocalDateTime.now());

        // Validação inicial
        if (!validateTransactionLocation(operation)) {
            throw new Exception("Transaction location not valid");
        }

        boolean isAuthorizedInSafety = validateAuthInSafetyPlace(operation, checkDistanceFromSafePlaces(operation));
        boolean isAuthorizedOutSafety = validateAuthOutSafetyPlace(operation);

        if (!isAuthorizedInSafety && !isAuthorizedOutSafety) {
            throw new Exception("Transaction not authorized");
        }

        if (!checkBalance(operation)) {
            throw new Exception("Insufficient balance");
        }

        // Atualização do saldo
        if (isAuthorizedInSafety) {
            sender.setBalance(sender.getBalance() - operation.getAmount());
            receiver.setBalance(receiver.getBalance() + operation.getAmount());
        } else if (isAuthorizedOutSafety) {
            sender.setBalance(sender.getBalance() + operation.getAmount());
            receiver.setBalance(receiver.getBalance() - operation.getAmount());
        }

        // Persistência no banco de dados
        operationRepository.save(operation);
        userService.save(sender);
        userService.save(receiver);

        return operation;
    }

   public boolean validateAuthInSafetyPlace(Operation operation, SafetyPlace safetyPlace) throws Exception {
    if (operation == null || operation.getOperationType() == null) {
        throw new Exception("Transação ou tipo de transação inválido!");
    }

    Authorization authorization = safetyPlace.getAuthorizationInSafetyPlace();
    if (authorization == null) {
        throw new RuntimeException("No authorization on this safetyPlace!");
    }

    boolean isAuthorized = switch (operation.getOperationType()) {
        case LOAN -> authorization.getLoan() >= operation.getAmount();
        case TED -> authorization.getTED() >= operation.getAmount();
        case PIX -> authorization.getPix() >= operation.getAmount();
        case BANK_SPLIT -> authorization.getBankSplit() >= operation.getAmount();
        case REGISTER_VIRTUAL_CARD -> authorization.canRegisterVirtualCard();
        case CHANGE_PASSWORD -> authorization.canChangePassword();
        default -> throw new RuntimeException("Not authorized operation");
    };

    if (!isAuthorized) {
        throw new RuntimeException("Not authorized operation");
    }

    return true;
}


    public boolean validateAuthOutSafetyPlace(Operation operation) throws RuntimeException {
    if (operation == null || operation.getOperationType() == null) {
        throw new RuntimeException("Invalid type of operation");
    }
    User user = userService.findById(operation.getSenderId());
    Authorization authorization = user.getAuthorizationOutSafetyPlace();

    if (authorization == null) {
        throw new RuntimeException("Authorization not found");
    }

    boolean isAuthorized = switch (operation.getOperationType()) {
        case BANK_SPLIT -> authorization.getBankSplit() >= operation.getAmount();
        case PIX -> authorization.getPix() >= operation.getAmount();
        case LOAN -> authorization.getLoan() >= operation.getAmount();
        case TED -> authorization.getTED() >= operation.getAmount();
        case REGISTER_VIRTUAL_CARD -> authorization.canRegisterVirtualCard();
        case CHANGE_PASSWORD -> authorization.canChangePassword();
        default -> throw new RuntimeException("Not authorized operation");
    };
    if (!isAuthorized) {
        throw new RuntimeException("Not authorized operation");
    }

    return true;
}


    public boolean validateTransactionLocation(Operation operation) {
        return checkDistanceFromSafePlaces(operation) != null;
    }

    public boolean checkBalance(Operation operation) {
        User sender = userService.findById(operation.getSenderId());
        return sender.getBalance() >= operation.getAmount();
    }

    public static double calculateDistanceBetweenCoordinates(
            double transactionLat, double transactionLng,
            double safetyPlaceLat, double safetyPlaceLng) {
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(safetyPlaceLat - transactionLat);
        double dLng = Math.toRadians(safetyPlaceLng - transactionLng);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.cos(Math.toRadians(transactionLat)) * Math.cos(Math.toRadians(safetyPlaceLat)) *
                   Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public SafetyPlace checkDistanceFromSafePlaces(Operation operation) {
        User sender = userService.findById(operation.getSenderId());
        double transactionLat = operation.getLocation().getLat();
        double transactionLng = operation.getLocation().getLng();

        List<SafetyPlace> userSafetyPlaces = sender.getSafetyPlaces();
        if (userSafetyPlaces.isEmpty()) {
            return null;
        }

        SafetyPlace closestPlace = null;
        double minDistance = Double.MAX_VALUE;

        for (SafetyPlace safetyPlace : userSafetyPlaces) {
            double distance = calculateDistanceBetweenCoordinates(
                    transactionLat,
                    transactionLng,
                    safetyPlace.getLocation().getLocationDetails()[0].getGeometry().getLocation().getLat(),
                    safetyPlace.getLocation().getLocationDetails()[0].getGeometry().getLocation().getLng());

            if (distance < minDistance) {
                minDistance = distance;
                closestPlace = safetyPlace;
            }
        }

        if (minDistance <= 0.01) {
            System.out.println("location distance: " + String.format("%.2f", minDistance) + "KM  safetyPlace: " + closestPlace.getName());
            return closestPlace;
        }
        throw new RuntimeException("Fora do local seguro");
    }
}
