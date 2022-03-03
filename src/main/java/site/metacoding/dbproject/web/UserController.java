package site.metacoding.dbproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class UserController {

    // 회원가입 페이지 (정적) - 로그인X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // 회원가입 - 로그인X
    @PostMapping("/join")
    public String join() {
        return "redirect:/user/loginForm"; // 로그인페이지 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지 (정적) - 로그인X
    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username=? AND password=?
    // 원래 SELECT 는 무조건 get요청
    // 그런데 로그인만 예외 (POST)
    // 이유 : 주소에 패스워드를 남길 수 없으니까!!
    // 로그인 - - 로그인X
    @PostMapping("/login")
    public String login() {
        return "메인페이지를 돌려주면 됨"; // PostController 만들고 수정하자.
    }

    // 유저상세 페이지 (동적) - 로그인O
    @GetMapping("/user/{id}")
    public String detail(@PathVariable Integer id) {
        return "user/detail";
    }

    // 유저수정 페이지 (동적) - 로그인O
    @GetMapping("/user/{id}/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    // 유저수정 - 로그인O
    @PutMapping("/user/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/user/" + id;
    }

    // 로그아웃 - 로그인O
    @GetMapping("/logout")
    public String logout() {
        return "메인페이지를 돌려주면 됨"; // PostController 만들고 수정하자.
    }
}
