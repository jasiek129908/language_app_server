package com.example.server_foregin_languages.service.userDetails;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.repo.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AppUser> userOptional = appUserRepository.findByEmail(email);
        AppUser user = userOptional
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user " +
                                "Found with email : " + email));
        return new UserDetailsImpl(user);
    }

}
