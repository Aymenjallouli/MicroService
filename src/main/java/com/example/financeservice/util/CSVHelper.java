package com.example.financeservice.util;

import com.example.financeservice.entity.Depense;
import com.example.financeservice.entity.Depense.TypeDepense;
import com.example.financeservice.entity.Depense.StatutDepense;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVHelper {
    
    public static final String TYPE = "text/csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType()) || 
               (file.getOriginalFilename() != null && 
                file.getOriginalFilename().endsWith(".csv"));
    }
    
    public static List<Depense> csvToDepenses(InputStream is) {
        List<Depense> depenses = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            // Skip header
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] values = parseCsvLine(line);
                if (values.length < 5) continue;
                
                Depense depense = new Depense();
                depense.setDescription(values[0]);
                depense.setMontant(Double.parseDouble(values[1].replace("\"", "")));
                depense.setType(TypeDepense.valueOf(values[2].trim().toUpperCase()));
                depense.setIdProjet(Long.parseLong(values[3].trim()));
                
                try {
                    depense.setDate(DATE_FORMAT.parse(values[4].trim()));
                } catch (ParseException e) {
                    depense.setDate(new Date());
                }
                
                depense.setStatut(StatutDepense.EN_ATTENTE);
                depenses.add(depense);
            }
            
            return depenses;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
    
    private static String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        
        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }
    
    public static String generateCSVTemplate() {
        StringBuilder template = new StringBuilder();
        
        // Header
        template.append("Description,Montant,Type,ProjetID,Date\n");
        
        // Examples
        template.append("\"Achat de béton pour fondations\",15000,MATERIEL,1,2025-05-20\n");
        template.append("\"Personnel pour installation de plomberie\",8500,MAIN_DOEUVRE,1,2025-05-21\n");
        template.append("\"Transport de matériaux\",2500,TRANSPORT,1,2025-05-22\n");
        template.append("\"Frais administratifs\",1000,AUTRES,1,2025-05-23\n");
        
        // Help info
        template.append("\n# Types disponibles: MATERIEL, MAIN_DOEUVRE, TRANSPORT, AUTRES\n");
        template.append("# Format de date: YYYY-MM-DD\n");
        
        return template.toString();
    }
}
