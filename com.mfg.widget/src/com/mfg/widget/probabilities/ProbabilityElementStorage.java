package com.mfg.widget.probabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProbabilityElementStorage {
	private UUID uUID;
	private List<ProbabilityElement> probabilityList;

	public ProbabilityElementStorage() {
		super();
		probabilityList = new ArrayList<>();
	}

	public ProbabilityElementStorage(UUID uUID1,
			List<ProbabilityElement> probabilityList1) {
		super();
		this.uUID = uUID1;
		this.probabilityList = probabilityList1;
	}

	public UUID getuUID() {
		return uUID;
	}

	public List<ProbabilityElement> getProbabilityList() {
		return probabilityList;
	}

	public void setuUID(UUID uUID1) {
		this.uUID = uUID1;
	}

	public void setProbabilityList(List<ProbabilityElement> probabilityList1) {
		this.probabilityList = probabilityList1;
	}

}
