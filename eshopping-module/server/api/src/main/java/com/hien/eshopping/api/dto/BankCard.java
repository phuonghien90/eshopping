package com.hien.eshopping.api.dto;

import java.time.YearMonth;

public class BankCard {

	public final String number;
	public final YearMonth expDate;
	public final String cvv;

	private BankCard() {
		this.number = null;
		this.expDate = null;
		this.cvv = null;
	}

	public BankCard(String number, YearMonth expDate, String cvv) {
		this.number = number;
		this.expDate = expDate;
		this.cvv = cvv;
	}
}
