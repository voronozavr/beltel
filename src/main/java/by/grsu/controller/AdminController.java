package by.grsu.controller;

import by.grsu.PdfSettings;
import by.grsu.entity.*;
import by.grsu.service.Impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

import org.w3c.dom.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private RepnoteResServiceImpl repnoteResService;

    @Autowired
    private ReportingNoticeServiceImpl reportingNoticeService;

    @Autowired
    private SectorServiceImpl sectorService;

    @Autowired
    private OperationsHistoryController operationsHistory;

    @Autowired
    private OperationsHistoryServiceImpl operationsHistoryService;

    @RequestMapping(value = "/new_notices", method = RequestMethod.GET)
    public String newNotices(ModelMap modelMap) {
        modelMap.addAttribute("mode", "new");
        modelMap.addAttribute("reportingNotice", reportingNoticeService.getByStatus(false));
        return "all_notices";
    }

    @RequestMapping(value = "/all_notices", method = RequestMethod.GET)
    public String allNotices(ModelMap modelMap) {
        modelMap.addAttribute("mode", "all");
        modelMap.addAttribute("reportingNotice", reportingNoticeService.getAll());
        return "all_notices";
    }

    @RequestMapping(value = "/edit_db", method = RequestMethod.GET)
    public String editDB() {
        return "edit_db";
    }

    @RequestMapping(value = "/operations_history")
    public String getOperationHistory(){
        return "operations_history";
    }

    @ResponseBody
    @RequestMapping(value = "/admin/get_operations_history")
    public List<OperationsHistory> getAllOperations(){
        return operationsHistoryService.getAll();
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteReportingNotice(@PathVariable long id) {
        repnoteResService.deleteByNoticeId(id);
        reportingNoticeService.delete(id);
        operationsHistory.saveOperation("Удаление заявки #" + id);
        return "redirect:/all_notices";
    }

    @RequestMapping(value = "/change_status/{id}")
    public String changeReportingNoticeStatus(@PathVariable long id) {
        ReportingNotice reportingNotice = reportingNoticeService.getById(id);
        if (reportingNotice.isStatus() == false) {
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            reportingNotice.setDateDone(date);
            reportingNotice.setStatus(true);
            reportingNoticeService.save(reportingNotice);
            operationsHistory.saveOperation("Одобрение заявки #" + id);
            return "redirect:/notice/" + id;
        }
        reportingNotice.setStatus(false);
        reportingNoticeService.save(reportingNotice);
        operationsHistory.saveOperation("Отклонение заявки #" + id);
        return "redirect:/notice/" + id;
    }

    @RequestMapping(value = "/pdf_settings", method = RequestMethod.GET)
    public String getPdfSettings(ModelMap modelMap, HttpServletRequest request){
        try{
            PdfSettings pdfSettings = new PdfSettings();
            pdfSettings.setBranch(getPdfSettingsByTag("branch", request));
            pdfSettings.setDirector(getPdfSettingsByTag("director", request));
            pdfSettings.setCity(getPdfSettingsByTag("city", request));

            modelMap.addAttribute("data", pdfSettings);
        }catch(Exception e){
            modelMap.addAttribute("error", "exception");
        }
        return "pdf_settings";
    }

    @RequestMapping(value = "/pdf_settings", method = RequestMethod.POST)
    public String setPdfSettings(@ModelAttribute("data") PdfSettings data, HttpServletRequest request, ModelMap modelMap){
        try {
            URL resource = request.getServletContext().getResource("/xml/pdf_settings.xml");
            String settingsPath = resource.getPath();
            File settings = new File(settingsPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document document = docBuilder.parse(settings);
            document.getDocumentElement().normalize();

            Element branch = (Element) document.getElementsByTagName("branch").item(0);
            Element director = (Element) document.getElementsByTagName("director").item(0);
            Element city = (Element) document.getElementsByTagName("city").item(0);

            branch.setTextContent(data.getBranch());
            director.setTextContent(data.getDirector());
            city.setTextContent(data.getCity());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(settings);
            transformer.transform(source, result);
        }catch (Exception e){
            modelMap.addAttribute("error", "exception");
        }
        operationsHistory.saveOperation("Изменение настроек формирования PDF");
        return "redirect:/pdf_settings";
    }

    public static String getPdfSettingsByTag(String tag, HttpServletRequest request){
        String data = null;
        try {
            URL resource = request.getServletContext().getResource("/xml/pdf_settings.xml");
            String settingsPath = resource.getPath();
            File settings = new File(settingsPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document document = docBuilder.parse(settings);
            document.getDocumentElement().normalize();
            Element element = (Element) document.getElementsByTagName(tag).item(0);
            data = element.getTextContent();
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/get_sectors/{divisionId}", method = RequestMethod.GET)
    public List<Sector> getSectorsByDivisionId(@PathVariable long divisionId){
        return sectorService.getByDivisionId(divisionId);
    }
}
