package com.harvest.bagain.users;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.harvest.bagain.BagainFileNameGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UsersDAO {
	private static final Logger logger = LoggerFactory.getLogger(UsersDAO.class);
	private BCryptPasswordEncoder bcpe;

	@Autowired
	private UsersRepository usersRepo;

	@Value("${users.images.directory}")
	private String usersImagesDirectory;

	public UsersDAO() {
		this.bcpe = new BCryptPasswordEncoder();
	}

	public String checkEmailDuplicate(String email) {
		return usersRepo.existsByEmail(email) ? "중복된 이메일입니다." : "사용 가능한 이메일입니다.";
	}

	public String checkNicknameDuplicate(String nickname) {
		return usersRepo.existsByNickname(nickname) ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.";
	}

	public Users getLoginUserByEmail(String email) {
		Optional<Users> userOptional = usersRepo.findByEmail(email);
		return userOptional.orElse(null);
	}
	
	public Map<String, Object> info(HttpSession session) {
		String userEmail = (String) session.getAttribute("userEmail");
		if (userEmail != null) {
			Users user = getLoginUserByEmail(userEmail);
			if (user != null) {
				Map<String, String> addressMap = splitAddress(user);
				Map<String, Object> response = new HashMap<>();
				response.put("email", user.getEmail());
				response.put("name", user.getName());
				response.put("nickname", user.getNickname());
				response.put("phoneNumber", user.getPhoneNumber());
				response.putAll(addressMap);
				String photoUrl = user.getPhoto() != null ? "https://file.bargainus.kr/users/images/" + user.getPhoto() : "";
				response.put("photoFilename", photoUrl);
				return response;
			}
		}
		return null;
	}
	
	public String join(UserJoinReq req, MultipartFile photo) {
        String fileName = null;
        try {
            // 프로필 사진 저장
            if (photo != null && !photo.isEmpty()) {
                fileName = BagainFileNameGenerator.generate(photo);
                photo.transferTo(new File(usersImagesDirectory + "/" + fileName));
                req.setPhotoFilename(fileName); // DTO에 파일명만 저장
            }
            // 비밀번호 암호화
            req.setPassword(bcpe.encode(req.getPassword()));
            Users user = new Users();
            user.setEmail(req.getEmail());
            user.setPassword(req.getPassword());
            user.setName(req.getName());
            user.setNickname(req.getNickname());
            user.setPhoneNumber(req.getPhoneNumber());
            user.setAddress(req.getPostalCode() + "!" + req.getAddress() + "!" + req.getDetailAddress());
            user.setPhoto(fileName);
            usersRepo.save(user);

            return "가입 성공";

        } catch (Exception e) {
            if (fileName != null) {
                new File(usersImagesDirectory + "/" + fileName).delete();
            }
            return "가입 실패";
        }
    }

	public Map<String, Object> login(String email, String password, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		session = request.getSession(true);
		Map<String, Object> response = new HashMap<>();
		try {
			Optional<Users> userOptional = usersRepo.findByEmail(email);
			if (userOptional.isPresent()) {
				Users user = userOptional.get();
				if (bcpe.matches(password, user.getPassword())) {
					// 세션에 사용자 이메일 저장
					session.setAttribute("userEmail", user.getEmail());
					session.setAttribute("loginMember", user);
					response.put("status", true);
					response.put("message", "로그인 성공");
					response.put("sessionId", session.getId());
					response.put("nickname", user.getNickname());
				} else {
					response.put("status", false);
					response.put("message", "로그인 실패: 비밀번호 오류");
				}
			} else {
				response.put("status", false);
				response.put("message", "로그인 실패: 가입되지 않은 이메일");
			}
		} catch (Exception e) {
			logger.error("로그인 중 오류 발생", e);
			response.put("status", false);
			response.put("message", "로그인 실패: 서버 오류");
		}
		return response;
	}

	public Map<String, Object> logout(HttpSession session) {
		session.invalidate();
		Map<String, Object> response = new HashMap<>();
		response.put("status", true);
		response.put("message", "로그아웃 성공");
		return response;
	}

	public Map<String, String> splitAddress(Users user) {
		Map<String, String> addressMap = new HashMap<>();
		if (user != null && user.getAddress() != null) {
			String[] addr = user.getAddress().split("!");
			addressMap.put("postalCode", addr.length > 0 ? addr[0] : "");
			addressMap.put("address", addr.length > 1 ? addr[1] : "");
			addressMap.put("detailAddress", addr.length > 2 ? addr[2] : "");
		} else {
			addressMap.put("postalCode", "");
			addressMap.put("address", "");
			addressMap.put("detailAddress", "");
		}
		return addressMap;
	}

	public Map<String, Object> update(String nickname, String phoneNumber, MultipartFile photo, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		Users loginMember = (Users) session.getAttribute("loginMember");
		if (loginMember == null) {
			response.put("status", false);
			response.put("message", "로그인이 필요합니다.");
			return response;
		}

		String oldFile = loginMember.getPhoto();
		String newFile = null;
		try {
			if (photo != null && !photo.isEmpty()) {
				newFile = BagainFileNameGenerator.generate(photo);
				photo.transferTo(new File(usersImagesDirectory + "/" + newFile));
			} else {
				newFile = oldFile;
			}
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", "수정 실패(파일)");
			return response;
		}

		try {
			loginMember.setNickname(nickname);
			loginMember.setPhoneNumber(phoneNumber);
			loginMember.setPhoto(newFile);

			usersRepo.save(loginMember);
			session.setAttribute("loginMember", loginMember);
			session.setAttribute("userEmail", loginMember.getEmail());

			if (!newFile.equals(oldFile) && oldFile != null) {
				new File(usersImagesDirectory + "/" + oldFile).delete();
			}

			response.put("status", true);
			response.put("message", "수정 성공");
			return response;
		} catch (Exception e) {
			if (newFile != null && !newFile.equals(oldFile)) {
				new File(usersImagesDirectory + "/" + newFile).delete();
			}
			response.put("status", false);
			response.put("message", "수정 실패(DB)");
			return response;
		}
	}

}