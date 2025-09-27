package com.vodacom.customerregistration.api.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving static pages (not under /api/v1)
 */
@Controller
public class PageResource {

    /**
     * {@code GET /account/reset/finish} : Serve password reset page
     */
    @GetMapping(path = "/account/reset/finish", produces = "text/html")
    public String resetPasswordPage() {
        return "forward:/account/reset/finish.html";
    }
}
