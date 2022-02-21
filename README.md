# walkbook-backend(Spring)

## ✨ 로컬에서 서버 실행

### 1️⃣ cmd로 실행하기
1. 콘솔창을 열고 다운받은 프로젝트 경로로 이동
```Linux
C:\> cd C:\Users\김세원\Desktop\snulion9th\walkbook-backend
```

2. gradlew.bat 파일 실행
```Linux
(Window)
C:\Users\김세원\Desktop\snulion9th\walkbook-backend> ./gradlew.bat build

(Mac)
./gradlew build
```

3. 프로젝트 경로에서 build/libs로 이동
```Linux
C:\Users\김세원\Desktop\snulion9th\walkbook-backend> cd build\libs
```

4. jar 파일 실행 -> 서버 실행 완료
```Linux
C:\Users\김세원\Desktop\snulion9th\walkbook-backend\build\libs> java -jar server-0.0.1-SNAPSHOT.jar
```

5. 서버 끄는 법  
좌측 하단에 커서를 두고 ctrl + C


### 2️⃣ IntelliJ로 실행하기
1. IntelliJ에서 Project Open
```
C:\Users\김세원\Desktop\snulion9th\walkbook-backend\build.gradle
```

2. Run > Run 'ServerApplication' (shift + F10)

3. 서버 끄는 법  
우측 상단의 🟥 버튼 / Run > Stop 'ServerApplication'
----
### ❗ 8080 port가 이미 사용중인 경우
1. cmd 창에서 netstat -ano 명령어 실행
2. 8080 주소를 사용하는 PID 확인 후 taskkill /pid [pid] /f 명령어 실행

## ✨ 배포된 서버   
https://walkbook-backend.herokuapp.com/  

## 🧐 API 명세 확인하기   
https://walkbook-backend.herokuapp.com/swagger-ui/index.html
