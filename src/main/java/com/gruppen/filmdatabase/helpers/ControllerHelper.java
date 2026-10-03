package com.gruppen.filmdatabase.helpers;

import com.gruppen.filmdatabase.controller.UserSpecificDetails;
import com.gruppen.filmdatabase.entity.*;
import com.gruppen.filmdatabase.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ControllerHelper {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private FilmRatingRepository filmRatingRepository;

    @Autowired
    private FilmRepository filmRepository;


    @Autowired
    private FilmStatisticRepository filmStatisticRepository;

    @Autowired
    private FilmInvitationRepository filmInvitationRepository;

    @Autowired
    private EmailSender emailSender;

    public User getCurrentUser() {
        try {
            String email = ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }


    public List<Film> getRecommendedFilms(Long userId, Long originalUserId) {
        User user = userRepository.findById(userId).get();

        User originalUser = userRepository.findById(originalUserId).get();

        List<FilmRating> originalUserRatings = originalUser.getFilmsRatings();
        List<Long> originalUserRatingsFilmIds = originalUserRatings.stream().map(r -> r.getFilm().getId()).toList();

        List<FilmRating> userRatings = user.getFilmsRatings();
        List<Long> userRatingsFilmIds = userRatings.stream().map(r -> r.getFilm().getId()).toList();


        userRatings.sort(Comparator.comparing(FilmRating::getWatchedAt));


        if (userRatings.size() > 10) {
            userRatings = userRatings.subList(Math.max(userRatings.size() - 10, 0), userRatings.size());
        }


        List<Film> recommendedFilms = new ArrayList<>();




        HashMap<String, Integer> categoriesMap = new HashMap<>();
        for (FilmRating filmRating : userRatings) {
            String[] categories = filmRating.getFilm().getCategory()
                    .replace(" ", "")
                    .split(",");

            for (String parsedCategory : categories) {
                String lowerCaseCategory = parsedCategory.toLowerCase();
                if (categoriesMap.containsKey(lowerCaseCategory)) {
                    categoriesMap.put(lowerCaseCategory, categoriesMap.get(lowerCaseCategory) + 1);
                } else {
                    categoriesMap.put(lowerCaseCategory, 1);
                }
            }
        }

        List<String> watchedCategories = new ArrayList<>(categoriesMap.keySet());


        watchedCategories.sort(Comparator.comparing(categoriesMap::get).reversed());

        for (String category : watchedCategories) {
            List<Film> films = filmRepository.findByCategoryCustom(category);


            for (Film film : films) {
                if (!originalUserRatingsFilmIds.contains(film.getId())) {
                    recommendedFilms.add(film);
                }
            }

            if (recommendedFilms.size() > 15) {
                break;
            }
        }

        List<Long> recommendedFilmsIds = recommendedFilms.stream().map(Film::getId).toList();

        System.out.println("Found " + recommendedFilmsIds.size() + " recommended films.");

        if (recommendedFilms.size() == 15) {
            return recommendedFilms;
        }

        if (recommendedFilms.size() < 15) {
            List<Film> randomFilms = filmRepository.findAll();


            Collections.shuffle(randomFilms);


            for (Film film : randomFilms) {
                if (!recommendedFilmsIds.contains(film.getId()) && !originalUserRatingsFilmIds.contains(film.getId())) {
                    recommendedFilms.add(film);
                }
                if (recommendedFilms.size() == 15) {
                    break;
                }
            }
            return recommendedFilms;
        }



        List<FilmRating> recommendedFilmsRatings = filmRatingRepository.findByFilmIdIn(
                recommendedFilmsIds
        );



        recommendedFilms.sort(
                new Comparator<Film>() {
                    @Override
                    public int compare(Film o1, Film o2) {


                        List<FilmRating> filmRating1
                                = recommendedFilmsRatings.stream().filter(r -> r.getFilm().getId().equals(o1.getId())).toList();


                        List<FilmRating> filmRating2
                                = recommendedFilmsRatings.stream().filter(r -> r.getFilm().getId().equals(o2.getId())).toList();



                        Integer sum1 = 0;
                        for (FilmRating rating : filmRating1) {
                            if (rating.getRating() == null) {
                                continue;
                            }
                            sum1 += rating.getRating();
                        }

                        Integer sum2 = 0;
                        for (FilmRating rating : filmRating2) {
                            if (rating.getRating() == null) {
                                continue;
                            }
                            sum2 += rating.getRating();
                        }

                        return -1 * sum1.compareTo(sum2);
                    }
                }
        );


        recommendedFilms = recommendedFilms.subList(0, 15);

        return recommendedFilms;
    }

    public FilmStatistic getFilmStatistic(Film film) {

        Optional<FilmStatistic> filmStatisticOptional = filmStatisticRepository.findByFilmId(film.getId());

        if (filmStatisticOptional.isEmpty()) {
            return new FilmStatistic(film);
        }

        return filmStatisticOptional.get();
    }


    public FilmStatistic updateFilmStatistic(FilmRating filmRating) {

        Film film = filmRating.getFilm();

        Long filmId = film.getId();


        Optional<FilmStatistic> filmStatisticOptional = filmStatisticRepository.findByFilmId(filmId);

        FilmStatistic filmStatistic = filmStatisticOptional.orElse(new FilmStatistic(film));



        filmStatistic.getFilmRatings().removeIf(r -> r.getFilm().getId().equals(filmId)
                && r.getUser().getId().equals(filmRating.getUser().getId()));

        filmStatistic.getFilmRatings().add(filmRating);

        int watchesCount = 0;
        int ratingCount = 0;
        int ratingSum = 0;
        for (FilmRating rating : filmStatistic.getFilmRatings()) {
            watchesCount++;

            if (rating.getRating() != null) {
                ratingCount++;
                ratingSum += rating.getRating();
            }

        }

        if (ratingCount != 0) {
            filmStatistic.setAverageRating(ratingSum * 1.0 / ratingCount);
        }
        filmStatistic.setNumberOfViews(watchesCount);
        filmStatistic.setNumberOfRatings(ratingCount);

        filmStatisticRepository.save(filmStatistic);

        return filmStatistic;
    }

    public FilmInvitation createAndSendInvite(Film film, User user, LocalDateTime localDateTime, String remark) {

        User currentUser = getCurrentUser();

        FilmInvitation filmInvitation = new FilmInvitation(currentUser, user, film, localDateTime, remark);

        emailSender.sendInvitationEmail(filmInvitation);

        return filmInvitationRepository.save(filmInvitation);
    }

    public UserStatistic createUserStatistic(User user, LocalDate from, LocalDate until) {

        List<FilmRating> filmRatings = user.getFilmsRatings();


        List<FilmRating> filteredFilmRatings = filmRatings.stream()
                .filter(r -> r.getWatchedAt().toLocalDate().isAfter(from) && r.getWatchedAt().toLocalDate().isBefore(until)).toList();


        double totalWatchTime = 0.0;

        Film favoriteFilm = null;
        double favoriteFilmRating = 0.0;

        HashMap<String, Integer> categoriesCountMap = new HashMap<>();
        HashMap<String, Integer> actorCountMap = new HashMap<>();

        // Create user statistic
        for (FilmRating filmRating : filteredFilmRatings) {

            totalWatchTime += Double.parseDouble(filmRating.getFilm().getLength().replaceAll(" min", "")
                    .replaceAll(" ", ""));

            if (filmRating.getRating() != null && (favoriteFilm == null || (favoriteFilmRating < filmRating.getRating()))) {
                favoriteFilm = filmRating.getFilm();
                favoriteFilmRating = filmRating.getRating();
            }

            String category = filmRating.getFilm().getCategory();

            String[] categories = filmRating.getFilm().getCategory().replace(" ", "").split(",");

            for (String parsedCategory : categories) {
                String lowerCaseCategory = parsedCategory.toLowerCase();
                if (categoriesCountMap.containsKey(lowerCaseCategory)) {
                    categoriesCountMap.put(lowerCaseCategory, categoriesCountMap.get(lowerCaseCategory) + 1);
                } else {
                    categoriesCountMap.put(lowerCaseCategory, 1);
                }
            }




            String[] actors = filmRating.getFilm().getCast().split("(?<!\\G\\S+)\\s");

            for (String actor : actors) {
                String lowerCaseActor = actor.toLowerCase();
                if (actorCountMap.containsKey(lowerCaseActor)) {
                    actorCountMap.put(lowerCaseActor, actorCountMap.get(lowerCaseActor) + 1);
                } else {
                    actorCountMap.put(lowerCaseActor, 1);
                }
            }

        }


        int maxCategoryCount = 0;
        String maxCategory = "";
        for (Map.Entry<String, Integer> entry : categoriesCountMap.entrySet()) {
            if (entry.getValue() > maxCategoryCount) {
                maxCategory = entry.getKey();
                maxCategoryCount = entry.getValue();
            }
        }



        int maxActorCount = 0;
        String maxActor = "";
        for (Map.Entry<String, Integer> entry : actorCountMap.entrySet()) {
            if (entry.getValue() > maxActorCount) {
                maxActor = entry.getKey();
                maxActorCount = entry.getValue();
            }
        }


        return new UserStatistic(from,
                until,
                maxActor,
                maxCategory,
                favoriteFilm,
                totalWatchTime);
    }


    public String getBase64Image(byte[] image) {
        String base64Image = "";
        if (image != null) {
            base64Image = Base64.getEncoder().encodeToString(image);
        }
        return base64Image;
    }
}
