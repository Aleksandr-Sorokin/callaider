package ru.sorokin.server.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import ru.sorokin.server.model.Book;
import ru.sorokin.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends CrudRepository<User, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<List<User>> findUserByBooks(Book book);
}
