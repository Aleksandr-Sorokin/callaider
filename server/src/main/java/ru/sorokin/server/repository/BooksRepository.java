package ru.sorokin.server.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import ru.sorokin.server.model.Book;
import ru.sorokin.server.model.User;

import java.util.List;
import java.util.Optional;

public interface BooksRepository extends CrudRepository<Book, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Book> findBookByNameContainsIgnoreCase(String name);
}
