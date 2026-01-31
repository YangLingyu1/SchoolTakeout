package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Transaction;
import api.cssc.ciallo.games.service.TransactionService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionsController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public Response<?> getTransactions(@RequestParam(required = false) String status) {
        List<Transaction> transactions;
        if (status != null) {
            transactions = transactionService.getTransactionsByStatus(status);
        } else {
            transactions = transactionService.getTransactions();
        }
        return Response.success(transactions);
    }

    @GetMapping("/{id}")
    public Response<?> getTransactionById(@PathVariable Integer id) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            return Response.notFound("交易记录不存在");
        }
        return Response.success(transaction);
    }

    @PutMapping("/{id}/approve")
    public Response<?> approveWithdrawal(@PathVariable Integer id) {
        transactionService.approveWithdrawal(id);
        return Response.success();
    }

    @PutMapping("/{id}/reject")
    public Response<?> rejectWithdrawal(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String remark = request.get("remark");
        transactionService.rejectWithdrawal(id, remark);
        return Response.success();
    }
}