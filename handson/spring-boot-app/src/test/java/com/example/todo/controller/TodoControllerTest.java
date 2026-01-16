package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    void createTodo_正常系() throws Exception {
        Todo todo = new Todo("テストタスク");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("テストタスク")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void getTodoById_正常系() throws Exception {
        Todo saved = todoRepository.save(new Todo("取得テスト"));

        mockMvc.perform(get("/api/todos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("取得テスト")));
    }

    @Test
    void getTodoById_存在しない場合404() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTodo_正常系() throws Exception {
        Todo saved = todoRepository.save(new Todo("更新前"));
        saved.setTitle("更新後");
        saved.setCompleted(true);

        mockMvc.perform(put("/api/todos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("更新後")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void deleteTodo_正常系() throws Exception {
        Todo saved = todoRepository.save(new Todo("削除テスト"));

        mockMvc.perform(delete("/api/todos/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTodo_存在しない場合404() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    // ========== バリデーションテスト ==========

    @Test
    void createTodo_タイトルがnullの場合400エラー() throws Exception {
        // Arrange: タイトルがnullのTodoを準備
        String jsonWithNullTitle = "{\"title\":null,\"completed\":false}";

        // Act & Assert: 400 Bad Requestが返ることを検証
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullTitle))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTodo_タイトルが空文字の場合400エラー() throws Exception {
        // Arrange: タイトルが空文字のTodoを準備
        Todo todo = new Todo("");

        // Act & Assert: 400 Bad Requestが返ることを検証
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTodo_タイトルが空白のみの場合400エラー() throws Exception {
        // Arrange: タイトルが空白のみのTodoを準備
        Todo todo = new Todo("   ");

        // Act & Assert: 400 Bad Requestが返ることを検証
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTodo_タイトルが空の場合400エラー() throws Exception {
        // Arrange: 既存のTodoを作成し、空のタイトルで更新を試みる
        Todo saved = todoRepository.save(new Todo("元のタイトル"));
        Todo updateRequest = new Todo("");
        updateRequest.setId(saved.getId());

        // Act & Assert: 400 Bad Requestが返ることを検証
        mockMvc.perform(put("/api/todos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
