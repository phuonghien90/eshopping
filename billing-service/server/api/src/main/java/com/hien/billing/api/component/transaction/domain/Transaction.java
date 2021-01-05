package com.hien.billing.api.component.transaction.domain;

import java.io.Serializable;
import java.math.BigDecimal;
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
import com.hien.eshopping.db.schemaReflection.TransactionTable;

@Entity
@Table(name = TransactionTable.name)
public class Transaction implements Serializable {

	private static final long serialVersionUID = 8175974725485567839L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = TransactionTable.Column.id)
	public Long id;

	@Column(name = TransactionTable.Column.accountNumber)
	public String accountNumber;

	@Column(name = TransactionTable.Column.amount)
	public BigDecimal amount;
	
	@Column(name = TransactionTable.Column.otp)
	public String otp;

	@Enumerated(EnumType.STRING)
	@Column(name = TransactionTable.Column.status)
	public Status status;

	@Column(name = TransactionTable.Column.createdAt, updatable = false, insertable = true)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime createdAt = LocalDateTime.now();

	public static enum Status {
		confirming, reserved, committed, cancelled;
	}
}
