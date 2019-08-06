package com.piggymetrics.auth.service;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository repository;

	@Override
	public void create(User user) {

		User user1 = repository.findByUserName(user.getUsername());
		if (user1 != null) {
			log.error("user already exists:", user1.getUserName());
			throw new IllegalArgumentException("user already exists: " + user.getUserName());
		}

		String hash = encoder.encode(user.getPassWord());

		user.setPassWord(hash);

		repository.save(user);

		log.info("new user has been created: {}", user.getUserName());
	}
}
