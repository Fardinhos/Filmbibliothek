package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.entity.FilmError;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.helpers.EmailSender;
import com.gruppen.filmdatabase.repository.FilmErrorRepository;
import com.gruppen.filmdatabase.repository.FilmRepository;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ErrorController {


    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FilmErrorRepository filmErrorRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ControllerHelper controllerHelper;
    @Autowired
    private EmailSender emailSender;


    @GetMapping("/listErrors")
    public ModelAndView listErrors() {
        User currentUser = controllerHelper.getCurrentUser();

        if (!currentUser.getAdmin()) {
            ModelAndView modelAndView = new ModelAndView("error");

            modelAndView.addObject("errorMessage", "You are not an admin.");

            return modelAndView;
        }

        List<FilmError> errors = filmErrorRepository.findByIsResolvedFalse();

        ModelAndView mav = new ModelAndView("list-errors");

        mav.addObject("errors", errors);

        return mav;
    }

    @GetMapping("/showReportError")
    public ModelAndView showReportError(@RequestParam("filmId") Long filmId) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);
        ModelAndView mv = new ModelAndView("report-errors");

        mv.addObject("userId", user.getId());
        mv.addObject("filmId", filmId);
        return mv;
    }

    @PostMapping("/userReportError")
    public String userReportError(@RequestParam("userId") Long userId,
                                  @RequestParam("filmId") Long filmId,
                                  @RequestParam(value = "errorMessage", required = false) String errorMessage) {


        //get user
        User user = userRepository.findById(userId).get();


        Film film = filmRepository.findById(filmId).get();


        FilmError error = new FilmError(user, film, LocalDateTime.now(), errorMessage);

        error.setIsResolved(false);

        filmErrorRepository.save(error);

        emailSender.sendFilmErrorEmail(error);

        return "redirect:/showFilms";
    }

    @PostMapping("/resolveError")
    public String resolveError(@RequestParam("errorId") Long errorId) {
        FilmError error = filmErrorRepository.findById(errorId).get();
        error.setIsResolved(true);
        filmErrorRepository.save(error);
        return "redirect:/listErrors";
    }

}
