package it.uninsubria.queryhandler;

import it.uninsubria.areaInteresse.AreaInteresse;
import it.uninsubria.centroMonitoraggio.CentroMonitoraggio;
import it.uninsubria.city.City;
import it.uninsubria.operatore.Operatore;
import it.uninsubria.operatore.OperatoreAutorizzato;
import it.uninsubria.parametroClimatico.NotaParametro;
import it.uninsubria.parametroClimatico.ParametroClimatico;
import it.uninsubria.util.IDGenerator;
import javafx.util.Pair;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Worker extends Thread{

    private static int workerCount = 0;
    private String workerID;
    private final String dbUrl;
    private final Properties props;

    private Connection conn;
    public Worker(String url, Properties props, String workerID){
        this.dbUrl = url;
        this.props = props;
        workerCount++;
        this.workerID = workerID + workerCount;
        try {
            this.conn = DriverManager.getConnection(url, props);
        }catch(SQLException sqle){sqle.printStackTrace(); }
    }


    public List<Pair<City, AreaInteresse>> selectAllCityJoinAi(){
        String query = "select * from city c join area_interesse ai on c.ascii_name = ai.denominazione";
        List<Pair<City, AreaInteresse>> resultList = new LinkedList<Pair<City, AreaInteresse>>();
        System.out.println(workerID + ":" + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                City c = extractCity(rSet);
                AreaInteresse ai = extractAreaInteresse(rSet);
                resultList.add(new Pair<City, AreaInteresse>(c, ai));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<City, CentroMonitoraggio>> selectAllCityJoinCm(){
        String query = "select * from city c join centro_monitoraggio cm on c.ascii_name = cm.comune";
        List<Pair<City, CentroMonitoraggio>> resultList = new LinkedList<Pair<City, CentroMonitoraggio>>();
        System.out.println(workerID + ":" + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                City c = extractCity(rSet);
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                resultList.add(new Pair<City, CentroMonitoraggio>(c, cm));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<NotaParametro, ParametroClimatico>> selectAllFromNotaJoin(){
        String query = "select * from nota_parametro_climatico npc join public.parametro_climatico pc using(notaid)";
        System.out.println(workerID + ":" + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            List<Pair<NotaParametro, ParametroClimatico>> resultList = new LinkedList<Pair<NotaParametro, ParametroClimatico>>();
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                NotaParametro np = extractNota(rSet);
                ParametroClimatico pc = extractParametroClimatico(rSet);
                resultList.add(new Pair<NotaParametro, ParametroClimatico>(np, pc));
            }
            return resultList;
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
    }

    public List<Pair<ParametroClimatico, CentroMonitoraggio>> selectAllPcJoinCm(){
        String query = "select * from parametro_climatico pc join centro_monitoraggio cm using(centroid)";
        List<Pair<ParametroClimatico, CentroMonitoraggio>> resultList
                = new LinkedList<Pair<ParametroClimatico, CentroMonitoraggio>>();
        System.out.println(workerID + ":" + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);
                CentroMonitoraggio cm = extractCentroMonitoraggio(rSet);
                resultList.add(new Pair<ParametroClimatico, CentroMonitoraggio>(pc, cm));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<ParametroClimatico, AreaInteresse>> selectAllPcJoinAi(){
        String query = "select * from parametro_climatico pc join area_interesse ai using(areaid)";
        System.out.println(workerID + ":" + query);
        List<Pair<ParametroClimatico, AreaInteresse>> resultList =
                new LinkedList<Pair<ParametroClimatico, AreaInteresse>>();
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);

                AreaInteresse ai = extractAreaInteresse(rSet);
                resultList.add(new Pair<ParametroClimatico, AreaInteresse>(pc, ai));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }

    public List<Pair<ParametroClimatico, NotaParametro>> selectAllPcJoinNpc(){
        String query = "select * from parametro_climatico pc join nota_parametro_climatico npc using(notaid)";
        List<Pair<ParametroClimatico, NotaParametro>> resultList =
                new LinkedList<Pair<ParametroClimatico, NotaParametro>>();
        System.out.println(workerID + ":" + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            ResultSet rSet = stat.executeQuery();
            while(rSet.next()){
                ParametroClimatico pc = extractParametroClimatico(rSet);
                NotaParametro nota = extractNota(rSet);
                resultList.add(new Pair<ParametroClimatico, NotaParametro>(pc, nota));
            }
        }catch(SQLException sqle){sqle.printStackTrace(); return null;}
        return resultList;
    }


    public Operatore executeLogin(String userID, String password){
        String query = "select * from operatore where userid = '%s' and password = '%s'".formatted(userID, password);
        System.out.println(workerID + ":" + query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            ResultSet rSet = stat.executeQuery();
            rSet.next();
            //expect 1 row
            Operatore o = extractOperatore(rSet);
            return o;
        }catch(SQLException sqle){
            sqle.printStackTrace();
            return null;
        }
    }

    public boolean insertOperatore(String nomeOp, String cognomeOp, String codFisc, String userID, String email, String password, String centroAfferenza){
        String query = "insert into operatore(nome, cognome, codice_fiscale, email, userid, password, centroid) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                .formatted(nomeOp, cognomeOp, codFisc, userID, email, password, centroAfferenza);
        System.out.println(workerID + ":" + query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            int res = stat.executeUpdate();
            System.out.println(res);
            return res == 1;
        }catch(SQLException sqle){sqle.printStackTrace(); return false;}
    }

    public boolean insertCentroMonitoraggio(String nomeCentro, String comune, String stato, List<String> areeInteresseAssociateIDs){
        String centroid = IDGenerator.generateID();
        int idsSize = areeInteresseAssociateIDs.size();
        StringBuilder arrayIDS = new StringBuilder();
        for(int i = 0; i < idsSize; i++){
            arrayIDS.append(areeInteresseAssociateIDs.get(i));
            if(i < idsSize - 1)
                arrayIDS.append(",");
        }
        String query = "insert into centro_monitoraggio(centroid, nomecentro, comune, country, aree_interesse_ids) values ('%s', '%s', '%s', '%s', '{%s}')"
                .formatted(centroid, nomeCentro, comune, stato, arrayIDS.toString());
        System.out.println(workerID + ":" + query);
        try{
            PreparedStatement stat = conn.prepareStatement(query);
            int res = stat.executeUpdate();
            System.out.println(res);
            return res == 1;
        }catch(SQLException sqle){sqle.printStackTrace(); return false;}
    }

    //WARNING: DOES NOT INSERT notaID
    public boolean insertParametroClimatico(ParametroClimatico pc){
        String query =
                "insert into parametro_climatico(parameterid, centroid, areaid, pubdate, valore_vento, valore_umidita, valore_pressione, valore_temperatura, valore_precipitazioni, valore_alt_ghiacciai, valore_massa_ghiacciai) " +
                        "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        query = query.formatted(
                pc.getParameterId(),
                pc.getIdCentro(),
                pc.getAreaInteresseId(),
                pc.getPubDate(),
                //pc.getNotaId(),
                pc.getVentoValue(),
                pc.getUmiditaValue(),
                pc.getPressioneValue(),
                pc.getTemperaturaValue(),
                pc.getPrecipitazioniValue(),
                pc.getAltitudineValue(),
                pc.getMassaValue());
        System.out.println(workerID + ": " + query);
        try(PreparedStatement stat = conn.prepareStatement(query)){
            int res = stat.executeUpdate();
            System.out.println(res);
            return res == 1;
        }catch(SQLException sqle){sqle.printStackTrace(); return false;}
    }
}
