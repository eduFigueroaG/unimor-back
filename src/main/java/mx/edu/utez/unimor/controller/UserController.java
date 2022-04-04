package mx.edu.utez.unimor.controller;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.User;
import mx.edu.utez.unimor.entity.dto.Login;
import mx.edu.utez.unimor.exception.RequestException;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@RestController
@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/user/")
public class UserController {
    private final UserService userService;

    @GetMapping("valid")
    public Object validUser(){
        try {
            return userService.returnData();
        }
        catch (Exception e){
            Map<String, Object> response = new HashMap<>();
            response.put("status", false);
            response.put("message", "UNAUTHORIZED");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("save")
    public Object save (@RequestBody User user){
        return userService.save(user);
    }

    @PutMapping("update")
    public Object update (@RequestBody User user){
       try{
           return userService.update(user);
       }catch (Exception e){
           return new ResponseEntity<>(response(2,"Error al actualizar"), HttpStatus.OK);
       }
    }

    @PostMapping("login")
    public Object login(@RequestBody Login user) throws RequestException {
        return userService.generateToken(user);
    }

    @PostMapping("profile")
    public Object profilePicture(@RequestParam("profile")MultipartFile file, @RequestParam Long id){
        return userService.uploadProfilePicture(file,id);

    }
    @PostMapping("profile/update")
    public Object profilePictureUpdate(@RequestParam("profile")MultipartFile file){
        return userService.updateProfilePicture(file);

    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
