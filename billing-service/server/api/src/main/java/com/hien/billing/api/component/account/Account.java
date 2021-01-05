package com.hien.billing.api.component.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hien.eshopping.db.schemaReflection.AccountTable;

@Entity
@Table(name = AccountTable.name)
public class Account implements Serializable {

	private static final long serialVersionUID = -2946702260127451360L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = AccountTable.Column.id)
	public Long id;

	@Column(name = AccountTable.Column.accountNumber)
	public String accountNumber;

	@Column(name = AccountTable.Column.phoneNumber)
	public String phoneNumber;
}
