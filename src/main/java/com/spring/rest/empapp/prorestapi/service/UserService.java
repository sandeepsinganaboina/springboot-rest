package com.spring.rest.empapp.prorestapi.service;

import com.spring.rest.empapp.prorestapi.model.User;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public User createUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        if (!users.containsKey(id)) return Optional.empty();
        updatedUser.setId(id);
        users.put(id, updatedUser);
        return Optional.of(updatedUser);
    }

    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }
}
