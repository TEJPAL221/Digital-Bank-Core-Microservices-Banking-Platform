package loanservice.controller;

import loanservice.model.Loan;
import loanservice.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan) {
        Loan saved = loanService.createLoan(loan);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Loan>> getLoansByAccount(@PathVariable Long accountId) {
        List<Loan> loans = loanService.getLoansForAccount(accountId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<Loan> makePayment(@PathVariable Long id, @RequestParam double payment) {
        Loan updated = loanService.updateOutstanding(id, payment);
        return ResponseEntity.ok(updated);
    }
}
