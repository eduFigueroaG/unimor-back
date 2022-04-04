package mx.edu.utez.unimor.service;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Category;
import mx.edu.utez.unimor.repository.CategoryRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(response(1, categoryRepository.findAll()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response(2, "Error al consultar"), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<?> save(Category category){
        try{
            if (categoryRepository.existsByName(category.getName())){
                return new ResponseEntity<>(response(2, ResponseMessage.CATEGORY_TAKEN), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(1, categoryRepository.save(category)), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al registrar"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
