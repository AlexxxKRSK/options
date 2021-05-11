import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

      public void readFile(Path path, List<Option> ls) {
        ls.clear();
        try (var stream = Files.lines(path)) {
            stream.skip(1)
//                    .limit(100)
                    .map(s -> Arrays.asList(s.split(";")))
                    .forEach(l -> ls.add(new Option(l)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(ls.size());
    }

    public Map.Entry<FileTime, Path> getLatestFile(Path source){
        try (Stream<Path> s = Files.walk(source)) {
            return s
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(FileHelper::getFileTime, v->v))
                    .entrySet().stream()
                    .reduce((e1,e2)-> e1.getKey().toMillis()>e2.getKey().toMillis()?e1:e2).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FileTime getFileTime(Path p) {
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
