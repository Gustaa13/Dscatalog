package com.gustaa13.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustaa13.dscatalog.dto.RoleDTO;
import com.gustaa13.dscatalog.dto.UserDTO;
import com.gustaa13.dscatalog.dto.UserInsertDTO;
import com.gustaa13.dscatalog.entities.Role;
import com.gustaa13.dscatalog.entities.User;
import com.gustaa13.dscatalog.repositories.RoleRepository;
import com.gustaa13.dscatalog.repositories.UserRepository;
import com.gustaa13.dscatalog.services.exceptions.DatabaseException;
import com.gustaa13.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {

        Page<User> userList = repository.findAll(pageable);

        return userList.map(user -> new UserDTO(user));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        
        Optional<User> entity = repository.findById(id);
        User user = entity.orElseThrow(() -> new ResourceNotFoundException("Id not found" + id));

        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO userInsertDTO) {
        
        User user = new User();
        copyUserDtoToUser(userInsertDTO, user);
        user.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));
        user = repository.save(user);

        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {

        try {
            User user = repository.getReferenceById(id);
            copyUserDtoToUser(userDTO, user);
            user = repository.save(user);

            return new UserDTO(user);
        } catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    public void delete(Long id) {
        if(!repository.existsById(id)) throw new ResourceNotFoundException("Id not found" + id);

        try {
            repository.deleteById(id);
        } catch(DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyUserDtoToUser(UserDTO userDTO, User user) {

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());

        user.getRoles().clear();
        for (RoleDTO roleDto : userDTO.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDto.getId());
            user.getRoles().add(role);
        }
    }
}
