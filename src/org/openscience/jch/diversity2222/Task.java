package org.openscience.jch.diversity2222;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.IteratingMolTableReader;

public class Task implements Callable {

    private  Initialize in;
    private  int id = 0;
    private  int start = 0;
    private  int stop = 0;
    private  Map< Integer, byte[]> map;

    public Task() {
    }

    public Task(int i, int st, int sp, Initialize ini) {
        System.out.println("cons"+ i);
        this.start = st;
        this.stop = sp;
        this.id = i;
        this.in = ini;
    }

    @Override
    public Object call() throws Exception {
        System.out.println("Call has been executed:"+this.start+"-"+this.stop+":"+this.id);
        IAtomContainer molecule = null;
        String ab ="";
        Statement stmt = this.in.getConnection().createStatement();
        MACCSFingerprinter mp = new MACCSFingerprinter();
        this.map = new HashMap< Integer, byte[]>();
        //System.out.println(start+","+stop+","+id);
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM completeDataSet WHERE ID>=" + this.start + " and ID<=" + this.stop + ";")) {
            while (rs.next()) {
                int idno = rs.getInt("ID");
                // System.out.println(this.id+";"+idno);
                InputStream is = new ByteArrayInputStream(rs.getString("STUCTURE").getBytes());
                IteratingMolTableReader reader = new IteratingMolTableReader(is, DefaultChemObjectBuilder.getInstance(), true);
                while (reader.hasNext()) {
                    molecule = reader.next();
                    break;
                }
                ab = ab+","+String.valueOf(idno);
                byte[] bi = mp.getBitFingerprint(molecule).asBitSet().toByteArray();
                this.map.put(idno, bi);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        //writeToFile(ab,this.id);
        updateMACCSEntries(map);
        //System.out.println(this.map.size()+","+this.start+","+this.stop+","+this.id+","+"this thread is done");
        return this.id;
    }
    
    
    
    public void writeToFile(String sb,int i){
        try {
			String content = sb;
 
			File file = new File("/Users/chandu/Desktop/mt/"+i+".txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void updateMACCSEntries(Map< Integer, byte[]> mp) throws SQLException {
        String sql = "UPDATE completeDataSet SET FINGERPRINT = ? WHERE ID = ?";
        PreparedStatement psUpdateRecord = this.in.getConnection().prepareStatement(sql);
        int[] iNoRows = null;
        for (int a : mp.keySet()) {
            psUpdateRecord.setBytes(1, mp.get(a));
            psUpdateRecord.setInt(2, a);
            psUpdateRecord.addBatch();
        }
        iNoRows = psUpdateRecord.executeBatch();
        this.in.getConnection().commit();
    }
}
