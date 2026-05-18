package net.javaguides.banking.dto;

public record TransferFundDto(Long debitAccountId, Long creditAccountId, double amount) {
}
