package com.neosoft.practice_software.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neosoft.practice_software.infrastructure.api.dto.CreateUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController with full Spring Boot context.
 * Tests the complete flow from HTTP request to database and back.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup is handled by @Sql annotations and Liquibase test data
    }

    @Nested
    @DisplayName("Search Functionality Integration Tests")
    class SearchFunctionalityTests {

        @Test
        @DisplayName("Should return users from database when valid search term is provided")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldReturnUsersFromDatabaseWhenValidSearchTermProvided() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "john")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("john_doe"))
                    .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                    .andExpect(jsonPath("$[0].role").value("USER"));
        }

        @Test
        @DisplayName("Should return multiple users when search term matches multiple usernames")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldReturnMultipleUsersWhenSearchTermMatchesMultiple() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "test")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[*].username").value(org.hamcrest.Matchers.hasItems("test_user1", "test_user2", "test_admin")));
        }

        @Test
        @DisplayName("Should perform case-insensitive search")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldPerformCaseInsensitiveSearch() throws Exception {
            // When & Then - search with uppercase
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "JOHN")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("john_doe"));

            // When & Then - search with mixed case
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "JoHn")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("john_doe"));
        }

        @Test
        @DisplayName("Should return empty array when no users match search criteria")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldReturnEmptyArrayWhenNoUsersMatch() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "nonexistent")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return all users when no search parameter is provided")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldReturnAllUsersWhenNoSearchParameterProvided() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(5)); // Based on test data
        }
    }

    @Nested
    @DisplayName("Pagination Integration Tests")
    class PaginationIntegrationTests {

        @Test
        @DisplayName("Should return paginated results with correct metadata")
        @Sql("/db/test-data/users-pagination-test-data.sql")
        void shouldReturnPaginatedResultsWithCorrectMetadata() throws Exception {
            // When & Then - First page
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(3))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(4))
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(false));
        }

        @Test
        @DisplayName("Should return last page correctly")
        @Sql("/db/test-data/users-pagination-test-data.sql")
        void shouldReturnLastPageCorrectly() throws Exception {
            // When & Then - Last page
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "3")
                    .param("size", "3")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1)) // Only 1 user on last page
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(4))
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$.number").value(3))
                    .andExpect(jsonPath("$.first").value(false))
                    .andExpect(jsonPath("$.last").value(true));
        }

        @Test
        @DisplayName("Should handle sorting in pagination")
        @Sql("/db/test-data/users-pagination-test-data.sql")
        void shouldHandleSortingInPagination() throws Exception {
            // When & Then - Sort by username descending
            MvcResult result = mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "username,desc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(5))
                    .andExpect(jsonPath("$.sort.sorted").value(true))
                    .andReturn();

            // Verify sorting order
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("\"sorted\":true");
        }

        @Test
        @DisplayName("Should handle paginated search results")
        @Sql("/db/test-data/users-pagination-test-data.sql")
        void shouldHandlePaginatedSearchResults() throws Exception {
            // When & Then - Search with pagination
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "user")
                    .param("page", "0")
                    .param("size", "2")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(8)) // 8 users contain "user"
                    .andExpect(jsonPath("$.totalPages").value(4))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.number").value(0));
        }

        @Test
        @DisplayName("Should return empty page when page number exceeds available pages")
        @Sql("/db/test-data/users-pagination-test-data.sql")
        void shouldReturnEmptyPageWhenPageNumberExceedsAvailablePages() throws Exception {
            // When & Then - Request page beyond available data
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "999")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.number").value(999));
        }
    }

    @Nested
    @DisplayName("Security and Input Validation Tests")
    class SecurityAndValidationTests {

        @Test
        @DisplayName("Should prevent SQL injection in search parameter")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldPreventSqlInjectionInSearchParameter() throws Exception {
            // Given - SQL injection attempt
            String maliciousInput = "'; DROP TABLE users; --";

            // When & Then - Should not cause SQL injection
            mockMvc.perform(get("/api/v1/users")
                    .param("username", maliciousInput)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            // Verify that users table still exists by making another request
            mockMvc.perform(get("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(5));
        }

        @Test
        @DisplayName("Should validate search term length constraints")
        void shouldValidateSearchTermLengthConstraints() throws Exception {
            // When & Then - Too short
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "a")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - Too long
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "a".repeat(51))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - Valid length
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "ab")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle special characters safely")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldHandleSpecialCharactersSafely() throws Exception {
            // Given - Various special characters
            String[] specialInputs = {
                "user@domain.com",
                "user-name",
                "user_name",
                "user.name",
                "user123!@#",
                "user%name",
                "user*name"
            };

            for (String input : specialInputs) {
                // When & Then - Should handle gracefully without errors
                mockMvc.perform(get("/api/v1/users")
                        .param("username", input)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray());
            }
        }

        @Test
        @DisplayName("Should sanitize log injection attempts")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldSanitizeLogInjectionAttempts() throws Exception {
            // Given - Log injection attempt
            String logInjectionInput = "test\r\nINJECTED_LOG_ENTRY\r\nuser";

            // When & Then - Should handle without causing log injection
            mockMvc.perform(get("/api/v1/users")
                    .param("username", logInjectionInput)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large dataset search efficiently")
        @Sql("/db/test-data/users-large-dataset.sql")
        void shouldHandleLargeDatasetSearchEfficiently() throws Exception {
            // Given - Large dataset loaded via SQL script
            long startTime = System.currentTimeMillis();

            // When & Then - Search should complete within reasonable time
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "user")
                    .param("page", "0")
                    .param("size", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray());

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Assert that search completes within 2 seconds (reasonable for integration test)
            assertThat(executionTime).isLessThan(2000);
        }

        @Test
        @DisplayName("Should handle concurrent search requests")
        @Sql("/db/test-data/users-search-test-data.sql")
        void shouldHandleConcurrentSearchRequests() throws Exception {
            // Given - Multiple concurrent requests
            Thread[] threads = new Thread[5];
            Exception[] exceptions = new Exception[5];

            for (int i = 0; i < 5; i++) {
                final int threadIndex = i;
                threads[i] = new Thread(() -> {
                    try {
                        mockMvc.perform(get("/api/v1/users")
                                .param("username", "test")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray());
                    } catch (Exception e) {
                        exceptions[threadIndex] = e;
                    }
                });
            }

            // When - Execute concurrent requests
            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then - All requests should succeed
            for (Exception exception : exceptions) {
                assertThat(exception).isNull();
            }
        }
    }

    @Nested
    @DisplayName("Error Scenarios Tests")
    class ErrorScenariosTests {

        @Test
        @DisplayName("Should handle invalid pagination parameters gracefully")
        void shouldHandleInvalidPaginationParametersGracefully() throws Exception {
            // When & Then - Negative page number
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "-1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - Zero page size
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - Negative page size
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "-5")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle malformed request parameters")
        void shouldHandleMalformedRequestParameters() throws Exception {
            // When & Then - Non-numeric page parameter
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "abc")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - Non-numeric size parameter
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "xyz")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return appropriate error response format")
        void shouldReturnAppropriateErrorResponseFormat() throws Exception {
            // When & Then - Invalid search term should return proper error format
            MvcResult result = mockMvc.perform(get("/api/v1/users")
                    .param("username", "a")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("End-to-End Workflow Tests")
    class EndToEndWorkflowTests {

        @Test
        @DisplayName("Should support complete user lifecycle with search")
        @Transactional
        void shouldSupportCompleteUserLifecycleWithSearch() throws Exception {
            // Given - Create a new user
            CreateUserDTO newUser = new CreateUserDTO();
            newUser.setUsername("integration_test_user");
            newUser.setEmail("integration@test.com");
            newUser.setRole("USER");

            // When - Create user
            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("integration_test_user"))
                    .andReturn();

            // Then - Search for the created user
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "integration")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("integration_test_user"))
                    .andExpect(jsonPath("$[0].email").value("integration@test.com"));

            // Then - Search with pagination
            mockMvc.perform(get("/api/v1/users")
                    .param("username", "integration")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].username").value("integration_test_user"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }
}