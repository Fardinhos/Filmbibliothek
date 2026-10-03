package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Group;
import com.gruppen.filmdatabase.entity.Message;
import com.gruppen.filmdatabase.entity.User;
import com.gruppen.filmdatabase.helpers.ControllerHelper;
import com.gruppen.filmdatabase.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ControllerHelper controllerHelper;


    @PostMapping("leaveGroup")
    public ModelAndView leaveGroup(@RequestParam("groupId") Long groupId) {
        User currentUser = controllerHelper.getCurrentUser();
        Group group = groupRepository.findById(groupId).get();

        User userToRemove = null;

        for (User user : group.getUsers()) {
            if (user.getId().equals(currentUser.getId())) {
                userToRemove = user;
                break;
            }
        }


        if(userToRemove != null) {
            group.getUsers().remove(userToRemove);
            groupRepository.save(group);
        }


        return new ModelAndView("redirect:/showGroup?groupId=" + groupId);
    }

    @PostMapping("joinGroup")
    public ModelAndView joinGroup(@RequestParam("groupId") Long groupId) {
        User currentUser = controllerHelper.getCurrentUser();
        Group group = groupRepository.findById(groupId).get();

        boolean isMember = false;

        for (User user : group.getUsers()) {
            if (user.getId().equals(currentUser.getId())) {
                isMember = true;
                break;
            }
        }


        if (group.getAdmin().getId().equals(currentUser.getId())) {
            isMember = true;
        }

        if(!isMember){
            group.getUsers().add(currentUser);
            groupRepository.save(group);
        }
        return new ModelAndView("redirect:/showGroup?groupId=" + groupId);
    }

    @GetMapping("/listGroups")
    public ModelAndView listGroups() {

        User currentUser = controllerHelper.getCurrentUser();

        List<Group> groups = groupRepository.findAll();



        List<Group> returnedGroups = new ArrayList<>();

        for (Group group : groups) {
            if (!group.getPrivate()) {
                returnedGroups.add(group);
                continue;
            }

            List<User> users = group.getUsers();
            List<Long> userIds = new ArrayList<Long>();

            for (User user : users) {
                userIds.add(user.getId());
            }

            if (userIds.contains(currentUser.getId()) || group.getAdmin().getId().equals(currentUser.getId())) {
                returnedGroups.add(group);
                continue;
            }


            List<User> friends = currentUser.getFriendsList();
            List<Long> friendIds = new ArrayList<Long>();

            for (User friend : friends) {
                friendIds.add(friend.getId());
            }

            if (friendIds.contains(group.getAdmin().getId())) {
                returnedGroups.add(group);
                continue;
            }
        }
        ModelAndView modelAndView = new ModelAndView("list-groups");

        modelAndView.addObject("groups", returnedGroups);

        return modelAndView;
    }

    @GetMapping("/showGroup")
    public ModelAndView showGroup(@RequestParam("groupId") Long groupId) {
        User currentUser = controllerHelper.getCurrentUser();

        Group group = groupRepository.findById(groupId).get();

        ModelAndView modelAndView = new ModelAndView("group-details");


        boolean isMember = false;

        for (User user : group.getUsers()) {
            if (user.getId().equals(currentUser.getId())) {
                isMember = true;
                break;
            }
        }


        if (group.getAdmin().getId().equals(currentUser.getId())) {
            isMember = true;
        }
        modelAndView.addObject("group", group);

        modelAndView.addObject("isMember", isMember);

        return modelAndView;
    }

    @PostMapping("/createGroup")
    public ModelAndView createGroup(@RequestParam("name") String name,
                                    @RequestParam(value = "isPrivate", defaultValue = "false") Boolean isPrivate) {
        User currentUser = controllerHelper.getCurrentUser();

        Group group = new Group(currentUser, name, isPrivate);

        group = groupRepository.save(group);

        return new ModelAndView("redirect:/showGroup?groupId=" + group.getId());
    }

    @PostMapping("/sendGroupMessage")
    public ModelAndView sendGroupMessage(@RequestParam("message") String message,
                                         @RequestParam("groupId") Long groupId) {

        User currentUser = controllerHelper.getCurrentUser();

        Group group = groupRepository.findById(groupId).get();

        group.getMessages().add(new Message(currentUser, message, LocalDateTime.now()));

        group = groupRepository.save(group);

        return new ModelAndView("redirect:/showGroup?groupId=" + group.getId());
    }
}
