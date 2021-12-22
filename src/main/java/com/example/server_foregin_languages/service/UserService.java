package com.example.server_foregin_languages.service;

import com.example.server_foregin_languages.domain.AppUser;
import com.example.server_foregin_languages.dto.ChangePasswordBody;
import com.example.server_foregin_languages.dto.ChangedEmailResponse;
import com.example.server_foregin_languages.dto.ResponseMessage;
import com.example.server_foregin_languages.repo.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AuthService authService;
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AppUser changeUserNickName(String newNickname) {
        AppUser loggedInUser = authService.getLoggedInUser();
        loggedInUser.setNickName(newNickname);
        appUserRepository.save(loggedInUser);
        return loggedInUser;
    }

    public ResponseMessage changeUserPassword(ChangePasswordBody changePassword) {
        AppUser loggedInUser = authService.getLoggedInUser();
        if (passwordEncoder.matches(changePassword.getOldPassword(), loggedInUser.getPassword())) {
            loggedInUser.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            appUserRepository.save(loggedInUser);
            return new ResponseMessage("Password changed");
        }else{
            return new ResponseMessage("Old password was incorrect");
        }
    }

    public ChangedEmailResponse changeUserEmail(String newEmail) {
        AppUser loggedInUser = authService.getLoggedInUser();
        loggedInUser.setEmail(newEmail);
        appUserRepository.save(loggedInUser);

        return new ChangedEmailResponse(newEmail);
    }

    public boolean checkIfAnyUserHasNickname(String nickname) {
        return appUserRepository.findByNickName(nickname).isPresent();
    }
}
