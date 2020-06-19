package edu.skku.map.changer.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Preference {
    int Gogh;
    int Munch;
    int Monet;
    int Lichtenstein;
    int Picasso;
    int Kutter;
    int Chirico;
    int Nolan;
    int Severini;
    int Kaleidoscope;
    int Rhombuses;

    public Preference(List<Integer> list){
        this.Gogh = list.get(0);
        this.Munch = list.get(1);
        this.Monet = list.get(2);
        this.Lichtenstein = list.get(3);
        this.Picasso = list.get(4);
        this.Kutter = list.get(5);
        this.Chirico = list.get(6);
        this.Nolan = list.get(7);
        this.Severini = list.get(8);
        this.Kaleidoscope = list.get(9);
        this.Rhombuses = list.get(10);
    }

    public Preference() {
        this.Gogh = 0;
        this.Munch = 0;
        this.Monet = 0;
        this.Lichtenstein = 0;
        this.Picasso = 0;
        this.Kutter = 0;
        this.Chirico = 0;
        this.Nolan = 0;
        this.Severini = 0;
        this.Kaleidoscope = 0;
        this.Rhombuses = 0;
    }

    public Preference(Map<String, Object> map) {
        if (map.get("Gogh") != null) this.Gogh = ((Long) map.get("Gogh")).intValue();
        else this.Gogh = -1;
        if (map.get("Munch") != null) this.Munch = ((Long) map.get("Munch")).intValue();
        else this.Munch = 0;
        if (map.get("Monet") != null) this.Monet = ((Long) map.get("Monet")).intValue();
        else this.Monet = 0;
        if (map.get("Lichtenstein") != null) this.Lichtenstein = ((Long) map.get("Lichtenstein")).intValue();
        else this.Lichtenstein = 0;
        if (map.get("Picasso") != null) this.Picasso = ((Long) map.get("Picasso")).intValue();
        else this.Picasso = 0;
        if (map.get("Kutter") != null) this.Kutter = ((Long) map.get("Kutter")).intValue();
        else this.Kutter = 0;
        if (map.get("Chirico") != null) this.Chirico = ((Long) map.get("Chirico")).intValue();
        else this.Chirico = 0;
        if (map.get("Nolan") != null) this.Nolan = ((Long) map.get("Nolan")).intValue();
        else this.Nolan = 0;
        if (map.get("Severini") != null) this.Severini = ((Long) map.get("Severini")).intValue();
        else this.Severini = 0;
        if (map.get("Kaleidoscope") != null) this.Kaleidoscope = ((Long) map.get("Kaleidoscope")).intValue();
        else this.Kaleidoscope = 0;
        if (map.get("Rhombuses") != null) this.Rhombuses = ((Long) map.get("Rhombuses")).intValue();
        else this.Rhombuses = 0;
    }

    public Map<String, Object> toPreference()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Gogh", Gogh);
        result.put("Munch", Munch);
        result.put("Monet", Monet);
        result.put("Lichtenstein", Lichtenstein);
        result.put("Picasso", Picasso);
        result.put("Kutter", Kutter);
        result.put("Chirico", Chirico);
        result.put("Nolan", Nolan);
        result.put("Severini", Severini);
        result.put("Kaleidoscope", Kaleidoscope);
        result.put("Rhombuses", Rhombuses);

        return result;
    }

    public void updateData(String filter)
    {
        if (filter.equals("Gogh")) this.Gogh += 1;
        else if (filter.equals("Munch")) this.Munch += 1;
        else if (filter.equals("Monet")) this.Monet += 1;
        else if (filter.equals("Lichtenstein")) this.Lichtenstein += 1;
        else if (filter.equals("Picasso")) this.Picasso += 1;
        else if (filter.equals("Kutter")) this.Kutter += 1;
        else if (filter.equals("Chirico")) this.Chirico += 1;
        else if (filter.equals("Nolan")) this.Nolan += 1;
        else if (filter.equals("Severini")) this.Severini += 1;
        else if (filter.equals("Kaleidoscope")) this.Kaleidoscope += 1;
        else if (filter.equals("Rhombuses")) this.Rhombuses += 1;

    }


}