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

import static ru.sorokin.server.ServerApplication.CAT;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/test")
public class TestController {
    private final UsersService usersService;
    private final BooksService booksService;
    private volatile int version = 0;

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

    @GetMapping("/users/start")
    public void getTreads() {
        Thread thread0 = new Thread(() -> {
            System.out.println("thread 0 start");
            try {
                findUserByBooksName("Аэлита");
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 0 отвалился ");
            }
            System.out.println(CAT + "\nthread 0 STOP ");
        });
        thread0.start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt();
//        }

        Thread thread1 = new Thread(() -> {
            System.out.println("thread 1 start");
            try {
                findBookByNameAndUpdate("Аэлита", "Аэлита1");
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 1 отвалился ");
            }
            System.out.println(CAT + "\nthread 1 STOP ");
        });
        thread1.start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt();
//        }

        Thread thread2 = new Thread(() -> {
            System.out.println("thread 2 start");
            try {
                findUserByBooksName("Аэлита");
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 2 отвалился ");
            }
            System.out.println(CAT + "\nthread 2 STOP ");
        });
        thread2.start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt();
//        }

        Thread thread3 = new Thread(() -> {
            System.out.println("thread 3 start");
            try {
                findBookByNameAndUpdate("Аэлита", "Аэлита2");
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 3 отвалился ");
            }
            System.out.println(CAT + "\nthread 3 STOP ");
        });
        thread3.start();
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ie) {
//            Thread.currentThread().interrupt();
//        }
    }
}
