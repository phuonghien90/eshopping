package com.hien.eshopping.api.component.order.domain;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepo extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

	class Spec {
		public static Specification<Order> phoneNumber(String phoneNumber) {
			return (root, query, cb) -> cb.and(cb.equal(root.get("status"), Order.Status.done),
					cb.equal(root.get("phoneNumber"), phoneNumber));

		}
	}

}
