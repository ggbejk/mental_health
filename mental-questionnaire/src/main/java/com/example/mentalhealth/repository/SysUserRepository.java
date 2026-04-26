package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    long countByRole(String role);

    Optional<SysUser> findByUsernameAndPasswordAndStatus(String username, String password, Integer status);

    Optional<SysUser> findByUsername(String username);

    List<SysUser> findByClassName(String className);

    List<SysUser> findByClassNameAndRole(String className, String role);
    
    List<SysUser> findByRole(String role);
}