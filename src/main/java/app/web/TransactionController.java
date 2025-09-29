package app.web;

import app.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public ModelAndView getTransaction(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaction-result");
        modelAndView.addObject("transaction", transactionService.getById(id));

        return modelAndView;
    }
}
