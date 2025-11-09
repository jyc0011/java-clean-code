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

빌드된 `jar` 파일을 이용하여 검사하고 싶은 Java 프로젝트의 소스 경로를 입력합니다.

```
java -jar build/libs/code.jar /path/to/your/target/project/src/main/java
```

### 실행 결과 예시

```
[INFO] Scanning project: /path/to/target/project...
[FAIL] Found 3 violations!

1. Order.java:45 [MethodLength]
   - 메서드 길이가 20라인입니다. (제한: 15라인)
   - 한 가지 기능만 하고 있는지 다시 확인해보세요.

2. Member.java:12 [NoSetter]
   - 무분별한 Setter 사용이 감지되었습니다.
   - 객체의 상태를 변경하는 명확한 의도를 가진 메서드를 만들어주세요.

...
```

---

## 📋 지원 규칙 (Rules)

현재 버전에서 지원하는 검사 규칙입니다.

### 🎨 Style & Clean Code

| **규칙 ID** | **설명** | **기본값** |
| --- | --- | --- |
| IndentDepth | 메서드 내 들여쓰기(indent) 깊이 제한 | Max 2 |
| MethodLength | 메서드 최대 길이 제한 | Max 15 lines |
| NoElse | `else` 예약어 사용 지양 (Early Return 권장) | On |

### ☕ Object-Oriented

| **규칙 ID** | **설명** |
| --- | --- |
| NoDataClass | Getter/Setter만 있는 데이터 클래스(DTO 제외) 감지 및 경고 |
| WrapPrimitive | (Experimental) 도메인 객체 내 과도한 원시값 사용 감지 |

---

## 📂 프로젝트 구조

```
src/main/java/clean/code
├── Application.java       # 프로그램 진입점 (CLI 파싱)
├── core/                  # 핵심 분석 엔진
│   ├── ProjectScanner.java
│   └── Analyzer.java
├── parser/                # AST 파싱 로직 (JavaParser Wrapper)
├── rules/                 # 검사 규칙 (인터페이스 및 구현체)
│   ├── Rule.java
│   ├── implementations/
│   └── RuleRepository.java
└── report/                # 분석 결과 출력 담당
```

---

## 🛠️ 기술 스택

- Language: Java 21
- Build Tool: Gradle
- Core Library: JavaParser (AST 분석)
- Testing: JUnit 5, AssertJ

---
