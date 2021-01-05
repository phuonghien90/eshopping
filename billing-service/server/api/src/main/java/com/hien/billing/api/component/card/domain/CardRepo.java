package com.hien.billing.api.component.card.domain;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepo extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

	class Spec {
		public static Specification<Card> cardNumber(final String cardNumber) {
			return (root, query, cb) -> cb.equal(root.get("cardNumber"), cardNumber);
		}
	}

}
