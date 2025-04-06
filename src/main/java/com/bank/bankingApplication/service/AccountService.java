package com.bank.bankingApplication.service;

import com.bank.bankingApplication.model.Account;
import com.bank.bankingApplication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account){
        return accountRepository.save(account);
    }

    public Account getAccount(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account Not Found"));
    }

    public Account deposit(Long id, Double amount){
        Account account = getAccount(id);
        account.setBalance(amount + account.getBalance());
        return accountRepository.save(account);
    }

    public Account withdraw(Long id, Double amount){
        Account account = getAccount(id);
        account.setBalance(amount - account.getBalance());
        return accountRepository.save(account);
    }

    public String deleteAccount(Long id){
        accountRepository.deleteById(id);
        return "Account with ID {} deleted" + id;
    }
}
