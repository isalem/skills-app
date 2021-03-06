package com.skills.controller;

import com.skills.domain.model.Skill;
import com.skills.domain.model.User;
import com.skills.service.UserService;
import com.skills.view.CreateUserForm;
import com.skills.view.UserSearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Principal principal, Model model) {
        return "redirect:/user/" + principal.getName();
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    @PreAuthorize("#username == principal.username or hasAnyRole('HR', 'BUSINESS')")
    public String userPage(@PathVariable String username, Model model, Principal principal, HttpSession session) {
        User user = userService.findUserByUsername(username);
        Set<Skill> skills = user.getSkills();
        Boolean isEmptySkills = skills.isEmpty();

        Map<String, List<Skill>> skillsGropedByAreaTitle = skills.stream()
                .collect(Collectors.groupingBy(k -> k.getArea().getTitle()));

        model.addAttribute("isEmptySkills", isEmptySkills);
        model.addAttribute("skillsGropedByAreaTitle", skillsGropedByAreaTitle);
        model.addAttribute("user", user);
        return "user/user-page";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createNewUser(Model model) {
        model.addAttribute("createUserForm", new CreateUserForm());
        return "user/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(
            @Valid @ModelAttribute CreateUserForm createUserForm,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "user/create";
        }

        User newUser = new User(createUserForm);
        newUser = userService.save(newUser);

        return "redirect:/user/" + newUser.getUsername();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchUsers(@ModelAttribute("userSearchForm") UserSearchForm userSearchForm,
                              Principal principal, Model model) {

        Set<User> usersWithRequestedSkills = userService.findUsersWithSkills(userSearchForm.getParsedSearchRequest());
        model.addAttribute("usersWithRequestedSkills", usersWithRequestedSkills);
        return "user/search";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String userNotFound() {
        return "user/error";
    }
}
