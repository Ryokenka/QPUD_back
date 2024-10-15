package com.takima.backskeleton.services;

import com.takima.backskeleton.DTO.UserDto;
import com.takima.backskeleton.DTO.UserMapper;
import com.takima.backskeleton.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.takima.backskeleton.DTO.UserDto;
@Service
@RequiredArgsConstructor
public class UserService {
    private final com.takima.backskeleton.DAO.UserDao userDao;

    public List<UserDto> findAll() {
        return userDao.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        return userDao.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void addUser(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        userDao.save(user);
    }

    public void updateUser(Long id, UserDto userDto) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDto.getUsername());
        user.setAdmin(userDto.isAdmin());
        userDao.save(user);
    }

    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}
