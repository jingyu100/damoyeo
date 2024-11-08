package Team6.Damoyeo.User.Controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        // 이메일 중복 체크 에러메세지
        if (userService.emailCheck(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
            return "redirect:/user/register";
        }

        // 기본값 설정
        user.setJoinDate(LocalDateTime.now());
        userService.registerUser(user);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {

        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("userEmail") String userEmail,
                            @RequestParam("userPassword") String userPassword,
                            RedirectAttributes redirectAttributes) {
        try {
            // 디버깅을 위한 로그
            System.out.println("Login attempt - Email: " + userEmail + ", Password: " + userPassword);

            User user = userService.loginUser(userEmail, userPassword);
            System.out.println("Login successful for user: " + user.getEmail());

            // 성공 메시지 추가
            redirectAttributes.addFlashAttribute("message", "로그인에 성공했습니다!");
            redirectAttributes.addFlashAttribute("nickName", user.getNickname());

            return "redirect:/post/main";
        } catch (Exception e) {

            // 에러 로그 출력
            System.out.println("Login error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            // 입력한 이메일 유지
            redirectAttributes.addFlashAttribute("userEmail", userEmail);
            return "redirect:/user/login";
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean exists = userService.isEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

}