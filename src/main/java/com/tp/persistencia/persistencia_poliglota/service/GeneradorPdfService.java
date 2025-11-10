package com.tp.persistencia.persistencia_poliglota.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class GeneradorPdfService {

    private static final String DIRECTORIO_PDF = "informes_pdf";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public GeneradorPdfService() {
        // Crear directorio si no existe
        File directorio = new File(DIRECTORIO_PDF);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    public String generarInformeMaxMin(Long solicitudId, Map<String, Object> datos, Map<String, Object> parametros) {
        try {
            String nombreArchivo = DIRECTORIO_PDF + "/informe_max_min_" + solicitudId + ".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph("INFORME DE TEMPERATURAS Y HUMEDAD MÁXIMAS/MÍNIMAS")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Parámetros del informe
            document.add(new Paragraph("Parámetros de búsqueda:").setBold());
            if (parametros.containsKey("ciudad")) {
                document.add(new Paragraph("Ciudad: " + parametros.get("ciudad")));
            }
            if (parametros.containsKey("pais")) {
                document.add(new Paragraph("País: " + parametros.get("pais")));
            }
            if (parametros.containsKey("fechaDesde")) {
                document.add(new Paragraph("Fecha desde: " + parametros.get("fechaDesde")));
            }
            if (parametros.containsKey("fechaHasta")) {
                document.add(new Paragraph("Fecha hasta: " + parametros.get("fechaHasta")));
            }

            document.add(new Paragraph("\n"));

            // Resultados
            document.add(new Paragraph("Resultados:").setBold());
            
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(80));

            table.addCell(new Cell().add(new Paragraph("Concepto").setBold()));
            table.addCell(new Cell().add(new Paragraph("Valor").setBold()));

            table.addCell("Temperatura Máxima");
            table.addCell(String.format("%.2f °C", datos.get("temperaturaMax")));

            table.addCell("Temperatura Mínima");
            table.addCell(String.format("%.2f °C", datos.get("temperaturaMin")));

            table.addCell("Humedad Máxima");
            table.addCell(String.format("%.2f %%", datos.get("humedadMax")));

            table.addCell("Humedad Mínima");
            table.addCell(String.format("%.2f %%", datos.get("humedadMin")));

            table.addCell("Total de Mediciones");
            table.addCell(datos.get("totalMediciones").toString());

            document.add(table);

            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(formatter))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }

    public String generarInformePromedio(Long solicitudId, Map<String, Object> datos, Map<String, Object> parametros) {
        try {
            String nombreArchivo = DIRECTORIO_PDF + "/informe_promedio_" + solicitudId + ".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph("INFORME DE TEMPERATURAS Y HUMEDAD PROMEDIO")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Parámetros del informe
            document.add(new Paragraph("Parámetros de búsqueda:").setBold());
            if (parametros.containsKey("ciudad")) {
                document.add(new Paragraph("Ciudad: " + parametros.get("ciudad")));
            }
            if (parametros.containsKey("pais")) {
                document.add(new Paragraph("País: " + parametros.get("pais")));
            }
            if (parametros.containsKey("fechaDesde")) {
                document.add(new Paragraph("Fecha desde: " + parametros.get("fechaDesde")));
            }
            if (parametros.containsKey("fechaHasta")) {
                document.add(new Paragraph("Fecha hasta: " + parametros.get("fechaHasta")));
            }

            document.add(new Paragraph("\n"));

            // Resultados
            document.add(new Paragraph("Resultados:").setBold());
            
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(80));

            table.addCell(new Cell().add(new Paragraph("Concepto").setBold()));
            table.addCell(new Cell().add(new Paragraph("Valor").setBold()));

            table.addCell("Temperatura Promedio");
            table.addCell(String.format("%.2f °C", datos.get("temperaturaPromedio")));

            table.addCell("Humedad Promedio");
            table.addCell(String.format("%.2f %%", datos.get("humedadPromedio")));

            table.addCell("Total de Mediciones");
            table.addCell(datos.get("totalMediciones").toString());

            document.add(table);

            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(formatter))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }

    public String generarInformeAlertas(Long solicitudId, Map<String, Object> datos, Map<String, Object> parametros) {
        try {
            String nombreArchivo = DIRECTORIO_PDF + "/informe_alertas_" + solicitudId + ".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph("INFORME DE ALERTAS GENERADAS")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Parámetros
            document.add(new Paragraph("Parámetros de búsqueda:").setBold());
            if (parametros.containsKey("ciudad")) {
                document.add(new Paragraph("Ciudad: " + parametros.get("ciudad")));
            }
            if (parametros.containsKey("temperaturaMin")) {
                document.add(new Paragraph("Temperatura mínima alerta: " + parametros.get("temperaturaMin") + " °C"));
            }
            if (parametros.containsKey("temperaturaMax")) {
                document.add(new Paragraph("Temperatura máxima alerta: " + parametros.get("temperaturaMax") + " °C"));
            }
            if (parametros.containsKey("humedadMin")) {
                document.add(new Paragraph("Humedad mínima alerta: " + parametros.get("humedadMin") + " %"));
            }
            if (parametros.containsKey("humedadMax")) {
                document.add(new Paragraph("Humedad máxima alerta: " + parametros.get("humedadMax") + " %"));
            }

            document.add(new Paragraph("\n"));

            // Resultados
            document.add(new Paragraph("Resultados:").setBold());
            document.add(new Paragraph("Total de alertas generadas: " + datos.get("alertasGeneradas")));
            document.add(new Paragraph("Mediciones fuera de rango: " + datos.get("medicionesFueraRango")));

            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(formatter))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }

    public String generarInformeConsulta(Long solicitudId, Map<String, Object> datos, Map<String, Object> parametros) {
        try {
            String nombreArchivo = DIRECTORIO_PDF + "/informe_consulta_" + solicitudId + ".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(nombreArchivo));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            Paragraph titulo = new Paragraph("INFORME DE CONSULTA DE MEDICIONES")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // Parámetros
            document.add(new Paragraph("Parámetros de búsqueda:").setBold());
            if (parametros.containsKey("sensorId")) {
                document.add(new Paragraph("Sensor ID: " + parametros.get("sensorId")));
            }
            if (parametros.containsKey("fechaDesde")) {
                document.add(new Paragraph("Fecha desde: " + parametros.get("fechaDesde")));
            }
            if (parametros.containsKey("fechaHasta")) {
                document.add(new Paragraph("Fecha hasta: " + parametros.get("fechaHasta")));
            }

            document.add(new Paragraph("\n"));

            // Resultados
            document.add(new Paragraph("Resultados:").setBold());
            document.add(new Paragraph("Total de registros: " + datos.get("totalRegistros")));

            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Nota: Los datos detallados están disponibles en formato JSON.")
                    .setFontSize(10)
                    .setItalic());

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(formatter))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}
