package com.hien.billing.api.component.transaction.api.purchase;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.hien.base.exception.InvalidException;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.billing.api.component.account.Account;
import com.hien.billing.api.component.account.AccountRepo;
import com.hien.billing.api.component.card.domain.Card;
import com.hien.billing.api.component.card.domain.CardRepo;
import com.hien.billing.api.component.transaction.domain.Transaction;
import com.hien.billing.api.component.transaction.domain.TransactionRepo;
import com.hien.billing.api.component.transaction.service.SendSmsService;

@RestController
@RequestMapping(path = { "/api/v1/purchase/verify" })
public class Verify {

	@Autowired
	private CardRepo cardRepo;
	@Autowired
	private AccountRepo accountRepo;
	@Autowired
	private TransactionRepo transactionRepo;
	@Autowired
	private SendSmsService sendSmsService;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestBody Request req) {

		Card card = cardRepo.findOne(CardRepo.Spec.cardNumber(req.cardNumber));
		if (card == null) {
			throw new InvalidException("card");
		}
		if (card.expDate.isBefore(req.expDate)) {
			throw new InvalidException("card");
		}
		if (!card.cvv.equals(req.cvv)) {
			throw new InvalidException("card");
		}

		Account account = accountRepo.findOne(AccountRepo.Spec.accountNumber(card.accountNumber));
		if (account == null) {
			throw new InvalidException("card");
		}

		if (req.amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidException("amount");
		}
		// TODO: add more validation here such as NPE, check balance,...

		String otp = "999"; // TODO: generate otp here

		Transaction txn = new Transaction();
		txn.accountNumber = card.accountNumber;
		txn.amount = BigDecimal.ZERO.subtract(req.amount);
		txn.otp = otp; // TODO: should use redis instead of this
		txn.status = Transaction.Status.confirming;
		transactionRepo.save(txn);

		// TODO: lock amount

		sendSmsService.sendOtpAsync(otp, account);

		return ImmutableMap.of("purchaseId", txn.id);
	}

	public static class Request {
		public String cardNumber;

		public YearMonth expDate;

		public String cvv;

		public BigDecimal amount;
	}
}
