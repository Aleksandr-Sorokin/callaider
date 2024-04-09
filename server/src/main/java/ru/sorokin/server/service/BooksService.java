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
    public Book findBookByName(String name, int index) throws RuntimeException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Transactional Сервис findBookByName старт индекс транзакции = " + index);
        Book book = repository.findBookByNameContainsIgnoreCase(name).orElseThrow(RuntimeException::new);
        System.out.println("Transactional Сервис findBookByName стоп индекс транзакции = " + index);
        return book;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Book updateBook(Book book, int index) {

        System.out.println("Transactional Сервис updateBook старт индекс транзакции = " + index);
        Book book1 =  repository.save(book);
        System.out.println("Transactional Сервис updateBook стоп индекс транзакции = " + index);
        return book1;
    }
}
