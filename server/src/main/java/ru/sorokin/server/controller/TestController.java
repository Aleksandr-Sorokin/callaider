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

import static ru.sorokin.server.ServerApplication.CAT;

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
        System.out.println(index + " Поток в контроллере user начат");
//        List<User> users = new ArrayList<>();
//        Thread thread = new Thread(() -> {
//            Long start = System.currentTimeMillis();
//            System.out.println("Захват транзакции findUserByBooks из контроллера");
        List<User> usersList = usersService.findUserByBooks(title, index);
//            users.addAll(usersList);
//            Book book = booksService.findBookByName(title, index);
//            book.setName(title);
//            booksService.updateBook(book, index);
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            Long stop = System.currentTimeMillis();
//            System.out.println(index + " Поток в контроллере user завершен" + (stop - start) + " ");
//        });
//        thread.start();
//        try {
//            thread.join(); // Ожидание завершения потока
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return usersList;
    }

    @GetMapping("/users/update")
    public Book updateBook(@RequestParam String title) {
        int index = version++;
        System.out.println(index + " Поток в контроллере book начат");
        Book book = booksService.findBookByName(title, index);
        book.setName(title + "1");
        Book book1 = booksService.updateBook(book, index);
        System.out.println(index + " Поток в контроллере book завершен");
        return book1;
    }

    @GetMapping("/users/start")
    public void start() {
        Thread thread0 = new Thread(() -> {
            System.out.println("thread 0 start");
            List<User> users = new ArrayList<>();
            try {
                users.addAll(findUserByBooksName("Аэлита"));
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 0 отвалился " + users);
            }
            System.out.println(CAT + "\nthread 0 stop " + users);
        });
        thread0.start();

        Thread thread = new Thread(() -> {
            System.out.println("thread 1 start");
            Book book = null;
            try {
                Thread.sleep(1000);
                book = updateBook("элита");
            } catch (RuntimeException | InterruptedException e) {
                System.out.println(CAT + "\nthread 1 отвалился " + book.toString());
            }
            System.out.println(CAT + "\nthread 1 stop " + book.toString());
        });
        thread.start();

        Thread thread1 = new Thread(() -> {
            System.out.println("thread 2 start");
            List<User> users = new ArrayList<>();
            try {
                users.addAll(findUserByBooksName("элита1"));
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 2 отвалился " + users);
            }
            System.out.println(CAT + "\nthread 2 stop " + users);
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            System.out.println("thread 3 start");
            Book book = null;
            try {
                Thread.sleep(1000);
                book = updateBook("лита12");
            } catch (RuntimeException | InterruptedException e) {
                System.out.println(CAT + "\nthread 3 отвалился " + book.toString());
            }
            System.out.println(CAT + "\nthread 3 stop " + book.toString());
        });
        thread2.start();

        Thread thread3 = new Thread(() -> {
            System.out.println("thread 4 start");
            List<User> users = new ArrayList<>();
            try {
                users = findUserByBooksName("лита1");
            } catch (RuntimeException e) {
                System.out.println(CAT + "\nthread 4 отвалился " + users);
            }
            System.out.println(CAT + "\nthread 4 stop " + users);
        });
        thread3.start();

        Thread thread5 = new Thread(() -> {
            System.out.println("thread 5 start");
            Book book = null;
            try {
                Thread.sleep(60000);
                book = updateBook("лита");
            } catch (RuntimeException | InterruptedException e) {
                System.out.println(CAT + "\nthread 5 отвалился " + book.toString());
            }
            System.out.println(CAT + "\nthread 5 stop " + book.toString());
        });
        thread5.start();
    }

}
