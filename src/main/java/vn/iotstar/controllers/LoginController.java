package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.services.IEmailService;
import vn.iotstar.services.IOtpService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEmailService emailService;
	
	@Autowired
	private IOtpService otpService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping
	public String showLoginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		// Kiểm tra xem người dùng đã đăng nhập chưa qua session
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("account") != null) {
			return "redirect:/user/home"; // Nếu đã đăng nhập thì chuyển hướng tới trang đích
		}

		// Kiểm tra cookie nhớ mật khẩu
		Cookie[] cookies = request.getCookies();
		String username = "";
		String password = ""; // Mật khẩu sẽ được thêm vào nếu có cookie nhớ mật khẩu
		boolean rememberMe = false;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("COOKIE_REMEMBER".equals(cookie.getName())) {
					String[] userData = cookie.getValue().split("::");
					if (userData.length == 2) {
						username = userData[0]; // Lấy tên đăng nhập từ cookie
						password = userData[1]; // Lấy mật khẩu từ cookie
						rememberMe = true; // Đánh dấu là đã chọn nhớ mật khẩu
					}
				}
			}
		}

		// Truyền vào model để hiển thị thông tin trên form
		model.addAttribute("username", username);
		model.addAttribute("password", password); // Truyền mật khẩu vào model
		model.addAttribute("rememberMe", rememberMe); // Checkbox nhớ mật khẩu được đánh dấu nếu cookie tồn tại

		return "login";
	}

	@PostMapping
	public String handleLogin(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam(value = "remember", required = false) String remember, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		boolean isRememberMe = "on".equals(remember); // Kiểm tra xem người dùng có chọn nhớ mật khẩu không
		String alertMsg = "";
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			alertMsg = "Tài khoản hoặc mật khẩu không được rỗng";
			model.addAttribute("alert", alertMsg);
			return "login";
		}

		// Kiểm tra thông tin đăng nhập
		User user = userService.login(username, password);
		if (user != null) {
			if(user.isActive() == true)
			{
				HttpSession session = request.getSession(true);
				session.setAttribute("account", user);

				// Nếu chọn nhớ mật khẩu thì lưu cookie
				if (isRememberMe) {
					saveRememberMe(response, username, password);
				} else {
					deleteRememberMeCookie(response); // Nếu không chọn thì xóa cookie
				}

				return "redirect:/user/home";
			}
			else
			{
				alertMsg = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để xử lý.";
				model.addAttribute("alert", alertMsg);
				return "login";
			}
		} else {
			alertMsg = "Tài khoản hoặc mật khẩu không đúng";
			model.addAttribute("alert", alertMsg);
			return "login";
		}
	}

	// Lưu cookie nhớ mật khẩu
	private void saveRememberMe(HttpServletResponse response, String username, String password) {
		String userData = username + "::" + password;
		Cookie cookie = new Cookie("COOKIE_REMEMBER", userData);
		cookie.setMaxAge(30 * 60); // Cookie tồn tại trong 30 phút
		cookie.setPath("/"); // Áp dụng cho toàn bộ các URL
		response.addCookie(cookie);
	}

	// Xóa cookie khi không chọn "Nhớ mật khẩu"
	private void deleteRememberMeCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("COOKIE_REMEMBER", "");
		cookie.setMaxAge(0); // Xóa cookie ngay lập tức
		cookie.setPath("/"); // Áp dụng cho toàn bộ các URL
		response.addCookie(cookie);
	}

	@GetMapping("forgot-password")
	public String showForgotPasswordPage() {
		return "forgot-password";

	}
	
	@PostMapping("forgot-password")
	public String sendOtpToEmail(@RequestParam("email") String email, Model model) 
	{
		if (!userService.existsByEmail(email)) {
            model.addAttribute("alert", "Email không tồn tại trong hệ thống!");
            return "forgot-password";  // Quay lại trang nhập email nếu không tìm thấy email
        }

        // Tạo mã OTP và gửi qua email
        String otp = otpService.generateOtp(email);
        String subject = "Mã OTP xác nhận thay đổi mật khẩu";
        String text = "Mã OTP của bạn là: " + otp + ". Mã OTP sẽ hết hạn sau 5 phút.";
        emailService.sendEmail(email, subject, text);

        model.addAttribute("email", email);  // Gửi lại email để điền vào ô khi xác thực OTP
        return "verify-otp-pass";  // Chuyển sang trang nhập OTP
	}
	
	@PostMapping("verify-otp-pass")
	 public String verifyOtp(@RequestParam String otp, @RequestParam String newPassword, @RequestParam String email, Model model) {
        boolean isValidOtp = otpService.validateOtp(email, otp);

        if (!isValidOtp) {
            model.addAttribute("alert", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            model.addAttribute("email", email);
            return "verify-otp-pass";  // Quay lại trang nhập OTP
        }

        // Cập nhật mật khẩu mới
        User user = userService.findByEmail(email);
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userService.save(user);

        model.addAttribute("successMessage", "Mật khẩu đã được thay đổi thành công!");
        return "login";  // Chuyển đến trang đăng nhập
    }
	
}