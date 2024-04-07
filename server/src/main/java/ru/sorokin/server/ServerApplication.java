package ru.sorokin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        String banner =
                "    ▬▬▬.◙.▬▬▬\n" +
                        "      ═▂▄▄▓▄▄▂\n" +
                        "    ◢◤ █▀▀████▄▄▄▄◢◤\n" +
                        "    █▄ █ █▄ ███▀▀▀▀▀▀▀╬\n" +
                        "    ◥█████◤ приложение стартануло                        ^\n" +
                        "    ══╩══╩═                                             （ﾟ. ｡ 7\n" +
                        "                                                         l、 ~ヽ　.•　　\n" +
                        "                                                         じしf_, )ノ";
        SpringApplication.run(ServerApplication.class, args);
        System.out.println(banner);
    }
}
