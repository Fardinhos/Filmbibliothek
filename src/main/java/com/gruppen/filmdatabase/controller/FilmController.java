package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.entity.FilmRating;
import com.gruppen.filmdatabase.entity.FilmStatistic;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.helpers.StatisticJsonExporter;
import com.gruppen.filmdatabase.repository.FilmRatingRepository;
import com.gruppen.filmdatabase.repository.FilmRepository;
import com.gruppen.filmdatabase.repository.FilmStatisticRepository;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

import static com.gruppen.filmdatabase.controller.ScraperV2.scrape;

@Controller
public class FilmController {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRatingRepository filmRatingRepository;

    @Autowired
    private FilmStatisticRepository filmStatisticRepository;
    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private StatisticJsonExporter statisticJsonExporter;


    @GetMapping("downloadStatistic")
    public ResponseEntity downloadStatistic(@RequestParam("filmStatisticId") Long filmStatisticId)
            throws IOException, SQLException {
        FilmStatistic filmStatistic = filmStatisticRepository.findById(filmStatisticId).get();

        String json = statisticJsonExporter.export(filmStatistic);

        byte[] jsonBytes = json.getBytes();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=statistics.json")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(jsonBytes.length)
                .body(jsonBytes);
    }


    @PostMapping("resetStatistic")
    public String resetStatistic(@RequestParam("filmId") Long filmId) {
        FilmStatistic filmStatistic = controllerHelper.getFilmStatistic(filmRepository.findById(filmId).get());
        filmStatistic.setNumberOfRatings(0);
        filmStatistic.setNumberOfViews(0);
        filmStatistic.setAverageRating(0);
        filmStatistic.setFilmRatings(new ArrayList<>());
        filmStatisticRepository.save(filmStatistic);
        return "redirect:/showFilm?filmId=" + filmId;
    }

    @PostMapping("/filterFilms")
    public ModelAndView filterMovies(@RequestParam(value = "categoryFilter", required = false) String category,
                                     @RequestParam(value = "pubDateFilter", required = false) String pubDate,
                                     @RequestParam(value = "castFilter", required = false) String cast,
                                     @RequestParam(value = "nameFilter", required = false) String name) throws SQLException, IOException {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        ModelAndView mav = new ModelAndView("list-films");

        pubDate = pubDate.length() > 0 ? "(" + pubDate + ")" : null;
        List<Film> filteredFilms = filmRepository.filterByParams(name.length() > 0 ? name : null
                , category.length() > 0 ? category : null, cast.length() > 0 ? cast : null,
                pubDate);
        mav.addObject("films", filteredFilms);
        mav.addObject("user", user);

        Map<Long, String> filmIdToBannerMap = new HashMap<>();

        for (Film film : filteredFilms) {
            String base64Image = controllerHelper.getBase64Image(film.getBanner());
            filmIdToBannerMap.put(film.getId(), base64Image);
        }
        mav.addObject("filmIdToBannerMap", filmIdToBannerMap);
        return mav;
    }

    @GetMapping("showRecommendedFilms")
    public ModelAndView showRecommendedFilms() {
        ModelAndView modelAndView = new ModelAndView("recommended-films");
        modelAndView.addObject("recommendedFilms", filmRepository.findAll());
        return modelAndView;
    }


    @PostMapping("rateFilm")
    public ModelAndView rateFilm(@RequestParam("filmId") Long filmId, @RequestParam(name = "ratingText",
            required = false) String ratingText, @RequestParam(name = "rate") String rating) {
        Integer ratingInteger = Integer.parseInt(rating);


        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        FilmRating filmRating = user.getFilmsRatings().stream()
                .filter(fr -> fr.getFilm().getId().equals(filmId)).findFirst().orElse(null);

        if (filmRating == null) {
            Film film = filmRepository.findById(filmId).get();
            filmRating = new FilmRating(film, user);
            user.getFilmsRatings().add(filmRating);
        }
        filmRating.setComment(ratingText);
        filmRating.setRating(ratingInteger);

        controllerHelper.updateFilmStatistic(filmRating);

        userRepository.save(user);

        return new ModelAndView("redirect:/showFilms");
    }

    @GetMapping("addToWatchList")
    public ModelAndView addToWatchList(@RequestParam("filmId") Long filmId) {
        String email =
                SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        Film film = filmRepository.findById(filmId).get();


        for (FilmRating filmRating : user.getFilmsRatings()) {
            if (filmRating.getFilm().getId().equals(filmId)) {
                return new ModelAndView("redirect:/showFilms");

            }
        }


        for (Film watchListFilm : user.getWatchList()) {
            if (watchListFilm.getId().equals(filmId)) {
                return new ModelAndView("redirect:/showFilms");

            }
        }

        if (!user.getWatchList().contains(film)) {
            user.getWatchList().add(film);
            userRepository.save(user);
        }

        return new ModelAndView("redirect:/showFilms");

    }

    @GetMapping({"/watchedFilm"})
    public ModelAndView watchedFilm(@RequestParam Long filmId) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ModelAndView("error");
        }

        if (user.getFilmsRatings() == null) {
            user.setFilmsRatings(new ArrayList<>());
        }
        Film toBeRemovedFilm = null;

        for (Film watchedFilm : user.getWatchList()) {
            if (watchedFilm.getId().equals(filmId)) {
                toBeRemovedFilm = watchedFilm;
            }
        }


        if (toBeRemovedFilm != null) {
            user.getWatchList().remove(toBeRemovedFilm);


            FilmRating watchedFilm = user.getFilmsRatings().stream()
                    .filter(fr -> fr.getFilm().getId().equals(filmId)).findFirst().orElse(null);



            if (watchedFilm == null) {
                user.getFilmsRatings().add(new FilmRating(toBeRemovedFilm, user));
            }


            userRepository.save(user);

            controllerHelper.updateFilmStatistic(user.getFilmsRatings().get(user.getFilmsRatings().size() - 1));
        }


        return new ModelAndView("redirect:/showFilms");
    }

    @GetMapping({"/showFilm"})
    public ModelAndView showFilm(@RequestParam("filmId") Long filmId) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);


        if (user == null) {
            return new ModelAndView("error");
        }

        FilmRating filmRating = user.getFilmsRatings().stream()
                .filter(fr -> fr.getFilm().getId().equals(filmId)).findFirst().orElse(null);

        ModelAndView mav = new ModelAndView("show-film");
        Film film = filmRepository.findById(filmId).get();

        List<FilmRating> filmRatings = filmRatingRepository.findFilmRatingByFilmId(filmId);

        FilmStatistic filmStatistic = controllerHelper.getFilmStatistic(film);

        mav.addObject("film", film);
        mav.addObject("filmRating", filmRating);
        mav.addObject("filmRatings", filmRatings);
        mav.addObject("filmStatistic", filmStatistic);
        mav.addObject("user", user);

        String base64Image = controllerHelper.getBase64Image(film.getBanner());

        mav.addObject("base64Image", base64Image);

        return mav;
    }

    @GetMapping({"/showFilms", "/list"})
    public ModelAndView showFilms() {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ModelAndView("error");
        }
        ModelAndView mav = new ModelAndView("list-films");

        List<Film> list = filmRepository.findAll();

        Map<Long, String> filmIdToBannerMap = new HashMap<>();

        for (Film film : list) {
            String base64Image = controllerHelper.getBase64Image(film.getBanner());
            filmIdToBannerMap.put(film.getId(), base64Image);
        }
        mav.addObject("filmIdToBannerMap", filmIdToBannerMap);


        mav.addObject("films", list);
        mav.addObject("user", user);


        return mav;
    }

    @GetMapping("/addFilmForm")
    public ModelAndView addFilmForm() {

        ModelAndView mav = new ModelAndView("add-film-form");
        Film newFilm = new Film();
        mav.addObject("film", newFilm);
        return mav;
    }

    @PostMapping("/saveFilm")
    public ModelAndView saveFilm(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("length") String length,
            @RequestParam("director") String director,
            @RequestParam("scriptAuthor") String scriptAuthor,
            @RequestParam("cast") String cast,
            @RequestParam(name = "pubDate", required = false) String pubDate,
            @RequestParam(name = "picture", required = false)
            MultipartFile picture) throws IOException {

        Film film = id != null && id.length() > 0 ?
                filmRepository.findById(Long.parseLong(id)).get()
                : new Film(name, category, length, null, director, cast, scriptAuthor);

        ModelAndView mav = new ModelAndView("redirect:/list");

        if (picture != null) {
            InputStream iStream = null;
            try {
                iStream = picture.getInputStream();
            } catch (IOException e) {
                ModelAndView modelAndView = new ModelAndView("error");
                modelAndView.addObject("errorMessage", "Error while uploading picture");
                return modelAndView;
            }

            byte[] bytes = iStream.readAllBytes();
            film.setBanner(bytes);
        }
        filmRepository.save(film);
        return mav;

    }


    @GetMapping("/saveFilms")
    public String saveMovies() throws SQLException, IOException {
        scrape(/*category*/);
        return "redirect:/list";
    }

    @PostMapping("/saveFilms")
    public String saveMovies(@RequestParam(name = "category") String category, @RequestParam(name = "pubDate") String pubDate) throws SQLException, IOException {
        scrape(category, pubDate);
        return "redirect:/list";
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long filmId) {
        ModelAndView mav = new ModelAndView("add-film-form");
        Film film = filmRepository.findById(filmId).get();
        mav.addObject("film", film);

        byte[] image = film.getBanner();
        String base64Image = "";
        if (image != null) {
            base64Image = Base64.getEncoder().encodeToString(image);
        }
        mav.addObject("base64Image", base64Image);
        return mav;
    }

    @GetMapping("/deleteFilm")
    public String deleteFilm(@RequestParam Long filmId) {
        filmRepository.deleteById(filmId);
        return "redirect:/list";
    }

}
