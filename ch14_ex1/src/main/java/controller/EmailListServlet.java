package controller;

import javax.mail.MessagingException;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import model.User;
import data.UserDB;
import data.MailUtilGmail;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy hành động hiện tại
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";  // hành động mặc định
        }

        String url = "/index.jsp";        

        // Nếu người dùng chọn join → quay lại form
        if (action.equals("join")) {
            url = "/index.jsp";
        } 
        // Nếu người dùng chọn add → xử lý dữ liệu
        else if (action.equals("add")) {
            // Lấy dữ liệu từ form
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            // Lưu vào đối tượng User
            User user = new User(firstName, lastName, email);
            UserDB.insert(user);  // chèn vào DB giả hoặc thực
            request.setAttribute("user", user);

            // Gửi email cục bộ
            String to = email;
            String from = "email_list@murach.com";
            String subject = "Welcome to our email list";
            String body = "Dear " + firstName + ",\n\n"
                    + "Thanks for joining our email list. "
                    + "We'll make sure to send you announcements "
                    + "about new products and promotions.\n\n"
                    + "Have a great day!\n\n"
                    + "Mike Murach & Associates";
            boolean isBodyHTML = false;

            try {
                MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
            } catch (MessagingException e) {
                String errorMessage =
                        "ERROR: Unable to send email. "
                        + "Check Tomcat logs for details.<br>"
                        + "NOTE: You may need to configure your system "
                        + "as described in Chapter 14.<br>"
                        + "ERROR MESSAGE: " + e.getMessage();
                request.setAttribute("errorMessage", errorMessage);

                this.log(
                    "Unable to send email.\n"
                    + "Here is the email you tried to send:\n"
                    + "=====================================\n"
                    + "TO: " + email + "\n"
                    + "FROM: " + from + "\n"
                    + "SUBJECT: " + subject + "\n\n"
                    + body + "\n\n"
                );
            }

            // Sau khi xử lý xong, chuyển đến trang cảm ơn
            url = "/thanks.jsp";
        }

        // Chuyển hướng đến trang tương ứng
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }    
}
