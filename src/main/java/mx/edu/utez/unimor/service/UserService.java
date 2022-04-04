package mx.edu.utez.unimor.service;


import lombok.RequiredArgsConstructor;
import mx.edu.utez.unimor.entity.Person;
import mx.edu.utez.unimor.entity.Role;
import mx.edu.utez.unimor.entity.User;
import mx.edu.utez.unimor.entity.dto.Login;
import mx.edu.utez.unimor.repository.PersonRepository;
import mx.edu.utez.unimor.repository.RoleRepository;
import mx.edu.utez.unimor.repository.UserRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.response.ResponseMessage;
import mx.edu.utez.unimor.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;


    @Transactional(readOnly = true)
    public Object findAll(){
        try{
            return new ResponseEntity<>(response(1,userRepository.findAll()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(3,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Object findById(Long id){
        try{
            return new ResponseEntity<>(response(1,userRepository.findById(id)), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(3, ResponseMessage.USER_NOT_FOUND), HttpStatus.OK);
        }
    }

    @Transactional
    public Object save(User user){
        try{
            Role role =  roleRepository.findById(user.getRole().getId()).orElse(null);
            if (!roleRepository.existsById(user.getRole().getId())){
                return new ResponseEntity<>(response(2,ResponseMessage.ROLE_NOT_FOUND), HttpStatus.OK);
            }
            if (userRepository.existsByEmail(user.getEmail())){
                return new ResponseEntity<>(response(2,ResponseMessage.EMAIL_TAKEN), HttpStatus.OK);
            }else {
                Person person;
                person = personRepository.save(user.getPerson());
                user.setPerson(person);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRole(role);
                return new ResponseEntity<>(response(1,userRepository.save(user)), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(3,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Object update(User user){
        try{
            if (!roleRepository.existsById(user.getRole().getId())){
                return new ResponseEntity<>(response(2,ResponseMessage.ROLE_NOT_FOUND), HttpStatus.OK);
            }
            if (!userRepository.existsById(user.getId())){
                return new ResponseEntity<>(response(2,ResponseMessage.USER_NOT_FOUND), HttpStatus.OK);
            }
            else {
                Person person = user.getPerson();
                person = personRepository.save(person);
                user.setPerson(person);
                return new ResponseEntity<>(response(1,userRepository.save(user)), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2,"Error al actualizar"), HttpStatus.OK);
        }
    }



    @Transactional
    public Object uploadProfilePicture(MultipartFile file, Long id){
        if (!file.isEmpty()){
            String fileName = UUID.randomUUID().toString() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "");
            //Path filePath = Paths.get("uploads").resolve(fileName).toAbsolutePath();
            Path filePath = Paths.get("C:\\unimor\\").resolve(fileName).toAbsolutePath();
            try {
                Files.copy(file.getInputStream(), filePath);
                User user = userRepository.findById(id).orElse(null);
                assert user != null;
                user.setPhoto(fileName);
                user =  userRepository.save(user);
                return new ResponseEntity<>(response(1,userRepository.save(user)), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(response(2,"Ocurrio un error al subir la imagen"), HttpStatus.OK);
            }
        } else{
            return new ResponseEntity<>(response(1,"Sin imagen de perfil"), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public Object returnData() {
        try {
            String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(authentication);
            return new ResponseEntity<>(response(1,user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response(1,"UNAUTHORIZED"), HttpStatus.OK);
        }
    }

    @Transactional
    public Object updateProfilePicture(MultipartFile file){
        if (!file.isEmpty()){
            String fileName = UUID.randomUUID().toString() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "");
            //Path filePath = Paths.get("uploads").resolve(fileName).toAbsolutePath();
            Path filePath = Paths.get("C:\\unimor\\").resolve(fileName).toAbsolutePath();
            try {
                String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<User> user = userRepository.findByEmail(authentication);
                User obj =  user.get();
                Files.copy(file.getInputStream(), filePath);
                obj.setPhoto(fileName);
                obj =  userRepository.save(obj);
                return new ResponseEntity<>(response(1,obj), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(response(2,"Ocurrio un error al subir la imagen"), HttpStatus.OK);
            }
        } else{
            return new ResponseEntity<>(response(1,"Sin imagen de perfil"), HttpStatus.OK);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user);
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException(String.format(ResponseMessage.USER_NOT_FOUND, email));
        }
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public boolean authentication(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyUsername(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    public Object generateToken(@RequestBody Login user) {
        if (authentication(user.getEmail(), user.getPassword())) {
            String token = "";
            try {
                UserDetails userDetails = loadUserByUsername(user.getEmail());
                token = jwtTokenUtil.generateToken(userDetails);
                boolean isUserEnable = userRepository.findByEmail(user.getEmail()).get().isStatus();
                if (!isUserEnable) {
                    return new ResponseEntity<>(response(2,ResponseMessage.WRONG_CREDENTIALS), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(response(2, ResponseMessage.WRONG_CREDENTIALS), HttpStatus.OK);
            }
            User us = userRepository.findByEmail(user.getEmail()).orElse(null);
            Map<String, Object> response = new HashMap<>();
            response.put("status", true);
            response.put("auth", token);
            assert us != null;
            response.put("role", us.getRole().getName());
            return new ResponseEntity<>(response, HttpStatus.OK);
            //return new ResponseEntity<>(response(4, token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response(2,ResponseMessage.WRONG_CREDENTIALS), HttpStatus.OK);
        }
    }

    private List<GrantedAuthority> getUserAuthority(User user) {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return new ArrayList<>(roles);
    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
