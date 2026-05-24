package com.jordanrobin.financial_erp.domain.auth;

import com.jordanrobin.financial_erp.domain.auth.invitationtoken.InvitationToken;
import com.jordanrobin.financial_erp.domain.auth.invitationtoken.InvitationTokenService;
//import com.jordanrobin.financial_erp.domain.auth.jwt.RefreshTokenService;
//import com.jordanrobin.financial_erp.domain.auth.jwt.AccessTokenService;
//import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
//import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import com.jordanrobin.financial_erp.domain.auth.user.UserStatus;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.CreatePasswordCommand;
import com.jordanrobin.financial_erp.shared.exception.auth.InvalidInvitationTokenException;
import com.jordanrobin.financial_erp.shared.exception.resource.ResourceNotFoundException;
import com.jordanrobin.financial_erp.shared.utils.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    //    private final AuthenticationManager authenticationManager;
//    private final AccessTokenService tokenService;
//    private final RefreshTokenService refreshTokenService;
//    private final CustomUserDetailsService customUserDetailsService;
    private final InvitationTokenService invitationTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
//
//    public TokenPair login(String email, String password) {
//        Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(email, password)
//        );
//
//        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
//            throw new IllegalStateException("Le principal d'authentification est invalide ou absent");
//        }
//        String accessToken = tokenService.generateAccessToken(authentication);
//        String refreshToken = refreshTokenService.create(userDetails.getUser()).getTokenHash();
//
//        return TokenPair.builder().accessToken(accessToken).refreshToken(refreshToken).build();
//    }
//
//    public TokenPair refresh(String refreshToken) {
//        // TODO implémenter mesures de sécurité en cas de vol du refresh token
//        RefreshToken validToken = refreshTokenService.validateAndRotate(refreshToken);
//        User user = validToken.getUser();
//
//        CustomUserDetails userDetails = new CustomUserDetails(
//            user,
//            customUserDetailsService.getAuthorities(user)
//        );
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//            userDetails, null, userDetails.getAuthorities()
//        );
//
//        String newAccessToken = tokenService.generateAccessToken(authentication);
//        String newRefreshToken = refreshTokenService.create(user).getToken();
//
//        return TokenPair.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
//    }
//
//    public void logout(String refreshToken) {
//        refreshTokenService.revoke(refreshToken);
//    }

    @Transactional
    public void setPassword(CreatePasswordCommand command) {
        // Récupération et mise à jour de l'invitation token
        String tokenHash = HashUtils.sha256(command.invitationToken());
        InvitationToken token = invitationTokenService.getTokenWithUser(tokenHash)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    InvitationToken.class.getSimpleName(),
                    "token",
                    command.invitationToken()
                )
            );
        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidInvitationTokenException();
        }
        invitationTokenService.markAsUsed(token);

        // Mise à jour de l'utilisateur
        userService.activate(token.getUser(), passwordEncoder.encode(command.password()));
    }
}
