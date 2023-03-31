package academy.devdojo.springbootessentials.repository;

import academy.devdojo.springbootessentials.domain.DevDojoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long> {

    Optional<DevDojoUser> findByUsername(String username);
}
