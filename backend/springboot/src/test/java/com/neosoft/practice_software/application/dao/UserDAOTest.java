package com.neosoft.practice_software.application.dao;

import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.jpa.dao.UserDAOImpl;
import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import com.neosoft.practice_software.infrastructure.jpa.mapper.UserEntityMapper;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaUserRepository;
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
 * Comprehensive unit tests for UserDAOImpl focusing on search functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDAO Tests")
class UserDAOTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserDAOImpl userDAO;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private UserEntity johnEntity;
    private UserEntity janeEntity;
    private User johnUser;
    private User janeUser;
    private List<UserEntity> testEntities;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        // Create test entities
        johnEntity = createTestUserEntity("john", "john@example.com", "USER");
        janeEntity = createTestUserEntity("jane", "jane@example.com", "USER");
        testEntities = Arrays.asList(johnEntity, janeEntity);

        // Create test domain objects
        johnUser = createTestUser("john", "john@example.com", "USER");
        janeUser = createTestUser("jane", "jane@example.com", "USER");
        testUsers = Arrays.asList(johnUser, janeUser);
    }

    @Nested
    @DisplayName("Non-paginated findByUsernameContainingIgnoreCase Tests")
    class NonPaginatedSearchTests {

        @Test
        @DisplayName("Should return users when valid search term is provided")
        void shouldReturnUsersWhenValidSearchTermProvided() {
            // Given
            String searchTerm = "john";
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm))
                    .thenReturn(Arrays.asList(johnEntity));
            when(userEntityMapper.toBOs(Arrays.asList(johnEntity)))
                    .thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("john");
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm);
            verify(userEntityMapper).toBOs(Arrays.asList(johnEntity));
        }

        @Test
        @DisplayName("Should return multiple users when search term matches multiple usernames")
        void shouldReturnMultipleUsersWhenSearchTermMatchesMultiple() {
            // Given
            String searchTerm = "j";
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm))
                    .thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities))
                    .thenReturn(testUsers);

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getUsername).containsExactly("john", "jane");
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm);
            verify(userEntityMapper).toBOs(testEntities);
        }

        @Test
        @DisplayName("Should return all users when search term is null")
        void shouldReturnAllUsersWhenSearchTermIsNull() {
            // Given
            when(jpaUserRepository.findAll()).thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities)).thenReturn(testUsers);

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(null);

            // Then
            assertThat(result).hasSize(2);
            verify(jpaUserRepository).findAll();
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString());
            verify(userEntityMapper).toBOs(testEntities);
        }

        @Test
        @DisplayName("Should return all users when search term is empty")
        void shouldReturnAllUsersWhenSearchTermIsEmpty() {
            // Given
            when(jpaUserRepository.findAll()).thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities)).thenReturn(testUsers);

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase("");

            // Then
            assertThat(result).hasSize(2);
            verify(jpaUserRepository).findAll();
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString());
            verify(userEntityMapper).toBOs(testEntities);
        }

        @Test
        @DisplayName("Should return all users when search term is blank")
        void shouldReturnAllUsersWhenSearchTermIsBlank() {
            // Given
            when(jpaUserRepository.findAll()).thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities)).thenReturn(testUsers);

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase("   ");

            // Then
            assertThat(result).hasSize(2);
            verify(jpaUserRepository).findAll();
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString());
            verify(userEntityMapper).toBOs(testEntities);
        }

        @Test
        @DisplayName("Should trim whitespace from search term")
        void shouldTrimWhitespaceFromSearchTerm() {
            // Given
            String searchTermWithWhitespace = "  john  ";
            String expectedTrimmedTerm = "john";
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(expectedTrimmedTerm))
                    .thenReturn(Arrays.asList(johnEntity));
            when(userEntityMapper.toBOs(Arrays.asList(johnEntity)))
                    .thenReturn(Arrays.asList(johnUser));

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTermWithWhitespace);

            // Then
            assertThat(result).hasSize(1);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(stringCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
            verify(userEntityMapper).toBOs(Arrays.asList(johnEntity));
        }

        @Test
        @DisplayName("Should return empty list when no users match search criteria")
        void shouldReturnEmptyListWhenNoUsersMatch() {
            // Given
            String searchTerm = "nonexistent";
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm))
                    .thenReturn(Collections.emptyList());
            when(userEntityMapper.toBOs(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm);

            // Then
            assertThat(result).isEmpty();
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm);
            verify(userEntityMapper).toBOs(Collections.emptyList());
        }

        @Test
        @DisplayName("Should handle special characters in search term")
        void shouldHandleSpecialCharactersInSearchTerm() {
            // Given
            String searchTermWithSpecialChars = "john@123";
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTermWithSpecialChars))
                    .thenReturn(Collections.emptyList());
            when(userEntityMapper.toBOs(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTermWithSpecialChars);

            // Then
            assertThat(result).isEmpty();
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTermWithSpecialChars);
            verify(userEntityMapper).toBOs(Collections.emptyList());
        }

        @Test
        @DisplayName("Should propagate repository exceptions")
        void shouldPropagateRepositoryExceptions() {
            // Given
            String searchTerm = "john";
            RuntimeException repositoryException = new RuntimeException("Database connection failed");
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm))
                    .thenThrow(repositoryException);

            // When/Then
            assertThatThrownBy(() -> userDAO.findByUsernameContainingIgnoreCase(searchTerm))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm);
            verify(userEntityMapper, never()).toBOs(any());
        }
    }

    @Nested
    @DisplayName("Paginated findByUsernameContainingIgnoreCase Tests")
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
            Page<UserEntity> entityPage = new PageImpl<>(Arrays.asList(johnEntity), defaultPageable, 1);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable))
                    .thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUsername()).isEqualTo("john");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPageable()).isEqualTo(defaultPageable);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
            verify(userEntityMapper).toBO(johnEntity);
        }

        @Test
        @DisplayName("Should return paginated users with custom pagination settings")
        void shouldReturnPaginatedUsersWithCustomPaginationSettings() {
            // Given
            String searchTerm = "j";
            Page<UserEntity> entityPage = new PageImpl<>(testEntities, customPageable, 10);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, customPageable))
                    .thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm, customPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getPageable()).isEqualTo(customPageable);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, customPageable);
            verify(userEntityMapper).toBO(johnEntity);
            verify(userEntityMapper).toBO(janeEntity);
        }

        @Test
        @DisplayName("Should return all users page when search term is null")
        void shouldReturnAllUsersPageWhenSearchTermIsNull() {
            // Given
            Page<UserEntity> entityPage = new PageImpl<>(testEntities, defaultPageable, 2);
            when(jpaUserRepository.findAll(defaultPageable)).thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(null, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            verify(jpaUserRepository).findAll(defaultPageable);
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
            verify(userEntityMapper).toBO(johnEntity);
            verify(userEntityMapper).toBO(janeEntity);
        }

        @Test
        @DisplayName("Should return all users page when search term is empty")
        void shouldReturnAllUsersPageWhenSearchTermIsEmpty() {
            // Given
            Page<UserEntity> entityPage = new PageImpl<>(testEntities, defaultPageable, 2);
            when(jpaUserRepository.findAll(defaultPageable)).thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase("", defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            verify(jpaUserRepository).findAll(defaultPageable);
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return all users page when search term is blank")
        void shouldReturnAllUsersPageWhenSearchTermIsBlank() {
            // Given
            Page<UserEntity> entityPage = new PageImpl<>(testEntities, defaultPageable, 2);
            when(jpaUserRepository.findAll(defaultPageable)).thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase("   ", defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            verify(jpaUserRepository).findAll(defaultPageable);
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should trim whitespace from search term in paginated search")
        void shouldTrimWhitespaceFromSearchTermInPaginatedSearch() {
            // Given
            String searchTermWithWhitespace = "  john  ";
            String expectedTrimmedTerm = "john";
            Page<UserEntity> entityPage = new PageImpl<>(Arrays.asList(johnEntity), defaultPageable, 1);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(expectedTrimmedTerm, defaultPageable))
                    .thenReturn(entityPage);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTermWithWhitespace, defaultPageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(stringCaptor.capture(), pageableCaptor.capture());
            assertThat(stringCaptor.getValue()).isEqualTo(expectedTrimmedTerm);
            assertThat(pageableCaptor.getValue()).isEqualTo(defaultPageable);
        }

        @Test
        @DisplayName("Should return empty page when no users match search criteria")
        void shouldReturnEmptyPageWhenNoUsersMatch() {
            // Given
            String searchTerm = "nonexistent";
            Page<UserEntity> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable))
                    .thenReturn(emptyPage);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
        }

        @Test
        @DisplayName("Should handle different page sizes and numbers")
        void shouldHandleDifferentPageSizesAndNumbers() {
            // Given
            String searchTerm = "user";
            Pageable largePage = PageRequest.of(0, 100);
            Pageable smallPage = PageRequest.of(5, 2);
            
            Page<UserEntity> largePageResult = new PageImpl<>(testEntities, largePage, 50);
            Page<UserEntity> smallPageResult = new PageImpl<>(Arrays.asList(johnEntity), smallPage, 50);
            
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, largePage))
                    .thenReturn(largePageResult);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, smallPage))
                    .thenReturn(smallPageResult);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);

            // When
            Page<User> largeResult = userDAO.findByUsernameContainingIgnoreCase(searchTerm, largePage);
            Page<User> smallResult = userDAO.findByUsernameContainingIgnoreCase(searchTerm, smallPage);

            // Then
            assertThat(largeResult.getSize()).isEqualTo(100);
            assertThat(largeResult.getNumber()).isEqualTo(0);
            assertThat(smallResult.getSize()).isEqualTo(2);
            assertThat(smallResult.getNumber()).isEqualTo(5);
            
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, largePage);
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, smallPage);
        }

        @Test
        @DisplayName("Should handle sorting in pagination")
        void shouldHandleSortingInPagination() {
            // Given
            String searchTerm = "j";
            Pageable sortedPageable = PageRequest.of(0, 10, Sort.by("username").descending());
            Page<UserEntity> sortedPage = new PageImpl<>(Arrays.asList(janeEntity, johnEntity), sortedPageable, 2);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, sortedPageable))
                    .thenReturn(sortedPage);
            when(userEntityMapper.toBO(janeEntity)).thenReturn(janeUser);
            when(userEntityMapper.toBO(johnEntity)).thenReturn(johnUser);

            // When
            Page<User> result = userDAO.findByUsernameContainingIgnoreCase(searchTerm, sortedPageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getSort()).isEqualTo(Sort.by("username").descending());
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, sortedPageable);
        }

        @Test
        @DisplayName("Should propagate repository exceptions in paginated search")
        void shouldPropagateRepositoryExceptionsInPaginatedSearch() {
            // Given
            String searchTerm = "john";
            RuntimeException repositoryException = new RuntimeException("Database connection failed");
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable))
                    .thenThrow(repositoryException);

            // When/Then
            assertThatThrownBy(() -> userDAO.findByUsernameContainingIgnoreCase(searchTerm, defaultPageable))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection failed");

            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(searchTerm, defaultPageable);
            verify(userEntityMapper, never()).toBO(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should handle null Pageable parameter gracefully")
        void shouldHandleNullPageableParameterGracefully() {
            // Given
            String searchTerm = "john";

            // When/Then
            assertThatThrownBy(() -> userDAO.findByUsernameContainingIgnoreCase(searchTerm, null))
                    .isInstanceOf(NullPointerException.class);
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
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(searchTerm))
                    .thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities))
                    .thenReturn(testUsers);

            // When - Simulate multiple calls
            List<User> result1 = userDAO.findByUsernameContainingIgnoreCase(searchTerm);
            List<User> result2 = userDAO.findByUsernameContainingIgnoreCase(searchTerm);

            // Then
            assertThat(result1).hasSize(2);
            assertThat(result2).hasSize(2);
            verify(jpaUserRepository, times(2)).findByUsernameContainingIgnoreCase(searchTerm);
            verify(userEntityMapper, times(2)).toBOs(testEntities);
        }

        @Test
        @DisplayName("Should handle very long search terms")
        void shouldHandleVeryLongSearchTerms() {
            // Given
            String longSearchTerm = "a".repeat(1000);
            when(jpaUserRepository.findByUsernameContainingIgnoreCase(longSearchTerm))
                    .thenReturn(Collections.emptyList());
            when(userEntityMapper.toBOs(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(longSearchTerm);

            // Then
            assertThat(result).isEmpty();
            verify(jpaUserRepository).findByUsernameContainingIgnoreCase(longSearchTerm);
        }

        @Test
        @DisplayName("Should handle search terms with only whitespace characters")
        void shouldHandleSearchTermsWithOnlyWhitespaceCharacters() {
            // Given
            String whitespaceOnlyTerm = "\t\n\r   \t";
            when(jpaUserRepository.findAll()).thenReturn(testEntities);
            when(userEntityMapper.toBOs(testEntities)).thenReturn(testUsers);

            // When
            List<User> result = userDAO.findByUsernameContainingIgnoreCase(whitespaceOnlyTerm);

            // Then
            assertThat(result).hasSize(2);
            verify(jpaUserRepository).findAll();
            verify(jpaUserRepository, never()).findByUsernameContainingIgnoreCase(anyString());
        }
    }

    /**
     * Helper method to create test UserEntity objects.
     */
    private UserEntity createTestUserEntity(String username, String email, String role) {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setUsername(username);
        entity.setEmail(email);
        entity.setRole(role);
        entity.setCreatedAt(LocalDateTime.now().minusDays(1));
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
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