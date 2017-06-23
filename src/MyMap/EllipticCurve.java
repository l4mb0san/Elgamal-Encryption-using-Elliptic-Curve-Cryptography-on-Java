/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author lehuu
 */
public class EllipticCurve {
    public BigInteger a; //factor a
    public BigInteger b; //factor b
    public BigInteger p; //infinity field p
    public BigInteger n = BigInteger.ZERO; //number of points
    
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) {
        this.a = a;
        this.b = b;
        this.p = p;
    }
    
    public BigInteger numberofpoints() {
        return n;
    }
    
    /*check two numbers a and b factor belong to the finite field p*/
    public boolean belongsField() {
        return !(a.compareTo(p) == 1 || b.compareTo(p) == 1);
    }
    
    public boolean PointbelongsField(Point P) {
        if (P.x.compareTo(p) == 1 || P.y.compareTo(p) == 1) {
            return false;
        }
        BigInteger left_side = P.y.pow(2).mod(p);
        BigInteger right_side = P.x.pow(3).add(a.multiply(P.x)).add(b).mod(p);
        return left_side.compareTo(right_side) == 0;
    }
    
    /*all the points in a finite field p*/ 
    public void allpoints(String excelFilePath) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("All Points");
        BigInteger left_side;
        BigInteger right_side;
        int rowCount = 0;
        int columnCount = 0;
        n = BigInteger.ZERO;
        
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            Point point = new Point();
            Row row = sheet.createRow(rowCount);
            HandleFile.writeExcel(row, rowCount, columnCount, point);
            n = n.add(BigInteger.ONE);
            for (BigInteger i = BigInteger.ZERO; i.compareTo(p) == -1; i = i.add(BigInteger.ONE)) {
                left_side = i.pow(2).mod(p);
                for (BigInteger j = BigInteger.ZERO; j.compareTo(p) == -1; j = j.add(BigInteger.ONE)) {
                    right_side = j.pow(3).add(a.multiply(j)).add(b).mod(p);
                    if (left_side.compareTo(right_side) == 0) {
                        point = new Point(j,i);
                        System.out.println("(" + point.x + "," + point.y + ")");
                        HandleFile.writeExcel(row, rowCount, columnCount++, point);
                        if (columnCount == 256) { //256 (IV) is maximum number of columns in Excel
                            rowCount++;
                            columnCount = 0;
                            row = sheet.createRow(rowCount);
                        }
                        n = n.add(BigInteger.ONE);
                    }
                }
            }
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /*generating sets of points from P stored in excelFilePath File*/
    public BigInteger generateorderGroup(Point P, String excelFilePath) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Order Group");
        int rowCount = 0;
        int columnCount = 0;
        BigInteger numofpoints = BigInteger.ZERO;
        EllipticCurve E = new EllipticCurve(a, b, p);
        
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            Point Q = new Point();
            Row row = sheet.createRow(rowCount);
            HandleFile.writeExcel(row, rowCount, columnCount++, Q);
            numofpoints = numofpoints.add(BigInteger.ONE);
            HandleFile.writeExcel(row, rowCount, columnCount++, P);
            numofpoints = numofpoints.add(BigInteger.ONE);
            Q = P.Pointdoubling(E);
            HandleFile.writeExcel(row, rowCount, columnCount++, Q);
            numofpoints = numofpoints.add(BigInteger.ONE);
            while (true) {
                Q = P.Pointaddition(Q, E);
                if (Q.isPOSITIVE_INFINITY()) {
                    break;
                }
                if (columnCount == 256) { //256 (IV) is maximum number of columns in Excel
                    rowCount++;
                    columnCount = 0;
                    row = sheet.createRow(rowCount);
                }
                numofpoints = numofpoints.add(BigInteger.ONE);
                HandleFile.writeExcel(row, rowCount, columnCount++, Q);
            }
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        this.n = numofpoints;
        return numofpoints;
    }
    
}
