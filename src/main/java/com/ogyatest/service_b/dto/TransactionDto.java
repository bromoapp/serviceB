package com.ogyatest.service_b.dto;

import java.util.Date;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class TransactionDto {

	private UUID transactionId;
	private UUID customerId;
	private String customerName;
	private String listItemName;
	private String listCategory;
	private Long customerChange;
	private Long totalCost;
	private Long customerOldBalance;
	private Long customerNewBalance;
	private Date created;

	public TransactionDto() {
	}

	public TransactionDto(UUID transactionId, UUID customerId, String customerName, String listItemName,
			String listCategory, Long customerChange, Long totalCost, Long customerOldBalance, Long customerNewBalance,
			Date created) {
		super();
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.customerName = customerName;
		this.listItemName = listItemName;
		this.listCategory = listCategory;
		this.customerChange = customerChange;
		this.totalCost = totalCost;
		this.customerOldBalance = customerOldBalance;
		this.customerNewBalance = customerNewBalance;
		this.created = created;
	}

}
