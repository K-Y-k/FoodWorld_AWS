#!/bin/bash

# 자주 사용하는 값 변수에 저장
REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=FoodWorld_AWS

# build의 결과물 (jar 파일) 특정 위치로 복사
echo "> build 파일 복사"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"
# 현재 수행 중인 스프링 부트 애플리케이션의 프로세스 ID를 찾는다.
# 실행 중이면 종료하기 위해서다.
# 스프링 부트 애플리케이션 이름으로 된 다른 프로그램들이 있을 수 있어 해당 이름의 Jar 프로세스를 찾은 뒤 ID를 찾는다.
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID"
if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> Jar Name: $JAR_NAME"
echo "> $JAR_NAME 에 실행권한 추가"
# Jar 파일은 실행 권한이 없는 상태다.
# nohup으로 실행할 수 있게 실행 권한을 부여한다.
chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"
nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-real-db.properties,/home/ec2-user/app/application-s3.properties,classpath:/application-real.properties \
        -Dspring.profile.active=real \
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
        # nohup 실행 시 CodeDeploy는 무한 대기한다.
        # 이 문제를 해결하기 위해 nohup.out 파일을 표준 입출력용으로 별도로 사용한다.
        # 이렇게 하지 않으면 nohup.out 파일이 생기지 않고, CodeDeploy 로그에 표준 입출력이 출력된다.
        # nohup이 끝나기 전까지 CodeDeploy도 끝나지 않으니 꼭 이렇게 해야만 한다.