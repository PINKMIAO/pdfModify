package com.baven;

import com.meorient.PdfBoxTest;
import com.meorient.controller.FileController;
import com.meorient.mapper.CompanyMapper;
import com.meorient.pojo.Company;
import com.meorient.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
//import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyTest {
//
//    @Test
//    public void connectSQL() {
//        Connection connection = null;
//        ResultSet rs = null;
//        try {
//            Class.forName("com.netsuite.jdbc.openaccess.OpenAccessDriver");
//            String sql = "select b.TRANID,a.COMPANY_ID, c.FULL_NAME from TRANSACTION_LINES a left join TRANSACTIONS b on a.TRANSACTION_ID = b.TRANSACTION_ID left join entity c on a.COMPANY_ID = c.ENTITY_ID ";
//            connection = DriverManager. getConnection("jdbc:ns://5179288.connect.api.netsuite.com:1708;ServerDataSource=NetSuite.com;Encrypted=1;CustomProperties=(AccountID=5179288;RoleID=3)", "DataMigration@meorient.com", "@^*gLGyGCp");
//            PreparedStatement pst = connection.prepareStatement(sql);
//            rs = pst.executeQuery();
//            System.out.println(rs);
//            while (rs.next()) {
//                System.out.print(rs.getString(1) + ", ");
//                System.out.print(rs.getString(2) + ", ");
//                System.out.println(rs.getString(3));
//                System.out.println();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//

    @Test
    public void test01() {
        // 桌面 C:\Users\MeoAdmin\Desktop\202010浙米凭证打印.pdf
        PdfBoxTest pdfBoxTest = null;
        try {
            //pdfBoxTest = new PdfBoxTest("C:\\Users\\MeoAdmin\\Desktop\\202010浙米凭证打印.pdf");
            pdfBoxTest.getCompanyInfo();
            //pdfBoxTest.getCoordinate();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }

    @Test
    public void test02() {
        PdfBoxTest pdfBoxTest = null;
        try {
            InputStream in = new FileInputStream("C:\\Users\\MeoAdmin\\Desktop\\202010浙米凭证打印.pdf");
            //pdfBoxTest.getCoordinateTest(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test03() {

        SqlSession sqlSession = MybatisUtils.getSqlSession();

        CompanyMapper mapper = sqlSession.getMapper(CompanyMapper.class);

        List<Company> allMsg = mapper.getAllMsg();
        System.out.println("4");
        for (Company company : allMsg) {
            System.out.println(company);
        }
        sqlSession.close();
    }

    @Test
    public void test04() throws IOException {
        PdfBoxTest pdfBoxTest = new PdfBoxTest();
        pdfBoxTest.getCompanyInfo();

//        for (Map.Entry<String, List<Company>> entry : pdfBoxTest.map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        List<Company> list = pdfBoxTest.map.get("JE-ZM0015380");
        for (Company company : list) {
            System.out.println(company);
        }
//        System.out.println("====================");
//        List<Company> list1 = info.get("126");
//        for (Company company : list1) {
//            System.out.println(company);
//        }
    }

    @Test
    public void test05(){
        String str = "1111.pdf";
        System.out.println(str.substring(str.length()-4, str.length()));
    }

}

