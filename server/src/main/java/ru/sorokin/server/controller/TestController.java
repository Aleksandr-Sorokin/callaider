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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/test")
public class TestController {
    private final UsersService usersService;
    private final BooksService booksService;
    private volatile int version = 1;

    @GetMapping("/users")
    public List<User> findUserByBooksName(@RequestParam String title) {
        int index = version++;
        System.out.println("Поток в контроллере findUserByBooksName НАЧАТ номер = " + index);
        List<User> usersList = usersService.findUserByBooks(title, index);
        System.out.println("Поток в контроллере findUserByBooksName ЗАВЕРШЕН номер = " + index);
        return usersList;
    }

    @GetMapping("/users/update")
    public Book findBookByNameAndUpdate(@RequestParam String title, @RequestParam String newTitle) {
        int index = version++;
        System.out.println("Поток в контроллере findBookByNameAndUpdate НАЧАТ номер = " + index);
        Book book = booksService.findBookByNameAndUpdate(title, newTitle, index);
        System.out.println("Поток в контроллере findBookByNameAndUpdate ЗАВЕРШЕН номер = " + index);
        return book;
    }

    @GetMapping("/users/start/update")
    public Book getUserUpdateBook(@RequestParam String title) {
        int index = version++;
        System.out.println(index + " Поток в контроллере book начат");
        Book book = booksService.findBookByName(title, index);
        book.setName(title + "1");
        Book book1 = booksService.updateBook(book, index);
        System.out.println(index + " Поток в контроллере book завершен");
        return book1;
    }

}
