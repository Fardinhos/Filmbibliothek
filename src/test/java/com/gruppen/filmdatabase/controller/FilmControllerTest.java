package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.entity.FilmRating;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.repository.FilmRepository;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableWebMvc
public class FilmControllerTest {

    private MockMvc mockMvc;

    private String mockedUserEmail = "mockedUserEmail@mockmail.com";
    private String mockedPassword = "mockedPassword";
    private Long mockedId = 1l;
    private String mockedFirstName = "mockedFirstName";
    private String mockedLastName = "mockedLastName";

    private User mockedUser = new User(1l, mockedUserEmail, mockedPassword, mockedFirstName, mockedLastName,
            false, LocalDate.of(1998, 1, 1));

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FilmController filmController;

    @Mock
    private ControllerHelper controllerHelper;

    @Mock
    private FilmRepository filmRepository;


    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(filmController)
                .build();
//        https://stackoverflow.com/questions/360520/unit-testing-with-spring-security

        // This prepares the security context and mocks it with dummy user.
        Authentication authentication = Mockito.mock(Authentication.class);
        authentication.setAuthenticated(true);
        when(authentication.getName()).thenReturn(mockedUserEmail);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        // Resets user before each test
        mockedUser = new User(1l, mockedUserEmail, mockedPassword, mockedFirstName, mockedLastName,
                false, LocalDate.of(1998, 1, 1));
        when(userRepository.findByEmail(mockedUserEmail))
                .thenReturn(mockedUser);

    }

    @Test
    public void rateFilm_Test_olderRatingExists() throws Exception {

        Film expectedFilm = new Film();
        expectedFilm.setId(mockedId);
        expectedFilm.setName("MockedFilm");

        List<FilmRating> filmRatingList = new ArrayList<>();
        FilmRating filmRating = new FilmRating(expectedFilm, mockedUser);
        filmRatingList.add(filmRating);

        mockedUser.setFilmsRatings(filmRatingList);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/rateFilm")
                .param("filmId", "1")
                .param("rate", "5")
                .param("ratingText", "comment")
        );

        verify(userRepository).save(mockedUser);
        verify(controllerHelper).updateFilmStatistic(filmRating);

        assert (mockedUser.getFilmsRatings().size() == 1);
        assert (mockedUser.getFilmsRatings().get(0).getRating() == 5);
        assert (mockedUser.getFilmsRatings().get(0).getComment().equals("comment"));


    }

    @Test
    public void rateFilm_Test_noOlderRatingFound() throws Exception {

        Film expectedFilm = new Film();
        expectedFilm.setId(mockedId);
        expectedFilm.setName("MockedFilm");

        when(filmRepository.findById(mockedId)).thenReturn(java.util.Optional.of(expectedFilm));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/rateFilm")
                .param("filmId", "1")
                .param("rate", "5")
                .param("ratingText", "comment")
        );


        assert (mockedUser.getFilmsRatings().size() == 1);
        assert (mockedUser.getFilmsRatings().get(0).getRating() == 5);
        assert (mockedUser.getFilmsRatings().get(0).getComment().equals("comment"));

    }

    @Test
    public void addToWatchList_Test_filmAlreadyWatched() throws Exception {

        Film expectedFilm = new Film();
        expectedFilm.setId(mockedId);
        expectedFilm.setName("MockedFilm");

        List<FilmRating> filmRatingList = new ArrayList<>();
        FilmRating filmRating = new FilmRating(expectedFilm, mockedUser);
        filmRatingList.add(filmRating);
        mockedUser.setFilmsRatings(filmRatingList);

        when(filmRepository.findById(mockedId)).thenReturn(java.util.Optional.of(expectedFilm));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/addToWatchList")
                .param("filmId", "1")
        );


        // Watch list stays the same as the movie was already watched
        assert (mockedUser.getWatchList().size() == 0);

    }

    @Test
    public void addToWatchList_Test_filmAlreadyInWatchList() throws Exception {

        Film expectedFilm = new Film();
        expectedFilm.setId(mockedId);
        expectedFilm.setName("MockedFilm");

        List<Film> watchList = new ArrayList<>();
        watchList.add(expectedFilm);
        mockedUser.setWatchList(watchList);

        when(filmRepository.findById(mockedId)).thenReturn(java.util.Optional.of(expectedFilm));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/addToWatchList")
                .param("filmId", "1")
        );


        // Watch list stays the same as the movie already existed in the watch list
        assert (mockedUser.getWatchList().size() == 1);
    }

    @Test
    public void addToWatchList_Test_filmAdded() throws Exception {

        Film existing = new Film();
        existing.setId(2l);
        Film expectedFilm = new Film();
        expectedFilm.setId(mockedId);
        expectedFilm.setName("MockedFilm");

        List<Film> watchList = new ArrayList<>();
        watchList.add(existing);
        mockedUser.setWatchList(watchList);

        when(filmRepository.findById(mockedId)).thenReturn(java.util.Optional.of(expectedFilm));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/addToWatchList")
                .param("filmId", "1")
        );


        // Watchlist gets updated by one more movie as the movie did not exist in the watchlist nor in
        // the watched list.
        assert (mockedUser.getWatchList().size() == 2);

    }

}
