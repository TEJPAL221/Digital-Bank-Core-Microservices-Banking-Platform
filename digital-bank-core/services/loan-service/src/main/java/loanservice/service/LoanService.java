package loanservice.service;

import loanservice.model.Loan;
import loanservice.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public Loan createLoan(Loan loan) {
        loan.setOutstanding(loan.getAmount()); // New loan, outstanding equals amount
        return loanRepository.save(loan);
    }

    public List<Loan> getLoansForAccount(Long accountId) {
        return loanRepository.findByAccountId(accountId);
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan updateOutstanding(Long loanId, double payment) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            loan.setOutstanding(loan.getOutstanding().subtract(java.math.BigDecimal.valueOf(payment)));
            return loanRepository.save(loan);
        }
        throw new RuntimeException("Loan not found");
    }
}
