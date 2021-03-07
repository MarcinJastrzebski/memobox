package io.github.wojtekmarcin.memobox.repository;

import io.github.wojtekmarcin.memobox.entities.User;

import java.util.List;

public interface UserRepository {
    List<User> findAllUsers();

    boolean existsById(Integer id);

    User findUserByUser_id(Integer userId);

    User save(User entity);
}
