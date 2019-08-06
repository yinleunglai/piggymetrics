package com.piggymetrics.account.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piggymetrics.account.client.AuthServiceClient;
import com.piggymetrics.account.client.StatisticsServiceClient;
import com.piggymetrics.account.domain.*;
import com.piggymetrics.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private StatisticsServiceClient statisticsClient;

	@Autowired
	private AuthServiceClient authClient;

	@Autowired
	private AccountRepository repository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Accountinfo findByName(String accountName) {
		Assert.hasLength(accountName);
		return repository.findByName(accountName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Accountinfo create(User user) {

		Accountinfo existing = repository.findByName(user.getUserName());
		Assert.isNull(existing, "account already exists: " + user.getUserName());

		authClient.createUser(user);

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(0));
		saving.setCurrency(Currency.getDefault());
		saving.setInterest(new BigDecimal(0));
		saving.setDeposit(false);
		saving.setCapitalization(false);

		Accountinfo account = new Accountinfo();
		account.setName(user.getUserName());
		account.setLastSeen(new Date());

		Gson gson = new Gson();
		account.setSaving(gson.toJson(saving));

		repository.save(account);

		log.info("new account has been created: " + account.getName());

		return account;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveChanges(String name, Accountinfo update) {

		Accountinfo account = repository.findByName(name);
		Assert.notNull(account, "can't find account with name " + name);

		account.setIncomes(update.getIncomes());
		account.setExpenses(update.getExpenses());
		account.setLastSeen(new Date());
		account.setNote(update.getNote());
		account.setSaving(update.getSaving());

		repository.save(account);

		log.debug("account {} changes has been saved", name);

		//TODO set account
		statisticsClient.updateStatistics(name, new Gson().toJson(account));
	}
}
