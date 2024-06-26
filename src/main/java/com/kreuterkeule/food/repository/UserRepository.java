package com.kreuterkeule.food.repository;

import com.kreuterkeule.food.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findAllByUsernameLikeIgnoreCase(String string, Pageable pageable);
}
