package me.marat.warehouse.service.report;

import me.marat.warehouse.model.report.ReportFormat;
import me.marat.warehouse.model.report.ReportType;

import java.io.File;
import java.util.List;

public interface ReportBuilder {
    boolean support(ReportFormat format);
    File build(List<String> filteredItems, ReportType type);
}
