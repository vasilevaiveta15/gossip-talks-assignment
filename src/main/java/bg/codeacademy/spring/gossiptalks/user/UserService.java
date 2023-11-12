package bg.codeacademy.spring.gossiptalks.user;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService
{

  List<User> getUsers(String name, boolean f);

  User createUser(User user);

  User getCurrentUser();

  User changeCurrentUserPassword(String password, String passwordConfirmation, String oldPassword);

  User followUser(String username, boolean follow);

  User getByEmail(String email);

  User getByUsername(String username);

  ModelMapper getModelMapper();

  List<User> findAllFriendsOfUser(String username);


}
