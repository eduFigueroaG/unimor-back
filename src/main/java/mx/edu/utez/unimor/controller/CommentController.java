package mx.edu.utez.unimor.controller;

import lombok.AllArgsConstructor;
import mx.edu.utez.unimor.entity.Comment;
import mx.edu.utez.unimor.service.CommentService;

import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@AllArgsConstructor
@RequestMapping("/comment/")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("save/{id}")
    public Object save(@RequestBody Comment comment, @PathVariable Long id){
        return commentService.save(comment, id);
    }

    @GetMapping("valid/{id}")
    public Object validComment(@PathVariable Long id){
        return commentService.validateComment(id);
    }

}
