package walkbook.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {
    @GetMapping("/daum.html")
    public String callAddressHtml(){
        return "daum.html";
    }
}
