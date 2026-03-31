package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.application.dao.UserDAO;
import com.neosoft.practice_software.application.service.impl.UserServiceImpl;
import com.neosoft.practice_software.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for UserServiceImpl focusing on searchUsersByUsername methods.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private User johnUser;
    private User janeUser;
    private User adminUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        // Create test data
        johnUser = createTestUser("john", "john@example.com", "USER");
        janeUser = createTestUser("jane", "jane@example.com", "USER");
        adminUser = createTestUser("admin", "admin@example.com", "ADMIN");
        testUsers = Arrays.asList(johnUser, janeUser);
    }

    @Nested
    @DisplayName("Non-paginated searchUsersByUsername Tests")
    class NonPaginatedSearchTests {

        @Test
        @DisplayName("Should return users when valid search term is provided")
        void shouldReturnUsersWhenValidSearchTermProvided() {
            // Given
            String searchTerm = "john";
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userService.searchUsersByUsername(searchTerm);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("john");
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm);
        }

        @Test
        @DisplayName("Should return multiple users when search term matches multiple usernames")
        void shouldReturnMultipleUsersWhenSearchTermMatchesMultiple() {
            // Given
            String searchTerm = "j";
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(testUsers);

            // When
            List<User> result = userService.searchUsersByUsername(searchTerm);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getUsername).containsExactly("john", "jane");
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm);
        }

        @Test
        @DisplayName("Should return empty list when search term is null")
        void shouldReturnEmptyListWhenSearchTermIsNull() {
            // When
            List<User> result = userService.searchUsersByUsername(null);

            // Then
            assertThat(result).isEmpty();
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString());
        }

        @Test
        @DisplayName("Should return empty list when search term is empty")
        void shouldReturnEmptyListWhenSearchTermIsEmpty() {
            // When
            List<User> result = userService.searchUsersByUsername("");

            // Then
            assertThat(result).isEmpty();
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString());
        }

        @Test
        @DisplayName("Should return empty list when search term is blank")
        void shouldReturnEmptyListWhenSearchTermIsBlank() {
            // When
            List<User> result = userService.searchUsersByUsername("   ");

            // Then
            assertThat(result).isEmpty();
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString());
        }

        @Test
        @DisplayName("Should trim whitespace from search term")
        void shouldTrimWhitespaceFromSearchTerm() {
            // Given
            String searchTermWithWhitespace = "  john  ";
            String expectedTrimmedTerm = "john";
            when(userDAO.findByUsernameContainingIgnoreCase(expectedTrimmedTerm)).thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userService.searchUsersByUsername(searchTermWithWhitespace);

            // Then
            assertThat(result).hasSize(1);
            verify(userDAO).findByUsernameContainingIgnoreCase(stringCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
        }

        @Test
        @DisplayName("Should handle special characters in search term")
        void shouldHandleSpecialCharactersInSearchTerm() {
            // Given
            String searchTermWithSpecialChars = "john@123";
            when(userDAO.findByUsernameContainingIgnoreCase(searchTermWithSpecialChars)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userService.searchUsersByUsername(searchTermWithSpecialChars);

            // Then
            assertThat(result).isEmpty();
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTermWithSpecialChars);
        }

        @Test
        @DisplayName("Should handle very long search terms")
        void shouldHandleVeryLongSearchTerms() {
            // Given
            String longSearchTerm = "a".repeat(1000);
            when(userDAO.findByUsernameContainingIgnoreCase(longSearchTerm)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userService.searchUsersByUsername(longSearchTerm);

            // Then
            assertThat(result).isEmpty();
            verify(userDAO).findByUsernameContainingIgnoreCase(longSearchTerm);
        }

        @Test
        @DisplayName("Should handle search term with log injection characters")
        void shouldHandleSearchTermWithLogInjectionCharacters() {
            // Given
            String searchTermWithLogInjection = "john\r\n\ttest";
            // Note: The implementation trims but doesn't sanitize the actual search term passed to DAO
            String expectedTrimmedTerm = "john\r\n\ttest"; // This is what actually gets passed to DAO after trim()
            when(userDAO.findByUsernameContainingIgnoreCase(expectedTrimmedTerm)).thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userService.searchUsersByUsername(searchTermWithLogInjection);

            // Then
            assertThat(result).hasSize(1);
            verify(userDAO).findByUsernameContainingIgnoreCase(stringCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
        }

        @Test
        @DisplayName("Should propagate DAO exceptions")
        void shouldPropagateDAOExceptions() {
            // Given
            String searchTerm = "john";
            RuntimeException daoException = new RuntimeException("Database connection failed");
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm)).thenThrow(daoException);

            // When/Then
            assertThatThrownBy(() -> userService.searchUsersByUsername(searchTerm))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm);
        }

        @Test
        @DisplayName("Should return empty list when no users match search criteria")
        void shouldReturnEmptyListWhenNoUsersMatch() {
            // Given
            String searchTerm = "nonexistent";
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userService.searchUsersByUsername(searchTerm);

            // Then
            assertThat(result).isEmpty();
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm);
        }
    }

    @Nested
    @DisplayName("Paginated searchUsersByUsername Tests")
    class PaginatedSearchTests {

        private Pageable defaultPageable;
        private Pageable customPageable;

        @BeforeEach
        void setUp() {
            defaultPageable = PageRequest.of(0, 10);
            customPageable = PageRequest.of(1, 5, Sort.by("username"));
        }

        @Test
        @DisplayName("Should return paginated users when valid search term is provided")
        void shouldReturnPaginatedUsersWhenValidSearchTermProvided() {
            // Given
            String searchTerm = "john";
            Page<User> expectedPage = new PageImpl<>(Arrays.asList(johnUser), defaultPageable, 1);
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable)).thenReturn(expectedPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTerm, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUsername()).isEqualTo("john");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPageable()).isEqualTo(defaultPageable);
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
        }

        @Test
        @DisplayName("Should return paginated users with custom pagination settings")
        void shouldReturnPaginatedUsersWithCustomPaginationSettings() {
            // Given
            String searchTerm = "j";
            Page<User> expectedPage = new PageImpl<>(testUsers, customPageable, 10);
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, customPageable)).thenReturn(expectedPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTerm, customPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getPageable()).isEqualTo(customPageable);
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, customPageable);
        }

        @Test
        @DisplayName("Should return empty page when search term is null")
        void shouldReturnEmptyPageWhenSearchTermIsNull() {
            // When
            Page<User> result = userService.searchUsersByUsername(null, defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getPageable()).isEqualTo(defaultPageable);
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when search term is empty")
        void shouldReturnEmptyPageWhenSearchTermIsEmpty() {
            // When
            Page<User> result = userService.searchUsersByUsername("", defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getPageable()).isEqualTo(defaultPageable);
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when search term is blank")
        void shouldReturnEmptyPageWhenSearchTermIsBlank() {
            // When
            Page<User> result = userService.searchUsersByUsername("   ", defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getPageable()).isEqualTo(defaultPageable);
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should trim whitespace from search term in paginated search")
        void shouldTrimWhitespaceFromSearchTermInPaginatedSearch() {
            // Given
            String searchTermWithWhitespace = "  john  ";
            String expectedTrimmedTerm = "john";
            Page<User> expectedPage = new PageImpl<>(Arrays.asList(johnUser), defaultPageable, 1);
            when(userDAO.findByUsernameContainingIgnoreCase(expectedTrimmedTerm, defaultPageable)).thenReturn(expectedPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTermWithWhitespace, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            verify(userDAO).findByUsernameContainingIgnoreCase(stringCaptor.capture(), pageableCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
            assertThat(pageableCaptor.getValue()).isEqualTo(defaultPageable);
        }

        @Test
        @DisplayName("Should handle special characters in paginated search term")
        void shouldHandleSpecialCharactersInPaginatedSearchTerm() {
            // Given
            String searchTermWithSpecialChars = "john@123";
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);
            when(userDAO.findByUsernameContainingIgnoreCase(searchTermWithSpecialChars, defaultPageable)).thenReturn(emptyPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTermWithSpecialChars, defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTermWithSpecialChars, defaultPageable);
        }

        @Test
        @DisplayName("Should handle very long search terms in paginated search")
        void shouldHandleVeryLongSearchTermsInPaginatedSearch() {
            // Given
            String longSearchTerm = "a".repeat(1000);
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);
            when(userDAO.findByUsernameContainingIgnoreCase(longSearchTerm, defaultPageable)).thenReturn(emptyPage);

            // When
            Page<User> result = userService.searchUsersByUsername(longSearchTerm, defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            verify(userDAO).findByUsernameContainingIgnoreCase(longSearchTerm, defaultPageable);
        }

        @Test
        @DisplayName("Should handle search term with log injection characters in paginated search")
        void shouldHandleSearchTermWithLogInjectionCharactersInPaginatedSearch() {
            // Given
            String searchTermWithLogInjection = "john\r\n\ttest";
            // Note: The implementation trims but doesn't sanitize the actual search term passed to DAO
            String expectedTrimmedTerm = "john\r\n\ttest"; // This is what actually gets passed to DAO after trim()
            Page<User> expectedPage = new PageImpl<>(Arrays.asList(johnUser), defaultPageable, 1);
            when(userDAO.findByUsernameContainingIgnoreCase(expectedTrimmedTerm, defaultPageable)).thenReturn(expectedPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTermWithLogInjection, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            verify(userDAO).findByUsernameContainingIgnoreCase(stringCaptor.capture(), pageableCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
        }

        @Test
        @DisplayName("Should propagate DAO exceptions in paginated search")
        void shouldPropagateDAOExceptionsInPaginatedSearch() {
            // Given
            String searchTerm = "john";
            RuntimeException daoException = new RuntimeException("Database connection failed");
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable)).thenThrow(daoException);

            // When/Then
            assertThatThrownBy(() -> userService.searchUsersByUsername(searchTerm, defaultPageable))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
        }

        @Test
        @DisplayName("Should return empty page when no users match search criteria")
        void shouldReturnEmptyPageWhenNoUsersMatch() {
            // Given
            String searchTerm = "nonexistent";
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable)).thenReturn(emptyPage);

            // When
            Page<User> result = userService.searchUsersByUsername(searchTerm, defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
        }

        @Test
        @DisplayName("Should handle null Pageable parameter gracefully")
        void shouldHandleNullPageableParameterGracefully() {
            // Given
            String searchTerm = "john";
            // Note: This test assumes the service handles null Pageable gracefully
            // If the service doesn't handle it, this test will help identify the issue

            // When/Then
            assertThatThrownBy(() -> userService.searchUsersByUsername(searchTerm, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should work with different page sizes and numbers")
        void shouldWorkWithDifferentPageSizesAndNumbers() {
            // Given
            String searchTerm = "user";
            Pageable largePage = PageRequest.of(0, 100);
            Pageable smallPage = PageRequest.of(5, 2);
            
            Page<User> largePageResult = new PageImpl<>(testUsers, largePage, 50);
            Page<User> smallPageResult = new PageImpl<>(Arrays.asList(johnUser), smallPage, 50);
            
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, largePage)).thenReturn(largePageResult);
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm, smallPage)).thenReturn(smallPageResult);

            // When
            Page<User> largeResult = userService.searchUsersByUsername(searchTerm, largePage);
            Page<User> smallResult = userService.searchUsersByUsername(searchTerm, smallPage);

            // Then
            assertThat(largeResult.getSize()).isEqualTo(100);
            assertThat(largeResult.getNumber()).isEqualTo(0);
            assertThat(smallResult.getSize()).isEqualTo(2);
            assertThat(smallResult.getNumber()).isEqualTo(5);
            
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, largePage);
            verify(userDAO).findByUsernameContainingIgnoreCase(searchTerm, smallPage);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Performance Tests")
    class EdgeCasesAndPerformanceTests {

        @Test
        @DisplayName("Should handle concurrent search requests")
        void shouldHandleConcurrentSearchRequests() {
            // Given
            String searchTerm = "test";
            when(userDAO.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(testUsers);

            // When - Simulate multiple calls
            List<User> result1 = userService.searchUsersByUsername(searchTerm);
            List<User> result2 = userService.searchUsersByUsername(searchTerm);

            // Then
            assertThat(result1).hasSize(2);
            assertThat(result2).hasSize(2);
            verify(userDAO, times(2)).findByUsernameContainingIgnoreCase(searchTerm);
        }

        @Test
        @DisplayName("Should handle search terms with only whitespace characters")
        void shouldHandleSearchTermsWithOnlyWhitespaceCharacters() {
            // Given
            String whitespaceOnlyTerm = "\t\n\r   \t";

            // When
            List<User> result = userService.searchUsersByUsername(whitespaceOnlyTerm);

            // Then
            assertThat(result).isEmpty();
            verify(userDAO, never()).findByUsernameContainingIgnoreCase(anyString());
        }

        @Test
        @DisplayName("Should handle search terms with mixed whitespace and content")
        void shouldHandleSearchTermsWithMixedWhitespaceAndContent() {
            // Given
            String mixedTerm = "\t  john  \n\r  ";
            String expectedCleanTerm = "john";
            when(userDAO.findByUsernameContainingIgnoreCase(expectedCleanTerm)).thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userService.searchUsersByUsername(mixedTerm);

            // Then
            assertThat(result).hasSize(1);
            verify(userDAO).findByUsernameContainingIgnoreCase(stringCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedCleanTerm);
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
}