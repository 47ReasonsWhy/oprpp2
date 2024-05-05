package hr.fer.oprpp2.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.hssf.usermodel.HSSFRow;

import java.io.IOException;

/**
 * A servlet that generates an Excel file containing a table of powers of numbers in a given range.
 * The servlet expects three parameters: a, b and n. Parameters a and b define the range of numbers
 * for which the powers will be calculated, while parameter n defines the power to which the numbers
 * will be raised. The servlet generates an Excel file with n sheets, each containing a table of numbers
 * and their corresponding powers.
 */
@WebServlet("/powers")
public class PowerTableServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int a, b, n;

        try {
            a = Integer.parseInt(req.getParameter("a"));
            b = Integer.parseInt(req.getParameter("b"));
            n = Integer.parseInt(req.getParameter("n"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please provide integer values for parameters a, b and n.");
            return;
        }

        if (a < -100 || a > 100) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter a must be between -100 and 100.");
            return;
        }
        if (b < -100 || b > 100) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter b must be between -100 and 100.");
            return;
        }
        if (n < 1 || n > 5) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter n must be between 1 and 5.");
            return;
        }
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }

        HSSFWorkbook hwb = new HSSFWorkbook();

        for (int i = 1; i <= n; i++) {
            HSSFSheet sheet =  hwb.createSheet(i + getSuffix(i) + " power");
            HSSFRow rowhead = sheet.createRow(0);
            rowhead.createCell(0).setCellValue("Number");
            rowhead.createCell(1).setCellValue(i + getSuffix(i) + " power");

            for (int j = a; j <= b; j++) {
                HSSFRow row = sheet.createRow(j - a + 1);
                row.createCell(0).setCellValue(j);
                row.createCell(1).setCellValue(Math.pow(j, i));
            }
        }

        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment; filename=\"powers.xls\"");

        hwb.write(resp.getOutputStream());

        hwb.close();
    }

    /**
     * Returns the suffix for a given number n ("st" for 1, "nd" for 2, "rd" for 3, "th" for all other numbers).
     *
     * @param n the number for which the suffix is returned
     * @return the suffix for the given number
     */
    private String getSuffix(int n) {
        if (n == 1) return "st";
        if (n == 2) return "nd";
        if (n == 3) return "rd";
        return "th";
    }
}
