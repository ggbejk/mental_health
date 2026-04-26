package com.example.mentalhealth.controller;

import com.example.mentalhealth.dto.LoginDTO;
import com.example.mentalhealth.dto.LoginVO;
import com.example.mentalhealth.entity.SysUser;
import com.example.mentalhealth.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @PostMapping("/login")
    public LoginVO login(@RequestBody LoginDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        SysUser user = sysUserRepository
                .findByUsernameAndPasswordAndStatus(dto.getUsername(), dto.getPassword(), 1)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        String token = UUID.randomUUID().toString();

        return new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getCollege(),
                user.getClassName(),
                token
        );
    }
}