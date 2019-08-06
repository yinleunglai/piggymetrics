package com.piggymetrics.statistics.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

	@Valid
	@NotNull
	private String incomes;

	@Valid
	@NotNull
	private String expenses;

	@Valid
	@NotNull
	private String saving;

	public String getIncomes() {
		return incomes;
	}

	public void setIncomes(String incomes) {
		this.incomes = incomes;
	}

	public String getExpenses() {
		return expenses;
	}

	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}

	public String getSaving() {
		return saving;
	}

	public void setSaving(String saving) {
		this.saving = saving;
	}
}
