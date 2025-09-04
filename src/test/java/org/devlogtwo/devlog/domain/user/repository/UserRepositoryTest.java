package org.devlogtwo.devlog.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = User.signUp("testuser1", "개발자", "test1@example.com", "password", UserRole.USER);
        User user2 = User.signUp("myuser", "테스트", "test2@example.com", "password", UserRole.USER);
        User user3 = User.signUp("another", "test", "test3@example.com", "password", UserRole.USER);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("username 또는 name에 키워드가 포함된 사용자를 검색할 수 있다.")
    void findAllByUsernameContainsOrNameContains() {
        // given
        String keyword = "test";

        // when
        List<User> foundUsers = userRepository.findAllByUsernameContainsOrNameContains(keyword, keyword);

        // then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getUsername).containsExactlyInAnyOrder("testuser1", "another");
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 리스트를 반환한다.")
    void findAllByUsernameContainsOrNameContains_NoResult() {
        // given
        String keyword = "nonexistent";

        // when
        List<User> foundUsers = userRepository.findAllByUsernameContainsOrNameContains(keyword, keyword);

        // then
        assertThat(foundUsers).isEmpty();
    }
}
