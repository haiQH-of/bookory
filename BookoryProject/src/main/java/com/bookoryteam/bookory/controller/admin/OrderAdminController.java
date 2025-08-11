package com.bookoryteam.bookory.controller.admin;

import com.bookoryteam.bookory.model.Order;
import com.bookoryteam.bookory.model.OrderItem;
import com.bookoryteam.bookory.service.OrderService;
import com.bookoryteam.bookory.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * NOTE: [Đã hoàn thành - Khang]
 * - Đã thêm tính năng xóa mềm (soft delete) cho đơn hàng theo yêu cầu
 * - Đã tạo trang hiển thị đơn hàng đã xóa mềm (order-deleted.html)
 * - Đã thêm chức năng khôi phục đơn hàng đã xóa mềm
 * - Đã thêm hiển thị thống kê đơn hàng theo yêu cầu
 * - Đã thêm chức năng xóa cứng (hard delete) cho đơn hàng
 */
@Controller
@RequestMapping("/admin/order")
public class OrderAdminController {
	// Yêu cầu cho Phạm Vĩnh Khang ở trang quản trị đối với [Order]:
    // 1. Chỉ có thể Xóa mềm (Soft Delete).
    // 2. Không được phép Chỉnh sửa (Edit) thông tin đơn.
    // 4. Có ô hiển thị các chỉ số thống kê: Tổng đơn hàng, Tổng tiền (như trang admin/dashboard).
	// 5. Khi bấm vào hàng vào đó thì sẽ chuyển hướng đến trang admin/order-item và hiển thị các món bên trong [Order]	
	
    private final OrderService orderService;
    private final UserService userService;

    public OrderAdminController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrders(Model model) {
        // Lấy danh sách đơn hàng và tính toán thống kê
        List<Order> orders = orderService.findByDeletedFalse();
        
        long totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long pendingOrders = countOrdersByStatus(orders, "PENDING");
        long deliveredOrders = countOrdersByStatus(orders, "DELIVERED");
        
        // Đưa dữ liệu vào model
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("currentPageTitle", "Quản Lý Đơn Hàng");
        
        return "admin/order";
    }

    /**
     * NOTE: [Đã hoàn thành - Khang]
     * - Đã thêm trang hiển thị đơn hàng đã xóa mềm
     * - Được triển khai theo yêu cầu của leader về quản lý đơn hàng
     */
    @GetMapping("/deleted")
    public String listDeletedOrders(Model model) {
        // Lấy danh sách đơn hàng đã xóa mềm
        List<Order> deletedOrders = orderService.findByDeletedTrue();
        
        // Đưa dữ liệu vào model
        model.addAttribute("orders", deletedOrders);
        model.addAttribute("isDeletedList", true);
        model.addAttribute("currentPageTitle", "Đơn Hàng Đã Xóa");
        
        return "admin/order-deleted";
    }

    /**
     * NOTE: [Đã hoàn thành - Khang]
     * - Đã triển khai chức năng xóa mềm đơn hàng
     * - Đảm bảo dữ liệu đơn hàng vẫn được giữ lại trong DB
     */
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return orderService.findById(id)
                .map(order -> {
                    order.setDeleted(true);
                    orderService.save(order);
                    redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xóa thành công!");
                    return "redirect:/admin/order";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                    return "redirect:/admin/order";
                });
    }
    
    /**
     * NOTE: [Đã hoàn thành - Khang]
     * - Đã thêm chức năng khôi phục đơn hàng đã xóa mềm
     * - Cho phép admin quản lý đơn hàng một cách linh hoạt
     */
    @GetMapping("/restore/{id}")
    public String restoreOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return orderService.findById(id)
                .map(order -> {
                    order.setDeleted(false);
                    orderService.save(order);
                    redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được khôi phục thành công!");
                    return "redirect:/admin/order/deleted";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                    return "redirect:/admin/order/deleted";
                });
    }
    
    /**
     * NOTE: [Đã hoàn thành - Khang]
     * - Đã thêm chức năng xóa cứng (hard delete) đơn hàng
     * - Xóa hoàn toàn đơn hàng khỏi cơ sở dữ liệu
     * - Chỉ có thể thực hiện với đơn hàng đã bị xóa mềm trước đó
     */
    @GetMapping("/hard-delete/{id}")
    public String hardDeleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return orderService.findById(id)
                .map(order -> {
                    if (order.getDeleted()) {
                        orderService.deleteById(id);
                        redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xóa vĩnh viễn khỏi hệ thống!");
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Chỉ có thể xóa cứng đơn hàng đã bị xóa mềm!");
                    }
                    return "redirect:/admin/order/deleted";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                    return "redirect:/admin/order/deleted";
                });
    }
    
    @GetMapping("/view/{id}")
    public String viewOrderItems(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return orderService.findById(id)
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("orderItems", order.getOrderItems());
                    model.addAttribute("currentPageTitle", "Chi Tiết Đơn Hàng #" + id);
                    return "admin/order-item";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
                    return "redirect:/admin/order";
                });
    }
    
    // Helper method để đếm đơn hàng theo trạng thái
    private long countOrdersByStatus(List<Order> orders, String status) {
        return orders.stream()
                .filter(order -> status.equals(order.getStatus()))
                .count();
    }
}