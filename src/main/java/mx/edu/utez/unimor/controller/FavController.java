package mx.edu.utez.unimor.controller;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.service.FavService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/fav/")
public class FavController {
    private final FavService favService;

    @GetMapping("find")
    public Object findByUser(@RequestParam Optional<Integer> page){
        return favService.findAllByUser(PageRequest.of(page.orElse(0), 8));
    }

    @PostMapping("save/{id}")
    public Object save(@PathVariable Long id){
        return favService.save(id);
    }

    @PostMapping("exist/{id}")
    public Object exist(@PathVariable Long id){
        return favService.exist(id);
    }

    @DeleteMapping("delete/{id}")
    public Object delete(@PathVariable Long id){
        return favService.delete(id);
    }
}
