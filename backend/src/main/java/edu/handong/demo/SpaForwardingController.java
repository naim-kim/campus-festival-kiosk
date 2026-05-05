package edu.handong.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Lets a SPA handle client-side routes by forwarding non-API paths to index.html.
 */
@Controller
public class SpaForwardingController {

    @RequestMapping(value = {
            "/{path:^(?!api$).*$}",
            "/**/{path:^(?!api$).*$}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}

