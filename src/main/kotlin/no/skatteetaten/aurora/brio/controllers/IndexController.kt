package no.skatteetaten.aurora.brio.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {

    @GetMapping("/")
    fun index() = "redirect:/docs/index.html"
}
