package me.marat.warehouse.service.report;

import me.marat.warehouse.exception.NotImplementedYet;
import me.marat.warehouse.model.report.ReportFormat;
import me.marat.warehouse.model.report.ReportType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class PdfReportBuilder implements ReportBuilder {

    @Override
    public boolean support(ReportFormat format) {
        return format == ReportFormat.PDF;
    }

    @Override
    public File build(List<String> filteredItems, ReportType type) {
        throw new NotImplementedYet("Pdf reports not implemented yet");
    }
}
