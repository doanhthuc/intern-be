package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.auth.ChangePasswordRequestDTO;
import com.mgmtp.easyquizy.dto.user.UserDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.mapper.UserMapper;
import com.mgmtp.easyquizy.model.role.RoleEntity;
import com.mgmtp.easyquizy.model.user.UserEntity;
import com.mgmtp.easyquizy.repository.RoleRepository;
import com.mgmtp.easyquizy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return user.get();
    }

    @Override
    @Transactional
    public UserEntity loadUserById(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) {
            throw new UsernameNotFoundException("Failed");
        }
        return userEntityOptional.get();
    }

    @Override
    public void changePassword(UserEntity user, ChangePasswordRequestDTO request) throws InvalidFieldsException {
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw InvalidFieldsException.fromFieldError("currentPassword", "Current password is incorrect.");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw InvalidFieldsException.fromFieldError("newPassword", "New password must be different from current password");
        }
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Override
    public String resetPassword(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No User record exists for the given id: " + id));

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 1);
        CharacterData specialCharacterCustom = new CharacterData() {
            public static final String REGEX_SPECIAL = "ư!@#$%^&*()-_+=[]{};:',.<>/?";
            @Override
            public String getErrorCode() {
                return "INSUFFICIENT_SPECIAL";
            }

            @Override
            public String getCharacters() {
                return REGEX_SPECIAL;
            }
        };
        CharacterRule specialCharRule = new CharacterRule(specialCharacterCustom, 1);
        String newPassword = passwordGenerator.generatePassword(8, upperCaseRule, lowerCaseRule, digitRule, specialCharRule);

        String encodeNewPassword = passwordEncoder.encode(newPassword);
        userEntity.setPassword(encodeNewPassword);
        userRepository.save(userEntity);
        return newPassword;
    }

    @Override
    public UserDTO getUserById(Long id) throws RecordNotFoundException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No User record exists for the given id: " + id));
        return userMapper.entityToUserDTO(userEntity);
    }

    @Override
    public Page<UserDTO> getAllUsers(String keyword, int offset, int limit) {
        int pageNo = offset / limit;
        Specification<UserEntity> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").descending());

        Page<UserEntity> page = userRepository.findAll(filterSpec, pageable);
        return page.map(userMapper::entityToUserDTO);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is a required field");
        }

        UserEntity userEntity = userRepository.findById(userDTO.getId()).orElseThrow(() ->
                new RecordNotFoundException("No User record exists for the given id: " + userDTO.getId()));
        UserEntity updated = userMapper.userDtoToUserEntity(userDTO);
        List<RoleEntity> roleEntities = roleRepository.findAllByRoleNameIsIn(userDTO.getRoles());
        updated.setPassword(userEntity.getPassword());
        updated.setRoles(roleEntities);
        userRepository.save(updated);
        return userMapper.entityToUserDTO(updated);
    }

    @Override
    public void deleteUserById(Long id) throws RecordNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new RecordNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}