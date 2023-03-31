package academy.devdojo.springbootessentials.service;

import academy.devdojo.springbootessentials.repository.DevDojoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevDojoUserDetailsService implements UserDetailsService {

    private final DevDojoUserRepository devDojoUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return devDojoUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("DevDojo User not found"));
    }
}
