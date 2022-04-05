package mx.edu.utez.unimor.controller;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Company;
import mx.edu.utez.unimor.entity.Photo;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.service.CompanyService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/company/")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("find/{id}")
    public Object findById(@PathVariable Long id) {
        return companyService.findById(id);
    }

    @GetMapping("all")
    public Object findAll(@RequestParam Optional<Integer> page) {
        return companyService.findAllByStatus(PageRequest.of(page.orElse(0), 8));
    }

    @GetMapping("all/admin")
    public Object findAllAdmin(@RequestParam Optional<Integer> page) {
        return companyService.findAllNoStatus(PageRequest.of(page.orElse(0), 8));
    }

    @GetMapping("all/top")
    public Object findTop() {
        return companyService.topCompanies();
    }

    @PostMapping("save")
    public Object save(@RequestBody Company company){
        try{
            return companyService.save(company);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al registrar, verifique los datos"), HttpStatus.OK);
        }
    }

    @PostMapping("update")
    public Object update(@RequestBody Company company){
        try{
            return companyService.update(company);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al actualizar, verifique los datos"), HttpStatus.OK);
        }
    }

    @PutMapping("change/{id}")
    public Object delete(@PathVariable Long id){
        try{
            return companyService.changeCompanyStatus(id);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al cambiar el estado de la empresa"), HttpStatus.OK);
        }
    }

    @PostMapping("photo")
    public Object photo(@RequestParam("photo") MultipartFile file, @RequestParam Long id){
        return companyService.uploadPhoto(file,id);

    }


    @GetMapping("photos/find/{id}")
    public Object findAllPhotosByCompany(@PathVariable Long id){
        return companyService.findAllPhotosByCompany(id);

    }

    @PostMapping("photo/update")
    public Object findAllPhotosByCompany(@RequestParam("file") MultipartFile file, @RequestParam Long id){
        return companyService.uploadPhoto(file, id);

    }

    @DeleteMapping("photo/delete/{photo}/{id}")
    public Object deletePhoto(@PathVariable Long photo,@PathVariable Long id){
        return companyService.deletePhoto(photo, id);
    }

    @GetMapping("photo/img/{name:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String name){
        Path path = Paths.get("C:\\unimor\\").resolve(name).toAbsolutePath();
        //Path path = Paths.get("uploads").resolve(name).toAbsolutePath();
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (Exception e) {
        }
        if(!resource.exists() && !resource.isReadable()) {
            throw new RuntimeException("Error no se pudo cargar la imagen: " + name);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }

    @GetMapping(value = "image/{img:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> image(@PathVariable String img) throws IOException {
        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
                "C:\\unimor\\"+ img
        )));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);

    }
}
