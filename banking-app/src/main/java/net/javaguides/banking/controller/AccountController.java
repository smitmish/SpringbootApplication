package net.javaguides.banking.controller;

import net.javaguides.banking.dto.AccountDto;
import net.javaguides.banking.dto.TransactionDto;
import net.javaguides.banking.dto.TransferFundDto;
import net.javaguides.banking.entity.Account;
import net.javaguides.banking.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //Add Account REST API
    @PostMapping("/addAccount")
    public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto) {
        return new ResponseEntity<>(accountService.createAccount(accountDto),HttpStatus.CREATED);
    }

    //Get Account REST API
    @GetMapping("/getAccount/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.getAccountById(id),HttpStatus.OK);
    }

    //Deposit amount REST API
    @PutMapping("deposit/{id}")
    public ResponseEntity<AccountDto> deposit(@PathVariable Long id, @RequestBody Map<String,Double> request) {
        AccountDto accountDto = accountService.deposit(id,request.get("amount"));
        return new ResponseEntity<>(accountDto,HttpStatus.OK);
    }

    //Withdraw amount REST API
    @PutMapping("withdraw/{id}")
    public ResponseEntity<AccountDto> withdraw(@PathVariable Long id, @RequestBody Map<String,Double> request) {
        AccountDto accountDto = accountService.withdrawal(id,request.get("amount"));
        return new ResponseEntity<>(accountDto,HttpStatus.OK);
    }

    //Get All accounts REST API
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(){
        return new ResponseEntity<>(accountService.getAllAccounts(),HttpStatus.OK);
    }

    //Delete Account REST API
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id){
        accountService.deleteAccount(id);
        return new ResponseEntity<>("Account has been deleted successfully",HttpStatus.OK);
    }

    //Transfer amount REST API
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFund(@RequestBody TransferFundDto transferFundDto){
        accountService.transferFunds(transferFundDto);
        return new ResponseEntity<>("Amount has been transferred successfully",HttpStatus.OK);
    }

    //Get Transaction history REST API
    @GetMapping("/transactions/{id}")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable Long id){
        List<TransactionDto> transactions = accountService.getAccountTransactions(id);
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }
}
