package com.BaneseLabes.LocalSeguro.dto;

import java.time.LocalDateTime;

public record OperationDTO(Double value, String senderId, String receiverId, LocalDateTime timestamp, Double lat, Double lng) {
}
