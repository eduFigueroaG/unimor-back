package mx.edu.utez.unimor.service;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.repository.RoleRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(response(1, roleRepository.findAll()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response(2, "Error al consultar"), HttpStatus.OK);
        }
    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
