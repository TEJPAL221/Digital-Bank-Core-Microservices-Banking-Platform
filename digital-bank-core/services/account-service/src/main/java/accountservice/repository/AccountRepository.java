package accountservice.repository;

import accountservice.model.Account;
import accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findByUser(User user);
}
