package ru.sorokin.server.service;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sorokin.server.model.Book;
import ru.sorokin.server.repository.BooksRepository;

@Service
@RequiredArgsConstructor
public class BooksService {
    private final BooksRepository repository;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Book findBookByName(String name) throws RuntimeException {
        return repository.findBookByNameContainsIgnoreCase(name).orElseThrow(RuntimeException::new);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Book updateBook(Book book) {
        return repository.save(book);
    }
}
