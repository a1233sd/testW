package com.ex.wallet.service;

import com.ex.wallet.dbase.User;
import com.ex.wallet.exception.UserNotFoundException;
import com.ex.wallet.exception.WalletNotFoundException;
import com.ex.wallet.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
    }

    public User createUser(String name, String surname, String phoneNumber) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }
}
