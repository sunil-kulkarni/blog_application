package com.example.blog.controller;

import com.example.blog.entity.Blog;
import com.example.blog.entity.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.CommentService;
import com.example.blog.service.ReactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebBlogController {

    private final BlogService blogService;
    private final CommentService commentService;
    private final ReactionService reactionService;

    public WebBlogController(BlogService blogService, CommentService commentService, ReactionService reactionService) {
        this.blogService = blogService;
        this.commentService = commentService;
        this.reactionService = reactionService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Blog> blogs = blogService.findAll();
        // sort blogs by newest first
        blogs.sort((b1, b2) -> {
            if (b1.getCreatedAt() == null) return 1;
            if (b2.getCreatedAt() == null) return -1;
            return b2.getCreatedAt().compareTo(b1.getCreatedAt());
        });
        model.addAttribute("blogs", blogs);
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        List<Blog> myBlogs = blogService.findByUserId(loggedInUser.getId());
        myBlogs.sort((b1, b2) -> {
            if (b1.getCreatedAt() == null) return 1;
            if (b2.getCreatedAt() == null) return -1;
            return b2.getCreatedAt().compareTo(b1.getCreatedAt());
        });
        model.addAttribute("myBlogs", myBlogs);
        model.addAttribute("loggedInUser", loggedInUser);
        return "dashboard";
    }

    @GetMapping("/blog/{id}")
    public String viewBlog(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            Blog blog = blogService.view(id);
            model.addAttribute("blog", blog);
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            model.addAttribute("loggedInUser", loggedInUser);
            return "blog-detail";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/blog/new")
    public String showCreateBlogForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("blog", new Blog());
        model.addAttribute("loggedInUser", loggedInUser);
        return "blog-form";
    }

    @PostMapping("/blog/new")
    public String createBlog(@RequestParam String title, 
                             @RequestParam String content, 
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             HttpSession session, 
                             RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        try {
            Blog newBlog = blogService.create(loggedInUser.getId(), title, content, image);
            redirectAttributes.addFlashAttribute("toastSuccess", "Story published successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastError", "Failed to publish story: " + e.getMessage());
            return "redirect:/blog/new";
        }
    }

    @GetMapping("/blog/{id}/edit")
    public String showEditBlogForm(@PathVariable Integer id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        try {
            Blog blog = blogService.view(id);
            // Check if user owns the blog
            if (!blog.getUser().getId().equals(loggedInUser.getId())) {
                return "redirect:/blog/" + id;
            }
            model.addAttribute("blog", blog);
            model.addAttribute("loggedInUser", loggedInUser);
            return "blog-form";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/blog/{id}/edit")
    public String editBlog(@PathVariable Integer id, 
                           @RequestParam String title, 
                           @RequestParam String content, 
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        try {
            Blog blog = blogService.view(id);
             if (!blog.getUser().getId().equals(loggedInUser.getId())) {
                redirectAttributes.addFlashAttribute("toastError", "You are not authorized to edit this story.");
                return "redirect:/blog/" + id;
            }
            
            blogService.edit(id, title, content, image);
            redirectAttributes.addFlashAttribute("toastSuccess", "Story updated successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("toastError", "Failed to update story: " + e.getMessage());
             return "redirect:/blog/" + id + "/edit";
        }
    }

    @PostMapping("/blog/{id}/delete")
    public String deleteBlog(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        try {
            Blog blog = blogService.view(id);
            if (!blog.getUser().getId().equals(loggedInUser.getId())) {
                 redirectAttributes.addFlashAttribute("toastError", "You are not authorized to delete this story.");
                 return "redirect:/blog/" + id;
            }
            blogService.delete(id);
            redirectAttributes.addFlashAttribute("toastSuccess", "Story deleted successfully!");
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("toastError", "Failed to delete story: " + e.getMessage());
             return "redirect:/dashboard";
        }
    }

    @PostMapping("/blog/{id}/comment")
    public String addComment(@PathVariable Integer id, @RequestParam String content, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        commentService.add(loggedInUser.getId(), id, content);
        return "redirect:/blog/" + id;
    }

    @PostMapping("/blog/{id}/like")
    public String toggleLike(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, @RequestHeader(value = "Referer", required = false) String referer) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        try {
            blogService.view(id); // Ensure blog exists
            reactionService.toggleLike(loggedInUser.getId(), id);
        } catch (RuntimeException e) {
             redirectAttributes.addFlashAttribute("toastError", "Stop trying to hack!");
             return "redirect:/";
        }
        return "redirect:" + (referer != null ? referer : "/blog/" + id);
    }
}
