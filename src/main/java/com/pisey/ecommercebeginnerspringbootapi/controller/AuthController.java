package com.pisey.ecommercebeginnerspringbootapi.controller;


import com.pisey.ecommercebeginnerspringbootapi.config.security.jwt.JwtUtils;
import com.pisey.ecommercebeginnerspringbootapi.domain.*;
import com.pisey.ecommercebeginnerspringbootapi.dto.LogOutRequest;
import com.pisey.ecommercebeginnerspringbootapi.event.OnUserLogoutSuccessEvent;
import com.pisey.ecommercebeginnerspringbootapi.exception.TokenRefreshException;
import com.pisey.ecommercebeginnerspringbootapi.exception.UserLogoutException;
import com.pisey.ecommercebeginnerspringbootapi.payload.request.LoginRequest;
import com.pisey.ecommercebeginnerspringbootapi.payload.request.SignupRequest;
import com.pisey.ecommercebeginnerspringbootapi.payload.request.TokenRefreshRequest;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.JwtResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.MessageResponse;
import com.pisey.ecommercebeginnerspringbootapi.payload.response.TokenRefreshResponse;
import com.pisey.ecommercebeginnerspringbootapi.repository.RoleRepository;
import com.pisey.ecommercebeginnerspringbootapi.repository.UserRepository;
import com.pisey.ecommercebeginnerspringbootapi.service.RefreshTokenService;
import com.pisey.ecommercebeginnerspringbootapi.service.UserDeviceService;
import com.pisey.ecommercebeginnerspringbootapi.service.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);


        //new flow

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));

//        userDeviceService.findByUserId(user.getId())
//                .map(UserDevice::getRefreshToken)
//                .map(RefreshToken::getId)
//                .ifPresent(refreshTokenService::deleteByUserId);

        Optional<UserDevice> userDeviceOptional = userDeviceService.findByUserId(user.getId());
        if (userDeviceOptional.isPresent()) {
            refreshTokenService.deleteByUserId(user.getId());
        }

        UserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        userDevice.setUser(user);
        userDevice.setRefreshToken(refreshToken);
        refreshToken.setUserDevice(userDevice);
        refreshToken.setUser(user);
        refreshToken = refreshTokenService.save(refreshToken);

        //new flow


        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        Optional<String> token = Optional.of(refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    refreshTokenService.verifyExpiration(refreshToken);
                    userDeviceService.verifyRefreshAvailability(refreshToken);
                    refreshTokenService.increaseCount(refreshToken);
                    return refreshToken;
                })
                .map(RefreshToken::getUserDevice)
                .map(UserDevice::getUser)
                .map(u -> jwtUtils.generateTokenFromUsername(u.getUsername()))
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Missing refresh token in database.Please login again")));

        return ResponseEntity.ok().body(new TokenRefreshResponse(token.get(), requestRefreshToken, jwtUtils.getExpiryDuration()));

//        return refreshTokenService.findByToken(requestRefreshToken)
//                .map(refreshTokenService::verifyExpiration)
//                .map(RefreshToken::getUser)
//                .map(user -> {
//                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
//                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
//                })
//                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
//                        "Refresh token is not in database!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                true);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {

        String deviceId = logOutRequest.getDeviceInfo().getDeviceId();
        UserDevice userDevice = userDeviceService.findByUserId(logOutRequest.getUserId())
                .filter(device -> device.getDeviceId().equals(deviceId))
                .orElseThrow(() -> new UserLogoutException(logOutRequest.getDeviceInfo().getDeviceId(), "Invalid device Id supplied. No matching device found for the given user "));

        //refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        Long userId = userDevice.getRefreshToken().getUser().getId();
        if (userId > 0) {
            refreshTokenService.deleteByUserId(userId);
        }

        OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent("", logOutRequest.getToken(), logOutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);

        return ResponseEntity.ok(new MessageResponse("User has successfully logged out from the system!"));
    }


}
