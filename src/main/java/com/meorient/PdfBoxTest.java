package com.meorient;

import com.meorient.mapper.CompanyMapper;
import com.meorient.pojo.Company;
import com.meorient.pojo.Coordinate;
import com.meorient.utils.MybatisUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class PdfBoxTest extends PDFTextStripper {
    // PDF文件路径
    private String pdfPath;
    private String voucherTitle = "记账凭证";
    private String subCompany = "子公司 : ";
    private String docId = "文档编号 : ";

    public Map<String, List<Company>> map;
    File dstFile = new File("D:\\temp\\xxx.pdf");

    private List<Coordinate> customerPosList = new ArrayList<>();
    private List<Coordinate> exhibitionPosList = new ArrayList<>();
    private List<String> voucherId = new ArrayList<>();

    public List<Coordinate> getCustomerPosList() {
        return customerPosList;
    }
    public List<Coordinate> getExhibitionPosList() {
        return exhibitionPosList;
    }

    float y;

    public PdfBoxTest() throws IOException { }
    public PdfBoxTest(String pdfPath) throws IOException {
        super();
        super.setSortByPosition(true);
        this.pdfPath = pdfPath;
    }


    public void getCoordinate(InputStream in) throws IOException {
        PDDocument newDoc = new PDDocument();
        System.out.println("执行 => getCoordinate");
        try {
            // 固定三行    pdf 的页数、系统自带字体
            document = PDDocument.load(in);
            int pages = document.getNumberOfPages();
            PDFont font = PDType0Font.load(document, new File("c:/windows/fonts/SIMLI.TTF"));

            System.out.println("开始执行 复制");

            for (int i = 1; i <= pages; i++) {
                customerPosList = new ArrayList<>();
                voucherId = new ArrayList<>();

                // 固定
                super.setSortByPosition(true);
                super.setStartPage(i);
                super.setEndPage(i);
                Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
                super.writeText(document, dummy);

                int mark = 0;
                for (int j = 0; j < voucherId.size(); j++) {    // 每页两个编号
                    Coordinate customer = customerPosList.get(j);
                    List<Company> companyList = map.get(voucherId.get(j));
                    // 一下判断是否为单个客户
                    System.out.println(voucherId.get(j));
                    if (CollectionUtils.isNotEmpty(companyList)) {

                        if (companyList.size() == 1){
                            Company company = companyList.get(0);
                            addWatermarkText(document, document.getPage(i - 1), font, company.getFullName(), customer.getX(), customer.getY());
                            if(mark++ == 0) { // 避免了一页两表单打印分别打印一次
                                copyOnePage(document, newDoc, i);
                            }
                        } else {
                            if(mark++ == 0) { // 避免了一页两表单打印分别打印一次
                                copyOnePage(document, newDoc, i);
                            }
//                            List<Company> list = map.get(voucherId.get(j));
                            addNewOnePage(newDoc, font, companyList);
                        }
                    }
                }
            }
            newDoc.save(dstFile);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (newDoc != null) {
                document.close();
                newDoc.close();
            }
        }
    }

    /**
     * 获取客户信息
     * @return
     * @throws Exception
     */
    public void getCompanyInfo(){
        System.out.println("执行 => getCompanyInfo : 获取信息");
        map = new HashMap<>();

        SqlSession sqlSession = MybatisUtils.getSqlSession();
        CompanyMapper mapper = sqlSession.getMapper(CompanyMapper.class);
        List<Company> companies = mapper.getAllMsg();
        sqlSession.close();

        for (Company company : companies) {
            if (company.getFullName() == null) {
                continue;
            }
            List<Company> list;
            if (!map.containsKey(company.getTranId())) {
                list = new ArrayList<>();
                list.add(company);
                map.put(company.getTranId(), list);
            } else {
                list = map.get(company.getTranId());
                list.add(company);
                map.put(company.getTranId(), list);
            }

        }
    }



    private static void addNewOnePage(PDDocument newDoc, PDFont font, List<Company> companies){
        PDPage page = new PDPage();

        try (PDPageContentStream cs =
                     new PDPageContentStream(newDoc, page, PDPageContentStream.AppendMode.APPEND, true, true)){
            addTable(newDoc, font, cs, companies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newDoc.addPage(page);
    }


    public static boolean addTable(PDDocument doc, PDFont font, PDPageContentStream cs, List<Company> companies) {
        try {
            PDPage page = doc.getPage(0);
            float width = page.getMediaBox().getWidth();
            float height = page.getMediaBox().getLowerLeftY()+page.getMediaBox().getHeight();

            float x = width - countMM(210);            // 里右侧的距离
            float y = height - countMM(32);            // 离页头的距离
            float tempY = y;

            // 所有表格高度mm转point
            float allTableheight = countMM(8);

            // 上层左起第一个表格的宽度
            float topOneTableWidth = countMM(12);
            // 上层左起第二个表格
            float topTwoTableWidth = countMM(60);
            float topTwoTableX = x + topOneTableWidth;    // 加上之前的宽度
            float topThrTableWidth = countMM(18);
            float topThrTableX = topTwoTableX + topTwoTableWidth;
            float topFouTableWidth = countMM(18);
            float topFouTableX = topThrTableX + topThrTableWidth;
            float topFivTableWidth = countMM(97);
            float topFivTableX = topFouTableX + topFouTableWidth;

            // 提示作用 -- 做参考
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
                drawTable(cs, x,tempY,topOneTableWidth,allTableheight);
                drawTable(cs, topTwoTableX,tempY,topTwoTableWidth,allTableheight);
                drawTable(cs, topThrTableX,tempY,topThrTableWidth,allTableheight);
                drawTable(cs, topFouTableX,tempY,topFouTableWidth,allTableheight);
                drawTable(cs, topFivTableX,tempY,topFivTableWidth,allTableheight);
                tempY -= allTableheight;
            }
            tempY = y;

            // 提示作用 -- 绘制不同的第二行表格
//            drawTable(cs, x,bottomTableY,bottomOneTableWidth,allTableheight2);
//            drawTable(cs, bottomTwoTableX,bottomTableY,bottomTwoTableWidth,allTableheight2);
//            drawTable(cs, bottomThreeTableX,bottomTableY,bottomThreeTableWidth,allTableheight2);
//            drawTable(cs, bottomFourTableX,bottomTableY,bottomFourTableWidth,allTableheight2);

            //  第一行必填信息  文字上移2毫米,居中
            float move2mm = countMM(3);
            int fontSizeTitle = 11;
            writeText(cs, font, fontSizeTitle, x, y + countMM(10), "文档编号：" + companies.get(0).getTranId());
            writeText(cs, font, fontSizeTitle, x + countMM(2), y + move2mm, "行号");
            writeText(cs, font, fontSizeTitle, topTwoTableX + countMM(2), y + move2mm, "客户名称");
            writeText(cs, font, fontSizeTitle, topThrTableX + countMM(2), y + move2mm, "借记");
            writeText(cs, font, fontSizeTitle, topFouTableX + countMM(2), y + move2mm, "贷记");
            writeText(cs, font, fontSizeTitle, topFivTableX + countMM(2), y + move2mm, "摘要");

            int count = 1;
            for (Company company : companies) {
                tempY -= allTableheight;
                // 行号
                writeText(cs, font, 10, x + countMM(3), tempY + move2mm, String.valueOf(count++));
                if (company.getFullName().length() > 37) {
                    String fullName = company.getFullName();
                    float line = tempY + countMM(5);
                    String str = fullName.substring(0, 37);
                    writeText(cs, font, 9, topTwoTableX + countMM(1), line, str);
                    line -= countMM(3);
                    String str1 = fullName.substring(37, fullName.length());
                    writeText(cs, font, 9, topTwoTableX + countMM(1), line, str1);
                } else {
                    writeText(cs, font, 9, topTwoTableX + countMM(2), tempY + move2mm, company.getFullName());
                }

                // 借贷
                if (Float.valueOf(company.getAmount()) >= 0) {     // Integer.valueOf(company.getAmount())
                    writeText(cs, font, 9, topThrTableX + countMM(2), tempY + move2mm, company.getAmount());
                } else {
                    String amount = company.getAmount();
                    writeText(cs, font, 9, topFouTableX + countMM(2), tempY + move2mm, amount.substring(1, amount.length()));
                }

                // 摘要
                if (StringUtils.defaultString(company.getMemo()).length() > 36) {
                    String memo = company.getMemo();
                    float line = tempY + countMM(5);
                    String str = memo.substring(0, 36);
                    writeText(cs, font, 8, topFivTableX + countMM(1), line, str);
                    line -= countMM(3);
                    String str1 = memo.substring(36, memo.length());
                    writeText(cs, font, 8, topFivTableX + countMM(1), line, str1);
                } else {
                    writeText(cs, font, 8, topFivTableX + countMM(1), tempY + move2mm, company.getMemo());
                }
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static void copyOnePage(PDDocument oldDoc, PDDocument newDoc, int i) throws IOException {
        newDoc.addPage(takeOnePage(oldDoc, i));
    }
    private static PDPage takeOnePage(PDDocument document, int i) {
        PDPage page = document.getPage(i - 1);
        return page;
    }

    private static float countMM(float point){
        // 将毫米转换成坐标点
        return (float) (point*72/25.4);
    }
    private static void writeText(PDPageContentStream cs,
                                  PDFont font, float fontSize, float startX,
                                  float startY, String txt) throws IOException {
        if(StringUtils.isEmpty(txt)){
            return;
        }
        cs.beginText();
        // 文字位置
        cs.newLineAtOffset(startX, startY);
        // 设置字体type,size
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(0, 0, 0);
        // 插入文本
        cs.showText(txt);
        cs.endText();
    }
    private static void addWatermarkText(PDDocument oldDoc, PDPage page, PDFont font, String customerName, float x, float y)
            throws IOException {
        try (PDPageContentStream cs
                     = new PDPageContentStream(oldDoc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            float fontSize = 9;
            float height = page.getMediaBox().getHeight();
            cs.setFont(font, fontSize);
            cs.setNonStrokingColor(Color.red);
            cs.setStrokingColor(Color.red);
            cs.beginText();
            cs.newLineAtOffset(x, height - y);
            cs.showText(customerName);
            cs.endText();
        }
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



    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        TextPosition textPosition = textPositions.get(0);
        //找到记账凭证位置
        if (voucherTitle.equals(string)) {
            y = textPosition.getY();
        } else if (string.startsWith(subCompany)) {
            customerPosList.add(new Coordinate(textPosition.getX(), y));
        } else if (string.startsWith(docId)) {
            voucherId.add(StringUtils.substringAfter(string, docId));
            exhibitionPosList.add(new Coordinate(textPosition.getX(), y));
        }
    }

}
