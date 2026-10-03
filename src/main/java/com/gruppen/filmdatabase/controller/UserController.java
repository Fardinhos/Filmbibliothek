package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.helpers.EmailSender;
import com.gruppen.filmdatabase.controller.twofa.SecretKeyGenerator;
import com.gruppen.filmdatabase.entity.*;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class UserController {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private FilmErrorRepository filmErrorRepository;


    @Autowired
    private FilmInvitationRepository filmInvitationRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;


    @Autowired
    private ControllerHelper controllerHelper;

    @GetMapping("/goToUserStatistic")
    public String goToUserStatistic() {
        return "create-user-statistic";
    }

    @PostMapping("/createUserStatistic")
    public ModelAndView createUserStatistic(@RequestParam("from") String fromString,
                                            @RequestParam("until") String untilString) {
        User user = controllerHelper.getCurrentUser();

        ModelAndView modelAndView = new ModelAndView("show-user-statistic");

        UserStatistic userStatistic = controllerHelper.createUserStatistic(user,
                LocalDate.parse(fromString), LocalDate.parse(untilString));

        modelAndView.addObject("userStatistic", userStatistic);

        return modelAndView;
    }

    @PostMapping("acceptInvitation")
    public ModelAndView acceptInvitation(@RequestParam("invitationId") Long invitationId) {
        FilmInvitation invitation = filmInvitationRepository.findById(invitationId).get();
        invitation.setStatus(FilmInvitationStatus.ACCEPTED);

        filmInvitationRepository.save(invitation);
        emailSender.sendInvitationResponseEmail(invitation);

        return new ModelAndView("redirect:/showUser/?userId=" + invitation.getInvitedUser().getId());
    }

    @PostMapping("declineInvitation")
    public ModelAndView declineInvitation(@RequestParam("invitationId") Long invitationId) {
        FilmInvitation invitation = filmInvitationRepository.findById(invitationId).get();
        invitation.setStatus(FilmInvitationStatus.DECLINED);
        filmInvitationRepository.save(invitation);
        emailSender.sendInvitationResponseEmail(invitation);
        return new ModelAndView("redirect:/showUser/?userId=" + invitation.getInvitingUser().getId());
    }

    @GetMapping("/inviteUserToFilm")
    public ModelAndView goToInvitationPage(@RequestParam("filmId") Long filmId,
                                           @RequestParam("userId") Long userId
    ) {
        Film film = filmRepository.findById(filmId).get();
        User user = userRepository.findById(userId).get();

        ModelAndView modelAndView = new ModelAndView("invite-user-film");

        modelAndView.addObject("film", film);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/createInvitation")
    public ModelAndView createInvitation(@RequestParam("filmId") Long filmId,
                                         @RequestParam("userId") Long userId,
                                         @RequestParam("date") String date,
                                         @RequestParam(value = "remark", required = false) String remark) {
        Film film = filmRepository.findById(filmId).get();
        User user = userRepository.findById(userId).get();
        LocalDateTime localDateTime = LocalDateTime.parse(date);


        controllerHelper.createAndSendInvite(film, user, localDateTime, remark);

        return new ModelAndView("redirect:/showUser/?userId=" + user.getId());
    }

    @GetMapping("/listRecommendedFilms")
    public ModelAndView listRecommendedFilms(@RequestParam(value = "friendId", required = false) Long friendId) {
        User currentUser = controllerHelper.getCurrentUser();
        User user;
        if (friendId != null) {
            user = userRepository.findById(friendId).get();

        } else {
            user = currentUser;

        }
        List<Film> recommendedFilms = controllerHelper.getRecommendedFilms(user.getId(), currentUser.getId());

        ModelAndView modelAndView = new ModelAndView("recommendations");
        modelAndView.addObject("films", recommendedFilms);
        modelAndView.addObject("user", currentUser);

        return modelAndView;
    }


    @GetMapping("/listUsers")
    public ModelAndView listUsers(@RequestParam(name = "searchString", required = false) String searchString) {

        List<User> users = userRepository
                .filterBySearchString(searchString != null && searchString.length() > 0 ? searchString : null);

        ModelAndView mav = new ModelAndView("list-users");

        mav.addObject("users", users);

        return mav;
    }

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam("userId") Long userId) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "error";
        }
        User userToShow = userRepository.findById(userId).orElse(null);

        if (userToShow == null) {
            return "error";
        }


        boolean isFriend = user.getFriendsList().stream()
                .filter(u -> u.getId().equals(userId)).findFirst().orElse(null) !=
                null;

        if (!isFriend) {
            FriendRequest friendRequest = new FriendRequest(user, userToShow);
            friendRequestRepository.save(friendRequest);
            user.getSentFriendRequests().add(friendRequest);
            userToShow.getPendingFriendRequests().add(friendRequest);
        }


        return "redirect:/showUser/?userId=" + userId;
    }

    @PostMapping("/replyToFriendRequest")
    public String addFriend(@RequestParam("friendRequestId") Long friendRequestId,
                            @RequestParam("accept") boolean accept) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "error";
        }

        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId).get();

        Long userId = friendRequest.getSentBy().getId();

        User otherUser = userRepository.findById(userId).orElse(null);

        if (otherUser == null) {
            return "error";
        }


        boolean isFriend = user.getFriendsList().stream()
                .filter(u -> u.getId().equals(userId)).findFirst().orElse(null) !=
                null;

        if (!isFriend && accept) {
            user.getFriendsList().add(otherUser);
            otherUser.getFriendsList().add(user);


            user.getPendingFriendRequests().removeIf(fr ->
                    fr.getId().equals(friendRequestId));
            otherUser.getSentFriendRequests().removeIf(fr -> fr.getId().equals(friendRequestId));

            userRepository.save(user);
            userRepository.save(otherUser);

            friendRequestRepository.deleteById(friendRequestId);
        } else if (!accept) {
            user.getPendingFriendRequests().removeIf(fr -> fr.getId().equals(friendRequestId));
            otherUser.getSentFriendRequests().removeIf(fr -> fr.getId().equals(friendRequestId));
            userRepository.save(user);
            userRepository.save(otherUser);
            friendRequestRepository.deleteById(friendRequestId);
        }


        return "redirect:/showCurrentUser";
    }

    @PostMapping("/changeRights")
    public ModelAndView changeRights(@RequestParam("userId") Long userId,
                                     @RequestParam(value = "showFriendsList") String showFriendsList,
                                     @RequestParam(value = "showWatchList") String showWatchList,
                                     @RequestParam(value = "showRatings") String showRatings) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        User toUser = userRepository.findById(userId).orElse(null);


        if (user == null || toUser == null || !Objects.equals(user.getId(), toUser.getId())) {
            return new ModelAndView("error");
        }

        toUser.getUserRights().setShowFriends(UserRightsType.valueOf(showFriendsList));
        toUser.getUserRights().setShowRating(UserRightsType.valueOf(showRatings));
        toUser.getUserRights().setShowWatch(UserRightsType.valueOf(showWatchList));

        userRepository.save(user);

        return new ModelAndView("redirect:/showUser/?userId=" + userId);
    }

    @GetMapping("/showCurrentUser")
    public ModelAndView showCurrentUser() {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ModelAndView("error");
        }

        return new ModelAndView("redirect:/showUser/?userId=" + user.getId());
    }

    @PostMapping("/sendMessage")
    public ModelAndView sendMessage(@RequestParam("toUserId") Long toUserId,
                                    @RequestParam("content") String content) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        User toUser = userRepository.findById(toUserId).orElse(null);
        if (user == null || toUser == null) {
            return new ModelAndView("error");
        }

        Optional<ChatHistory> chatHistoryOptional = chatHistoryRepository
                .findByFirstUserIdAndSecondUserId(user.getId(), toUser.getId());


        if (chatHistoryOptional.isEmpty()) {
            chatHistoryOptional = chatHistoryRepository
                    .findByFirstUserIdAndSecondUserId(toUser.getId(), user.getId());
        }
        ChatHistory chatHistory;
        if (chatHistoryOptional.isEmpty()) {
            chatHistory = new ChatHistory(user, toUser);
            chatHistory = chatHistoryRepository.save(chatHistory);
        } else {
            chatHistory = chatHistoryOptional.get();
        }

        chatHistory.getMessages().add(new Message(user, content, LocalDateTime.now()));

        chatHistoryRepository.save(chatHistory);

        return new ModelAndView("redirect:/showUser/?userId=" + toUserId);
    }

    @GetMapping("/showUser")
    public ModelAndView showUser(@RequestParam("userId") Long userId) {
        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        List<Film> films = filmRepository.findAll();
        if (user == null) {
            return new ModelAndView("error");
        }
        Boolean isCurrentUser = user.getId().equals(userId);

        Boolean currentUserIsAdmin = user.getAdmin();

        User userToShow = userRepository.findById(userId).orElse(null);


        Boolean isFriend = user.getFriendsList().stream()
                .filter(u -> u.getId().equals(userId)).findFirst().orElse(null) !=
                null;


        if (userToShow == null) {
            return new ModelAndView("error");
        }


        Optional<ChatHistory> chatHistoryOptional = chatHistoryRepository
                .findByFirstUserIdAndSecondUserId(user.getId(), userToShow.getId());


        if (chatHistoryOptional.isEmpty()) {
            chatHistoryOptional = chatHistoryRepository
                    .findByFirstUserIdAndSecondUserId(userToShow.getId(), user.getId());
        }
        ChatHistory chatHistory;
        if (chatHistoryOptional.isEmpty()) {
            chatHistory = new ChatHistory(user, userToShow);
            chatHistory = chatHistoryRepository.save(chatHistory);
        } else {
            chatHistory = chatHistoryOptional.get();
        }

        FriendRequest friendRequest = user.getSentFriendRequests().stream()
                .filter(fr -> fr.getSentTo().getId().equals(userId)).findFirst().orElse(null);

        ModelAndView mav = new ModelAndView("show-user");

        mav.addObject("user", userToShow);
        mav.addObject("isCurrentUser", isCurrentUser);
        mav.addObject("isFriend", isFriend);
        mav.addObject("chatHistory", chatHistory);
        mav.addObject("films", films);
        mav.addObject("sentFriendRequest", friendRequest != null);
        mav.addObject("currentUserIsAdmin", currentUserIsAdmin);



        if (isCurrentUser) {
            List<FilmInvitation> invitedTo = filmInvitationRepository.findByInvitedUserId(userId);
            List<FilmInvitation> invitedBy = filmInvitationRepository.findByInvitingUserId(userId);
            mav.addObject("invitedTo", invitedTo);
            mav.addObject("invitedBy", invitedBy);
        }


        userRepository.save(user);


        byte[] image = user.getProfilePicture();
        String base64Image = "";
        if (image != null) {
            base64Image = Base64.getEncoder().encodeToString(image);
        }
        mav.addObject("base64Image", base64Image);


        return mav;
    }

    @PostMapping("/twofa-confirmed")
    public ModelAndView twofaConfirmed(@RequestParam("secretKey") String secretKey) {

        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ModelAndView("error");
        }


        String expectedSecretkey = user.getLatestSecretKey();

        if (expectedSecretkey == null || !expectedSecretkey.equals(secretKey)) {
            return new ModelAndView("error");
        }


        user.setLatestSecretKey(SecretKeyGenerator.generateSecretKey());
        userRepository.save(user);


        return new ModelAndView("redirect:/");

    }

    @GetMapping("/twofa")
    public ModelAndView twofa() {

        String email =
                ((UserSpecificDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ModelAndView("error");
        }



        user.setLatestSecretKey(SecretKeyGenerator.generateSecretKey());

        userRepository.save(user);

        emailSender.sendtwofaEmail(user.getEmail(), user.getLatestSecretKey());

        ModelAndView mv = new ModelAndView("twofa");
        mv.addObject("expectedSecretKey", user.getLatestSecretKey());
        System.out.println(user.getLatestSecretKey());


        return mv;

    }
}
