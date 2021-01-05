package com.hien.eshopping.db.schemaReflection;

public class OrderTable {
	public static final String name = "`order`";

	public static class Column {
		public static final String id = "id";
		public static final String phoneNumber = "phone_number";
		public static final String skuCode = "sku_code";
		public static final String purchaseId = "purchase_id";
		public static final String voucherCode = "voucher_code";
		public static final String status = "status";
		public static final String createdAt = "created_at";
	}
}
