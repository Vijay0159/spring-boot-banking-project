package com.bank.bankingApplication.repository;

import com.bank.bankingApplication.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
