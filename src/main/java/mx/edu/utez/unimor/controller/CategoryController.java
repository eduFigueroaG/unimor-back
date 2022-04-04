package mx.edu.utez.unimor.controller;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Category;
import mx.edu.utez.unimor.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/category/")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("all")
    public Object findAll() {
        return categoryService.findAll();
    }

    @PostMapping("save")
    public Object save(@RequestBody Category category){
        return categoryService.save(category);
    }
}
