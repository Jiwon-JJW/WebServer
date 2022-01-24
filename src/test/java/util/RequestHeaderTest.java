package util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static util.status.HttpHeader.*;
import static util.status.HttpHeader.createAccept;

class RequestHeaderTest {

    private SoftAssertions softly;
    private RequestHeader requestHeader;

    @BeforeEach
    void set() throws IOException {
        softly = new SoftAssertions();
        requestHeader = RequestHeader.of(new BufferedReader(new StringReader(createRequestHeaders())));
    }

    @AfterEach
    void execution() {
        softly.assertAll();
    }

    @Test
    void getContentLength() {
        softly.assertThat(requestHeader.getContentLength()).isEqualTo(10);
    }

    @Test
    void containsKey() {
        softly.assertThat(requestHeader.containsKey(HOST)).isTrue();
        softly.assertThat(requestHeader.containsKey(CONNECTION)).isTrue();
        softly.assertThat(requestHeader.containsKey(CONTENT_LENGTH)).isTrue();
        softly.assertThat(requestHeader.containsKey(ACCEPT)).isTrue();
        softly.assertThat(requestHeader.containsKey(COOKIE)).isTrue();
    }

    @Test
    void isLogin() {
        softly.assertThat(requestHeader.isLogin()).isTrue();
    }

    @Test
    void getExtension() {
        softly.assertThat(requestHeader.getExtension()).isEqualTo("html");
    }

    private String createRequestHeaders() {
        return "GET /index.html HTTP/1.1" + System.lineSeparator() +
                createHost("localhost:8080") + System.lineSeparator() +
                createConnection("keep-alive") + System.lineSeparator() +
                createCookie("logined=true") + System.lineSeparator() +
                createAccept("text/html") + System.lineSeparator() +
                createContentLength(10) + System.lineSeparator() +
                "" + System.lineSeparator();
    }
}
