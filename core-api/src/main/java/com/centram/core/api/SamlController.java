package com.centram.core.api;

import com.centram.common.service.JasyptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class SamlController {

    private final String uiSsoPath = "/sso/sign-in/";
    @Value("${app.base.url}")
    private String appBaseUrl;
    @Autowired
    private JasyptService jasyptService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/auth")
    public String handleSamlAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return "redirect:/home";
        } else {
            return "/";
        }
    }

    @RequestMapping("/login-sso")
    public String home(HttpServletResponse httpServletResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //new String(Base64.encodeBase64(authentication.getPrincipal().toString().getBytes(StandardCharsets.UTF_8)))
        if (authentication != null) {
            httpServletResponse.setHeader("Location", appBaseUrl.concat(uiSsoPath)
                    .concat(URLEncoder.encode(
                                    jasyptService.encrypt(
                                            authentication.getPrincipal().toString()
                                    ), StandardCharsets.UTF_8
                            )
                    )
            );
            httpServletResponse.setStatus(302);
            return "/";
        } else {
            return "/";
        }
    }
}
