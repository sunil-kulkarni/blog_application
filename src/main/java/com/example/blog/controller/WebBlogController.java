//

//

//

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

/**
 * Web MVC controller responsible for rendering blog-related pages and handling
 * browser form submissions for the blog application.
 *
 * <p>This controller manages://

//

//

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

/**
 * Web MVC controller responsible for rendering blog-related pages and handling
 * browser form submissions for the blog application.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Public pages (home and blog detail)</li>
 *   <li>Authenticated user pages (dashboard, create/edit/delete blog)</li>
 *   <li>Interactions (comments and likes)</li>
 * </ul>
 *
 * <p>Authentication state is resolved from {@code HttpSession} via the
 * {@code loggedInUser} attribute.
 */

/**
 * Renders the home page with all blogs sorted by newest first.
 *
 * <p>Adds the following model attributes:
 * <ul>
 *   <li>{@code blogs} - all blogs sorted descending by creation time</li>
 *   <li>{@code loggedInUser} - current session user (nullable)</li>
 * </ul>
 *
 * @param model   Spring MVC model used by the view
 * @param session current HTTP session
 * @return the home template name
 */

/**
 * Renders the authenticated user's dashboard page.
 *
 * <p>If no user is logged in, redirects to login.
 * Adds:
 * <ul>
 *   <li>{@code myBlogs} - current user's blogs sorted newest first</li>
 *   <li>{@code loggedInUser} - current session user</li>
 * </ul>
 *
 * @param model   Spring MVC model used by the view
 * @param session current HTTP session
 * @return dashboard template name, or redirect to login
 */

/**
 * Displays a single blog detail page by its identifier.
 *
 * <p>Adds:
 * <ul>
 *   <li>{@code blog} - requested blog entity</li>
 *   <li>{@code loggedInUser} - current session user (nullable)</li>
 * </ul>
 *
 * <p>If the blog does not exist, redirects to home.
 *
 * @param id      blog identifier
 * @param model   Spring MVC model used by the view
 * @param session current HTTP session
 * @return blog detail template name, or redirect to home on error
 */

/**
 * Shows the form to create a new blog post.
 *
 * <p>Requires authentication; otherwise redirects to login.
 * Adds:
 * <ul>
 *   <li>{@code blog} - empty blog object for form binding</li>
 *   <li>{@code loggedInUser} - current session user</li>
 * </ul>
 *
 * @param model   Spring MVC model used by the view
 * @param session current HTTP session
 * @return blog form template name, or redirect to login
 */

/**
 * Handles blog creation submission.
 *
 * <p>Requires authentication; otherwise redirects to login.
 * On success, sets a success toast flash message and redirects to dashboard.
 * On failure, sets an error toast flash message and redirects back to create form.
 *
 * @param title              blog title
 * @param content            blog content/body
 * @param image              optional uploaded image
 * @param session            current HTTP session
 * @param redirectAttributes flash attributes for user feedback
 * @return redirect to dashboard on success, or redirect to new blog form on failure
 */

/**
 * Shows the edit form for an existing blog.
 *
 * <p>Requires authentication and ownership of the target blog.
 * If user is not owner, redirects to blog detail page.
 * If blog is not found, redirects to home.
 *
 * @param id      blog identifier
 * @param model   Spring MVC model used by the view
 * @param session current HTTP session
 * @return blog form template, blog detail redirect, login redirect, or home redirect
 */

/**
 * Handles blog update submission.
 *
 * <p>Requires authentication and ownership of the target blog.
 * On unauthorized access, sets error toast and redirects to blog detail.
 * On success, sets success toast and redirects to dashboard.
 * On failure, sets error toast and redirects back to edit form.
 *
 * @param id                 blog identifier
 * @param title              updated blog title
 * @param content            updated blog content
 * @param image              optional replacement image
 * @param session            current HTTP session
 * @param redirectAttributes flash attributes for user feedback
 * @return redirect target based on authorization and operation result
 */

/**
 * Deletes an existing blog.
 *
 * <p>Requires authentication and ownership of the target blog.
 * On unauthorized access, sets error toast and redirects to blog detail.
 * On success, sets success toast and redirects to dashboard.
 * On failure, sets error toast and redirects to dashboard.
 *
 * @param id                 blog identifier
 * @param session            current HTTP session
 * @param redirectAttributes flash attributes for user feedback
 * @return redirect to login, blog detail, or dashboard depending on outcome
 */

/**
 * Adds a comment to a blog.
 *
 * <p>Requires authentication; otherwise redirects to login.
 * On success, redirects back to the blog detail page.
 *
 * @param id      blog identifier
 * @param content comment text/content
 * @param session current HTTP session
 * @return redirect to login if unauthenticated, otherwise redirect to blog detail
 */

/**
 * Toggles like/unlike reaction for the current user on a blog.
 *
 * <p>
 * Requires authentication; otherwise redirects to login.
 * Validates that the blog exists before toggling reaction.
 * If validation fails, sets an error toast and redirects to home.
 * On success, redirects to the referring page when available,
 * otherwise falls back to the blog detail page.
 *
 * @param id                 blog identifier
 * @param session            current HTTP session
 * @param redirectAttributes flash attributes for user feedback
 * @param referer            optional HTTP Referer header used for redirect
 * @return redirect to login, home, referer, or blog detail depending on outcome
 */
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
            if (b1.getCreatedAt() == null)
                return 1;
            if (b2.getCreatedAt() == null)
                return -1;
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
            if (b1.getCreatedAt() == null)
                return 1;
            if (b2.getCreatedAt() == null)
                return -1;
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
            blogService.create(loggedInUser.getId(), title, content, image);
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
    public String toggleLike(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
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
