package com.bank.bankingApplication.service;

import com.bank.bankingApplication.model.Account;
import com.bank.bankingApplication.repository.AccountRepository;
import com.bank.bankingApplication.dto.DepositAndWithdrawRequest;
import com.bank.bankingApplication.dto.DepositAndWithdrawResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        if (account.getAccountHolder() == null || account.getAccountHolder().trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty");
        }

        if (account.getBalance() == null || account.getBalance()<=0) {
            throw new IllegalArgumentException("Invalid Balance");
        }
        return accountRepository.save(account);
    }

    public ResponseEntity<?> getAccountDetails(Long id, HttpServletRequest request) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            return ResponseEntity.ok(accountOpt.get());
        } else {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("message", "Account not found for ID: " + id);
            errorResponse.put("path", request.getRequestURI());
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    public ResponseEntity<?> getAllAccountDetails(HttpServletRequest request) {
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("message", "No accounts found.");
            errorResponse.put("path", request.getRequestURI());
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return ResponseEntity.ok(accounts);
    }



    public ResponseEntity<DepositAndWithdrawResponse> deposit(DepositAndWithdrawRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(request.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (request.getAmount() > 0) {
                account.setBalance(account.getBalance() + request.getAmount());
                accountRepository.save(account);
                return buildResponse(account, "Deposit successful. New balance: " + account.getBalance());
            } else {
                return buildResponse(account, "Deposit unsuccessful. Invalid amount: " + request.getAmount());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<DepositAndWithdrawResponse> withdraw(DepositAndWithdrawRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(request.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account.getBalance() >= request.getAmount() && request.getAmount() >= 0) {
                account.setBalance(account.getBalance() - request.getAmount());
                accountRepository.save(account);
                return buildResponse(account, "Withdraw successful. New balance: " + account.getBalance());
            } else {
                return buildResponse(account, "Withdraw unsuccessful. Invalid amount: " + request.getAmount());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<String> deleteAccountById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok("Account with ID " + id + " has been deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with ID " + id + " not found.");
        }
    }

    private ResponseEntity<DepositAndWithdrawResponse> buildResponse(Account account, String remarks) {
        DepositAndWithdrawResponse response = new DepositAndWithdrawResponse();
        response.setId(account.getId());
        response.setAccountHolder(account.getAccountHolder());
        response.setBalance(account.getBalance());
        response.setRemarks(remarks);
        return ResponseEntity.ok(response);
    }
}
