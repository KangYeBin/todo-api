package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    String uploadRootPath;

    public boolean isDuplicated(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            return true;
        } else return false;
    }

    public UserSignUpResponseDTO create(final UserSignUpRequestDTO dto,
                                        final String uploadRootPath) {
        String email = dto.getEmail();

        if (isDuplicated(email)) {
            throw new RuntimeException("중복된 이메일입니다");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        // dto를 User entity로 변환해서 저장
        User saved = userRepository.save(dto.toEntity(uploadRootPath));
        log.info("회원 가입 정상 수행됨 - saved user - {}", saved);

        return new UserSignUpResponseDTO(saved);
    }

    public LoginResponseDTO authenticate(final LoginRequestDTO dto) {

        // 이메일을 통해 회원 정보 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다"));

        // 패스워드 검증
        String rawPassword = dto.getPassword(); // 입력한 비밀번호
        String encodedPassword = user.getPassword();    // DB에 저장된 암호화된 비밀번호

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다");
        }

        log.info("{}님 로그인 성공", user.getUserName());

        // 로그인 성공 후 로그인 유지를 위해 클라이언트에게 JWT를 발급해주어야 한다
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }

    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) {

        User user = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new NoRegisteredArgumentException("회원 조회에 실패했습니다"));

        // 일반(COMMON) 회원이 아니라면 예외 발생
        if (userInfo.getRole() != Role.COMMON) {
            throw new IllegalArgumentException("일반 회원이 아니라면 등급을 상승시킬 수 없습니다");
        }

        // 등급 변경
        user.changeRole(Role.PREMIUM);
        User saved = userRepository.save(user);

        // 변경된 정보가 반영된 토큰으로 재발급 (토큰에 Role이 포함되므로)
        String token = tokenProvider.createToken(saved);

        return new LoginResponseDTO(saved, token);
    }

    /**
     * 업로드 된 파일을 서버에 저장하고 저장 경로를 리턴
     *
     * @param profileImage - 업로드 된 파일 정보
     * @return 실제로 저장된 이미지 경로
     */
    public String uploadProfileImage(MultipartFile profileImage) throws IOException {

        // 루트 디렉토리가 실존하는지 확인 후 존재하지 않으면 생성
        File rootDir = new File(uploadRootPath);
        if (!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 충돌 가능성을 대비해서 유니크하게 변경
        // 여기서는 UUID와 원본파일명 결합
        String uniqueFileName = UUID.randomUUID() + "-" + profileImage.getOriginalFilename();

        // 파일 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        profileImage.transferTo(uploadFile);

        return uniqueFileName;
    }

    public String findProfilePath(String userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);

        // DB에는 파일명만 저장 -> service가 갖고 있는 Root Path와 연결해서 리턴
        return uploadRootPath + "/" + user.getProfileImg();
    }
}
