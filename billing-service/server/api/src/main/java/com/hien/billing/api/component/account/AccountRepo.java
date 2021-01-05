package com.hien.billing.api.component.account;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

	class Spec {
		public static Specification<Account> accountNumber(final String accountNumber) {
			return (root, query, cb) -> cb.equal(root.get("accountNumber"), accountNumber);
		}
	}

}
