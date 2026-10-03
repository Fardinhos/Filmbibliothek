package com.gruppen.filmdatabase.helpers;

import com.gruppen.filmdatabase.entity.*;
import com.gruppen.filmdatabase.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


public class ControllerHelperTest {

    @InjectMocks
    private ControllerHelper controllerHelper;

    @Mock
    private FilmStatisticRepository filmStatisticRepository;

    @Mock
    private FilmRatingRepository filmRatingRepository;

    @Mock
    private EmailSender emailSender;

    @Mock
    private FilmInvitationRepository filmInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilmRepository filmRepository;

    private LocalDate from = LocalDate.of(2020, 1, 1);

    private LocalDate to = LocalDate.of(2020, 1, 30);


    private User mockedUser = new User();

    @BeforeEach
    void setup() {
        mockedUser = new User();
        mockedUser.setId(5l);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserStatistic_emptyWatchedMovies() {
        mockedUser.setFilmsRatings(new ArrayList<>());

        UserStatistic result = controllerHelper.createUserStatistic(mockedUser, from, to);

        assert (result.getFavoriteActor().equals(""));
        assert (result.getFavoriteCategory().equals(""));
        assert (result.getFavoriteFilm() == null);
        assert (result.getWatchTime() == 0.0);
        assert (result.getFrom().isEqual(from));
        assert (result.getUntil().isEqual(to));
    }


    @Test
    public void testCreateUserStatistic_noMoviesInTimePeriod() {

        List<FilmRating> filmRatingList = new ArrayList<>();

        Film film1 = new Film();
        film1.setLength("20 min");
        film1.setCategory("Action, Adventure,Drama");
        film1.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor4FName Actor4LName");


        Film film2 = new Film();
        film2.setLength("30 min");
        film2.setCategory("Action, Comedy,Horror");
        film2.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor3FName Actor3LName");


        Film film3 = new Film();
        film3.setLength("40 min");
        film3.setCategory("Action, Adventure,Drama");
        film3.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");

        FilmRating filmRating1 = new FilmRating();
        filmRating1.setFilm(film1);
        filmRating1.setWatchedAt(LocalDateTime.of(2019, 1, 1, 12, 12));
        filmRating1.setRating(5);

        FilmRating filmRating2 = new FilmRating();
        filmRating2.setFilm(film2);
        filmRating2.setWatchedAt(LocalDateTime.of(2020, 2, 25, 12, 14));
        filmRating1.setRating(4);

        FilmRating filmRating3 = new FilmRating();
        filmRating3.setFilm(film3);
        filmRating3.setWatchedAt(LocalDateTime.of(2021, 1, 1, 12, 12));
        filmRating1.setRating(3);

        filmRatingList.add(filmRating1);
        filmRatingList.add(filmRating2);
        filmRatingList.add(filmRating3);


        mockedUser.setFilmsRatings(filmRatingList);

        UserStatistic result = controllerHelper.createUserStatistic(mockedUser, from, to);

        assert (result.getFavoriteActor().equals(""));
        assert (result.getFavoriteCategory().equals(""));
        assert (result.getFavoriteFilm() == null);
        assert (result.getWatchTime() == 0.0);
        assert (result.getFrom().isEqual(from));
        assert (result.getUntil().isEqual(to));


    }


    @Test
    public void testCreateUserStatistic_success() {
        List<FilmRating> filmRatingList = new ArrayList<>();

        Film film1 = new Film();
        film1.setLength("20 min");
        film1.setCategory("Action, Adventure,Drama");
        film1.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor4FName Actor4LName");


        Film film2 = new Film();
        film2.setLength("30 min");
        film2.setCategory("Action, Comedy,Horror");
        film2.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor3FName Actor3LName");


        Film film3 = new Film();
        film3.setLength("40 min");
        film3.setCategory("Action, Adventure,Drama");
        film3.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");

        FilmRating filmRating1 = new FilmRating();
        filmRating1.setFilm(film1);
        filmRating1.setWatchedAt(LocalDateTime.of(2019, 1, 1, 12, 12));
        filmRating1.setRating(5);

        FilmRating filmRating2 = new FilmRating();
        filmRating2.setFilm(film2);
        filmRating2.setWatchedAt(LocalDateTime.of(2020, 2, 25, 12, 14));
        filmRating1.setRating(4);

        FilmRating filmRating3 = new FilmRating();
        filmRating3.setFilm(film3);
        filmRating3.setWatchedAt(LocalDateTime.of(2021, 1, 1, 12, 12));
        filmRating1.setRating(3);

        filmRatingList.add(filmRating1);
        filmRatingList.add(filmRating2);
        filmRatingList.add(filmRating3);


        mockedUser.setFilmsRatings(filmRatingList);

        LocalDate acceptedFrom = LocalDate.of(2018, 1, 1);
        LocalDate acceptedUntil = LocalDate.of(2021, 1, 2);

        UserStatistic result = controllerHelper.createUserStatistic(mockedUser, acceptedFrom, acceptedUntil);

        assert (result.getFavoriteActor().equals("actor1fname actor1lname"));
        assert (result.getFavoriteCategory().equals("action"));
        assert (result.getFavoriteFilm().equals(film1));
        assert (result.getWatchTime() == 90.0);
        assert (result.getFrom().isEqual(acceptedFrom));
        assert (result.getUntil().isEqual(acceptedUntil));
    }

    @Test
    public void updateFilmStatistic_filmStatisticNotExist() {
        Long filmId = 1l;

        when(filmStatisticRepository.findByFilmId(filmId)).thenReturn(Optional.empty());

        Film film = new Film();
        film.setId(filmId);
        FilmRating filmRating = new FilmRating();
        filmRating.setFilm(film);
        filmRating.setUser(mockedUser);
        filmRating.setRating(4);

        FilmStatistic result = controllerHelper.updateFilmStatistic(filmRating);

        assert (result.getFilm().getId().equals(filmId));
        assert (result.getNumberOfViews() == 1);
        assert (result.getNumberOfRatings() == 1);
        assert (result.getAverageRating() == 4);
        assert (result.getFilmRatings().size() == 1);
    }

    @Test
    public void updateFilmStatistic_filmStatisticExists() {
        Long filmId = 1l;

        Film film = new Film();
        film.setId(filmId);
        FilmRating filmRating = new FilmRating();
        filmRating.setFilm(film);
        filmRating.setUser(mockedUser);
        filmRating.setRating(4);

        FilmRating filmRating2 = new FilmRating();
        filmRating2.setFilm(film);
        User user2 = new User();
        user2.setId(2l);
        filmRating2.setUser(user2);
        filmRating2.setRating(5);


        FilmStatistic filmStatistic = new FilmStatistic(film);
        filmStatistic.getFilmRatings().add(filmRating);
        filmStatistic.getFilmRatings().add(filmRating2);

        when(filmStatisticRepository.findByFilmId(filmId)).thenReturn(Optional.of(filmStatistic));


        FilmStatistic result = controllerHelper.updateFilmStatistic(filmRating);

        assert (result.getFilm().getId().equals(filmId));
        assert (result.getNumberOfViews() == 2);
        assert (result.getNumberOfRatings() == 2);
        assert (result.getAverageRating() == 4.5);
        assert (result.getFilmRatings().size() == 2);
    }


    @Test
    public void createAndSendInvite_Test() {
        Film film = new Film();
        film.setId(1l);
        User user = new User();
        user.setId(2l);
        LocalDateTime localDateTime = LocalDateTime.of(2019, 1, 1, 12, 12);
        String remark = "remark";

        when(filmInvitationRepository.save(any(FilmInvitation.class))).
                thenReturn(new FilmInvitation(mockedUser, user, film, localDateTime, remark));
        FilmInvitation result = controllerHelper.createAndSendInvite(film, user, localDateTime, remark);

        assert (result.getFilm().getId().equals(film.getId()));
        assert (result.getInvitedUser().getId().equals(user.getId()));
        assert (result.getInvitingUser().getId().equals(mockedUser.getId()));
        assert (result.getInvitedAt().isEqual(localDateTime));
        assert (result.getRemark().equals(remark));
        assert (result.getStatus().equals(FilmInvitationStatus.PENDING));
        verify(emailSender, times(1)).sendInvitationEmail(any());

    }


    @Test
    public void getRecommendedFilms_sameUser_Test() {

        List<Film> films = new ArrayList<>();
        Film film1 = new Film();
        film1.setId(11l);
        film1.setName("film1");
        film1.setLength("20 min");
        film1.setCategory("Action, Adventure,Drama");
        film1.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor4FName Actor4LName");


        Film film2 = new Film();
        film2.setId(22L);
        film2.setName("film2");
        film2.setLength("30 min");
        film2.setCategory("Action, Adventure,Horror");
        film2.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor3FName Actor3LName");


        Film film3 = new Film();
        film3.setId(33L);
        film3.setName("film3");
        film3.setLength("40 min");
        film3.setCategory("Action, Adventure,Drama");
        film3.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");

        films.add(film1);
        films.add(film2);
        films.add(film3);
        for (int i = 0; i < 20; i++) {
            Film newFilm = new Film();
            newFilm.setId(i * 5L);
            newFilm.setName("film_" + i);
            newFilm.setLength("40 min");
            newFilm.setCategory("Adventure");
            newFilm.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");
            films.add(newFilm);
        }


        FilmRating filmRating1 = new FilmRating();
        filmRating1.setFilm(film1);
        filmRating1.setRating(5);
        filmRating1.setWatchedAt(LocalDateTime.now());
        FilmRating filmRating2 = new FilmRating();
        filmRating2.setFilm(film1);
        filmRating2.setRating(4);
        filmRating2.setWatchedAt(LocalDateTime.now());


        List<FilmRating> originalUserFilmRatings = new ArrayList<>();
        FilmRating filmRatingOriginal1 = new FilmRating();
        filmRatingOriginal1.setFilm(film1);
        filmRatingOriginal1.setRating(5);
        filmRatingOriginal1.setWatchedAt(LocalDateTime.now());
        originalUserFilmRatings.add(filmRatingOriginal1);


        Long userId = 1L;
        Long originalUserId = mockedUser.getId();


        mockedUser.setFilmsRatings(originalUserFilmRatings);
        when(filmRepository.findByCategoryCustom("action")).thenReturn(List.of(film1, film2, film3));
        when(filmRepository.findByCategoryCustom("adventure")).thenReturn(films);
        when(filmRepository.findByCategoryCustom("drama")).thenReturn(List.of(film1, film2, film3));
        when(filmRatingRepository.findByFilmIdIn(
                any()
        )).thenReturn(originalUserFilmRatings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.findById(originalUserId)).thenReturn(Optional.of(mockedUser));

        List<Film> result = controllerHelper.getRecommendedFilms(userId, originalUserId);
        assert (result.size() == 15);

        // Loop on result and make sure each movie has adventure as cateogry since it's the highest category used
        for (Film film : result) {
            assert (film.getCategory().contains("Adventure"));
        }
    }


    @Test
    public void getRecommendedFilms_differentUser_Test() {

        List<Film> films = new ArrayList<>();
        Film film1 = new Film();
        film1.setId(11l);
        film1.setName("film1");
        film1.setLength("20 min");
        film1.setCategory("Action, Adventure,Drama");
        film1.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor4FName Actor4LName");


        Film film2 = new Film();
        film2.setId(22L);
        film2.setName("film2");
        film2.setLength("30 min");
        film2.setCategory("Action, Adventure,Horror");
        film2.setCast("Actor1FName Actor1LName Actor2FName Actor2LName Actor3FName Actor3LName");


        Film film3 = new Film();
        film3.setId(33L);
        film3.setName("film3");
        film3.setLength("40 min");
        film3.setCategory("Action, Adventure,Drama");
        film3.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");

        films.add(film1);
        films.add(film2);
        films.add(film3);
        for (int i = 0; i < 20; i++) {
            Film newFilm = new Film();
            newFilm.setId(i * 5L);
            newFilm.setName("film_" + i);
            newFilm.setLength("40 min");
            newFilm.setCategory("Adventure");
            newFilm.setCast("Actor1FName Actor1LName Actor4FName Actor4LName Actor5FName Actor5LName");
            films.add(newFilm);
        }


        List<FilmRating> toSearchUserFilmRatings = new ArrayList<>();
        FilmRating filmRating1 = new FilmRating();
        filmRating1.setFilm(film1);
        filmRating1.setRating(5);
        filmRating1.setWatchedAt(LocalDateTime.now());
        toSearchUserFilmRatings.add(filmRating1);

        FilmRating filmRating2 = new FilmRating();
        filmRating2.setFilm(film1);
        filmRating2.setRating(4);
        filmRating2.setWatchedAt(LocalDateTime.now());
        toSearchUserFilmRatings.add(filmRating2);


        List<FilmRating> originalUserFilmRatings = new ArrayList<>();
        FilmRating filmRatingOriginal1 = new FilmRating();
        filmRatingOriginal1.setFilm(film1);
        filmRatingOriginal1.setRating(5);
        filmRatingOriginal1.setWatchedAt(LocalDateTime.now());
        originalUserFilmRatings.add(filmRatingOriginal1);


        Long userId = 1L;
        Long originalUserId = mockedUser.getId();

        User userToSearch = new User();
        userToSearch.setId(userId);

        userToSearch.setFilmsRatings(toSearchUserFilmRatings);
        mockedUser.setFilmsRatings(originalUserFilmRatings);
        when(filmRepository.findByCategoryCustom("action")).thenReturn(List.of(film1, film2, film3));
        when(filmRepository.findByCategoryCustom("adventure")).thenReturn(films);
        when(filmRepository.findByCategoryCustom("drama")).thenReturn(List.of(film1, film2, film3));
        when(filmRatingRepository.findByFilmIdIn(
                any()
        )).thenReturn(originalUserFilmRatings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToSearch));
        when(userRepository.findById(originalUserId)).thenReturn(Optional.of(mockedUser));

        List<Film> result = controllerHelper.getRecommendedFilms(userId, originalUserId);
        assert (result.size() == 15);

        // Loop on result and make sure each movie has adventure as cateogry since it's the highest category used
        for (Film film : result) {
            assert (film.getCategory().contains("Adventure"));
        }
    }

}
