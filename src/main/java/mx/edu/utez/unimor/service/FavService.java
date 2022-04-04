package mx.edu.utez.unimor.service;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Company;
import mx.edu.utez.unimor.entity.Fav;
import mx.edu.utez.unimor.entity.User;
import mx.edu.utez.unimor.repository.CompanyRepository;
import mx.edu.utez.unimor.repository.FavRepository;
import mx.edu.utez.unimor.repository.UserRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.response.ResponseMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavService {
    private final FavRepository favRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public Object findAllByUser(Pageable pageable){
        try{
            String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(authentication);
            if (userRepository.existsById(user.get().getId())){
                return new ResponseEntity<>(response(1,
                        favRepository.findAllByUserFav(user.get().getId(), pageable)), HttpStatus.OK);
            }else return new ResponseEntity<>(response(2, ResponseMessage.USER_NOT_FOUND), HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(response(2, "Error al consultar"), HttpStatus.OK);

        }
    }

    @Transactional
    public Object save(Long id){
        try{
            if (companyRepository.existsById(id)){
                String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
                Company company = companyRepository.findById(id).orElse(null);
                Optional<User> user = userRepository.findByEmail(authentication);
                Fav fav = new Fav();
                fav.setFav(company);
                fav.setUser(user.get());
                return new ResponseEntity<>(response(1, favRepository.save(fav)), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);

            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al guardar"), HttpStatus.OK);
        }
    }

    @Transactional
    public Object exist(Long fav){
        try{
            if (companyRepository.existsById(fav)){
                String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<User> user = userRepository.findByEmail(authentication);
                return new ResponseEntity<>(response(1,
                        favRepository.existsByUser_IdAndFav_Id(user.get().getId(), fav)), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);

            }
        }catch (Exception e){
            return new ResponseEntity<>(response(1, false), HttpStatus.OK);
        }
    }

    @Transactional
    public Object delete(Long fav){
        try{
            if (companyRepository.existsById(fav)){
                String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<User> user = userRepository.findByEmail(authentication);
                return new ResponseEntity<>(response(1,
                        favRepository.deleteByUser_IdAndFav_Id(user.get().getId(), fav)), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);

            }
        }catch (Exception e){
            return new ResponseEntity<>(response(1, false), HttpStatus.OK);
        }
    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
