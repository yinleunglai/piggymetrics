package com.piggymetrics.auth.repository;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.service.security.MongoUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Test
	public void shouldSaveAndFindUserByName() {

		User user = new User();
		user.setUserName("name");
		user.setPassWord("password");
		repository.save(user);

		User user1 = repository.findByUserName(user.getUserName());
		assertNull(user1);
		assertEquals(user.getUsername(), user1.getUsername());
		assertEquals(user.getPassword(), user1.getPassword());
	}
}
