package wholesalemarket_LMP;
//Download JAVA Excel API: http://sourceforge.net/projects/jexcelapi/files/jexcelapi/

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import jxl.*;
import jxl.read.biff.BiffException;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class ReadExcel {

    private String inputFile;

    public void setInputFile(String _inputFile) {
        this.inputFile = _inputFile;
    }

    public double[][] read(String _sheetName, int rowSize, int columnSize, boolean _isAgent) throws IOException {
        double[][] dataMatrix = new double[rowSize][columnSize];
        File inputWorkbook = new File(inputFile);
        Workbook w;
        int startCell = WholesaleMarket.START_HOUR + 1; // Excel does not start at 0. The hour 0 is the row number 1
        int endCell = startCell + WholesaleMarket.HOUR_PER_DAY;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            //System.out.println("Excel Sheet: "+_sheetName);
            // Select the excel sheet
            Sheet sheet = w.getSheet(_sheetName);
            int index = 0;
            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j, i);
                    //System.out.println("Cell: "+i+":"+j);
                    if (i != 0) {
                        if (_isAgent) {
                            if (i >= startCell && i < endCell) { //The start Hour and the End Hour are important
                                dataMatrix[index][j] = Double.parseDouble(cell.getContents().replace(",", "."));
                                if (j == sheet.getColumns() - 1) {
                                    index++;
                                }
                            }
                        } else {
                            dataMatrix[i - 1][j] = Double.parseDouble(cell.getContents().replace(",", "."));
                        }
                    }
                }
            }
        } catch (BiffException ex) {
        }
        //System.out.println("End of: "+_sheetName);

        return dataMatrix;
    }

    public static double[][] readExcelData(String _fileLocation, String _excelSheet, int rowSize, int columnSize, boolean _isAgent) {
        ReadExcel excel_file = new ReadExcel();
        excel_file.setInputFile(_fileLocation);
        double[][] auxMatrix = new double[rowSize][columnSize];
        try {
            auxMatrix = excel_file.read(_excelSheet, rowSize, columnSize, _isAgent);
        } catch (IOException ioEx) {
            JOptionPane.showMessageDialog(null, _fileLocation + " not found! (No such file or directory)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return auxMatrix;
    }
}
