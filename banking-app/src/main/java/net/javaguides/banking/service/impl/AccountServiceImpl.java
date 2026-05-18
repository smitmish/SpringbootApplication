package net.javaguides.banking.service.impl;

import net.javaguides.banking.dto.AccountDto;
import net.javaguides.banking.dto.TransactionDto;
import net.javaguides.banking.dto.TransferFundDto;
import net.javaguides.banking.entity.Account;
import net.javaguides.banking.entity.Transaction;
import net.javaguides.banking.exception.AccountException;
import net.javaguides.banking.mapper.AccountMapper;
import net.javaguides.banking.repository.AccountRepository;
import net.javaguides.banking.repository.TransactionRepository;
import net.javaguides.banking.service.AccountService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

    public AccountServiceImpl(AccountRepository accountRepository,  TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.maptoAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.maptoAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).
                orElseThrow(()->new AccountException("Account doest not exist: "+id));
        return AccountMapper.maptoAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new AccountException("Account doest not exist: "+id));
        account.setBalance(account.getBalance()+amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.maptoAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdrawal(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new AccountException("Account doest not exist: "+id));
        account.setBalance(account.getBalance()-amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.maptoAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.maptoAccountDto(account))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new AccountException("Account doest not exist: "+id));
        accountRepository.delete(account);
    }

    @Override
    public void transferFunds(TransferFundDto  transferFundDto) {
        //Retrieve the debit account
        Account debitAccount = accountRepository.findById(transferFundDto.debitAccountId())
                .orElseThrow(()-> new AccountException("Debit account does not exist: "+transferFundDto.debitAccountId()));
        //Retrieve the credit account
        Account creditAccount = accountRepository.findById(transferFundDto.creditAccountId())
                .orElseThrow(()-> new AccountException("Credit account does not exist: "+transferFundDto.debitAccountId()));
        if(debitAccount.getBalance() < transferFundDto.amount())
            throw new AccountException("Insufficient amount.");
        //Debit the amount
        debitAccount.setBalance(debitAccount.getBalance()-transferFundDto.amount());
        //Credit the amount
        creditAccount.setBalance(creditAccount.getBalance()+transferFundDto.amount());
        accountRepository.save(debitAccount);
        accountRepository.save(creditAccount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(transferFundDto.debitAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
        return transactions.stream()
                .map(t->convertToTransactionDto(t))
                .collect(Collectors.toList());
    }

    private TransactionDto convertToTransactionDto(Transaction transaction) {
        return new TransactionDto(transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp());
    }
}
