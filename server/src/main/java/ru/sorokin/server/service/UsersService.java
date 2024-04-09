package ru.sorokin.server.service;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sorokin.server.model.Book;
import ru.sorokin.server.model.User;
import ru.sorokin.server.repository.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository repository;
    private final BooksService booksService;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<User> findUserByBooks(String bookTitle, int index) {

        System.out.println("Transactional Сервис findUserByBooks booksService.findBookByName старт индекс транзакции = " + index);
        Book book = booksService.findBookByName(bookTitle, index);
        System.out.println(index + " index " + book);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Transactional Сервис findUserByBooks repository.findUserByBooks(book) старт индекс транзакции = " + index);
        System.out.println(index + " index " + book);
        List<User> users = repository.findUserByBooks(book).orElse(new ArrayList<>());
        System.out.println(index + " index " + book);
        System.out.println(index + " index " + users);
        System.out.println("Transactional Сервис findUserByBooks стоп индекс транзакции = " + index);
        return users;
    }
}
