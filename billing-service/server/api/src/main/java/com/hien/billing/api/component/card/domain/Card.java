package com.hien.billing.api.component.card.domain;

import java.io.Serializable;
import java.time.YearMonth;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hien.base.converter.YearMonthConverter;
import com.hien.eshopping.db.schemaReflection.CardTable;

@Entity
@Table(name = CardTable.name)
public class Card implements Serializable {

	private static final long serialVersionUID = -7295022249849991776L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = CardTable.Column.id)
	public Long id;

	@Column(name = CardTable.Column.cardNumber)
	public String cardNumber;

	@Column(name = CardTable.Column.expDate)
	@Convert(converter = YearMonthConverter.class)
	public YearMonth expDate;

	@Column(name = CardTable.Column.cvv)
	public String cvv;

	@Column(name = CardTable.Column.accountNumber)
	public String accountNumber;
}
