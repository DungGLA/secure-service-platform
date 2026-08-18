package com.example.secure_service_platform.config;

import com.example.secure_service_platform.permission.entity.Permission;
import com.example.secure_service_platform.permission.repository.PermissionRepository;
import com.example.secure_service_platform.role.entity.Role;
import com.example.secure_service_platform.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {

        return args -> {

            Permission read =
                    permissionRepository.findByName("USER_READ")
                            .orElseGet(() ->
                                    permissionRepository.save(
                                            Permission.builder()
                                                    .name("USER_READ")
                                                    .build()
                                    ));

            Permission update =
                    permissionRepository.findByName("USER_UPDATE")
                            .orElseGet(() ->
                                    permissionRepository.save(
                                            Permission.builder()
                                                    .name("USER_UPDATE")
                                                    .build()
                                    ));

            Role userRole =
                    roleRepository.findByName("USER")
                            .orElseGet(() ->
                                    roleRepository.save(
                                            Role.builder()
                                                    .name("USER")
                                                    .build()
                                    ));

            userRole.getPermissions().add(read);
            userRole.getPermissions().add(update);

            roleRepository.save(userRole);
        };
    }
}
