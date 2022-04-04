package mx.edu.utez.unimor.service;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.*;
import mx.edu.utez.unimor.entity.dto.Top;
import mx.edu.utez.unimor.repository.CategoryRepository;
import mx.edu.utez.unimor.repository.CompanyRepository;
import mx.edu.utez.unimor.repository.PhotoRepository;
import mx.edu.utez.unimor.response.ApiResponse;
import mx.edu.utez.unimor.response.ResponseMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoRepository photoRepository;

    @Transactional(readOnly = true)
    public Object findById(Long id){
        try{
            return new ResponseEntity<>(response(1, companyRepository.findById(id)), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllByStatus(Pageable pageable) {
        try{
            return new ResponseEntity<>(response(1, companyRepository.findAllPaginated(pageable)), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al consultar"), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllNoStatus(Pageable pageable) {
        try{
            return new ResponseEntity<>(response(1, companyRepository.findAllPaginatedNoStatus(pageable)), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al consultar"), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<?> save(Company company){
        try{
            if (companyRepository.existsByName(company.getName())){
                return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_TAKEN), HttpStatus.OK);
            }else{
                Category category = categoryRepository.findById(company.getCategory().getId()).orElse(null);
                company.setCategory(category);
                return new ResponseEntity<>(response(1, companyRepository.save(company)), HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(response(2, "Error al registrar"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> update(Company company){
        try{
            if (companyRepository.existsById(company.getId())){
                company = companyRepository.save(company);
                return new ResponseEntity<>(response(1, company), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2, ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al registrar, verifique los datos"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Object uploadPhoto(MultipartFile file, Long id){
        if (!file.isEmpty()){
            String fileName = UUID.randomUUID().toString() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "");
            //Path filePath = Paths.get("uploads").resolve(fileName).toAbsolutePath();
            Path filePath = Paths.get("C:\\unimor\\").resolve(fileName).toAbsolutePath();
            try {
                Photo photo = new Photo();
                Files.copy(file.getInputStream(), filePath);
                Company company = companyRepository.findById(id).orElse(null);
                photo.setName(fileName);
                photo.setCompany(company);
                photo = photoRepository.save(photo);
                assert company != null;
                List<Photo> photos = company.getPhotos();
                photos.add(photo);
                company.setPhotos(photos);
                return new ResponseEntity<>(response(1,companyRepository.save(company)), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(response(2,"Ocurrio un error al subir la imagen"), HttpStatus.OK);
            }
        } else{
            return new ResponseEntity<>(response(1,"Sin imagen de perfil"), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public Object findAllPhotosByCompany(Long id){
        List<Photo> photos = new ArrayList<>();
        try{
            if (companyRepository.existsById(id)){
                photos = photoRepository.findAllByCompany_Id(id);
                return new ResponseEntity<>(response(1,photos), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2,ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2,"Error al consultar"), HttpStatus.OK);
        }
    }

    @Transactional
    public Object deletePhoto(Long photo, Long id){
        try{
            if (companyRepository.existsById(id)){
                Company company = companyRepository.findById(id).orElse(null);
                Photo photo1 = photoRepository.findById(photo).orElse(null);
                assert company != null;
                List<Photo> photos = company.getPhotos();
                photos.remove(photo1);
                company.setPhotos(photos);
                company = companyRepository.save(company);
                photoRepository.deleteById(photo);
                return new ResponseEntity<>(response(1,"OK"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response(2,ResponseMessage.COMPANY_NOT_FOUND), HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(response(2,"Error al eliminar"), HttpStatus.OK);

        }
    }

    @Transactional(readOnly = true)
    public Object topCompanies(){
        try {
            List<Company> topCompanies = companyRepository.findAllByStatus(true);
            Top[] arrayTop = new Top[topCompanies.size()];
            int index =0;
            if (topCompanies.size()>=5){
                for (Company top:topCompanies) {
                    Top top1 = new Top();
                    top1.setRating(getRating(top.getComments()));
                    top1.setId(top.getId());
                    arrayTop[index] = top1;
                    index++;
                }
                int n = arrayTop.length;
                Top temp = new Top();
                temp.setRating(0L);
                for(int i=0; i < n; i++){
                    for(int j=1; j < (n-i); j++){
                        if(arrayTop[j-1].getRating() < arrayTop[j].getRating()){
                            // ¿Nunca pensaron que usarian Bubble sort en la vida? aqui se uso xdddddddddd
                            temp = arrayTop[j-1];
                            arrayTop[j-1] = arrayTop[j];
                            arrayTop[j] = temp;
                        }
                    }
                }
                List<Company> topFiveCompanies = new ArrayList<>();
                for (Top top : arrayTop) {
                    if (topFiveCompanies.size()<5){
                        Company company = companyRepository.findById(top.getId()).orElse(null);
                        topFiveCompanies.add(company);
                    }
                }
                return new ResponseEntity<>(response(1,topFiveCompanies), HttpStatus.OK);

            }else{
                return new ResponseEntity<>(response(2,"No hay empresas suficientes"), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(response(2,"Error al consultar"), HttpStatus.OK);
        }
    }

    @Transactional
    public Object changeCompanyStatus(Long id){
        try{
            if (companyRepository.existsById(id)){
                Company company = companyRepository.findById(id).orElse(null);
                assert company != null;
                company.setStatus(!company.isStatus());
                return new ResponseEntity<>(response(1, companyRepository.save(company)), HttpStatus.OK);
            }else return new ResponseEntity<>(response(2, "Error al registrar"), HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(response(2, "Error al cambiar el estado de la empresa"), HttpStatus.OK);

        }
    }

    public Long getRating(List<Comment> comments){
        long rating = 0L;
        if (!comments.isEmpty()){
            for (Comment comment: comments) {
                rating = rating +comment.getRating();
            }
            return rating/comments.size();
        }else return 0L;

    }

    private Map<String, Object> response(int option, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.returnResponse(option, data);
    }
}
