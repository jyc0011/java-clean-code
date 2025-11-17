# 우아한 테크코스 프리코스 : 오픈 미션

---

> 3주 동안 구글 자바 스타일 가이드 지키기, 클린 코드 원칙, 객체지향적 설계 등을 학습하고 적용했다. 해당 경험을 바탕으로 프로그램을 제작하려한다.

## 📖 프로젝트 소개

학습 과정에서 배운 여러 원칙들은 습관이 되기 전까지는 의식적인 노력을 통해 익숙해져야한다.
CLI 환경에서 실행되는 이 프로그램을 통해 클린 코드와 친숙해지며, 보다 객체 지향적인 설계를 하며 원칙과 가까워져, 비지니스 로직에 집중할 수 있도록 돕는 것을 목표로 한다.
Git Hook과 연동하여 커밋 전에 자동으로 코드를 점검할 수 있다.

---

## 📍목표

- CLI로 Java 프로젝트 경로를 입력 받아 해당 부분을 스캔한다.
- 구글 자바 스타일 가이드를 지켰는지 확인한다.
- 클린코드 원칙을 지켰는지 확인한다.
- 객체 지향 여부를 지켰는지 확인한다.

---

## ✨ 주요 기능

- CLI 기반 동작: 터미널에서 간단한 명령어로 프로젝트 전체를 스캔
- AST 기반 분석: JavaParser를 이용한 AST(Abstract Syntax Tree) 분석으로 정확한 문맥을 파악
- 검증:
    1. Style Check: Google Java Style Guide 준수 여부 확인
    2. Clean Code Check: 메서드 길이, 들여쓰기(Indent) 깊이 등 가독성 저해 요소 감지
    3. OOP Check: 무분별한 Getter/Setter 사용, 원시값 포장 여부 등 객체 지향 원칙 준수 여부 확인
- 리포팅: 위반 사항이 발생한 파일 위치와 라인 번호, 구체적인 개선 가이드를 제공

---

## 🚀시작하기

### 설치 및 빌드

```
# 저장소 클론
git clone https://github.com/jyc0011/java-clean-code.git

# 프로젝트 이동
cd java-clean-code

# 빌드 (Jar 파일 생성)
./gradlew clean build
```

### 사용 방법

빌드된 `jar` 파일을 이용하여 검사하고 싶은 Java 프로젝트의 소스 경로를 입력

```
java -jar build/libs/code.jar /path/to/your/target/project/src/main/java
```

### 실행 결과 예시

```
[INFO] Scanning project: /Users/me/my-project/src/main/java...
[FAIL] Found 5 violations in 3 files!

🔴 Order.java:45 [MethodLength]
   - 메서드 길이가 20라인입니다. (허용 기준: 15라인)
   - 한 가지 기능만 담당하도록 메서드를 더 작게 분리해보세요.

🟠 Member.java:12 [NoSetter]
   - 핵심 도메인 객체에 무분별한 @Setter 사용이 감지되었습니다.
   - 객체의 상태를 변경하는 '의도'가 드러나는 메서드(예: changeAddress())를 만들어주세요.

🔴 Payment.java:30 [DemeterLaw]
   - 한 줄에 너무 많은 점(.)이 사용되었습니다. (location.current.representation.substring(0, 1))
   - 디미터 법칙("친구하고만 대화하라")을 위반했을 가능성이 큽니다. 내부 구조를 묻지 말고, 작업을 시키세요.
...
```

---

## 📋 지원 규칙 (Rules)

현재 버전에서 개발중인 검사 규칙

### 🎨 Style

| 규칙 ID            | 설명                                  | 중요도 |
|------------------|-------------------------------------|-----|
| NamingConvention | 패키지, 클래스, 상수 등의 명명 규칙               | 🔴  |
| NoWildcardImport | 와일드카드 임포트 사용 금지                     | 🔴  |
| ImportOrder      | 임포트 순서 검사                           | 🟠  |
| ModifierOrder    | public static final 등 제어자의 표준 순서 강제 | 🟠  |
| NoFinalizer      | Object.finalize 메서드 오버라이드 금지        | 🔴  |
| OverloadGrouping | 오버로드된 메서드들은 코드 상에 인접                | 🟠  |

### 🧹Clean Code

| 규칙 ID            | 설명                       | 중요도 | 기본값    |
|------------------|--------------------------|-----|--------|
| IndentDepth      | 메서드 내 들여쓰기 깊이 제한         | 🔴  | Max 2  |
| InstanceVarCount | 클래스의 인스턴스 변수 개수 제한       | 🟠  | Max 2  |
| MethodLength     | 메서드 최대 길이 제한             | 🔴  | Max 15 |
| MethodParameter  | 메서드의 인자 수를 제한            | 🔴  | Max 3  |
| NoElse           | `else` 예약어 사용 지양         | 🟠  | ON     |
| LawOfDemeter     | 코드 한 줄에 점 사용을 줄여 결합도 낮춤	 | 🟠  | Max 1  |
| NoHardcoding     | 하드코딩된 문자열이나 숫자 확인        | 🟠  | ON     |

### ☕ OOP

| 규칙 ID           | 설명                                        | 중요도 |
|-----------------|-------------------------------------------|-----|
| NoDataClass     | Getter/Setter만 있는 데이터 클래스(DTO 제외) 감지 및 경고 | 🔴  |
| WrapPrimitive   | 도메인 객체 내 과도한 원시값 사용 감지                    | 🟠  |
| FirstCollection | 컬렉션을 포함한 클래스에서 다른 멤버 변수 있는지 확인            | 🟠  |

---

## 📂 프로젝트 구조

```
src/main/java/clean/code
├── Application.java          # 프로그램 진입점 및 CLI 파서
├── config                    # 규칙 On/Off 및 임계값 설정 관리
├── core
│   ├── ProjectScanner.java   # 디렉토리 재귀 탐색
│   └── Analyzer.java         # 분석 엔진 메인 컨트롤러
├── rules                     # 검사 규칙 (인터페이스 및 구현체)
│   ├── cleancode             # 클린코드 관련 규칙 구현체
│   │   ├── IndentDepthRule.java  
│   │   ├── InstanceVarCountRule.java  
│   │   ├── LawOfDemeterRule.java  
│   │   ├── MethodLengthRule.java  
│   │   ├── MethodParameterRule.java  
│   │   ├── NoElseRule.java  
|   │   └── NoHardcodingRule.java  
│   ├── oop                   # 객체 지향 관련 규칙 구현
│   │   ├── FirstCollectionRule.java  
│   │   ├── NoDataClassRule.java  
|   │   └── WrapPrimitiveRule.java  
│   ├── style                 # 스타일 관련 규칙 구현체
│   │   ├── ImportOrderRule.java  
│   │   ├── ModifierOrderRule.java  
│   │   ├── NamingConventionRule.java  
│   │   ├── NoFinalizerRule.java  
│   │   ├── NoWildcardImportRule.java  
|   │   └── OverloadGroupingRule.java  
│   ├── Rule.java             # 규칙 인터페이스
│   └── RuleRegistry.java     # 활성화된 규칙 관리
└── report                 # 분석 결과 출력 담당
    ├── Violation.java        # 위반 사항 데이터 모델
    └── ConsoleReporter.java  # 사용자 친화적 결과 출력
```

---

## 🛠️ 기술 스택

Picocli

- Language: Java 21
- Build Tool: Gradle
- Core Library: JavaParser (AST 분석)
- CLI: Picocli (옵션 파싱)
- Testing: JUnit 5, AssertJ

---

## 🚀 Git Pre-commit Hook 연동하기

`git commit`을 실행할 때마다 이 검사기를 자동으로 실행하여, 클린 코드 위반 사항이 있으면 커밋을 중단시킬 수 있습니다.

### 1. Fat JAR 빌드

먼저, 모든 의존성이 포함된 실행 가능한 JAR 파일을 빌드합니다.

```bash
# 프로젝트 루트에서 실행
./gradlew clean shadowJar
```

### 2. pre-commit 스크립트 설정
검사를 원하는 다른 프로젝트(예: /path/to/my-project)로 이동하여, 해당 프로젝트의 Git Hook을 설정합니다.

```bash
# 1. 검사하고 싶은 프로젝트로 이동
cd /path/to/my-project

# 2. .git/hooks/pre-commit 파일 생성
touch .git/hooks/pre-commit

# 3. pre-commit 파일에 실행 권한 부여
chmod +x .git/hooks/pre-commit
```

### 3. 스크립트 작성
방금 생성한 .git/hooks/pre-commit 에 해당 내용 추가
```bash
#!/bin/sh

# -----------------------------------------------
# --- Java Clean Code Checker Pre-commit Hook ---
# -----------------------------------------------

# 1. 빌드된 'java-clean-code-checker.jar'의 절대 경로
# ❗️❗️ 이 경로를 사용자의 실제 JAR 경로로 수정하세요! ❗️❗️
CHECKER_JAR_PATH="/Users/me/dev/java-clean-code/build/libs/java-clean-code-1.0-SNAPSHOT-all.jar"

# 2. (선택) 설정 파일(JSON)의 절대 경로 (없으면 기본값 사용)
# CONFIG_PATH="/Users/me/dev/java-clean-code/checker-config.json"

# 3. 검사할 이 프로젝트의 소스 경로
SRC_PATH="./src/main/java"


# --- 실행 ---
echo "Running Java Clean Code Checker..."

# 설정 파일이 있는 경우
# CONFIG_ARG="-c $CONFIG_PATH"

# 설정 파일이 없는 경우 (CONFIG_ARG를 비워둠)
CONFIG_ARG=""

# 4. 검사기 실행 (자바 17+ 필요)
# (경로에 공백이 있어도 처리되도록 "$SRC_PATH" 사용)
RESULT=$(java -jar "$CHECKER_JAR_PATH" "$SRC_PATH" $CONFIG_ARG)

echo "-----------------------------------------------"
echo "$RESULT"
echo "-----------------------------------------------"


# 5. 결과 분석
# 출력된 결과(RESULT)에 '[FAIL]' 문자열이 포함되어 있는지 확인
if echo "$RESULT" | grep -q "\[FAIL\]"; then
  echo "🔴 Clean Code Violations Found! Commit aborted."
  exit 1
else
  echo "✅ Clean Code Check Passed. Proceeding with commit."
  exit 0
fi
```

git commit 실행 시 같이 실행됨