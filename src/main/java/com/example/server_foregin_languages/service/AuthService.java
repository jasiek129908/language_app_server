package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.dto.AuthenticationResponse;
import com.example.server_foregin_languages.dto.LoginBody;
import com.example.server_foregin_languages.dto.RefreshTokenRequest;
import com.example.server_foregin_languages.dto.RegisterBody;
import com.example.server_foregin_languages.mapper.UtilMapper;
import com.example.server_foregin_languages.repo.AppUserRepository;
import com.example.server_foregin_languages.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UtilMapper utilMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AppUser registerUser(RegisterBody newUser) {
        AppUser appUser = utilMapper.mapFromDtoToUser(newUser);
        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    public AuthenticationResponse loginUser(LoginBody loginBody) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginBody.getEmail(), loginBody.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateAndSaveRefreshToken().getToken())
                .expiresAt(Instant.now().plusSeconds(jwtProvider.getExpirationTimeInSeconds()))
                .email(loginBody.getEmail())
                .nickName(appUserRepository.findByEmail(loginBody.getEmail()).get().getNickName())
                .build();
    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserMail(refreshTokenRequest.getEmail());
        AuthenticationResponse build = AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .email(refreshTokenRequest.getEmail())
                .expiresAt(Instant.now().plusSeconds(jwtProvider.getExpirationTimeInSeconds()))
                .build();
        return build;
    }

    public AppUser getLoggedInUser() {
        UserDetails principal = (UserDetails)SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return appUserRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }
}
