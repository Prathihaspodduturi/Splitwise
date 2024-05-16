package com.PrathihasProjects.PrathihasSplitwise.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SplitwiseController {

    @GetMapping("/splitwise/")
    public String sampleConnection() {
        return "Connected";
    }

}
