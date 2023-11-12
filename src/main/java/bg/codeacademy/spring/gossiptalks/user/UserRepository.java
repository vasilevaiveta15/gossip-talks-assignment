package bg.codeacademy.spring.gossiptalks.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long>
{
  List<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String nameSubstring, String usernameSubstring);

  User findByEmail(String email);

  User findByUsername(String username);

  @Query(value = "SELECT u.friends FROM User u WHERE u.username = ?1")
  List<User> findAllFriendsOfUser(String username);
}
