package com.hien.eshopping.api.component.order.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hien.eshopping.db.schemaReflection.OrderTable;

@Entity
@Table(name = OrderTable.name)
public class Order implements Serializable {

	private static final long serialVersionUID = 4451771591155543547L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = OrderTable.Column.id)
	public Long id;

	@Column(name = OrderTable.Column.phoneNumber)
	public String phoneNumber;

	@Column(name = OrderTable.Column.skuCode)
	public String skuCode;

	@Column(name = OrderTable.Column.purchaseId)
	public Long purchaseId;

	@Column(name = OrderTable.Column.voucherCode)
	public String voucherCode;

	@Enumerated(EnumType.STRING)
	@Column(name = OrderTable.Column.status)
	public Status status;

	@Column(name = OrderTable.Column.createdAt, updatable = false, insertable = true)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime createdAt = LocalDateTime.now();

	public static enum Status {
		pending, confirming, reserved, gotVoucher, done, rejected;
	}
}
