package com.bank.bankingApplication.controller;

import com.bank.bankingApplication.model.Account;
import com.bank.bankingApplication.model.User;
import com.bank.bankingApplication.repository.UserRepository;
import com.bank.bankingApplication.service.AccountService;
import com.bank.bankingApplication.dto.DepositAndWithdrawRequest;
import com.bank.bankingApplication.dto.DepositAndWithdrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("")
@Tag(name = "Bank Controller", description = "Handles operations related to bank accounts")
public class BankController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    // In-memory store to keep track of logged-in session IDs
    private Set<String> loggedInSessions = ConcurrentHashMap.newKeySet();

    private boolean isLoggedIn(HttpServletRequest request) {
        return loggedInSessions.contains(request.getSession().getId());
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", "first login in login api"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login using credentials stored in the user table.")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            loggedInSessions.add(request.getSession().getId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Login successful"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", "Invalid username or password"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logs out the current user by invalidating the session.")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String sessionId = request.getSession().getId();

        if (loggedInSessions.contains(sessionId)) {
            loggedInSessions.remove(sessionId);
            request.getSession().invalidate(); // Optional: destroys session on server
            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "You are not logged in"));
        }
    }


    @GetMapping("/")
    @Operation(summary = "Landing page", description = "Base page for all APIs")
    public ResponseEntity<?> dummy(HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return ResponseEntity.ok("Hello: " + request.getSession().getId());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new bank account", description = "Creates a new account for a user.")
    public ResponseEntity<?> createAccount(@RequestBody Account account, HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return ResponseEntity.ok(accountService.createAccount(account));
    }
    //changes
    @PostMapping("/getAccount")
    @Operation(summary = "Fetch account details", description = "Fetches an existing bank account using ID.")
    public ResponseEntity<?> getAccountDetails(@RequestBody Account account, HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return accountService.getAccountDetails(account.getId(), request);
    }

    @GetMapping("/getAllAccounts")
    @Operation(summary = "Fetch all account details", description = "Fetches all existing bank accounts.")
    public ResponseEntity<?> getAllAccountDetails(HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return accountService.getAllAccountDetails(request);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit amount", description = "Deposits the given amount if valid.")
    public ResponseEntity<?> deposit(@RequestBody DepositAndWithdrawRequest req, HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return accountService.deposit(req);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw amount", description = "Withdraws the given amount if valid and sufficient.")
    public ResponseEntity<?> withdraw(@RequestBody DepositAndWithdrawRequest req, HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return accountService.withdraw(req);
    }

    @DeleteMapping("/deleteAccount")
    @Operation(summary = "Delete account", description = "Deletes the account for the given ID.")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        if (!isLoggedIn(request)) return unauthorizedResponse();
        return accountService.deleteAccountById(body.get("id"));
    }
}
