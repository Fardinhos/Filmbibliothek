package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.entity.FilmInvitation;
import com.gruppen.filmdatabase.entity.FilmInvitationStatus;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.helpers.EmailSender;
import com.gruppen.filmdatabase.repository.FilmInvitationRepository;
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
import java.util.Optional;

import static org.mockito.Mockito.*;

@EnableWebMvc
public class UserControllerTest {

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
    private UserController userController;

    @Mock
    private EmailSender emailSender;

    @Mock
    private FilmInvitationRepository filmInvitationRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private ControllerHelper controllerHelper;


    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
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
    public void acceptInvitation_Test() throws Exception {

        Long invitationId = 1l;

        FilmInvitation filmInvitation = new FilmInvitation();
        filmInvitation.setId(invitationId);
        filmInvitation.setInvitedUser(mockedUser);

        when(filmInvitationRepository.findById(invitationId)).thenReturn(Optional.of(filmInvitation));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/acceptInvitation")
                .param("invitationId", invitationId.toString())
        );


        verify(emailSender, times(1)).sendInvitationResponseEmail(filmInvitation);
        verify(filmInvitationRepository, times(1)).save(filmInvitation);

        // Watchlist gets updated by one more movie as the movie did not exist in the watchlist nor in
        // the watched list.
        assert (filmInvitation.getStatus() == FilmInvitationStatus.ACCEPTED);

    }

    @Test
    public void declineInvitation_Test() throws Exception {

        Long invitationId = 1l;

        FilmInvitation filmInvitation = new FilmInvitation();
        filmInvitation.setId(invitationId);
        filmInvitation.setInvitingUser(mockedUser);

        when(filmInvitationRepository.findById(invitationId)).thenReturn(Optional.of(filmInvitation));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/declineInvitation")
                .param("invitationId", invitationId.toString())
        );


        verify(emailSender, times(1)).sendInvitationResponseEmail(filmInvitation);
        verify(filmInvitationRepository, times(1)).save(filmInvitation);

        // Watchlist gets updated by one more movie as the movie did not exist in the watchlist nor in
        // the watched list.
        assert (filmInvitation.getStatus() == FilmInvitationStatus.DECLINED);

    }

    @Test
    public void sendInvitation_Test() throws Exception {

        Long filmId = 11l;

        Film film = new Film();
        film.setId(filmId);

        when(filmRepository.findById(filmId)).thenReturn(Optional.of(film));
        when(userRepository.findById(mockedUser.getId())).thenReturn(Optional.of(mockedUser));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/createInvitation")
                .param("filmId", film.getId().toString())
                .param("userId", mockedUser.getId().toString())
                .param("date", "2022-07-13T09:48:37.768958190")

        );


        verify(controllerHelper, times(1)).createAndSendInvite(any(), any(), any(), any());
    }
}
