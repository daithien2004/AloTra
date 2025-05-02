package vn.iotstar.controllers.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.entity.Branch;
import vn.iotstar.entity.MilkTea;
import vn.iotstar.entity.MilkTeaType;
import vn.iotstar.entity.User;
import vn.iotstar.services.IBranchMilkTeaService;
import vn.iotstar.services.IIncomeService;
import vn.iotstar.services.IMilkTeaTypeService;
import vn.iotstar.services.IUserService;

@Controller
@RequestMapping("user")
public class UHomeController {
    
    @Autowired
    private IMilkTeaTypeService milkteaType;
    
    @Autowired
    private IUserService userService; // Giả sử bạn có một service để cập nhật thông tin người dùng
    
	@Autowired
	private IIncomeService iIncomeService;
	
	@Autowired
	private IBranchMilkTeaService branchMilkTeaService;
    
    @GetMapping("home")
    public String index(Model model) {
    	// Đưa các loại trà sữa hiển thị lên sidebar
        List<MilkTeaType> list = milkteaType.findAll();
        model.addAttribute("listType", list);
        List<Branch> list1 = iIncomeService.getTop4BranchesByTotalValue();
		model.addAttribute("listBranch", list1);
		List<MilkTea> listMilkTea = branchMilkTeaService.getTop3MilkTea();
		model.addAttribute("listMilkTea", listMilkTea);
        // Dữ liệu `user` đã được thêm từ Interceptor, không cần xử lý lại
        return "user/homeuser"; // Trả về trang homeuser.html
    }
    
    // Hiển thị trang Hồ sơ
    @GetMapping("account")
    public String showAccountPage(Model model) {
    	 // Dữ liệu `user` đã được thêm từ Interceptor, không cần xử lý lại
        return "user/account"; // Trả về trang account.html
    }
    @PostMapping("/account/{id}")
    public String editAccount(Model model, @PathVariable("id") Integer userId, @ModelAttribute User updatedUser) {
        // Lấy user từ database
        Optional<User> optionalUser = userService.findById(userId);
        
        if (optionalUser.isPresent()) { // Kiểm tra nếu user tồn tại
            User user = optionalUser.get(); // Lấy đối tượng User từ Optional
            // Cập nhật thông tin
            user.setFullName(updatedUser.getFullName());
            user.setAddress(updatedUser.getAddress());
            user.setPhone(updatedUser.getPhone());
            userService.save(user); // Lưu user đã cập nhật vào database
            model.addAttribute("message", "Cập nhật thông tin thành công!");
        } else {
            model.addAttribute("message", "Không tìm thấy người dùng!");
        }
        
        model.addAttribute("user", optionalUser.orElse(null)); // Đưa user (nếu có) vào model
        return "user/account"; // Trả về trang account
    }

   
    
}