package com.piggymetrics.auth.repository;

import com.piggymetrics.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        User findByUserName(String name);



}
