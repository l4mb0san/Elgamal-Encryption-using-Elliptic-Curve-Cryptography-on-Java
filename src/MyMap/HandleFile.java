/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyMap;

import java.math.BigInteger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author lehuu
 */
public class HandleFile {
    
     public static void writeExcel(Row row, int rowCount, int columnCount, Point point) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue("(" + point.x + "," + point.y + ")");
    }
    
    public static Point getcellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String c = cell.getStringCellValue();
        c = c.replaceAll("[^0-9]+", " ");
        String[] xy = c.trim().split(" ");
        if (xy[0] == null || xy[1] == null) {
            System.err.println("Not Found!");
            return null;
        }
        Point Q = new Point(new BigInteger(xy[0]), new BigInteger(xy[1]));
        return Q;
    } 

}
