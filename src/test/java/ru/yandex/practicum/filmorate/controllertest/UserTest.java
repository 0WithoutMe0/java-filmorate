package ru.yandex.practicum.filmorate.controllertest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.adapter.DurationAdapter;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    private HttpClient client;

    private URI url;

    @LocalServerPort
    private int port;

    private Gson gson;

    @BeforeEach
    public void setUp() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:" + port + "/users");
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = builder.create();
    }

    @Test
    public void userCorrectRequestTest() throws IOException, InterruptedException {
        String userStr = gson.toJson(new User(0L, "i-love-yandex@yandex.ru", "Яндексоид","Олег", LocalDate.of(1997, 9, 23)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void userCorrectEmptyNameTest() throws IOException, InterruptedException {
        String userStr = gson.toJson(new User(0L, "i-love-yandex@yandex.ru", "Яндексоид",null, LocalDate.of(1997, 9, 23)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User userResult = gson.fromJson(response.body(), User.class);

        assertEquals(200, response.statusCode());
        assertEquals("Яндексоид", userResult.getName());
    }

    @Test
    public void userIncorrectEmailTest() throws IOException, InterruptedException {
        String userStr1 = gson.toJson(new User(0L, "i-love-yandexyandex.ru", "Яндексоид","Олег", LocalDate.of(1997, 9, 23)));
        HttpRequest request1 = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr1)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response1.statusCode());

        String userStr2 = gson.toJson(new User(0L, "", "Яндексоид","Олег", LocalDate.of(1997, 9, 23)));
        HttpRequest request2 = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr2)).build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response2.statusCode());
    }

    @Test
    public void userIncorrectLoginTest() throws IOException, InterruptedException {
        String userStr1 = gson.toJson(new User(0L, "i-love-yandex@yandex.ru", "","Олег", LocalDate.of(1997, 9, 23)));
        HttpRequest request1 = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr1)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response1.statusCode());

        String userStr2 = gson.toJson(new User(0L, "i-love-yandex@yandex.ru", "Янде ксоид","Олег", LocalDate.of(1997, 9, 23)));
        HttpRequest request2 = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr2)).build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response2.statusCode());
    }

    @Test
    public void userIncorrectBirthdayTest() throws IOException, InterruptedException {
        String userStr = gson.toJson(new User(0L, "i-love-yandex@yandex.ru", "Яндексоид","Олег", LocalDate.of(2030, 9, 23)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }
}
