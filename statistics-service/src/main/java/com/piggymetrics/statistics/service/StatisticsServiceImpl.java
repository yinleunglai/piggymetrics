package com.piggymetrics.statistics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piggymetrics.statistics.domain.*;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import com.piggymetrics.statistics.domain.timeseries.StatisticMetric;
import com.piggymetrics.statistics.repository.DataPointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataPointRepository repository;

	@Autowired
	private ExchangeRatesService ratesService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DataPoint> findByAccountName(String accountName) {
		Assert.hasLength(accountName);
		return repository.getDataPointList(accountName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataPoint save(String accountName, Account account) {

		Instant instant = LocalDate.now().atStartOfDay()
				.atZone(ZoneId.systemDefault()).toInstant();

		DataPointId pointId = new DataPointId(accountName, Date.from(instant));


		Gson gson = new Gson();

		List<Item> incomesList = gson.fromJson(account.getIncomes(), new TypeToken<List<Item>>() {}.getType());
		List<Item>expensesList = gson.fromJson(account.getExpenses(), new TypeToken<List<Item>>() {}.getType());
		Saving saving = gson.fromJson(account.getSaving(), Saving.class);
        //TODO
		Set<ItemMetric> incomes = incomesList.stream()
				.map(this::createItemMetric)
				.collect(Collectors.toSet());

		Set<ItemMetric> expenses = expensesList.stream()
				.map(this::createItemMetric)
				.collect(Collectors.toSet());

		Map<StatisticMetric, BigDecimal> statistics = createStatisticMetrics(incomes, expenses, saving);

		ObjectMapper mapper = new ObjectMapper(); //转换器


		DataPoint dataPoint = new DataPoint();
		try {
		    dataPoint.setIncomes(mapper.writeValueAsString(incomes));
			dataPoint.setExpenses(mapper.writeValueAsString(expenses));
			dataPoint.setStatistics(mapper.writeValueAsString(statistics));
			dataPoint.setRates(mapper.writeValueAsString(ratesService.getCurrentRates()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		dataPoint.setAccount(accountName);
		dataPoint.setDate(Date.from(instant));


		log.debug("new datapoint has been created: {}", pointId);

		return repository.save(dataPoint);
	}

	private Map<StatisticMetric, BigDecimal> createStatisticMetrics(Set<ItemMetric> incomes, Set<ItemMetric> expenses, Saving saving) {

		BigDecimal savingAmount = ratesService.convert(saving.getCurrency(), Currency.getBase(), saving.getAmount());

		BigDecimal expensesAmount = expenses.stream()
				.map(ItemMetric::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal incomesAmount = incomes.stream()
				.map(ItemMetric::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return ImmutableMap.of(
				StatisticMetric.EXPENSES_AMOUNT, expensesAmount,
				StatisticMetric.INCOMES_AMOUNT, incomesAmount,
				StatisticMetric.SAVING_AMOUNT, savingAmount
		);
	}

	/**
	 * Normalizes given item amount to {@link Currency#getBase()} currency with
	 * {@link TimePeriod#getBase()} time period
	 */
	private ItemMetric createItemMetric(Item item) {

		BigDecimal amount = ratesService
				.convert(item.getCurrency(), Currency.getBase(), item.getAmount())
				.divide(item.getPeriod().getBaseRatio(), 4, RoundingMode.HALF_UP);

		return new ItemMetric(item.getTitle(), amount);
	}
}
