package com.piggymetrics.account.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piggymetrics.account.domain.*;
import com.piggymetrics.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private Gson gson;

	@PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
	@RequestMapping(path = "/{name}", method = RequestMethod.GET)
	public Account getAccountByName(@PathVariable String name) {
		Accountinfo accountinfo = accountService.findByName(name);

		List<Item> incomesList = gson.fromJson(accountinfo.getIncomes(), new TypeToken<List<Item>>() {}.getType());
		List<Item> expensesList = gson.fromJson(accountinfo.getExpenses(), new TypeToken<List<Item>>() {}.getType());
		Saving saving = gson.fromJson(accountinfo.getSaving(), Saving.class);

		Account account = new Account();
		account.setName(accountinfo.getName());
		account.setExpenses(expensesList);
		account.setIncomes(incomesList);
		account.setLastSeen(accountinfo.getLastSeen());
		account.setSaving(saving);
		account.setNote(accountinfo.getNote());
		return account;
	}

	@RequestMapping(path = "/current", method = RequestMethod.GET)
	public Account getCurrentAccount(Principal principal) {
		Accountinfo accountinfo = accountService.findByName(principal.getName());
		List<Item> incomesList = gson.fromJson(accountinfo.getIncomes(), new TypeToken<List<Item>>() {}.getType());
		List<Item> expensesList = gson.fromJson(accountinfo.getExpenses(), new TypeToken<List<Item>>() {}.getType());
		Saving saving = gson.fromJson(accountinfo.getSaving(), Saving.class);

		Account account = new Account();
		account.setName(accountinfo.getName());
		account.setExpenses(expensesList);
		account.setIncomes(incomesList);
		account.setLastSeen(accountinfo.getLastSeen());
		account.setSaving(saving);
		account.setNote(accountinfo.getNote());
		return account;
	}

	@RequestMapping(path = "/current", method = RequestMethod.PUT)
	public void saveCurrentAccount(Principal principal, @Valid @RequestBody Account account) {

		Accountinfo accountinfo = new Accountinfo();
		accountinfo.setName(account.getName());
		accountinfo.setExpenses(gson.toJson(account.getExpenses()));
		accountinfo.setIncomes(gson.toJson(account.getIncomes()));
		accountinfo.setSaving(gson.toJson(account.getSaving()));
		accountinfo.setNote(account.getNote());
		accountinfo.setLastSeen(new Date());

		accountService.saveChanges(principal.getName(), accountinfo);
	}

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Account createNewAccount(@Valid @RequestBody User user) {

		Accountinfo accountinfo = accountService.create(user);
		List<Item> incomesList = gson.fromJson(accountinfo.getIncomes(), new TypeToken<List<Item>>() {}.getType());
		List<Item> expensesList = gson.fromJson(accountinfo.getExpenses(), new TypeToken<List<Item>>() {}.getType());
		Saving saving = gson.fromJson(accountinfo.getSaving(), Saving.class);

		Account account = new Account();
		account.setName(accountinfo.getName());
		account.setExpenses(expensesList);
		account.setIncomes(incomesList);
		account.setLastSeen(accountinfo.getLastSeen());
		account.setSaving(saving);
		account.setNote(accountinfo.getNote());
		return account;
	}
}
