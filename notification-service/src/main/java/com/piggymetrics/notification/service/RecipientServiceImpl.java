package com.piggymetrics.notification.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.repository.RecipientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecipientServiceImpl implements RecipientService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RecipientRepository repository;

	@Override
	public Recipient findByAccountName(String accountName) {
		Assert.hasLength(accountName);
		return repository.findByAccountName(accountName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Recipient save(String accountName, Recipient recipient) {

		recipient.setAccountName(accountName);



		getNotificationsMap(recipient.getScheduledNotifications()).values()
				.forEach(settings -> {
					if (settings.getLastNotified() == null) {
						settings.setLastNotified(new Date());
					}
				});

		repository.save(recipient);

		log.info("recipient {} settings has been updated", recipient);

		return recipient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Recipient> findReadyToNotify(NotificationType type) {
		switch (type) {
			case BACKUP:
				return repository.findReadyForBackup();
			case REMIND:
				return repository.findReadyForRemind();
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markNotified(NotificationType type, Recipient recipient) {
		getNotificationsMap(recipient.getScheduledNotifications()).get(type).setLastNotified(new Date());
		repository.save(recipient);
	}

	public Map<NotificationType, NotificationSettings> getNotificationsMap(String jsonStr) {
		Gson gson = new Gson();
		Map<NotificationType, NotificationSettings> notificationsMap =
				gson.fromJson(jsonStr, new TypeToken<Map<NotificationType, NotificationSettings>>() {}.getType());
		return notificationsMap;
	}
}
