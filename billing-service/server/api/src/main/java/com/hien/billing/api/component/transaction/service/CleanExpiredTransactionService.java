package com.hien.billing.api.component.transaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hien.base.log.LogHelpers;
import com.hien.base.log.LogObj;
import com.hien.billing.api.component.transaction.domain.TransactionRepo;

@Service
public class CleanExpiredTransactionService {

	@Autowired
	TransactionRepo transactionRepo;

	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 10000)
	public void exec() {
		LogObj log = new LogObj(getClass().getSimpleName());
		try {
			// clean expired undone transaction
		} catch (Throwable tr) {
			log.error(tr);
		} finally {
			LogHelpers.info(log);
		}
	}
}