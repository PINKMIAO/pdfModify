package com.meorient.controller;

import com.meorient.PdfBoxTest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

@Controller
public class FileController {

    private InputStream in;

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String fileUploadAndDeal(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request, Model model) throws IOException {

        //获取文件名  file.getOriginalFilename()
        String uploadFileName = file.getOriginalFilename();

        //如果文件名为空或不是pdf文件，直接返回首页
        if ("".equals(uploadFileName) ||
                ".pdf".equals(uploadFileName.substring(uploadFileName.length()-4, uploadFileName.length()))) {
            System.out.println("未上传");
            return "redirect:/index";
        }
        System.out.println("上传文件名:"+uploadFileName);

        in = file.getInputStream();  //文件输出流

        modifyPDF(in);
        in.close();
        model.addAttribute("msg", "上传成功");

        return "redirect:/index";
    }

    /**
     * 获取公司信息及处理
     * @param in
     */
    public void modifyPDF(InputStream in) {

        PdfBoxTest pdfBoxTest = null;
        try {
            pdfBoxTest = new PdfBoxTest();
            pdfBoxTest.getCompanyInfo();
            pdfBoxTest.getCoordinate(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/download")
    public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;

        response.reset(); // 非常重要
        if (isOnLine) { // 在线打开方式
            URL u = new URL("file:///" + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        br.close();
        out.close();
    }


}
