package com.hien.eshopping.db.schemaReflection;

public class TransactionTable {
	public static final String name = "transaction";

	public static class Column {
		public static final String id = "id";
		public static final String accountNumber = "account_number";
		public static final String amount = "amount";
		public static final String otp = "otp";
		public static final String status = "status";
		public static final String createdAt = "created_at";
	}
}
