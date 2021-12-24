package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.project.entities.User;
import ua.training.project.repositories.RoleRepository;
import ua.training.project.repositories.UserRepository;
import ua.training.project.utils.ConstantHolder;
import ua.training.project.utils.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

    public void registerUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleRepository.findByName("ROLE_STUDENT"));
        user.setBalance(BigDecimal.valueOf(0.00));
        userRepository.save(user);
    }

    public Page<User> findUsersByNameOrSurname(String searchValue, Integer pageNumber){
        return userRepository.findByNameOrSurname(searchValue, PageRequest.of(pageNumber, ConstantHolder.MAX_RECORDS_PER_PAGE,
                Sort.by(Sort.Direction.ASC, "name")));
    }

    @Transactional
    public User updateUserBalance(Long userId, BigDecimal value){
        User user = userRepository.getById(userId);
        user.setBalance(user.getBalance().add(value));
        return userRepository.save(user);
    }



}
