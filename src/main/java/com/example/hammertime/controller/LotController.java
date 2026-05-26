package com.example.hammertime.controller;

import com.example.hammertime.dto.CommentForm;
import com.example.hammertime.dto.LotForm;
import com.example.hammertime.model.Comment;
import com.example.hammertime.model.Lot;
import com.example.hammertime.service.CommentService;
import com.example.hammertime.service.LotService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/lots")
public class LotController {

    private final LotService lotService;
    private final CommentService commentService;

    public LotController(LotService lotService,
                         CommentService commentService) {
        this.lotService = lotService;
        this.commentService = commentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {

        List<Lot> lots = lotService.findAll(q, date);

        model.addAttribute("lots", lots);
        model.addAttribute("q", q);
        model.addAttribute("date", date);

        return "lots/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("form", new LotForm());
        return "lots/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") LotForm form,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {

        if (bindingResult.hasErrors()) {
            return "lots/create";
        }

        try {
            lotService.createLot(form, principal.getName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "lots/create";
        }

        return "redirect:/lots";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Lot lot = lotService.findById(id);
        List<Comment> comments = commentService.findByLotId(id);

        model.addAttribute("lot", lot);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());

        return "lots/details";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute("commentForm") CommentForm commentForm,
                             BindingResult bindingResult,
                             Model model,
                             Principal principal) {

        if (bindingResult.hasErrors()) {
            Lot lot = lotService.findById(id);
            List<Comment> comments = commentService.findByLotId(id);

            model.addAttribute("lot", lot);
            model.addAttribute("comments", comments);

            return "lots/details";
        }

        try {
            commentService.addComment(id, commentForm, principal.getName());
        } catch (IllegalArgumentException e) {
            Lot lot = lotService.findById(id);
            List<Comment> comments = commentService.findByLotId(id);

            model.addAttribute("lot", lot);
            model.addAttribute("comments", comments);
            model.addAttribute("error", e.getMessage());

            return "lots/details";
        }

        return "redirect:/lots/" + id;
    }
}