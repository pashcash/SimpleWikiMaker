package com.example.SimpleWiki.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.example.SimpleWiki.model.File;
import com.example.SimpleWiki.repository.FileRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    FileRepository mdRepository;
    FileRepository htmlRepository;

    FileRepository settingsRepository;

    public HomeController()
    {
        mdRepository = new FileRepository();
        htmlRepository = new FileRepository();
        settingsRepository = new FileRepository();
    }

    @RequestMapping("/")
    public String index(Model model) 
    {
        return "index";
    }

    @RequestMapping("/mdDirButtonClick")
    public String MdDirClick(Model model, @RequestParam(value = "name") String name)
    {
        mdRepository.CurrentFilesUp(name);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        model.addAttribute("showMdBackButton", false);
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlDirButtonClick")
    public String HtmlDirClick(Model model, @RequestParam(value = "name") String name)
    {
        htmlRepository.CurrentFilesUp(name);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        model.addAttribute("showHtmlBackButton", false);
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/setListOfMdFiles")
    public String SetCurrentFiles(Model model, @RequestBody List<File> allFiles) 
    {
        mdRepository = new FileRepository();
        htmlRepository = new FileRepository();

        mdRepository.SetAllFiles(allFiles);
        mdRepository.SetCurrentFiles();
        model.addAttribute("showMdBackButton", true);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    } 

    @RequestMapping("/mdBackButtonClick")
    public String MdSetCurrentFilesDown(Model model) 
    {
        mdRepository.CurrentFilesDown();
        model.addAttribute("showMdBackButton", mdRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/htmlBackButtonClick")
    public String HtmlSetCurrentFilesDown(Model model) 
    {
        htmlRepository.CurrentFilesDown();
        model.addAttribute("showHtmlBackButton", htmlRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/convertRepoToHtml")
    public String ConvertMdFilesToHtml(Model model) 
    {
        htmlRepository = new FileRepository();
        htmlRepository.SetHtmlRepositoryByMd(mdRepository);
        model.addAttribute("showHtmlBackButton", true);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/getMdFolder")
    public String GetMdFolder(Model model)
    {
        model.addAttribute("showMdBackButton", mdRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentMdFiles", mdRepository.GetCurrentFiles());
        return "fragments/listOfMdFiles";
    }

    @RequestMapping("/getHtmlFolder")
    public String GetHtmlFolder(Model model)
    {
        model.addAttribute("showHtmlBackButton", htmlRepository.GetCurrentFolderPath().equals("/") ? true : false);
        model.addAttribute("currentHtmlFiles", htmlRepository.GetCurrentFiles());
        return "fragments/listOfHtmlFiles";
    }

    @RequestMapping("/getHtmlPath")
    @ResponseBody
    public String GetHtmlPathByName(@RequestParam(value = "name") String name) 
    {
        return htmlRepository.GetPathByName(name);
    }

    @RequestMapping("/setSettingsFiles")
    @ResponseBody
    public void SetSettingsFiles(@RequestBody List<File> settingFiles)
    {
        settingsRepository = new FileRepository();
        settingsRepository.SetAllFiles(settingFiles);
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, value = "/p/**")
    @ResponseBody
    public String GetHtmlPage(HttpServletRequest request) {
        String restOfTheUrl = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());
        File htmlFile = htmlRepository.GetFileByPath("/" + restOfTheUrl + ".html");
        if (htmlFile == null)
        {
            return "<html>\n" + "<head><title>Welcome</title></head>\n" +
          "<body>\n" + "File doesnt exist" + "\n" + "</body>\n" + "</html>";
        }
        return "<html>\n" + "<head><title>Welcome</title>"
        + "<style>" + (settingsRepository.GetAllFiles().size() == 0 ? "" : settingsRepository.GetAllFiles().get(0).GetText()) + "</style>" + "</head>\n" 
        + "<body>\n" + htmlFile.GetText() + "\n" + "</body>\n" + "</html>";
    }

}