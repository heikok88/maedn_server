package maedn_server.logic.luts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import maedn_server.messages.server.Figure;

public abstract class Fields {

    private final HashMap<Integer, List<Integer>> fields;
    protected final Figure[] figures;

    public Fields(int cnt) {
        this.fields = new HashMap<>();
        figures = new Figure[cnt];
    }

    protected void addPair(int index, List<Integer> xy) {
        fields.put(index, xy);
    }

    public Integer getIndex(List<Integer> xy) {
        Integer index = null;
        for (Map.Entry<Integer, List<Integer>> field : fields.entrySet()) {
            if (field.getValue().equals(xy)) {
                index = field.getKey();
                break;
            }
        }
        return index;
    }

    public List<Integer> getXY(int index) {
        return fields.get(index);
    }

    public void setFigure(int index, Figure f) {
        figures[index] = f;
        f.setXY(getXY(index));
    }

    public boolean isFigure(int index) {
        return (figures[index] != null);
    }

    public void delFigure(int index) {
        figures[index] = null;
    }

    public Figure getFigure(int index) {
        return figures[index];
    }

    public List<Figure> getAllFigures() {
        List<Figure> list = new ArrayList<>();
        for (Figure f : figures) {
            if (f != null) {
                list.add(f);
            }
        }
        return list;
    }

    public int size() {
        int cnt = 0;
        for (int i = 0; i < figures.length; i++) {
            if (figures[i] != null) {
                cnt++;
            }
        }
        return cnt;
    }

}
