package sg.edu.nus.iss.app.day18lovecalcprac.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.app.day18lovecalcprac.model.Calculator;
import sg.edu.nus.iss.app.day18lovecalcprac.service.LoveCalcService;

@Controller
@RequestMapping(path = "calculator")
public class LoveCalcController {
    @Autowired
    private LoveCalcService loveCalcSvc;

    @GetMapping
    public String getResult(@RequestParam(required = true) String sname,
            @RequestParam(required = true) String fname, Model model) throws IOException, InterruptedException {
        Optional<Calculator> c = loveCalcSvc.getResult(sname, fname);
        model.addAttribute("calculator", c.get());
        return "calculator";
    }

    @GetMapping(path = "/list")
    public String getAllResult(Model model) throws IOException{
        Calculator[] results = loveCalcSvc.findAll();
        model.addAttribute("results", results);
        return "listResult";
    }   
}
