package com.ex.wallet.repository;

import com.ex.wallet.dbase.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
