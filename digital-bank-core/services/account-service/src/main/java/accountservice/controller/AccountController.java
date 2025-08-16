package accountservice.controller;

import accountservice.model.Account;
import accountservice.model.User;
import accountservice.service.AccountService;
import accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  @Autowired
  private AccountService accountService;
  @Autowired
  private UserRepository userRepository;

  @PostMapping("/create")
  public ResponseEntity<?> createAccount(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String type) {
    if (userDetails == null) {
      return ResponseEntity.status(401).build();
    }
    var user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(401).build();
    }
    Account account = accountService.createAccount(user, type);
    return ResponseEntity.ok(account);
  }

  @GetMapping("/my")
  public ResponseEntity<?> getMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(401).build();
    }
    var user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(401).build();
    }
    List<Account> accounts = accountService.getAccountsForUser(user);
    return ResponseEntity.ok(accounts);
  }
}
