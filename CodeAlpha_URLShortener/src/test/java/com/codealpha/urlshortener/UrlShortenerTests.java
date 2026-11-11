package com.codealpha.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerTests {
    @Autowired MockMvc mvc;

    @Test
    void shortenThenRedirect() throws Exception {
        mvc.perform(post("/api/shorten").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/page\",\"alias\":\"mytest1\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/mytest1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/page"));
    }

    @Test
    void rejectsInvalidUrl() throws Exception {
        mvc.perform(post("/api/shorten").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"not a url\"}"))
                .andExpect(status().isBadRequest());
    }
}
