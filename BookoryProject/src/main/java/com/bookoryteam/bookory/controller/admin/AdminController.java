package com.bookoryteam.bookory.controller.admin;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookoryteam.bookory.model.Order;
import com.bookoryteam.bookory.service.AuthorService;
import com.bookoryteam.bookory.service.BookService;
import com.bookoryteam.bookory.service.CategoryService;
import com.bookoryteam.bookory.service.OrderService;
import com.bookoryteam.bookory.service.PublisherService;
import com.bookoryteam.bookory.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	// Yêu cầu cho Nguyễn Tấn Duy ở trang quản trị đối với [Dashboard]:
	// 1. Không cần đụng tới "Hoạt động gần đây"
	// 2. Show ra được tổng của tất cả như mẫu đã cho 
	

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BookService bookService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/dashboard")
    public String getDashboardStats(Model model) {
    	
//        model.addAttribute("totalRevenue", totalRevenue); chua load len duoc
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalOrders", orderService.countOrders());
        model.addAttribute("totalBooksSold", orderService.countBooksSold());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("totalBooks", bookService.countBooks());
        model.addAttribute("totalPublishers", publisherService.countPublishers());
        model.addAttribute("totalAuthors", authorService.countAuthors());
        model.addAttribute("totalCategories", categoryService.countCategories());

        return "admin/dashboard";  // trả về tên view Thymeleaf
    }
		
	@GetMapping("/setting")
	public String setting() {
		return "admin/setting";
	}
}