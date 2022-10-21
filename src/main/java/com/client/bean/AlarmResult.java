package com.client.bean;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AlarmResult {
	@JsonProperty(value = "PlateResult")
	private PlateResult plateResult;

	public PlateResult getPlateResult() {
		return plateResult;
	}

	public void setPlateResult(PlateResult plateResult) {
		this.plateResult = plateResult;
	}



	
}
