package com.mikhailova.lesson;


import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import net.lingala.zip4j.ZipFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {

    @Test
    void downloadFileTest() throws Exception {
        open("https://github.com/selenide/selenide/blob/master/README.md");
        File download = $("#raw-url").download();
        String result;
        try (InputStream is = new FileInputStream(download)) {
            result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThat(result).contains("Selenide = UI Testing Framework powered by Selenium WebDriver");
    }

    @Test
    void uploadFileTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("example.txt");
        $("#file-submit").click();
        $("#uploaded-files")
                .shouldHave(text("example.txt"));
    }

    @Test
    void downloadPdfTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File download = $(byText("PDF download")).download();
        PDF parsed = new PDF(download);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void downloadExcelTest() throws Exception {
//        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
//        File download = $(byText("PDF download")).download();
        try (InputStream inputStream  = getClass().getClassLoader().getResourceAsStream("175.xlsx")) {
            XLS parsed = new XLS(inputStream);
            assertThat(parsed.excel.getSheetAt(1).getRow(22).getCell(1).getStringCellValue())
                    .isEqualTo("Зацепина Ольга Владимировна");
        }
    }

    @Test
    void parseCsvTest() throws Exception {
        URL url = getClass().getClassLoader().getResource("file.csv");
        CSVReader reader = new CSVReader(new FileReader(new File(url.toURI())));

        List<String[]> strings = reader.readAll();

        assertThat(strings).contains(
                new String[] {"actor", "film"},
                new String[] {"D. Tennant", "Doctor Who"},
                new String[] {"Alisa Milano", "Charmed"}
        );
    }

    @Test
    public void parseZipFileTest() throws Exception {
        String zipPassword = "qwer123";
        ZipFile zipFile = new ZipFile(new File("src/test/resources/test.zip"));
        if (zipFile.isEncrypted())
        {
            zipFile.setPassword(zipPassword.toCharArray());
        }
        assertThat(zipFile.getFileHeaders().toString()).containsIgnoringCase("test.txt");
    }
}
