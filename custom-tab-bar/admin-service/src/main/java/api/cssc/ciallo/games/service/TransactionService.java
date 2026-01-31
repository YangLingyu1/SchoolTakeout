package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction getTransactionById(Integer id);
    List<Transaction> getTransactions();
    List<Transaction> getTransactionsByUserId(Integer userId);
    List<Transaction> getTransactionsByRiderId(Integer riderId);
    List<Transaction> getTransactionsByStatus(String status);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    void deleteTransaction(Integer id);
    void approveWithdrawal(Integer id);
    void rejectWithdrawal(Integer id, String remark);
}