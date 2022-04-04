package mx.edu.utez.unimor.service;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Comment;
import mx.edu.utez.unimor.entity.Company;
import mx.edu.utez.unimor.entity.User;
import mx.edu.utez.unimor.repository.CommentRepository;
import mx.edu.utez.unimor.repository.CompanyRepository;
import mx.edu.utez.unimor.repository.UserRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public Object save (Comment comment, Long id){
        try{
            String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Comment> comments = new ArrayList<>();
            Company company = companyRepository.findById(id).orElse(null);
            Optional<User> user = userRepository.findByEmail(authentication);
            comment.setCompany(company);
            comment.setUser(user.get());
            comment.setCreateAt(new Date());
            comment = commentRepository.save(comment);
            comments.add(comment);
            assert company != null;
            company.setComments(comments);
            company = companyRepository.save(company);
            return new ResponseEntity<>(response(1, company.getComments()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al registrar"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Object validateComment(Long company){
        try {
            Company company1 = companyRepository.findById(company).orElse(null);
            String authentication = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(authentication);
            Comment comment = commentRepository.findByCompanyAndUser(company1, user.get());
            return new ResponseEntity<>(response(1, comment), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(1, null), HttpStatus.OK);
        }
    }

    private Map<String, Object> response (int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
