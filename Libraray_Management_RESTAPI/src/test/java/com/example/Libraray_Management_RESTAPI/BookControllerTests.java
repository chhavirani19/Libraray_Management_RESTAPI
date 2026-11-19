package com.example.Libraray_Management_RESTAPI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    // Helper: adds a book through the API and returns its new ID
    private int createBook(String title, String author, String category, int copies) throws Exception {
        String json = """
                {"title": "%s", "author": "%s", "category": "%s", "availableCopies": %d}
                """.formatted(title, author, category, copies);
        String response = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).intValue();
    }

    // (b) POST
    @Test
    void addBook_returns201WithGeneratedId() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  Let Us C \",\"author\":\"Yashavant Kanetkar\",\"category\":\"Programming\",\"availableCopies\":6}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Let Us C"))
                .andExpect(jsonPath("$.author").value("Yashavant Kanetkar"))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.availableCopies").value(6));
    }

    @Test
    void addBook_withInvalidData_returns400() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\" \",\"availableCopies\":-2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.author").exists())
                .andExpect(jsonPath("$.errors.category").exists())
                .andExpect(jsonPath("$.errors.availableCopies").value("Available copies cannot be negative"));
    }

    @Test
    void addBook_withBrokenJson_returns400() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest());
    }

    // (c) GET all and GET by ID
    @Test
    void getAllBooks_returnsList() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    void getBookById_returnsBook() throws Exception {
        int id = createBook("Operating System Concepts", "Galvin", "Operating Systems", 2);
        mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Operating System Concepts"));
    }

    @Test
    void getBookById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with ID: 99999"));
    }

    @Test
    void getBookById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/books/abc"))
                .andExpect(status().isBadRequest());
    }

    // (d) PUT
    @Test
    void updateBook_changesAllDetails() throws Exception {
        int id = createBook("Old Title", "Old Author", "Old Category", 1);
        mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Title\",\"author\":\"New Author\",\"category\":\"Fiction\",\"availableCopies\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.author").value("New Author"))
                .andExpect(jsonPath("$.category").value("Fiction"))
                .andExpect(jsonPath("$.availableCopies").value(9));
    }

    @Test
    void updateBook_notFound_returns404() throws Exception {
        mockMvc.perform(put("/api/books/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"author\":\"Y\",\"category\":\"Z\",\"availableCopies\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_withInvalidData_returns400() throws Exception {
        int id = createBook("Some Book", "Someone", "General", 3);
        mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Some Book\",\"author\":\"Someone\",\"category\":\"General\",\"availableCopies\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.availableCopies").exists());
    }

    // (e) DELETE
    @Test
    void deleteBook_removesIt_thenGetReturns404() throws Exception {
        int id = createBook("To Be Deleted", "Nobody", "Misc", 1);
        mockMvc.perform(delete("/api/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book with ID " + id + " deleted successfully"))
                .andExpect(jsonPath("$.deletedBook.id").value(id));
        mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book not found with ID: 99999"));
    }
}
