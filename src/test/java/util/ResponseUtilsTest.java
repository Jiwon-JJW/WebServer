package util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseUtilsTest {

    private SoftAssertions softly;
    private ByteArrayOutputStream baos;
    private DataOutputStream dos;

    @BeforeEach
    void set() {
        softly = new SoftAssertions();
        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
    }

    @AfterEach
    void execution() {
        softly.assertAll();
    }

    @Test
    void response200Header() {
        ResponseUtils.response200Header(dos, 100, "html");
        String responseMessage = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8;\r\n" +
                "Content-length: 100\r\n" +
                "\r\n";

        assertThat(baos.toString()).isEqualTo(responseMessage);
    }

    @Test
    void response302Header() {
        ResponseUtils.response302Header(dos, "/index.html");
        String responseMessage = "HTTP/1.1 302 Found\r\n" +
                "Location: http://localhost:8080/index.html\r\n" +
                "\r\n";

        assertThat(baos.toString()).isEqualTo(responseMessage);
    }

    @Test
    void testResponse302Header() {
        ResponseUtils.response302Header(dos, "/index.html", true);
        String responseMessage = "HTTP/1.1 302 Found\r\n" +
                "Location: http://localhost:8080/index.html\r\n" +
                "Set-Cookie: logined=true; Path=/\r\n" +
                "\r\n";

        assertThat(baos.toString()).isEqualTo(responseMessage);
    }

    @Test
    void responseBody() {
        byte[] body = "Hello World".getBytes(StandardCharsets.UTF_8);
        ResponseUtils.responseBody(dos, body);

        assertThat(baos.toString()).isEqualTo("Hello World");
    }
}
