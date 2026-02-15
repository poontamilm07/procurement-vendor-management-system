package com.procurement.procurement.service.report;

import com.procurement.procurement.dto.report.ReportRequestDTO;
import com.procurement.procurement.entity.procurement.PurchaseOrder;
import com.procurement.procurement.entity.procurement.PurchaseOrderItem;
import com.procurement.procurement.entity.vendor.Vendor;
import com.procurement.procurement.repository.procurement.PurchaseOrderRepository;
import com.procurement.procurement.repository.vendor.VendorRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ReportService {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public ReportService(VendorRepository vendorRepository,
                         PurchaseOrderRepository purchaseOrderRepository) {
        this.vendorRepository = vendorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    private JasperPrint generateReport(String reportPath,
                                       List<?> data,
                                       Map<String, Object> parameters) throws JRException {

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(data);

        InputStream reportStream =
                getClass().getResourceAsStream(reportPath);

        if (reportStream == null) {
            throw new RuntimeException("JRXML file not found at: " + reportPath);
        }

        JasperReport jasperReport =
                JasperCompileManager.compileReport(reportStream);

        return JasperFillManager.fillReport(
                jasperReport,
                parameters,
                dataSource
        );
    }

    private byte[] exportPdf(JasperPrint jasperPrint) throws JRException {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private byte[] exportExcel(JasperPrint jasperPrint) throws JRException {

        JRXlsxExporter exporter = new JRXlsxExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(outputStream)
        );

        exporter.exportReport();

        return outputStream.toByteArray();
    }

    public byte[] generateVendorReport(ReportRequestDTO request,
                                       String format) {

        try {

            List<Vendor> vendors = vendorRepository.findAll();
            List<Map<String, Object>> reportData = new ArrayList<>();

            for (Vendor vendor : vendors) {

                List<PurchaseOrder> orders =
                        purchaseOrderRepository.findByVendor(vendor);

                for (PurchaseOrder po : orders) {

                    for (PurchaseOrderItem item : po.getItems()) {

                        Map<String, Object> row = new HashMap<>();

                        row.put("vendorName", vendor.getName());
                        row.put("poNumber", po.getPoNumber());
                        row.put("status", po.getStatus());
                        row.put("productName", item.getProductName());
                        row.put("quantity", item.getQuantity());
                        row.put("unitPrice", item.getUnitPrice());
                        row.put("total",
                                item.getQuantity() * item.getUnitPrice());

                        reportData.add(row);
                    }
                }
            }

            Map<String, Object> params = new HashMap<>();
            params.put("title", "Vendor Purchase Report");

            JasperPrint jasperPrint = generateReport(
                    "/jasper/vendor_report.jrxml",
                    reportData,
                    params
            );

            if ("excel".equalsIgnoreCase(format)) {
                return exportExcel(jasperPrint);
            }

            return exportPdf(jasperPrint);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Report generation failed: " + e.getMessage());
        }
    }
}
