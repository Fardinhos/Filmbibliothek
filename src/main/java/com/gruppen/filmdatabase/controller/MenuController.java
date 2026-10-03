package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.controller.twofa.SecretKeyGenerator;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@Controller
public class MenuController {
    @Autowired
    private UserRepository repo;



    @Autowired
    private ControllerHelper controllerHelper;
    private final String adminSecretKey = "secret_123";


    @GetMapping("/error")
    public String error() {
        return "error";

    }

    @GetMapping("/")
    public ModelAndView showMainMenu() {

        User currentUser = controllerHelper.getCurrentUser();

        ModelAndView mav = new ModelAndView("MainMenu");

        mav.addObject("currentUser", currentUser);

        return mav;
    }

    @GetMapping("/loginPage")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register/admin")
    public String showRegistrationAdmin(Model model) {
        model.addAttribute("user", new User());
        return "registration-admin";
    }

    @GetMapping("/register/user")
    public String showRegistrationUser(Model model) {
        model.addAttribute("user", new User());
        return "registration-user";
    }

    @PostMapping("/process_register")
    public ModelAndView processRegistration(@RequestParam("email") String email,
                                            @RequestParam("firstName") String firstName,
                                            @RequestParam("lastName")
                                            String lastName,
                                            @RequestParam("password")
                                            String password,
                                            @RequestParam("birthDate")
                                            String birthDate,
                                            @RequestParam(name = "adminKey", required = false)
                                            String adminKey,
                                            @RequestParam(name = "profilePic", required = false)
                                            MultipartFile profilePic) throws IOException {


        User existingUser = repo.findByEmail(email);

        if (existingUser != null) {
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMessage", "User already exists");
            return modelAndView;
        }

        User user = new User(email, password, firstName, lastName, LocalDate.parse(birthDate));

        if (adminKey != null) {
            if (adminKey.equals(adminSecretKey)) {
                user.setAdmin(true);
            }

            else {
                return new ModelAndView("admin-error");
            }
        } else {

            user.setAdmin(false);
        }
        if (profilePic != null) {
            InputStream iStream = null;
            try {
                iStream = profilePic.getInputStream();
            } catch (IOException e) {
                ModelAndView modelAndView = new ModelAndView("error");
                modelAndView.addObject("errorMessage", "Error while uploading profile picture");
                return modelAndView;
            }

            byte[] bytes = iStream.readAllBytes();
            user.setProfilePicture(bytes);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);


        user.setLatestSecretKey(SecretKeyGenerator.generateSecretKey());

        repo.save(user);

        return new ModelAndView("registration-succesful");

    }
}
