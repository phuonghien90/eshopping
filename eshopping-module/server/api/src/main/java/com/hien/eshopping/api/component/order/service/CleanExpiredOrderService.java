package com.hien.eshopping.api.component.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hien.base.log.LogHelpers;
import com.hien.base.log.LogObj;
import com.hien.eshopping.api.component.order.domain.OrderRepo;

@Service
public class CleanExpiredOrderService {

	@Autowired
	OrderRepo orderRepo;

	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 10000)
	public void exec() {
		LogObj log = new LogObj(getClass().getSimpleName());
		try {
			// clean expired undone order
		} catch (Throwable tr) {
			log.error(tr);
		} finally {
			LogHelpers.info(log);
		}
	}
}