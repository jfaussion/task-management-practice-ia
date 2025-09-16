package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.application.service.UserService;
import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.api.dto.UserDTO;
import com.neosoft.practice_software.infrastructure.api.mapper.UserDTOMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for UserController focusing on search functionality.
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDTOMapper userDTOMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private User johnUser;
    private User janeUser;
    private UserDTO johnDTO;
    private UserDTO janeDTO;
    private List<User> testUsers;
    private List<UserDTO> testDTOs;

    @BeforeEach
    void setUp() {
        // Create test domain objects
        johnUser = createTestUser("john", "john@example.com", "USER");
        janeUser = createTestUser("jane", "jane@example.com", "USER");
        testUsers = Arrays.asList(johnUser, janeUser);

        // Create test DTOs
        johnDTO = createTestUserDTO("john", "john@example.com", "USER");
        janeDTO = createTestUserDTO("jane", "jane@example.com", "USER");
        testDTOs = Arrays.asList(johnDTO, janeDTO);
    }

    @Nested
    @DisplayName("GET /api/v1/users - Search without pagination")
    class SearchWithoutPaginationTests {

        @Test
        @DisplayName("Should return users when valid search term is provided")
        void shouldReturnUsersWhenValidSearchTermProvided() throws Exception {
            // Given
            String searchTerm = "john";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Arrays.asList(johnUser));
            when(userDTOMapper.toDTOs(Arrays.asList(johnUser))).thenReturn(Arrays.asList(johnDTO));

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("john"))
                    .andExpect(jsonPath("$[0].email").value("john@example.com"));

            verify(userService).searchUsersByUsername(searchTerm);
            verify(userDTOMapper).toDTOs(Arrays.asList(johnUser));
        }

        @Test
        @DisplayName("Should return multiple users when search term matches multiple usernames")
        void shouldReturnMultipleUsersWhenSearchTermMatchesMultiple() throws Exception {
            // Given
            String searchTerm = "j";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(testUsers);
            when(userDTOMapper.toDTOs(testUsers)).thenReturn(testDTOs);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].username").value("john"))
                    .andExpect(jsonPath("$[1].username").value("jane"));

            verify(userService).searchUsersByUsername(searchTerm);
            verify(userDTOMapper).toDTOs(testUsers);
        }

        @Test
        @DisplayName("Should return empty array when no users match search criteria")
        void shouldReturnEmptyArrayWhenNoUsersMatch() throws Exception {
            // Given
            String searchTerm = "nonexistent";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Collections.emptyList());
            when(userDTOMapper.toDTOs(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userService).searchUsersByUsername(searchTerm);
            verify(userDTOMapper).toDTOs(Collections.emptyList());
        }

        @Test
        @DisplayName("Should return all users when no search parameter is provided")
        void shouldReturnAllUsersWhenNoSearchParameterProvided() throws Exception {
            // Given
            when(userService.getAllUsers()).thenReturn(testUsers);
            when(userDTOMapper.toDTOs(testUsers)).thenReturn(testDTOs);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(userService).getAllUsers();
            verify(userService, never()).searchUsersByUsername(anyString());
            verify(userDTOMapper).toDTOs(testUsers);
        }

        @Test
        @DisplayName("Should return bad request when search term is too short")
        void shouldReturnBadRequestWhenSearchTermTooShort() throws Exception {
            // Given
            String shortSearchTerm = "a";

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", shortSearchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).searchUsersByUsername(anyString());
            verify(userDTOMapper, never()).toDTOs(any());
        }

        @Test
        @DisplayName("Should return bad request when search term is too long")
        void shouldReturnBadRequestWhenSearchTermTooLong() throws Exception {
            // Given
            String longSearchTerm = "a".repeat(51);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", longSearchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).searchUsersByUsername(anyString());
            verify(userDTOMapper, never()).toDTOs(any());
        }

        @Test
        @DisplayName("Should handle search term with special characters")
        void shouldHandleSearchTermWithSpecialCharacters() throws Exception {
            // Given
            String searchTermWithSpecialChars = "john@123";
            when(userService.searchUsersByUsername(searchTermWithSpecialChars)).thenReturn(Collections.emptyList());
            when(userDTOMapper.toDTOs(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTermWithSpecialChars)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userService).searchUsersByUsername(searchTermWithSpecialChars);
        }

        @Test
        @DisplayName("Should trim whitespace from search term")
        void shouldTrimWhitespaceFromSearchTerm() throws Exception {
            // Given
            String searchTermWithWhitespace = "  john  ";
            String expectedTrimmedTerm = "john";
            when(userService.searchUsersByUsername(expectedTrimmedTerm)).thenReturn(Arrays.asList(johnUser));
            when(userDTOMapper.toDTOs(Arrays.asList(johnUser))).thenReturn(Arrays.asList(johnDTO));

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTermWithWhitespace)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("john"));

            verify(userService).searchUsersByUsername(expectedTrimmedTerm);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users - Search with pagination")
    class SearchWithPaginationTests {

        @Test
        @DisplayName("Should return paginated users when search term and pagination parameters are provided")
        void shouldReturnPaginatedUsersWhenSearchTermAndPaginationProvided() throws Exception {
            // Given
            String searchTerm = "j";
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(testUsers, pageable, 2);
            Page<UserDTO> dtoPage = userPage.map(user -> user.getUsername().equals("john") ? johnDTO : janeDTO);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(userPage);
            when(userDTOMapper.toDTO(johnUser)).thenReturn(johnDTO);
            when(userDTOMapper.toDTO(janeUser)).thenReturn(janeDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.number").value(0));

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return paginated users with custom page size")
        void shouldReturnPaginatedUsersWithCustomPageSize() throws Exception {
            // Given
            String searchTerm = "j";
            Pageable pageable = PageRequest.of(0, 5);
            Page<User> userPage = new PageImpl<>(testUsers, pageable, 10);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(userPage);
            when(userDTOMapper.toDTO(johnUser)).thenReturn(johnDTO);
            when(userDTOMapper.toDTO(janeUser)).thenReturn(janeDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "5")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.number").value(0));

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return paginated users with sorting")
        void shouldReturnPaginatedUsersWithSorting() throws Exception {
            // Given
            String searchTerm = "j";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("username").descending());
            Page<User> userPage = new PageImpl<>(Arrays.asList(janeUser, johnUser), pageable, 2);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(userPage);
            when(userDTOMapper.toDTO(johnUser)).thenReturn(johnDTO);
            when(userDTOMapper.toDTO(janeUser)).thenReturn(janeDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "username,desc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.sort.sorted").value(true));

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return all users with pagination when no search parameter is provided")
        void shouldReturnAllUsersWithPaginationWhenNoSearchParameterProvided() throws Exception {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(testUsers, pageable, 2);
            
            when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
            when(userDTOMapper.toDTO(johnUser)).thenReturn(johnDTO);
            when(userDTOMapper.toDTO(janeUser)).thenReturn(janeDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2));

            verify(userService).getAllUsers(any(Pageable.class));
            verify(userService, never()).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no users match search criteria")
        void shouldReturnEmptyPageWhenNoUsersMatch() throws Exception {
            // Given
            String searchTerm = "nonexistent";
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle large page numbers gracefully")
        void shouldHandleLargePageNumbersGracefully() throws Exception {
            // Given
            String searchTerm = "j";
            Pageable pageable = PageRequest.of(999, 10);
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 2);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "999")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.number").value(999));

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should return bad request for search term exactly at minimum length boundary")
        void shouldReturnBadRequestForSearchTermAtMinimumBoundary() throws Exception {
            // Given - exactly 1 character (below minimum of 2)
            String searchTerm = "a";

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).searchUsersByUsername(anyString());
        }

        @Test
        @DisplayName("Should accept search term exactly at minimum valid length")
        void shouldAcceptSearchTermAtMinimumValidLength() throws Exception {
            // Given - exactly 2 characters (minimum valid)
            String searchTerm = "ab";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Collections.emptyList());
            when(userDTOMapper.toDTOs(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(userService).searchUsersByUsername(searchTerm);
        }

        @Test
        @DisplayName("Should accept search term exactly at maximum valid length")
        void shouldAcceptSearchTermAtMaximumValidLength() throws Exception {
            // Given - exactly 50 characters (maximum valid)
            String searchTerm = "a".repeat(50);
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Collections.emptyList());
            when(userDTOMapper.toDTOs(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(userService).searchUsersByUsername(searchTerm);
        }

        @Test
        @DisplayName("Should return bad request for search term exactly at maximum length boundary")
        void shouldReturnBadRequestForSearchTermAtMaximumBoundary() throws Exception {
            // Given - exactly 51 characters (above maximum of 50)
            String searchTerm = "a".repeat(51);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).searchUsersByUsername(anyString());
        }

        @Test
        @DisplayName("Should handle invalid pagination parameters gracefully")
        void shouldHandleInvalidPaginationParametersGracefully() throws Exception {
            // Given
            String searchTerm = "john";

            // When & Then - negative page number
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "-1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            // When & Then - zero page size
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).searchUsersByUsername(anyString(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Response Format Tests")
    class ResponseFormatTests {

        @Test
        @DisplayName("Should return correct JSON structure for non-paginated response")
        void shouldReturnCorrectJsonStructureForNonPaginatedResponse() throws Exception {
            // Given
            String searchTerm = "john";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Arrays.asList(johnUser));
            when(userDTOMapper.toDTOs(Arrays.asList(johnUser))).thenReturn(Arrays.asList(johnDTO));

            // When & Then
            MvcResult result = mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].username").exists())
                    .andExpect(jsonPath("$[0].email").exists())
                    .andExpect(jsonPath("$[0].role").exists())
                    .andExpect(jsonPath("$[0].createdAt").exists())
                    .andExpect(jsonPath("$[0].updatedAt").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("\"username\":\"john\"");
        }

        @Test
        @DisplayName("Should return correct JSON structure for paginated response")
        void shouldReturnCorrectJsonStructureForPaginatedResponse() throws Exception {
            // Given
            String searchTerm = "j";
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(testUsers, pageable, 2);
            
            when(userService.searchUsersByUsername(searchTerm, any(Pageable.class))).thenReturn(userPage);
            when(userDTOMapper.toDTO(johnUser)).thenReturn(johnDTO);
            when(userDTOMapper.toDTO(janeUser)).thenReturn(janeDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.pageable").exists())
                    .andExpect(jsonPath("$.totalElements").exists())
                    .andExpect(jsonPath("$.totalPages").exists())
                    .andExpect(jsonPath("$.size").exists())
                    .andExpect(jsonPath("$.number").exists())
                    .andExpect(jsonPath("$.sort").exists())
                    .andExpect(jsonPath("$.first").exists())
                    .andExpect(jsonPath("$.last").exists())
                    .andExpect(jsonPath("$.numberOfElements").exists());

            verify(userService).searchUsersByUsername(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return consistent response format for empty results")
        void shouldReturnConsistentResponseFormatForEmptyResults() throws Exception {
            // Given
            String searchTerm = "nonexistent";
            when(userService.searchUsersByUsername(searchTerm)).thenReturn(Collections.emptyList());
            when(userDTOMapper.toDTOs(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("username", searchTerm)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userService).searchUsersByUsername(searchTerm);
        }
    }

    /**
     * Helper method to create test User objects.
     */
    private User createTestUser(String username, String email, String role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Helper method to create test UserDTO objects.
     */
    private UserDTO createTestUserDTO(String username, String email, String role) {
        UserDTO dto = new UserDTO();
        dto.setId(UUID.randomUUID());
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setCreatedAt(LocalDateTime.now().minusDays(1));
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
}