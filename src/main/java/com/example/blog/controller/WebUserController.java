package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebUserController {

    private final UserService userService;

    public WebUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password,
                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("toastSuccess", "Welcome back, " + user.getName() + "!");
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Invalid credentials. Please try again.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String name, @RequestParam String email,
                                      @RequestParam String password, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.register(name, email, password);
            redirectAttributes.addFlashAttribute("toastSuccess", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("toastSuccess", "You have been logged out successfully.");
        return "redirect:/";
    }
}
