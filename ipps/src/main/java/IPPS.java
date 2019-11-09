//Kevin Valenzuela
//Makda Woldeselassie
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IPPS {
    public static HashMap<Integer, String> drgMap = new HashMap<>();
    public static HashMap<String, String> hrrMap = new HashMap<>();
    public static HashMap<Integer, ArrayList> provMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        FileInputStream fis =new FileInputStream("/Users/kevin/IdeaProjects/ipps/src/main/java/connection.prop");
        Properties prop = new Properties();
        prop.load(fis);
        String url = (String)prop.getProperty("URL");
        String user = (String)prop.getProperty("user");
        String password = (String)prop.getProperty("password");
        String connectURL = url + "?serverTimezone=UTC&user=" + user + "&password=" + password;
        System.out.println("User : " + user + " " + password);
        Connection conn = DriverManager.getConnection(connectURL);
        System.out.println("Connection to MySQL database was successful!");

        String csvFile = "/Users/kevin/Documents/1Fall19/DataBase/dba02/ipps.csv";
        csvSplit(csvFile, conn);
        sendDRG(conn);
        sendHRR(conn);
        sendProv(conn);

        System.out.println("Closing DBA Connection, Bye!");
        conn.close();
    }

    public static void csvSplit(String csvFile, Connection conn){
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                StringBuilder build = new StringBuilder();
                List<String> tokens = new ArrayList<>();
                boolean inQuotes = false;
                boolean dashSeen = false;
                for(char c : line.toCharArray()){
                    switch(c){
                        case ',':
                            if(inQuotes) {
                                build.append(c);
                            }else{
                                tokens.add(build.toString());
                                build = new StringBuilder();
                            }
                            break;
                        case '\"':
                            inQuotes = !inQuotes;
                            break;
                        case ' ':
                            if(dashSeen){
                                dashSeen = false;
                                break;
                            }
                        default:
                            build.append(c);
                            break;
                    }
                }
                tokens.add(build.toString());

                System.out.println(tokens);
//                System.out.println(tokens.size());
                sendStatements(tokens, conn);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static void sendStatements(List<String> values, Connection conn) throws SQLException {

        int drgID = Integer.parseInt(values.get(0).substring(0, 3));
        String drgDef = values.get(0).substring(values.get(0).indexOf('-') + 2);
        drgMap.put(drgID, drgDef);

        int provID = Integer.parseInt(values.get(1));
        String provName = values.get(2);
        String provAddr = values.get(3);
        String provCity = values.get(4);
        String provState = values.get(5);
        String provZip = values.get(6);
        provMap.put(provID, new ArrayList<String>(Arrays.asList(provName, provAddr, provCity, provState, provZip)));

        String hospRegion = values.get(7).substring(0, 2);
        String hospDesc = values.get(7).substring(values.get(7).indexOf('-') + 2);
        hrrMap.put(hospDesc, hospRegion);

        int totDischarges = Integer.parseInt(values.get(8));
        float avgCovCharges = Float.parseFloat(values.get(9));
        float avgTotPayments = Float.parseFloat(values.get(10));
        float avgMedPayments = Float.parseFloat(values.get(11));

        String chargesInsert = "INSERT INTO Charges VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement prpStmt4 = conn.prepareStatement(chargesInsert);
        prpStmt4.setInt(1, totDischarges);
        prpStmt4.setFloat(2, avgCovCharges);
        prpStmt4.setFloat(3, avgTotPayments);
        prpStmt4.setFloat(4, avgMedPayments);
        prpStmt4.setInt(5, drgID);
        prpStmt4.setInt(6,provID);
        prpStmt4.execute();
    }

    public static void sendDRG(Connection conn) throws SQLException {
        for(int key : drgMap.keySet()){
            int first = key;
            String second = drgMap.get(key);
            String diagnoseInsert = "INSERT INTO Diagnose VALUES (?, ?)";
            PreparedStatement prpStmt1 = conn.prepareStatement(diagnoseInsert);
            prpStmt1.setInt(1, first);
            prpStmt1.setString(2, second);
            prpStmt1.execute();
        }
    }

    public static void sendHRR(Connection conn) throws SQLException {
        for(String key : hrrMap.keySet()){
            String first = hrrMap.get(key);
            String second = key;
            String hospitalInsert = "INSERT INTO HospitalReferral(HospitalReferralRegion, HospitalReferralRegionDesc) VALUES (?, ?)";
            PreparedStatement prpStmt3 = conn.prepareStatement(hospitalInsert);
            prpStmt3.setString(1, first);
            prpStmt3.setString(2, second);
            prpStmt3.execute();
        }
    }

    public static void sendProv(Connection conn) throws SQLException {
        for(int key : provMap.keySet()){
            int provId = key;
            ArrayList list = provMap.get(key);
            String pName = (String) list.get(0);
            String pAddr = (String) list.get(1);
            String pCity = (String) list.get(2);
            String pState = (String) list.get(3);
            String pZip = (String) list.get(4);
            String providerInsert = "INSERT INTO Provider(ProviderID, ProviderName, ProviderStreetAddress, ProviderCity, ProviderState, ProviderZip) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement prpStmt2 = conn.prepareStatement(providerInsert);
            prpStmt2.setInt(1, key);
            prpStmt2.setString(2, pName);
            prpStmt2.setString(3, pAddr);
            prpStmt2.setString(4, pCity);
            prpStmt2.setString(5, pState);
            prpStmt2.setString(6, pZip);
            prpStmt2.execute();
        }
    }

}