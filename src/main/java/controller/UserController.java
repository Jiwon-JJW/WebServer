package controller;

import annotation.Controller;
import annotation.GetMapping;
import annotation.PostMapping;
import model.User;
import service.UserService;

import java.io.IOException;

@Controller
public class UserController {
    @GetMapping(url = "/index")
    public String main() throws IOException {
        return "/index.html";
    }

    @GetMapping(url = "/user/login")
    public String login() {
        return "/user/login.html";
    }

    @GetMapping(url = "/user/form")
    public String joinUser() {
        return "/user/form.html";
    }

    @PostMapping(url = "/user/create")
    public String createUser(User user) {
        UserService.save(user);
        return "/index.html";
    }
}
