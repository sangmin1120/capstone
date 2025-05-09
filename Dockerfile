# java 17 jdk
FROM openjdk:17-alpine

#환경 변수 설정
ARG JAR_FILE=/build/libs/*.jar

#profile
ENV USER_PROFILE dev

#AWS
ENV AWS_REGION=ap-northeast-2

#작업 디렉토리 설정
WORKDIR /app

#JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

#포트 설정
EXPOSE 8080

#애플리케이션 실행
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=${USER_PROFILE}","/app/app.jar"]