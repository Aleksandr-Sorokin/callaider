package ru.sorokin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {
    public static String banner =
            "    ▬▬▬.◙.▬▬▬\n" +
                    "      ═▂▄▄▓▄▄▂\n" +
                    "    ◢◤ █▀▀████▄▄▄▄◢◤\n" +
                    "    █▄ █ █▄ ███▀▀▀▀▀▀▀╬\n" +
                    "    ◥█████◤ прилетел сказать что-то важное \n" +
                    "    ══╩══╩═ \n" ;
    public static String CAT =
            "   ^\n" +
                    "（ﾟ. ｡ 7\n" +
                    " l、 ~ヽ　.•　　\n" +
                    " じしf_, )ノ";

    public static void main(String[] args) {

        SpringApplication.run(ServerApplication.class, args);
        System.out.println(banner);
    }
}
