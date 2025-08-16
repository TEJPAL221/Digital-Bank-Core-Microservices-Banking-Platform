package accountservice.service;

import accountservice.model.Account;
import accountservice.model.User;
import accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
  @Autowired
  private AccountRepository accountRepository;

  public Account createAccount(User user, String type) {
    Account account = new Account();
    account.setUser(user);
    account.setType(type);
    account.setBalance(BigDecimal.ZERO);
    return accountRepository.save(account);
  }

  public List<Account> getAccountsForUser(User user) {
    return accountRepository.findByUser(user);
  }
}

