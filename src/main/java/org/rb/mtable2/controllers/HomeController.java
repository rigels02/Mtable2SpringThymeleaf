package org.rb.mtable2.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private static final String HOME = "MODE_HOME";
	
	//HOME
		@GetMapping(path="/")
		public String home(Model model){
			model.addAttribute("mode", HOME);
			return "index";
		}
}
