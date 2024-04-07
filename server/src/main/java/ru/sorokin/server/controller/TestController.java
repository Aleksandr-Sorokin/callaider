package ru.sorokin.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sorokin.server.model.Book;
import ru.sorokin.server.model.User;
import ru.sorokin.server.service.BooksService;
import ru.sorokin.server.service.UsersService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/test")
public class TestController {
    private final UsersService usersService;
    private final BooksService booksService;

    @GetMapping("/users")
    public List<User> findUserByBooksName(@RequestParam String title) {
        List<User> users = new ArrayList<>();
        Thread thread = new Thread(() -> {
            List<User> usersList = usersService.findUserByBooks(title);
            users.addAll(usersList);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Поток 1 завершен");
        });
        thread.start();
        try {
            thread.join(); // Ожидание завершения потока
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return users;
    }

    @GetMapping("/users/update")
    public Book updateBook(@RequestParam String title) {
        Book book = booksService.findBookByName(title);
        book.setName(book.getName() + "1");
        return booksService.updateBook(book);
    }

}
