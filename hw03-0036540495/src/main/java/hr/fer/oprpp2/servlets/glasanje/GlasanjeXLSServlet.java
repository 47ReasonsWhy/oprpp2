package hr.fer.oprpp2.servlets.glasanje;

import hr.fer.oprpp2.models.Band;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet used for exporting voting results to an Excel file.
 */
@WebServlet("/glasanje-xls")
public class GlasanjeXLSServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<Integer, Band> bands = GlasanjeUtil.loadBandsWithVotes(req);

        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet =  hwb.createSheet("Votes");

        HSSFRow rowhead = sheet.createRow(0);
        rowhead.createCell(0).setCellValue("ID");
        rowhead.createCell(1).setCellValue("Name");
        rowhead.createCell(2).setCellValue("Votes");
        rowhead.createCell(3).setCellValue("Song link");

        int i = 1;
        for (Band band : bands.values().stream().sorted((o1, o2) -> Integer.compare(o2.getVotes(), o1.getVotes())).toList()) {
            HSSFRow row = sheet.createRow(i);
            row.createCell(0).setCellValue(band.getId());
            row.createCell(1).setCellValue(band.getName());
            row.createCell(2).setCellValue(band.getVotes());
            row.createCell(3).setCellValue(band.getLink());
            i++;
        }

        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment; filename=\"votes.xls\"");

        hwb.write(resp.getOutputStream());

        hwb.close();
    }
}
