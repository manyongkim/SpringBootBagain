package com.harvest.bagain.users;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class UsersController {

	@Autowired
	private UsersDAO usersDAO;

	// 이메일 중복 확인
	@GetMapping("/check-email")
	public ResponseEntity<String> checkEmailDuplicate(@RequestParam String email) {
		String result = usersDAO.checkEmailDuplicate(email);
		return ResponseEntity.ok(result);
	}

	// 닉네임 중복 확인
	@GetMapping("/check-nickname")
	public ResponseEntity<String> checkNicknameDuplicate(@RequestParam String nickname) {
		String result = usersDAO.checkNicknameDuplicate(nickname);
		return ResponseEntity.ok(result);
	}

	// 사용자 등록 (회원가입)
	@PostMapping("/join")
	public ResponseEntity<String> join(@Validated @ModelAttribute UserJoinReq req,
			@RequestParam(required = false) MultipartFile photo) {
		String result = usersDAO.join(req, photo);
		return ResponseEntity.ok(result);
	}

	// 사용자 정보 확인(수정)
	@GetMapping("/info")
	public ResponseEntity<Map<String, Object>> userInfo(HttpSession session) {
		String userEmail = (String) session.getAttribute("userEmail");
		if (userEmail != null) {
			Users user = usersDAO.getLoginUserByEmail(userEmail);
			if (user != null) {
				Map<String, String> addressMap = usersDAO.splitAddress(session);
				Map<String, Object> response = new HashMap<>();
				response.put("email", user.getEmail());
				response.put("name", user.getName());
				response.put("nickname", user.getNickname());
				response.put("phoneNumber", user.getPhoneNumber());
				response.putAll(addressMap);
				response.put("photoFilename", user.getPhoto() != null ? user.getPhoto() : "");
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(404).body(null);
			}
		} else {
			return ResponseEntity.status(401).body(null);
		}
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestParam String email, @RequestParam String password,
			HttpServletRequest request) {
		Map<String, Object> result = usersDAO.login(email, password, request);
		if (result.get("status").equals(true)) {
			result.put("message", "로그인 성공");
			result.put("email", request.getSession().getAttribute("userEmail"));
		} else {
			result.put("message", result.get("message"));
		}
		return ResponseEntity.ok(result);
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
		usersDAO.logout(session);
		Map<String, Object> response = new HashMap<>();
		response.put("status", true);
		response.put("message", "로그아웃 성공");
		return ResponseEntity.ok(response);
	}

	// 회원정보 수정
	@PostMapping("/update")
	public ResponseEntity<Map<String, Object>> update(@RequestParam String nickname, @RequestParam String phoneNumber,
			@RequestParam(required = false) MultipartFile photo, HttpSession session) {
		Map<String, Object> result = usersDAO.update(nickname, phoneNumber, photo, session);
		if ((boolean) result.get("status")) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.ok(result);
		}
	}
}