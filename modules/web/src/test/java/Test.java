import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> list = new ArrayList<> ();
        list.add("1");
        list.add("2");
        for (String item : list) {
            if ("2".equals(item)) {
                list.remove(item);
            }
        }

        System.out.println(list);
    }
}