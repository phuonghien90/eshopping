package com.hien.eshopping.api.component.product.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hien.eshopping.db.schemaReflection.ProductTable;

@Entity
@Table(name = ProductTable.name)
public class Product implements Serializable {

	private static final long serialVersionUID = 4451771591155543547L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ProductTable.Column.id)
	public Long id;

	@Column(name = ProductTable.Column.skuCode)
	public String skuCode;

	@Column(name = ProductTable.Column.name)
	public String name;

	@Column(name = ProductTable.Column.price)
	public BigDecimal price;
}
