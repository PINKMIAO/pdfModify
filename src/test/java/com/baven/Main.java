package com.baven;

import com.meorient.PdfBoxTest;
import com.meorient.pojo.Company;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) throws IOException {
        PDDocument newDoc = new PDDocument();
        InputStream in = new FileInputStream("C:\\Users\\MeoAdmin\\Desktop\\202010浙米凭证打印.pdf");
        PDFont font = PDType0Font.load(newDoc, new File("c:/windows/fonts/SIMLI.TTF"));

        //PDDocument oldDoc = PDDocument.load(in);

//        copyPage(oldDoc, newDoc);
//        addNewOnePage(newDoc, font);
        PdfBoxTest pdfBoxTest = new PdfBoxTest();
        pdfBoxTest.getCompanyInfo();
        pdfBoxTest.getCoordinate(in);

        //newDoc.save(new File("D:\\temp\\xxx.pdf"));
        //newDoc.close();
        System.out.println("Done");

    }

    // 公司名称估计得改成List
    private static void addNewOnePage(PDDocument newDoc, PDFont font){
        PDPage page = new PDPage();

        // for test
        ArrayList<Company> companies = getList();

        try (PDPageContentStream cs =
                     new PDPageContentStream(newDoc, page, PDPageContentStream.AppendMode.APPEND, true, true)){
            addTable(newDoc, font, cs, companies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newDoc.addPage(page);
    }



    public static boolean addTable(PDDocument doc, PDFont font, PDPageContentStream contentStream, ArrayList<Company> companies) {
        try {
            PDPage page = doc.getPage(0);
            float width = page.getMediaBox().getWidth();
            float height = page.getMediaBox().getLowerLeftY()+page.getMediaBox().getHeight();
            System.out.println(width + " " + height);

            // 表格起始x,y坐标;印章距离右上角各5mm
            float x = width - countMM(210);            // 里右侧的距离
            float y = height - countMM(32);            // 离页头的距离
            float tempY = y;
            System.out.println(x + " " + y);

            // 所有表格高度mm转point
            float allTableheight = countMM(8);


            // 上层左起第一个表格
            float topOneTableWidth = countMM(12);   // 宽度
            // 上层左起第二个表格
            float topTwoTableWidth = countMM(80);   // 宽度
            float topTwoTableX = x + topOneTableWidth;    // 加上之前的宽度
            float topThrTableWidth = countMM(18);
            float topThrTableX = topTwoTableX + topTwoTableWidth;
            float topFouTableWidth = countMM(18);
            float topFouTableX = topThrTableX + topThrTableWidth;
            float topFivTableWidth = countMM(77);
            float topFivTableX = topFouTableX + topFouTableWidth;

            // 提示作用做参考
//            float bottomTableY = y - allTableheight2;
//            // 下层左起第一个表格
//            float bottomOneTableWidth = countMM(11);
//            // 下层左起第二个表格
//            float bottomTwoTableWidth = countMM(53);
//            float bottomTwoTableX = x + bottomOneTableWidth;
//            // 下层左起第三个表格
//            float bottomThreeTableWidth = countMM(11);
//            float bottomThreeTableX = bottomTwoTableX + bottomTwoTableWidth;
//            // 下层左起第四个表格
//            float bottomFourTableWidth = countMM(15);
//            float bottomFourTableX = bottomThreeTableX + bottomThreeTableWidth;

            // 最大 30 行
            for (int i = 0; i < companies.size() + 1; i++) {
                drawTable(contentStream, x,tempY,topOneTableWidth,allTableheight);
                drawTable(contentStream, topTwoTableX,tempY,topTwoTableWidth,allTableheight);
                drawTable(contentStream, topThrTableX,tempY,topThrTableWidth,allTableheight);
                drawTable(contentStream, topFouTableX,tempY,topFouTableWidth,allTableheight);
                drawTable(contentStream, topFivTableX,tempY,topFivTableWidth,allTableheight);
                tempY -= allTableheight;
            }
            tempY = y;

            // 提示作用 -- 绘制不同的第二行表格
//            drawTable(contentStream, x,bottomTableY,bottomOneTableWidth,allTableheight2);
//            drawTable(contentStream, bottomTwoTableX,bottomTableY,bottomTwoTableWidth,allTableheight2);
//            drawTable(contentStream, bottomThreeTableX,bottomTableY,bottomThreeTableWidth,allTableheight2);
//            drawTable(contentStream, bottomFourTableX,bottomTableY,bottomFourTableWidth,allTableheight2);

            //  第一行必填信息  文字上移2毫米,居中
            float move2mm = countMM(3);
            int fontSizeTitle = 11;
            writeText(contentStream, font, fontSizeTitle, 0, 0, "测试");                               // 标记
            writeText(contentStream, font, fontSizeTitle, x, y + countMM(10), "文档编号：" + companies.get(0).getTranId());
            writeText(contentStream, font, fontSizeTitle, x + countMM(2), y + move2mm, "行号");
            writeText(contentStream, font, fontSizeTitle, topTwoTableX + countMM(2), y + move2mm, "客户名称");
            writeText(contentStream, font, fontSizeTitle, topThrTableX + countMM(2), y + move2mm, "借记");
            writeText(contentStream, font, fontSizeTitle, topFouTableX + countMM(2), y + move2mm, "贷记");
            writeText(contentStream, font, fontSizeTitle, topFivTableX + countMM(2), y + move2mm, "摘要");

            int count = 1;
            int fontSizeMsg = 10;
            for (Company company : companies) {
                tempY -= allTableheight;
                writeText(contentStream, font, fontSizeMsg, x + countMM(2), tempY + move2mm, String.valueOf(count++));
                writeText(contentStream, font, fontSizeMsg, topTwoTableX + countMM(2), tempY + move2mm, company.getFullName());

                // 借贷
                if (Integer.valueOf(company.getAmount()) > 0) {
                    writeText(contentStream, font, fontSizeMsg, topThrTableX + countMM(2), tempY + move2mm, String.valueOf(company.getAmount()));
                } else {
                    writeText(contentStream, font, fontSizeMsg, topFouTableX + countMM(2), tempY + move2mm, String.valueOf(company.getAmount()));
                }

                // 摘要
                if (company.getMemo().length() > 30) {
                    String memo = company.getMemo();
                    float line = tempY + countMM(5);
                    String str = memo.substring(0, 30);
                    writeText(contentStream, font, 8, topFivTableX + countMM(1), line, str);
                    line -= countMM(3);
                    String str1 = memo.substring(30, memo.length());
                    writeText(contentStream, font, 8, topFivTableX + countMM(1), line, str1);
                } else {
                    writeText(contentStream, font, 9, topFivTableX + countMM(2), tempY + move2mm, company.getMemo());
                }
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static void copyPage(PDDocument oldDoc, PDDocument newDoc) throws IOException {
        newDoc.addPage(takeOnePage(oldDoc, 1));
    }
    private static PDPage takeOnePage(PDDocument document, int i) {
        PDPage page = document.getPage(i - 1);
        return page;
    }

    private static float countMM(float point){
        // 将毫米转换成坐标点
        return (float) (point*72/25.4);
    }
    private static void writeText(PDPageContentStream contentStream, PDFont font, float fontSize, float startX, float startY, String txt) throws IOException {
        if(StringUtils.isEmpty(txt)){
            return;
        }
        contentStream.beginText();
        // 文字位置
        contentStream.newLineAtOffset(startX, startY);
        // 设置字体type,size
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(0, 0, 0);
        // 插入文本
        contentStream.showText(txt);
        contentStream.endText();
    }
    private static void drawTable(PDPageContentStream contentStream, float x, float y, float tableWidth, float tableHeight){
        try {
            contentStream.setStrokingColor(Color.black);
            drawLine(contentStream,x,y,x,y+tableHeight);
            drawLine(contentStream,x,y+tableHeight,x+tableWidth,y+tableHeight);
            drawLine(contentStream,x+tableWidth,y+tableHeight,x+tableWidth,y);
            drawLine(contentStream,x+tableWidth,y,x,y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void drawLine(PDPageContentStream contentStream, float startX, float startY, float endX, float endY){
        try {
            contentStream.moveTo(startX,startY);
            contentStream.lineTo(endX,endY);
            contentStream.stroke();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // for test
    private static ArrayList<Company> getList() {
        ArrayList<Company> companies = new ArrayList<>();

        Company company1 = new Company();
        company1.setTranId("JE-ZM0018623");
        company1.setTransactionLineId("0");
        company1.setTransactionOrder("0");
        company1.setCompanyId("3011071");
        company1.setFullName("深圳前海智信达国际货运代理有限公司");
        company1.setTransactionId("604851");
        company1.setAmount("-97551");
        company1.setMemo("虚拟退款：本应由2020印度尼西亚转展36800至2020中东食品展/却从迪拜食品展转了36800，转展错误/调回36800元");


        Company company2 = new Company();
        company2.setTranId("JE-ZM0018623");
        company2.setTransactionLineId("0");
        company2.setTransactionOrder("0");
        company2.setCompanyId("3011071");
        company2.setFullName("深圳前海智信达国际货运代理有限公司");
        company2.setTransactionId("604851");
        company2.setAmount("97551");
        company2.setMemo("退款：瑞安市森迪机械设备有限公司/2020中国-南美（巴西）防疫物资国际贸易数字展览会（6月）退33800元");

        companies.add(company1);
        companies.add(company2);

        return companies;
    }



}
