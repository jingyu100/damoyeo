package Team6.Damoyeo.User.Service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // 로그 추가
        System.out.println("Login attempt - Email: " + email);

        User user = null;

        user = userRepository.findByEmail(email);

        // 사용자가 없는 경우
        if(user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 확인
        if(passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return userRepository.save(user);
    }
    
    //이메일 중복 체크 메서드
    public boolean emailCheck(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email); // 여기서 true/false 반환
    }

}