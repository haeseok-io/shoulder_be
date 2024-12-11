package org.shoulder.backend.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @GetMapping("/api/auth/test")
    public List<String> getTest() {
        List<String> items = new ArrayList<>();
        items.add("바나나");
        items.add("딸기");
        items.add("사과");

        return items;
    }
}
