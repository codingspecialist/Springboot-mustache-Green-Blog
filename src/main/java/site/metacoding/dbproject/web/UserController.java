package site.metacoding.dbproject.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.service.UserService;
import site.metacoding.dbproject.web.dto.ResponseDto;

@RequiredArgsConstructor
@Controller
public class UserController {

    // 컴퍼지션 (의존성 연결)
    private final UserService userService;
    private final HttpSession session;

    // http://localhost:8080/api/user/username/same-check?username=s
    // user의 username이 동일한지 확인해줄래? - 응답 (json)
    @GetMapping("/api/user/username/same-check")
    public @ResponseBody ResponseDto<String> sameCheck(String username) {
        String data = userService.유저네임중복검사(username);
        return new ResponseDto<String>(1, "통신성공", data);
    }

    // 회원가입 페이지 (정적) - 로그인X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // username=ssar&password=&email=ssar@nate.com 패스워드 공백
    // username=ssar&email=ssar@nate.com 패스워드 null
    // username=ssar&password=1234&email=ssar@nate.com (x-www-form)
    // 회원가입 - 로그인X

    @PostMapping("/join")
    public String join(User user) {

        // 필터의 역할
        // 1. username, password, email 1.null체크, 2.공백체크
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "redirect:/joinForm";
        }
        if (user.getUsername().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            return "redirect:/joinForm";
        }

        userService.회원가입(user);

        return "redirect:/loginForm"; // 로그인페이지 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지 (정적) - 로그인X
    @GetMapping("/loginForm")
    public String loginForm(HttpServletRequest request, Model model) {
        // jSessionId=fjsdklfjsadkfjsdlkj333333;remember=ssar
        // request.getHeader("Cookie");
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies(); // jSessionId, remember 두개가 있음.

            for (Cookie cookie : cookies) {
                System.out.println("쿠키값 : " + cookie.getName());
                if (cookie.getName().equals("remember")) {
                    model.addAttribute("remember", cookie.getValue());
                }

            }
        }

        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username=? AND password=?
    // 원래 SELECT 는 무조건 get요청
    // 그런데 로그인만 예외 (POST)
    // 이유 : 주소에 패스워드를 남길 수 없으니까!!
    // 로그인 - - 로그인X

    @PostMapping("/login")
    public String login(User user, HttpServletResponse response) {
        User userEntity = userService.로그인(user);

        if (userEntity != null) {
            session.setAttribute("principal", userEntity);
            if (user.getRemember() != null && user.getRemember().equals("on")) {
                response.addHeader("Set-Cookie", "remember=" + user.getUsername());
            }
            return "redirect:/";
        } else {
            return "redirect:/loginForm";
        }

    }

    // 로그아웃 - 로그인O
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/loginForm"; // PostController 만들고 수정하자.
    }

    // http://localhost:8080/user/1
    // 유저상세 페이지 (동적) - 로그인O
    @GetMapping("/s/user/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        // 유효성 검사 하기 (수십개....엄청 많겠죠?)

        User principal = (User) session.getAttribute("principal");

        // 1. 인증 체크
        if (principal == null) {
            return "error/page1";
        }

        // 2. 권한체크
        if (principal.getId() != id) {
            return "error/page1";
        }

        User userEntity = userService.유저정보보기(id);
        if (userEntity == null) {
            return "error/page1";
        } else {
            model.addAttribute("user", userEntity);
            return "user/detail";
        }
    }

    // 유저수정 페이지 (동적) - 로그인O
    @GetMapping("/s/user/updateForm")
    public String updateForm() {
        // 세션값을 출력했는데, 원래는 디비에서 가져와야 함.
        return "user/updateForm";
    }

    // 유저수정 - 로그인O
    @PutMapping("/s/user/{id}")
    public String update(@PathVariable Integer id) {

        return "redirect:/user/" + id;
    }

}
