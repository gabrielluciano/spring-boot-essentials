package br.com.devdojo.repository;

import br.com.devdojo.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
