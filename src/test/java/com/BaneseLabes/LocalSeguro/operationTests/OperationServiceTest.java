package com.BaneseLabes.LocalSeguro.operationTests;

import com.BaneseLabes.LocalSeguro.controller.GeocodeController;
import com.BaneseLabes.LocalSeguro.dto.OperationDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.model.location.Location;
import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import com.BaneseLabes.LocalSeguro.model.transaction.Operation;
import com.BaneseLabes.LocalSeguro.model.transaction.OperationType;
import com.BaneseLabes.LocalSeguro.repository.OperationRepository;
import com.BaneseLabes.LocalSeguro.service.SafetyPlaceService;
import com.BaneseLabes.LocalSeguro.service.OperationService;
import com.BaneseLabes.LocalSeguro.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @InjectMocks
    private OperationService operationService;

    @Mock
    private UserService userService;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private SafetyPlaceService safetyPlaceService;

    private Operation operation;
    private User sender;
    private User receiver;
    private SafetyPlace home;
    private SafetyPlace work;

    @Autowired
    private GeocodeController geocodeController;

    @BeforeEach
    void setUp() {
        // Criar transação de teste
        operation = new Operation();
        operation.setSenderId("123");
        operation.setReceiverId("456");
        operation.setTimestamp(LocalDateTime.now());

        // Criar usuario remetente com saldo suficiente
        sender = new User();
        sender.setId("123");
        sender.setBalance(500.00);
        Authorization senderAuth = new Authorization();
        receiver = new User();
        receiver.setId("456");
        receiver.setBalance(500.00);
        userService.save(receiver);

        // Criar local seguro
        home = new SafetyPlace();
        ResponseLocation locationHome = geocodeController.getGeoDetails("Rua + Wilton + Melo, 26");
        home.setLocation(locationHome);
        home.setName("Casa");
        sender.getSafetyPlaces().add(home);
        work = new SafetyPlace();
        ResponseLocation locationWork = geocodeController.getGeoDetails("Universidade + Tiradentes");
        work.setLocation(locationWork);
        work.setName("Trabalho");
        sender.getSafetyPlaces().add(work);



    }

    @Test
    void testCreateOperation_Success() throws Exception {
        // Simular que o usuario tem saldo suficiente
        when(userService.findById("123")).thenReturn(sender);
        when(userService.findById("456")).thenReturn(receiver);


        // Simular DTO
        Operation operation = new Operation(100.00, "123", "197199","456", new Location(-10.9265359, -37.0659983),LocalDateTime.now(), OperationType.PIX);

        operation = operationService.createTransaction(operation);

        // Verificar se a transação foi salva
        verify(operationRepository, times(1)).save(any(Operation.class));
    }
    @Test
void testCreateOperation_Failure() throws Exception {
    // Simular que o usuario não tem saldo suficiente
    sender.setBalance(50.00); // Saldo menor que o valor da transação

    when(userService.findById("123")).thenReturn(sender);
    when(userService.findById("456")).thenReturn(receiver);

        Operation operation = new Operation(50.00, "123", "197199","456", new Location(-10.9265359, -37.0659983),LocalDateTime.now(), OperationType.PIX);

    // Verificar se a exceção é lançada
   Exception exception = assertThrows(Exception.class, () -> {
    operationService.createTransaction(operation);
});

assertEquals("Could not make the operation", exception.getMessage());

    // Verificar que a transação **não** foi salva
    verify(operationRepository, never()).save(any(Operation.class));
}

    @Test
    void testValidateOperation_Balance_Success() throws Exception {
        when(userService.findById("123")).thenReturn(sender);
        when(userService.findById("456")).thenReturn(receiver);

        // Chamar o método
        boolean isValid = operationService.checkBalance(operation);

        // Verificar o resultado
        assertTrue(isValid);
    }

    @Test
    void testValidateOperation_Location() throws Exception {
        sender.setBalance(50.00);
        operation.setLocation(new Location(-10.9447321, -37.0475752));
        boolean isValid = operationService.validateTransactionLocation(operation);

        assertFalse(isValid);
    }

    @Test
void testCheckDistanceFromSafePlaces_Success() {
    // Criar uma transação com localização dentro do local seguro
    Location transactionLocation = new Location(-10.9689115, -37.0580716);
    operation.setLocation(transactionLocation);
    System.out.println("Lat: " + operation.getLocation().getLat() + ", Lng: " + operation.getLocation().getLng());
    operation.setAmount(50.00);
    // Simular que o usuario possui locais seguros
    sender.setSafetyPlaces(List.of(home, work));

    when(userService.findById("123")).thenReturn(sender);

    SafetyPlace result = operationService.checkDistanceFromSafePlaces(operation);
    System.out.println(result.getName());

    // Verificar se retornou o local seguro correto
    assertNotNull(result);
}


    @Test
void testCheckDistanceFromSafePlaces_Failure() {
    // Criar uma transação com localização muito distante
    operation.setLocation(new Location(-10.9447321, -37.0475752));
    operation.setAmount(50.00);
    // Simular que o usuario possui uma lista de locais seguros
    sender.setSafetyPlaces(List.of(home));

    when(userService.findById("123")).thenReturn(sender);

    // Testar exceção ao estar fora da área segura
    Exception exception = assertThrows(RuntimeException.class, () -> {
        operationService.checkDistanceFromSafePlaces(operation);
    });

    assertEquals("Fora do local seguro", exception.getMessage());
}

    @Test
    void validateAuthInSafetyPlaceSucess() throws Exception {
        operation.setOperationType(OperationType.PIX);
        Authorization safetyPlaceAuthorization = new Authorization();
        operation.setAmount(50.00);
        safetyPlaceAuthorization.setPix(200);
        home.setAuthorizationInSafetyPlace(safetyPlaceAuthorization);
        boolean isValid = operationService.validateAuthInSafetyPlace(operation,home);


        assertTrue(isValid);
    }

    @Test
    void validateAuthInSafetyPlaceFailure() throws RuntimeException {
        operation.setOperationType(OperationType.PIX);
        Authorization safetyPlaceAuthorization = new Authorization();
        operation.setAmount(200.00);
        safetyPlaceAuthorization.setPix(50.00);
        home.setAuthorizationInSafetyPlace(safetyPlaceAuthorization);

        // Testar exceção ao estar fora da área segura
    Exception exception = assertThrows(RuntimeException.class, () -> {
        operationService.validateAuthInSafetyPlace(operation,home);
    });


    assertEquals("Not authorized operation", exception.getMessage());
        System.out.println(exception.getMessage());

}


    @Test
    void validateAuthOutSafetyPlaceSucess() throws Exception {
        operation.setOperationType(OperationType.PIX);
        operation.setAmount(150.00);
        Authorization safetyPlaceAuthorization = new Authorization();
        safetyPlaceAuthorization.setPix(200);
        when(userService.findById("123")).thenReturn(sender);
        sender.setAuthorizationOutSafetyPlace(safetyPlaceAuthorization);
        boolean isValid = operationService.validateAuthOutSafetyPlace(operation);
        assertTrue(isValid);


    }
    @Test
    void validateAuthOutSafetyPlaceFailure() throws Exception {
        operation.setOperationType(OperationType.PIX);
        Authorization outSafetyPlaceAuthorization = new Authorization();
        outSafetyPlaceAuthorization.setPix(50.00);
        operation.setAmount(200.00);
        when(userService.findById("123")).thenReturn(sender);
        sender.setAuthorizationOutSafetyPlace(outSafetyPlaceAuthorization);
        Exception exception = assertThrows(RuntimeException.class, () -> {
        operationService.validateAuthOutSafetyPlace(operation);
    });


    assertEquals("Not authorized operation", exception.getMessage());
        System.out.println(exception.getMessage());

        }
    }

