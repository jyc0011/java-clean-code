package clean.code.rules.oop;

import static org.assertj.core.api.Assertions.assertThat;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoDataClassRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new NoDataClassRule();

    @Test
    @DisplayName("필드, Getter, Setter, 생성자만 가진 데이터 클래스를 감지한다.")
    void check_detectsDataClass() {
        String code = """
                    public class User { // line 1
                        private String name;
                        private int age;

                        public User(String name, int age) { this.name = name; this.age = age; }

                        public String getName() { return name; }
                        public void setName(String name) { this.name = name; }
                        public int getAge() { return age; }
                        public void setAge(int age) { this.age = age; }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(1);
        assertThat(violations.getFirst().ruleId()).isEqualTo("NoDataClass");
        assertThat(violations.getFirst().message()).contains("Getter/Setter만 가지고 있습니다.");
    }

    @Test
    @DisplayName("비즈니스 로직(Getter/Setter가 아닌 메서드)이 포함되면 통과시킨다.")
    void check_passesClassWithBusinessLogic() {
        String code = """
                    public class User {
                        private String name;
                        public String getName() { return name; }
                        
                        // Getter/Setter가 아닌 로직
                        public void changeName(String newName) { 
                            this.name = newName;
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("클래스 이름이 DTO로 끝나는 경우(DTO 허용) 통과시킨다.")
    void check_passesDtoClass() {
        String code = """
                    // DTO는 Getter/Setter만 있어도 허용
                    public class UserDTO { 
                        private String name;
                        public String getName() { return name; }
                        public void setName(String name) { this.name = name; }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}