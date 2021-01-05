package com.hien.billing.api.component.transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepo extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	class Spec {
	}

}
