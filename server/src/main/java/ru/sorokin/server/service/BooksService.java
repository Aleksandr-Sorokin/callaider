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
    public Book findBookByName(String title, int index) throws RuntimeException {
        System.out.println("Transactional findBookByName(" + title + ") СТАРТ индекс транзакции = " + index);
        Book book = repository.findBookByNameContainsIgnoreCase(title).orElseThrow(RuntimeException::new);
        System.out.println("Transactional findBookByName(" + title + ") СТОП индекс транзакции = " + index);
        return book;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Book findBookByNameAndUpdate(String title, String newTitle, int index) throws RuntimeException {
        System.out.println("Transactional findBookByNameAndUpdate старт индекс транзакции = " + index);
        System.out.println("repository.findBookByNameContainsIgnoreCase(" + title + ") старт индекс транзакции = " + index);
        Book book = repository.findBookByNameContainsIgnoreCase(title).orElseThrow(RuntimeException::new);
        System.out.println("repository.findBookByNameContainsIgnoreCase(" + title + ") стоп индекс транзакции = " + index);
        book.setName(newTitle);
        System.out.println("repository.save(book " + newTitle + ") старт индекс транзакции = " + index);
        repository.save(book);
        System.out.println("repository..save(book " + newTitle + ") стоп индекс транзакции = " + index);
        System.out.println("Transactional findBookByNameAndUpdate стоп индекс транзакции = " + index);
        return book;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Book updateBook(Book book, int index) {
        System.out.println("Transactional Сервис updateBook старт индекс транзакции = " + index);
        Book book1 = repository.save(book);
        System.out.println("Transactional Сервис updateBook стоп индекс транзакции = " + index);
        return book1;
    }
}
