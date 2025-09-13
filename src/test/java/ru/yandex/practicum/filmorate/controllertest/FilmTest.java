package ru.yandex.practicum.filmorate.controllertest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.adapter.DurationAdapter;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FilmTest {
    private HttpClient client;

    private URI url;

    private Gson gson;

    @BeforeEach
    public void setUp() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/films");
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = builder.create();
    }

    @Test
    public void filmCorrectRequestTest() throws IOException, InterruptedException {
        String filmStr = gson.toJson(new Film(0L, "Челюсти", "Акулки", LocalDate.of(1975, 1, 1), Duration.ofMinutes(110)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void filmEmptyNameTest() throws IOException, InterruptedException {
        String filmStr = gson.toJson(new Film(0L, " ", "Акулки", LocalDate.of(1975, 1, 1), Duration.ofMinutes(110)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void filmIncorrectDescriptionTest() throws IOException, InterruptedException {
        String filmStr = gson.toJson(new Film(0L, "Челюсти", "Акулки".repeat(200), LocalDate.of(1975, 1, 1), Duration.ofMinutes(110)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void filmIncorrectDateTest() throws IOException, InterruptedException {
        String filmStr = gson.toJson(new Film(0L, "Челюсти", "Акулки", LocalDate.of(1861, 1, 1), Duration.ofMinutes(110)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void filmIncorrectTimeTest() throws IOException, InterruptedException {
        String filmStr = gson.toJson(new Film(0L, "Челюсти", "Акулки", LocalDate.of(1861, 1, 1), Duration.ofMinutes(0)));
        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmStr)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }
}
