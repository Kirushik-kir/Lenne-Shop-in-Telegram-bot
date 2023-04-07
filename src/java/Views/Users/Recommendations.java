package Views.Users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Recommendations {
    public static String recommendationsText() throws IOException {
        StringBuilder recommendations = new StringBuilder();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("Recommendations.txt"))));

        String line = "";
        while ((line = reader.readLine()) != null) {
            recommendations.append(line).append("\n");
        }
        reader.close();
        if (!recommendations.isEmpty()) return recommendations.toString();
        else return "Рекомендаций пока нет";
    }
}
