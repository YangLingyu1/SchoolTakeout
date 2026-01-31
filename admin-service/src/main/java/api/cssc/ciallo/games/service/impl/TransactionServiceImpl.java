package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Transaction;
import api.cssc.ciallo.games.repository.TransactionRepository;
import api.cssc.ciallo.games.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Integer userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> getTransactionsByRiderId(Integer riderId) {
        return transactionRepository.findByRiderId(riderId);
    }

    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public void approveWithdrawal(Integer id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction != null && "withdrawal".equals(transaction.getType())) {
            transaction.setStatus("approved");
            transactionRepository.save(transaction);
        }
    }

    @Override
    public void rejectWithdrawal(Integer id, String remark) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction != null && "withdrawal".equals(transaction.getType())) {
            transaction.setStatus("rejected");
            transaction.setRemark(remark);
            transactionRepository.save(transaction);
        }
    }
}