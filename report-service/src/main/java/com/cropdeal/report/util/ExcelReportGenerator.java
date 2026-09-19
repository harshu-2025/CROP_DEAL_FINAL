package com.cropdeal.report.util;

import com.cropdeal.report.dto.OrderResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelReportGenerator {

    public byte[] generateOrderReport(List<OrderResponse> orders) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Orders");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Order ID");
            header.createCell(1).setCellValue("Order Number");
            header.createCell(2).setCellValue("Farmer ID");
            header.createCell(3).setCellValue("Dealer ID");
            header.createCell(4).setCellValue("Crop ID");
            header.createCell(5).setCellValue("Quantity");
            header.createCell(6).setCellValue("Agreed Price");
            header.createCell(7).setCellValue("Total Amount");
            header.createCell(8).setCellValue("Status");
            header.createCell(9).setCellValue("Payment Status");

            int rowNumber = 1;

            for (OrderResponse order : orders) {

                Row row = sheet.createRow(rowNumber++);

                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getOrderNumber());
                row.createCell(2).setCellValue(order.getFarmerId());
                row.createCell(3).setCellValue(order.getDealerId());
                row.createCell(4).setCellValue(order.getCropId());
                row.createCell(5).setCellValue(order.getQuantity());
                row.createCell(6).setCellValue(order.getAgreedPrice());
                row.createCell(7).setCellValue(order.getTotalAmount());
                row.createCell(8).setCellValue(order.getStatus());
                row.createCell(9).setCellValue(order.getPaymentStatus());
            }

            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Unable to generate Excel report");
        }
    }
}