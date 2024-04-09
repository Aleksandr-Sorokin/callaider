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
    public List<User> findUserByBooks(String title, int index) {
        System.out.println("Transactional findUserByBooks(" + title + ") СТАРТ индекс транзакции = " + index);
        Book book = booksService.findBookByName(title, index);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        List<User> users = repository.findUserByBooks(book).orElse(new ArrayList<>());
        System.out.println("Transactional findUserByBooks(" + title + ") СТОП индекс транзакции = " + index);
        return users;
    }
}
