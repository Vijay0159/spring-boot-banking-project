package com.bank.bankingApplication.controller;

import com.bank.bankingApplication.model.Account;
import com.bank.bankingApplication.service.AccountService;
import com.bank.bankingApplication.dto.DepositAndWithdrawRequest;
import com.bank.bankingApplication.dto.DepositAndWithdrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bank")
@Tag(name = "Bank Controller", description = "Handles operations related to bank accounts")
public class BankController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    @Operation(summary = "Create a new bank account", description = "Creates a new account for a user.")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account));
    }

    @PostMapping("/getAccount")
    @Operation(summary = "Fetch account details", description = "Fetches an existing bank account using ID.")
    public ResponseEntity<?> getAccountDetails(@RequestBody Account account, HttpServletRequest request) {
        return accountService.getAccountDetails(account.getId(), request);
    }

    @GetMapping("/getAllAccounts")
    @Operation(summary = "Fetch all account details", description = "Fetches all existing bank accounts.")
    public ResponseEntity<?> getAllAccountDetails(HttpServletRequest request) {
        return accountService.getAllAccountDetails(request);
    }


    @PostMapping("/deposit")
    @Operation(summary = "Deposit amount", description = "Deposits the given amount if valid.")
    public ResponseEntity<DepositAndWithdrawResponse> deposit(@RequestBody DepositAndWithdrawRequest request) {
        return accountService.deposit(request);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw amount", description = "Withdraws the given amount if valid and sufficient.")
    public ResponseEntity<DepositAndWithdrawResponse> withdraw(@RequestBody DepositAndWithdrawRequest request) {
        return accountService.withdraw(request);
    }

    @DeleteMapping("/deleteAccount")
    @Operation(summary = "Delete account", description = "Deletes the account for the given ID.")
    public ResponseEntity<String> deleteAccount(@RequestBody Map<String, Long> request) {
        return accountService.deleteAccountById(request.get("id"));
    }
}
