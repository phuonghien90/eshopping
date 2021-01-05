package com.hien.eshopping.api.component.product.domain;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	class Spec {
		public static Specification<Product> skuCode(final String skuCode) {
			return (root, query, cb) -> cb.equal(root.get("skuCode"), skuCode);
		}
	}

}
