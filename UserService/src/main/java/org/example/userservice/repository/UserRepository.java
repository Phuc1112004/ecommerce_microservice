package org.example.userservice.repository;

import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.entity.Users;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String userName);
    @Query("""
        SELECT new org.example.common.dto.UserInfoDTO(
            u.userId, u.userName, u.email, u.phone, u.role
        )
        FROM Users u
        WHERE u.userId = :userId
    """)
    UserResponseDTO findUserInfoByUserId(@Param("userId") Long userId);
}
