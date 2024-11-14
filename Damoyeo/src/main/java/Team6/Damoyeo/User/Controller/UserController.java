package Team6.Damoyeo.User.Controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_USER = "src/main/resources/static/uploads/";

    // 회원가입 폼 페이지로 이동
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // registerForm에서 post 전송할 때 User 타입 반환을 위함
        model.addAttribute("user", new User());

        return "user/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               RedirectAttributes redirectAttributes) {

        // 이메일 중복 체크 에러 메시지
        if (userService.emailCheck(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
            return "redirect:/user/register";
        }

        // 가입일 기본값 설정
        user.setJoinDate(LocalDateTime.now());

        // 프로필 디폴트 사진 설정
        user.setPhotoUrl("nullDefult.png");

        // 회원가입 서비스 호출
        userService.registerUser(user);

        return "redirect:/user/login";
    }

    // 로그인 폼 페이지로 이동
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "user/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String loginUser(@RequestParam("userEmail") String userEmail,
                            @RequestParam("userPassword") String userPassword,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest httpServletRequest) {

        try {
            // 디버깅용 로그 (이메일 + 패스워드 출력)
            System.out.println("Login attempt - Email: " + userEmail + ", Password: " + userPassword);

            // 세션 객체 가져오기
            HttpSession session = httpServletRequest.getSession();

            // 이메일과 비밀번호로 유저 정보 조회
            User user = userService.loginUser(userEmail, userPassword);

            // 로그인 성공 시 세션에 유저 ID 저장
            session.setAttribute("userId", user.getUserId());

            System.out.println("Login successful for user: " + user.getEmail());

            // 성공 메시지 추가
            redirectAttributes.addFlashAttribute("message", "로그인에 성공했습니다!");

            return "redirect:/";

        } catch (Exception e) {

            // 에러 발생 시 로그 출력
            System.out.println("Login error: " + e.getMessage());

            // 에러 메시지와 입력한 이메일 유지
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("userEmail", userEmail);

            return "redirect:/user/login";

        }
    }

    // 이메일 중복 체크 API
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {

        // 이메일 중복 여부 확인
        boolean exists = userService.isEmailExists(email);

        // 응답 데이터 설정
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        // 세션 종료
        HttpSession httpSession = request.getSession();
        httpSession.invalidate();

        return "redirect:/";

    }

    // 프로필 페이지로 이동
    @GetMapping("/profile")
    public String profile(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {

        // 세션에 저장된 userId로 사용자 정보 조회
        User user = userService.findByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);

        return "user/profile";

    }

    // 프로필 수정 폼 페이지로 이동
    @GetMapping("/editprofile")
    public String editProfile(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {

        // 세션에 저장된 userId로 사용자 정보 조회
        User user = userService.findByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);

        return "user/editprofile";

    }

    // 프로필 수정 요청 처리
    @PostMapping("/editprofile")
    public String updateProfile(@SessionAttribute(name = "userId") Integer userId,
                                @RequestParam("photo") MultipartFile file,
                                @ModelAttribute("user") User user) throws IOException {

        // userId로 기존 사용자 정보 가져오기
        User existingUser = userService.findByUser(userId);

        // 파일 업로드 경로 설정
        String uploadDir = UPLOAD_USER;
        Path path = Paths.get(uploadDir + file.getOriginalFilename());

        // 폴더 생성
        Files.createDirectories(path.getParent());
        file.transferTo(path);

        // 사용자 정보 업데이트
        existingUser.setNickname(user.getNickname());
        existingUser.setComment(user.getComment());
        existingUser.setArea(user.getArea());
        existingUser.setPhotoUrl(file.getOriginalFilename());
        userService.updateUser(existingUser);

        // 프로필 페이지로 리다이렉트
        return "redirect:/user/profile";

    }
}
