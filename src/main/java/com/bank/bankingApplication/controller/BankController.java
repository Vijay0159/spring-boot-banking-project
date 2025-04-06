package com.bank.bankingApplication.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.bank.bankingApplication.model.Account;
import com.bank.bankingApplication.repository.AccountRepository;
import com.bank.bankingApplication.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bank")
@Tag(name = "Bank Controller", description = "Handles operations related to bank accounts")
public class BankController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Operation(
            summary = "Create a new bank account",
            description = "This endpoint is used to create a new bank account for a user."
    )
    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }

    @Operation(
            summary = "Fetches the existing bank account",
            description = "This endpoint is used to fetch an existing bank account for a user."
    )
    @PostMapping("/getAccount")
    public ResponseEntity<?> getAccountDetails(@RequestBody Account account) {
        Long id = account.getId();
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            return ResponseEntity.ok(accountOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }

    @Operation(
            summary = "Deposits the amount",
            description = "This endpoint is used to deposit amount for a user."
    )
    @PostMapping("/deposit")
    public ResponseEntity<DepositAndWithdrawResponse> deposit(@RequestBody DepositAndWithdrawRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(request.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() + request.getAmount());
            accountRepository.save(account);
            DepositAndWithdrawResponse response = new DepositAndWithdrawResponse();
            response.setId(account.getId());
            response.setAccountHolder(account.getAccountHolder());
            response.setBalance(account.getBalance());
            response.setRemarks("Deposit successful. New balance: " + account.getBalance());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "withdraws the amount",
            description = "This endpoint is used to withdraw amount for a user."
    )
    @PostMapping("/withdraw")
    public ResponseEntity<DepositAndWithdrawResponse> withdraw(@RequestBody DepositAndWithdrawRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(request.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if(account.getBalance() >= request.getAmount()){
                account.setBalance(account.getBalance() - request.getAmount());
                accountRepository.save(account);
                DepositAndWithdrawResponse response = new DepositAndWithdrawResponse();
                response.setId(account.getId());
                response.setAccountHolder(account.getAccountHolder());
                response.setBalance(account.getBalance());
                response.setRemarks("Withdraw successful. New balance: " + account.getBalance());
                return ResponseEntity.ok(response);
            }
            else{
                account.setBalance(account.getBalance());
                accountRepository.save(account);
                DepositAndWithdrawResponse response = new DepositAndWithdrawResponse();
                response.setId(account.getId());
                response.setAccountHolder(account.getAccountHolder());
                response.setBalance(account.getBalance());
                response.setRemarks("Withdraw unsuccessful because entered amount was greater than the current balance: " + account.getBalance());
                return ResponseEntity.ok(response);
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteAccount")
    @Operation(
            summary = "Deletes an existing bank account",
            description = "This endpoint is used to delete an existing bank account for a user."
    )
    public ResponseEntity<String> deleteAccountById(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok("Account with ID " + id + " has been deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID " + id + " not found.");
        }
    }

}
