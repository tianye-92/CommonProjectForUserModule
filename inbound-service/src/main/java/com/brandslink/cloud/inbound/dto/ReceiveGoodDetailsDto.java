package com.brandslink.cloud.inbound.dto;

import java.io.Serializable;
import java.util.List;

import com.brandslink.cloud.inbound.entity.ReceiveArrivalNotice;
import com.brandslink.cloud.inbound.entity.ReceiveGoodDetails;

public class ReceiveGoodDetailsDto implements Serializable{
	
	private ReceiveArrivalNotice receiveArrivalNotice;
	
	private List<ReceiveGoodDetails> receiveGoodDetails;

	public ReceiveArrivalNotice getReceiveArrivalNotice() {
		return receiveArrivalNotice;
	}

	public void setReceiveArrivalNotice(ReceiveArrivalNotice receiveArrivalNotice) {
		this.receiveArrivalNotice = receiveArrivalNotice;
	}

	public List<ReceiveGoodDetails> getReceiveGoodDetails() {
		return receiveGoodDetails;
	}

	public void setReceiveGoodDetails(List<ReceiveGoodDetails> receiveGoodDetails) {
		this.receiveGoodDetails = receiveGoodDetails;
	}

	public ReceiveGoodDetailsDto(ReceiveArrivalNotice receiveArrivalNotice,
			List<ReceiveGoodDetails> receiveGoodDetails) {
		super();
		this.receiveArrivalNotice = receiveArrivalNotice;
		this.receiveGoodDetails = receiveGoodDetails;
	}
	
	
	
	

}
