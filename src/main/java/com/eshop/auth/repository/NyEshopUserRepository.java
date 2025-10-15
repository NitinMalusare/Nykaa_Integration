package com.eshop.auth.repository;

import com.eshop.auth.entity.NyEshopUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NyEshopUserRepository extends JpaRepository<NyEshopUser, Long> {

    Optional<NyEshopUser> findByUsername(String username);

    Optional<NyEshopUser> findByAccessToken(String accessToken);

}
