package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.domain.auth.invitationtoken.InvitationTokenService;
import com.jordanrobin.financial_erp.domain.auth.invitationtoken.dtos.CreateTokenResult;
import com.jordanrobin.financial_erp.domain.auth.user.mappers.SuperUserDomainMapper;
import com.jordanrobin.financial_erp.domain.auth.user.mappers.UserDomainMapper;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.CreateSuperUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserOutput;
import com.jordanrobin.financial_erp.infrastructure.messaging.EmailService;
import com.jordanrobin.financial_erp.shared.exception.resource.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserDomainMapper userDomainMapper;
    private final SuperUserDomainMapper superUserDomainMapper;
    private final InvitationTokenService invitationTokenService;
    private final EmailService emailService;
//    private final PasswordEncoder passwordEncoder;
//    private final RoleService roleService;
//
//    public UserResponse getByEmail(String email) {
//        return userRepository.findByEmail(email)
//            .map(userDomainMapper::entityToResponse)
//            .orElseThrow(() -> new UserExceptions.UserNotFoundException(email));
//    }
//
//    public UserResponse getById(UUID id) {
//        return userRepository.findById(id)
//            .map(userDomainMapper::entityToResponse)
//            .orElseThrow(() -> new UserExceptions.UserNotFoundException(id.toString()));
//    }

    @Transactional
    public UserOutput createSuperUser(CreateSuperUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new ResourceAlreadyExistsException(User.class.getSimpleName(), "email", command.email());
        }

        User user = superUserDomainMapper.commandToEntity(command);
        user.setStatus(UserStatus.PENDING);
        user.setSuperAdmin(true);
        User userSaved = userRepository.save(user);

        CreateTokenResult createTokenResult = invitationTokenService.createToken(userSaved);

        emailService.sendInvitation(userSaved.getEmail(), createTokenResult.rawToken());

        return UserOutput.builder()
            .id(userSaved.getId())
            .email(userSaved.getEmail())
            .status(userSaved.getStatus())
            .build();
    }

    @Transactional
    public void activate(User user, String hashedPassword) {
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
    }

//    @Transactional
//    public UserResponse create(CreateUserCommand request) {
//        if (userRepository.existsByEmail(request.email())) {
//            throw new UserExceptions.EmailAlreadyExistsException(request.email());
//        }
//        User user = userDomainMapper.commandToEntity(request);
//        user.setPassword(passwordEncoder.encode(request.password()));
//
//        Set<Role> roles = request.roles().stream()
//            .map(roleService::findByNameOrThrow)
//            .collect(Collectors.toSet());
//        user.setRoles(roles);
//
//        return userDomainMapper.entityToResponse(userRepository.save(user));
//    }
}
