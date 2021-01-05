package com.hien.telecom.api.component.voucher.service;

import org.springframework.stereotype.Service;

@Service
public class GenerateVoucherService {

	public String generateVoucher(String skuCode, String phoneNumber) {
		if ("2G-DATA".equals(skuCode)) {
			try {
				Thread.sleep(3 * 1000l);
			} catch (InterruptedException e) {
			}
			return "2G-DATA" + phoneNumber;
		}
		if ("3G-DATA".equals(skuCode)) {
			try {
				Thread.sleep(30 * 1000l);
			} catch (InterruptedException e) {
			}
			return "2G-DATA" + phoneNumber;
		}
		if ("4G-DATA".equals(skuCode)) {
			try {
				Thread.sleep(120 * 1000l);
			} catch (InterruptedException e) {
			}
			return "2G-DATA" + phoneNumber;
		}
		return "1G-DATA" + phoneNumber;
	}
}
